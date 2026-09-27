package org.sutormin.nanocraft.world.chunk;

import org.sutormin.nanocraft.NanoCraft;
import org.sutormin.nanocraft.world.Dimension;
import org.sutormin.nanocraft.data.Registries;
import org.sutormin.nanocraft.data.quickaccess.QuickAccessBlocks;
import org.sutormin.nanocraft.data.types.Block;
import org.sutormin.nanocraft.data.types.BlockShape;
import org.sutormin.nanocraft.world.Direction;
import org.sutormin.nanocraft.world.FaceCullCache;
import org.sutormin.nanocraft.world.biome.BiomeTint;
import org.sutormin.nanocraft.world.render.Mesh;

import java.util.List;

public class Chunk {
    public static final int ATLAS_SIZE = 8;
    public static final float TILE_SIZE = 1.0f / ATLAS_SIZE;
    public static final int SIZE_X = 16;
    public static final int SIZE_Y = 384;
    public static final int SIZE_Z = 16;

    public static final int SEA_LEVEL = 63;
    /** Biomes are stored per 4x4x4 cell. */
    public static final int BIOME_CELLS_Y = SIZE_Y / 4;

    private final ChunkPos worldPos;
    // 32 bits for: 16b = blockid, 16b = blockstate (redstone level, orientation, etc)
    private char[] blocks = new char[SIZE_X * SIZE_Y * SIZE_Z];

    // Opaque and cutout faces (alpha-tested), drawn first
    public Mesh mesh;
    // Translucent faces (water, stained glass, ice), blended after all opaque geometry
    public Mesh translucentMesh;

    // The 3x3 chunks around this one (index (dz+1)*3 + (dx+1)), looked up once per
    // buildMesh so neighbor reads at the chunk edges don't hit the world's map.
    private final Chunk[] neighbors = new Chunk[9];
    private float[] aoScratch = new float[8];

    // Per-thread vertex/index arrays, kept at their grown size between builds. Mesh.prepare
    // copies the data out, so reusing them avoids ~10 MB of garbage per chunk build.
    private static final class MeshBuffer {
        char[] vertices = new char[1024];
        int vCount;
        int[] indices = new int[1024];
        int iCount;

        void pushVertex(char v) {
            if (vCount == vertices.length) vertices = java.util.Arrays.copyOf(vertices, vertices.length * 2);
            vertices[vCount++] = v;
        }

        void pushIndex(int i) {
            if (iCount == indices.length) indices = java.util.Arrays.copyOf(indices, indices.length * 2);
            indices[iCount++] = i;
        }

        Mesh.Prepared prepare() {
            return Mesh.prepare(vertices, vCount, indices, iCount);
        }
    }
    private static final class Scratch {
        final MeshBuffer solid = new MeshBuffer();
        final MeshBuffer translucent = new MeshBuffer();
    }
    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    /** Output of {@link #buildMeshData}: one prepared mesh per render pass. */
    public record MeshData(Mesh.Prepared solid, Mesh.Prepared translucent) {
        public void free() {
            solid.free();
            translucent.free();
        }
    }
    // blocks whose face/texture count mismatch was already reported
    private static final java.util.Set<Integer> WARNED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public Chunk(ChunkPos worldPos) {
        this.worldPos = worldPos;
        this.mesh = new Mesh();
        mesh.setPos(this.worldPos.x(), this.worldPos.z());
        this.translucentMesh = new Mesh();
        translucentMesh.setPos(this.worldPos.x(), this.worldPos.z());
    }

    public void setBlocks(char[] blocks) {
        this.blocks = blocks;
    }

    // biome id per 4x4x4 cell, see getBiome
    private char[] biomes = new char[BIOME_CELLS_Y * 16];

    public void setBiomes(char[] biomes) {
        this.biomes = biomes;
    }

    /** Biome id of a cell: x/z 0..3 within the chunk, y 0..BIOME_CELLS_Y-1 from the bottom of the world. */
    public int getBiome(int cellX, int cellY, int cellZ) {
        return biomes[(cellY * 4 + cellZ) * 4 + cellX];
    }

    // ------------------------------------------------------------------
    // Noise and position hashing (hash(x, y, z) also picks random texture rotations)
    // ------------------------------------------------------------------

    private int hash(int x, int z) {
        int h = x * 374761393 ^ z * 668265263;
        h = (h ^ (h >>> 13)) * 1274126177;
        return Math.abs(h ^ (h >>> 16));
    }

    private int hash(int x, int y, int z) {
        int h = x * 374761393 ^ y * 668265263 ^ z * 83492791;
        h = (h ^ (h >>> 13)) * 1274126177;
        return (h ^ (h >>> 16)) & 0x7FFFFFFF;
    }

    private float sampleNoise2D(float x, float z) {
        int xi = (int) Math.floor(x);
        int zi = (int) Math.floor(z);

        float xf = x - (float) Math.floor(x);
        float zf = z - (float) Math.floor(z);

        float u = xf * xf * (3 - 2 * xf);
        float v = zf * zf * (3 - 2 * zf);

        int g00 = hash(xi, zi) % 4;
        int g10 = hash(xi + 1, zi) % 4;
        int g01 = hash(xi, zi + 1) % 4;
        int g11 = hash(xi + 1, zi + 1) % 4;

        float n00 = grad(g00, xf, zf);
        float n10 = grad(g10, xf - 1, zf);
        float n01 = grad(g01, xf, zf - 1);
        float n11 = grad(g11, xf - 1, zf - 1);

        float x1 = n00 + u * (n10 - n00);
        float x2 = n01 + u * (n11 - n01);

        return x1 + v * (x2 - x1);
    }

    private float grad(int hash, float x, float z) {
        return switch (hash & 3) {
            case 0 -> x + z;
            case 1 -> -x + z;
            case 2 -> x - z;
            default -> -x - z;
        };
    }

    // ------------------------------------------------------------------
    // Face geometry model
    //
    // A block's visible geometry now comes from its BlockShape: an explicit
    // list of vertices (in the same 0..128 per-axis fixed-point space the
    // mesh already packs into) plus a list of faces, each a set of vertex
    // indices, a BlockShape.Direction, and a shouldCull flag. This replaces
    // the previous hardcoded "6 faces of a unit cube" model, so a block can
    // define any shape (slabs, stairs, custom models, cross-plants, ...)
    // while culling and AO both stay correct.
    //
    // The per-direction (normal, tangent u, tangent v) basis, and the
    // face-vs-face cull caching, live in FaceCullCache; Chunk just uses
    // them for meshing.
    // ------------------------------------------------------------------

    /**
     * Looks up the block at chunk-local (x,y,z), which may reach into a
     * neighboring chunk. Returns {@code null} for air, out-of-world-height
     * space, or an unloaded neighbor chunk -- anywhere there's nothing to
     * occlude against.
     */
    private Block getBlockAt(int x, int y, int z) {
        if (y < 0 || y >= SIZE_Y) return null;
        char id = meshBlockAt(x, y, z);
        // NULL: unloaded chunk (can't occlude) or never-written air
        if (id == QuickAccessBlocks.AIR || id == QuickAccessBlocks.NULL) return null;
        return Registries.BLOCK.get(id);
    }

    /**
     * Checks whether {@code face} is actually hidden by whatever's on the
     * other side of it. See {@link FaceCullCache} for the actual geometry
     * test and caching; this just wires it up with this chunk's neighbor
     * lookup.
     */
    private boolean isFaceOccluded(int x, int y, int z, Block block, BlockShape shape, int faceIndex,
                                   FaceCullCache.FaceBasis basis) {
        if (!FaceCullCache.infoOf(shape).faceTouchesBoundary()[faceIndex]) return false; // skips the neighbor lookup entirely

        Block neighbor = getBlockAt(x + basis.nx(), y + basis.ny(), z + basis.nz());
        if (neighbor == null) return false;
        if (block.isWater() && neighbor.containsWater()) return true; // no water surface between water and waterlogged blocks
        if (!neighbor.hidesFacesOf(block)) return false; // e.g. stone stays visible behind glass
        BlockShape neighborShape = neighbor.getShape();
        if (neighborShape == null) return false;

        return FaceCullCache.occludes(shape.getFaces().get(faceIndex), shape.getVertices(), neighborShape);
    }

    // ------------------------------------------------------------------
    // Meshing
    // ------------------------------------------------------------------

    /** Builds and uploads the mesh right away. Main (GL) thread only. */
    public void buildMesh() {
        captureNeighbors();
        uploadMesh(buildMeshData());
    }

    /**
     * Records the 8 surrounding chunks for the next {@link #buildMeshData}.
     * Main thread only: the world's chunk map isn't thread-safe.
     */
    public void captureNeighbors() {
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                neighbors[(dz + 1) * 3 + (dx + 1)] = (dx == 0 && dz == 0) ? this
                        : NanoCraft.WORLD.getChunk(worldPos.offset(dx, dz));
            }
        }
    }

    /**
     * Builds the mesh data without touching OpenGL, so it can run on a worker thread.
     * Call {@link #captureNeighbors} first, and never run two builds of the same chunk at
     * once (they share the scratch fields). Blocks changed by the main thread mid-build may
     * give a stale mesh; World re-meshes the chunk afterwards because the change marks it dirty.
     */
    public MeshData buildMeshData() {
        Scratch scratch = SCRATCH.get();
        scratch.solid.vCount = scratch.solid.iCount = 0;
        scratch.translucent.vCount = scratch.translucent.iCount = 0;

        int worldOffsetX = worldPos.x() * SIZE_X;
        int worldOffsetZ = worldPos.z() * SIZE_Z;

        // z, y, x order walks the blocks array sequentially (see getIndex)
        for (int z = 0; z < SIZE_Z; z++) {
            for (int y = 0; y < SIZE_Y; y++) {
                for (int x = 0; x < SIZE_X; x++) {
                    char blockId = blocks[getIndex(x, y, z)];
                    if (blockId == QuickAccessBlocks.AIR || blockId == QuickAccessBlocks.NULL) continue;

                    Block block = Registries.BLOCK.get(blockId);
                    BlockShape shape = block.getShape();
                    if (shape == null || block.getRenderLayer() == Block.RenderLayer.INVISIBLE) continue;
                    MeshBuffer out = block.getRenderLayer() == Block.RenderLayer.TRANSLUCENT
                            ? scratch.translucent : scratch.solid;

                    int wx = worldOffsetX + x;
                    int wz = worldOffsetZ + z;
                    // quarter turns for randomly rotated textures, from the position within the chunk
                    Block.TextureRotation rotation = block.getTextureRotation();
                    int[][] uvVariants = block.getUvVariants();
                    int[] uvCodes = uvVariants == null ? null : uvVariants[((hash(x, y, z) >>> 8) & 0xFFFF) % uvVariants.length];
                    int variant = rotation == Block.TextureRotation.NONE ? 0 : (hash(x, y, z) >>> 8) & 3;
                    // RANDOM_MIRROR (vanilla stone): bit 0 mirrors every face, bit 1 turns top/bottom 180 degrees
                    boolean mirror = rotation == Block.TextureRotation.RANDOM_MIRROR && (variant & 1) != 0;
                    // biome grass color for tinted faces, packed as 5-bit RGB (0 = no tint)
                    int grassTint = block.hasBiomeTint() ? packTint(grassColorAt(wx, y, wz)) : 0;
                    boolean[] boundary = FaceCullCache.infoOf(shape).faceTouchesBoundary();
                    int turns = rotation == Block.TextureRotation.RANDOM_MIRROR ? variant & 2 : variant;
                    List<BlockShape.Vertex> verts = shape.getVertices();
                    List<BlockShape.Face> faces = shape.getFaces();
                    // one texture means "every face"; otherwise there should be one per face
                    if (block.getTextureCount() > 1 && faces.size() > block.getTextureCount()
                            && WARNED.add(block.getId())) {
                        System.err.println("Block " + block.getName() + " (id=" + block.getId() + ") has "
                                + faces.size() + " faces but only " + block.getTextureCount() + " textures");
                    }
                    for (int i = 0; i < faces.size(); i++) {
                        BlockShape.Face face = faces.get(i);
                        FaceCullCache.FaceBasis basis = FaceCullCache.basisOf(face.dir());

                        // shouldCull=true means "attempt culling": actually
                        // check what's on the other side (air, an unloaded
                        // chunk, or a neighbor block whose own geometry may
                        // or may not fully cover this face) rather than
                        // blindly hiding it. shouldCull=false skips the
                        // check entirely and always renders the face (e.g.
                        // a cross-plant quad that should never be culled).
                        if (face.shouldCull() && isFaceOccluded(x, y, z, block, shape, i, basis)) {
                            continue;
                        }

                        if (uvCodes != null) {
                            int code = i < uvCodes.length ? uvCodes[i] : 0;
                            addFace(out, wx, y, wz, x, y, z, basis, verts, face, block.getTexture(i), code & 3, (code & 4) != 0,
                                    block.hasSolidTexture(), block.isFaceTinted(i) ? grassTint : 0, boundary[i]);
                        } else {
                            boolean rotates = rotation == Block.TextureRotation.RANDOM_ALL
                                    || face.dir() == Direction.UP || face.dir() == Direction.DOWN;
                            addFace(out, wx, y, wz, x, y, z, basis, verts, face, block.getTexture(i), rotates ? turns : 0, mirror,
                                    block.hasSolidTexture(), block.isFaceTinted(i) ? grassTint : 0, boundary[i]);
                        }
                    }

                    // waterlogged blocks also sit in a block of still water, like vanilla's fluid state
                    if (block.isWaterlogged()) addWater(scratch.translucent, wx, wz, x, y, z, shape);
                }
            }
        }

        return new MeshData(scratch.solid.prepare(),
                Mesh.prepareSorted(scratch.translucent.vertices, scratch.translucent.vCount,
                        scratch.translucent.indices, scratch.translucent.iCount));
    }

    // ------------------------------------------------------------------
    // Biome tint
    // ------------------------------------------------------------------

    private final int[] zoomScratch = new int[3];

    /**
     * Grass color at a block (world x/z, array y), with vanilla's biome lookup at biome blend 0. The
     * lookup can land in a neighboring chunk's cell; if that chunk isn't loaded, the nearest cell here.
     */
    private int grassColorAt(int wx, int y, int wz) {
        int minY = Dimension.minY();
        BiomeTint.zoom(wx, y + minY, wz, zoomScratch);
        int cellX = zoomScratch[0], cellZ = zoomScratch[2];
        int cellY = Math.clamp(zoomScratch[1] - minY / 4, 0, BIOME_CELLS_Y - 1);
        int dx = Math.clamp(Math.floorDiv(cellX, 4) - worldPos.x(), -1, 1);
        int dz = Math.clamp(Math.floorDiv(cellZ, 4) - worldPos.z(), -1, 1);
        Chunk chunk = neighbors[(dz + 1) * 3 + (dx + 1)];
        int biome = chunk != null
                ? chunk.getBiome(Math.floorMod(cellX, 4), cellY, Math.floorMod(cellZ, 4))
                : getBiome(Math.clamp(cellX - worldPos.x() * 4, 0, 3), cellY, Math.clamp(cellZ - worldPos.z() * 4, 0, 3));
        return BiomeTint.grassColor(biome, wx, wz);
    }

    /** 0xRRGGBB to 5-bit RGB (red in the low bits), never 0 so it can't read as "no tint". */
    private static int packTint(int rgb) {
        int r = Math.round(((rgb >> 16) & 255) * 31 / 255f);
        int g = Math.round(((rgb >> 8) & 255) * 31 / 255f);
        int b = Math.round((rgb & 255) * 31 / 255f);
        return Math.max(1, r | (g << 5) | (b << 10));
    }

    /**
     * Emits the water around a waterlogged block, culled like a water source block at that spot. Like
     * vanilla, sides the block itself completely covers get no water face: it would lie exactly on the
     * block's own face and flicker against it.
     */
    private void addWater(MeshBuffer out, int wx, int wz, int x, int y, int z, BlockShape blockShape) {
        boolean[] coveredBySelf = FaceCullCache.infoOf(blockShape).fullSide();
        Block water = Registries.BLOCK.get(QuickAccessBlocks.WATER);
        BlockShape shape = water.getShape();
        if (shape == null) return;
        List<BlockShape.Face> faces = shape.getFaces();
        for (int i = 0; i < faces.size(); i++) {
            BlockShape.Face face = faces.get(i);
            FaceCullCache.FaceBasis basis = FaceCullCache.basisOf(face.dir());
            if (coveredBySelf[face.dir().ordinal()]) continue;
            if (face.shouldCull() && isFaceOccluded(x, y, z, water, shape, i, basis)) continue;
            addFace(out, wx, y, wz, x, y, z, basis, shape.getVertices(), face, water.getTexture(i), 0, false, false, 0,
                    FaceCullCache.infoOf(shape).faceTouchesBoundary()[i]);
        }
    }

    /** Uploads data from {@link #buildMeshData} and frees it. Main (GL) thread only. */
    public void uploadMesh(MeshData data) {
        mesh.upload(data.solid());
        translucentMesh.upload(data.translucent());
    }

    public ChunkPos getPos() {
        return worldPos;
    }

    /**
     * Emits one BlockShape face (an arbitrary vertex fan, not necessarily a
     * quad) for the block at world position (wx,wy,wz) / chunk-local
     * position (lx,ly,lz).
     */
    private void addFace(MeshBuffer out, int wx, int wy, int wz, int lx, int ly, int lz,
                         FaceCullCache.FaceBasis basis, List<BlockShape.Vertex> verts,
                         BlockShape.Face face, int tex, int uvTurns, boolean mirrorU, boolean solidTexture, int tint,
                         boolean onBoundary) {
        int[] indices = face.vertices();
        int n = indices.length;
        if (n < 3) return; // not a renderable polygon

        int startIndex = out.vCount / 7;
        float shade = SHADE[face.dir().ordinal()];
        if (aoScratch.length < n) aoScratch = new float[n];
        float[] aos = aoScratch;

        for (int i = 0; i < n; i++) {
            BlockShape.Vertex v = verts.get(indices[i]);
            aos[i] = vertexAO(lx, ly, lz, basis, v, onBoundary);

            char gx = (char) (((wx & 15) << 7) + v.x());
            char gy = (char) ((wy << 7) + v.y());
            char gz = (char) (((wz & 15) << 7) + v.z());

            int uv1 = Math.round(face.uv()[i][0] * 128.0f);
            int uv2 = Math.round(face.uv()[i][1] * 128.0f);
            if (mirrorU) uv1 = 128 - uv1; // horizontal flip, like vanilla's cube_mirrored models
            for (int t = 0; t < uvTurns; t++) { // quarter turn around the texture center
                int turned = 128 - uv2;
                uv2 = uv1;
                uv1 = turned;
            }
            char uv = (char) (uv1 * 129 + uv2);

            // brightness: ambient occlusion times vanilla's per-direction face shading
            char ao = (char) Math.floor(aos[i] * shade * 65535);

            char layer = (char) tex; // texture arrays have at most a few thousand layers
            // bit 0: draw see-through texture pixels with their stored color instead of cutting them out
            // bits 1-15: tint color as 5-bit RGB (0 = untinted)
            char flags = (char) ((solidTexture ? 1 : 0) | (tint << 1));

            out.pushVertex(gx);
            out.pushVertex(gy);
            out.pushVertex(gz);
            out.pushVertex(uv);
            out.pushVertex(ao);
            out.pushVertex(layer);
            out.pushVertex(flags);

        }

        if (n == 4) {
            // Standard quad: flip the diagonal split based on AO so lighting
            // interpolates smoothly instead of producing a visible seam
            // ("anisotropy fix"). Assumes vertices are listed BL, BR, TR, TL.
            float ao0 = aos[0];
            float ao1 = aos[1];
            float ao2 = aos[2];
            float ao3 = aos[3];

            if (ao0 + ao2 < ao1 + ao3) {
                out.pushIndex(startIndex);     out.pushIndex(startIndex + 1); out.pushIndex(startIndex + 3);
                out.pushIndex(startIndex + 1); out.pushIndex(startIndex + 2); out.pushIndex(startIndex + 3);
            } else {
                out.pushIndex(startIndex);     out.pushIndex(startIndex + 1); out.pushIndex(startIndex + 2);
                out.pushIndex(startIndex + 2); out.pushIndex(startIndex + 3); out.pushIndex(startIndex);
            }
        } else {
            // Arbitrary polygon: simple fan triangulation from vertex 0.
            for (int i = 1; i < n - 1; i++) {
                out.pushIndex(startIndex);
                out.pushIndex(startIndex + i);
                out.pushIndex(startIndex + i + 1);
            }
        }
    }

    // ------------------------------------------------------------------
    // Ambient occlusion
    // ------------------------------------------------------------------

    /** Vanilla's face shading by direction (Direction order: up, down, north, south, east, west). */
    private static final float[] SHADE = {1.0f, 0.5f, 0.8f, 0.8f, 0.6f, 0.6f};

    /*private float getAOValue(byte index) {
        return switch (index) {
            case 0b10 -> 0.8f;
            case 0b01 -> 0.6f;
            case 0b00 -> 0.4f;
            default -> 0.9f; // 0 blocks: completely open air (bright)
        };
    }*/

    private float getAOIndex(boolean side1, boolean side2, boolean corner) {
        if (side1 && side2) return 0.4f; // 3 blocks: corner enclosed (darkest)
        int count = 0;
        if (side1) count++;
        if (side2) count++;
        if (corner) count++;

        return switch (count) {
            case 1 -> 0.8f;
            case 2 -> 0.6f;
            case 3 -> 0.4f;
            default -> 1.0f; // 0 blocks: completely open air (bright)
        };
    }

    /**
     * Computes AO for one vertex of a face on the block at chunk-local
     * (x,y,z). Generalized from the original's six hand-written per-face
     * switch cases into one routine driven by the face's normal/tangent
     * basis and the vertex's own position, so it works for any face
     * direction *and* any vertex position on that face -- not just the
     * four corners of a full unit cube. A vertex sitting past the block's
     * midpoint (64 in the 0..128 fixed-point range) along a tangent axis
     * is treated as being on that axis's positive side for AO sampling
     * purposes, and vice versa; a vertex sitting exactly on the midpoint
     * doesn't get an extra diagonal/edge sample in that axis.
     */
    private float vertexAO(int x, int y, int z, FaceCullCache.FaceBasis basis, BlockShape.Vertex v, boolean onBoundary) {
        // like vanilla: a face on the block's edge samples the layer of blocks in front of it, a face
        // inside the block (a snow layer's top, a slab seen from inside) the block's own layer
        int nx = onBoundary ? x + basis.nx() : x;
        int ny = onBoundary ? y + basis.ny() : y;
        int nz = onBoundary ? z + basis.nz() : z;

        int uCoord = v.x() * basis.ux() + v.y() * basis.uy() + v.z() * basis.uz();
        int vCoord = v.x() * basis.vx() + v.y() * basis.vy() + v.z() * basis.vz();

        int du = Integer.compare(uCoord, 64);
        int dv = Integer.compare(vCoord, 64);

        boolean side1 = du != 0 && castsAmbientOcclusion(
                nx + du * basis.ux(), ny + du * basis.uy(), nz + du * basis.uz());
        boolean side2 = dv != 0 && castsAmbientOcclusion(
                nx + dv * basis.vx(), ny + dv * basis.vy(), nz + dv * basis.vz());
        boolean corner = du != 0 && dv != 0 && castsAmbientOcclusion(
                nx + du * basis.ux() + dv * basis.vx(),
                ny + du * basis.uy() + dv * basis.vy(),
                nz + du * basis.uz() + dv * basis.vz());

        return getAOIndex(side1, side2, corner);
    }

    // ------------------------------------------------------------------
    // Block storage / access
    // ------------------------------------------------------------------

    private int getIndex(int x, int y, int z) {
        return (z * SIZE_X * SIZE_Y) + (y * SIZE_X) + x;
    }

    public char getBlock(int x, int y, int z) {
        return blocks[getIndex(x, y, z)];
    }

    public void setBlock(int x, int y, int z, char block) {
        blocks[getIndex(x, y, z)] = block;
    }

    /**
     * Used for AO while meshing: like vanilla, only opaque full cubes (and leaves, even see-through ones)
     * cast ambient occlusion, so glass, water, plants, fences, walls, slabs, stairs, snow layers and
     * carpets don't darken their neighbors.
     * NULL (unloaded chunk or never-written air) casts none.
     */
    public boolean castsAmbientOcclusion(int x, int y, int z) {
        Block block = getBlockAt(x, y, z);
        if (block == null || (block.getRenderLayer() != Block.RenderLayer.OPAQUE && !block.isLeaves())) return false;
        BlockShape shape = block.getShape();
        return shape != null && FaceCullCache.infoOf(shape).fullCube();
    }

    /** Block at chunk-local (x,y,z), reaching up to one chunk over via the neighbors cached by buildMesh. */
    private char meshBlockAt(int x, int y, int z) {
        if (x >= 0 && x < SIZE_X && z >= 0 && z < SIZE_Z) return blocks[getIndex(x, y, z)];
        int dx = x < 0 ? -1 : x >= SIZE_X ? 1 : 0;
        int dz = z < 0 ? -1 : z >= SIZE_Z ? 1 : 0;
        Chunk chunk = neighbors[(dz + 1) * 3 + (dx + 1)];
        if (chunk == null) return QuickAccessBlocks.NULL;
        return chunk.getBlock(x - dx * SIZE_X, y, z - dz * SIZE_Z);
    }

    public char getBlockInterchunk(int x, int y, int z) {
        Chunk chunk = NanoCraft.WORLD.getChunk(worldPos.offset(Math.floorDiv(x, SIZE_X), Math.floorDiv(z, SIZE_Z)));
        if (chunk == null) return QuickAccessBlocks.NULL;
        return chunk.getBlock(Math.floorMod(x, SIZE_X), y, Math.floorMod(z, SIZE_Z));
    }

    public char getBlockChunkSafe(int x, int y, int z) {
        if (x < 0 || x >= SIZE_X || z < 0 || z >= SIZE_X) return QuickAccessBlocks.NULL;
        return blocks[getIndex(x, y, z)];
    }

    public void setBlockChunkSafe(int x, int y, int z, char block) {
        if (x < 0 || x >= SIZE_X || z < 0 || z >= SIZE_X) return;
        blocks[getIndex(x, y, z)] = block;
    }

    public void render() {
        if (mesh != null) mesh.render();
    }

    /** Draws the translucent faces farthest first from the camera (render coordinates). */
    public void renderTranslucent(float camX, float camY, float camZ) {
        if (translucentMesh == null) return;
        // re-sort after moving a block nearby, less often for faraway chunks
        float dx = worldPos.x() * SIZE_X + SIZE_X / 2f - camX, dz = worldPos.z() * SIZE_Z + SIZE_Z / 2f - camZ;
        translucentMesh.sortFor(camX, camY, camZ, Math.max(1f, (float) Math.sqrt(dx * dx + dz * dz) / 16f));
        translucentMesh.render();
    }

    public void cleanup() {
        if (mesh != null) mesh.cleanup();
        if (translucentMesh != null) translucentMesh.cleanup();
    }
}