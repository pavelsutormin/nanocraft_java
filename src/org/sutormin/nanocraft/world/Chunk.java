package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.NanoCraft;
import org.sutormin.nanocraft.block.BlockType;
import org.sutormin.nanocraft.block.BlockRegistry;
import org.sutormin.nanocraft.block.BlockTypes;
import org.sutormin.nanocraft.render.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Chunk {
    public static final int ATLAS_SIZE = 8;
    public static final float TILE_SIZE = 1.0f / ATLAS_SIZE;
    public static final int SIZE_X = 16;
    public static final int SIZE_Y = 384;
    public static final int SIZE_Z = 16;

    public static final int SEA_LEVEL = 63;

    private final ChunkPos worldPos;
    // 32 bits for: 16b = blockid, 16b = blockstate (redstone level, orientation, etc)
    private short[] blocks = new short[SIZE_X * SIZE_Y * SIZE_Z];
    public Mesh mesh;

    public Chunk(ChunkPos worldPos) {
        this.worldPos = worldPos;
        this.mesh = new Mesh();
        //generateTerrain();
    }

    public void setBlocks(short[] blocks){this.blocks=blocks;}

    /*private void generateTerrain() {
        int worldOffsetX = worldPos.x() * SIZE_X;
        int worldOffsetZ = worldPos.z() * SIZE_Z;

        for (int x = 0; x < SIZE_X; x++) {
            for (int z = 0; z < SIZE_Z; z++) {
                int wx = worldOffsetX + x;
                int wz = worldOffsetZ + z;

                float baseNoise = sampleNoise2D(wx * 0.02f, wz * 0.02f);
                float detailNoise = sampleNoise2D(wx * 0.08f, wz * 0.08f);

                int height = (int) (72 + (baseNoise * 32.0f) + (detailNoise * 3.0f));
                height = Math.max(1, Math.min(SIZE_Y - 1, height));

                for (int y = 0; y < SIZE_Y; y++) {
                    if (y < height - 4) {
                        blocks[getIndex(x, y, z)] = BlockTypes.STONE; // stone
                    } else if (y < height) {
                        blocks[getIndex(x, y, z)] = BlockTypes.DIRT; // dirt
                    } else if (y == height) {
                        if (height <= SEA_LEVEL + 1) {
                            blocks[getIndex(x, y, z)] = BlockTypes.SAND; // sand (unimp)
                        } else {
                            blocks[getIndex(x, y, z)] = BlockTypes.GRASS; // grass
                        }
                    } else if (y <= SEA_LEVEL) {
                        blocks[getIndex(x, y, z)] = BlockTypes.WATER; // water (unimp)
                    }
                }

                blocks[getIndex(x, 0, z)] = BlockTypes.BEDROCK;
            }
        }
    }*/

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

    public void buildMesh() {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int worldOffsetX = worldPos.x() * SIZE_X;
        int worldOffsetZ = worldPos.z() * SIZE_Z;

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                for (int z = 0; z < SIZE_Z; z++) {
                    if (blocks[getIndex(x, y, z)] == 0) continue;
                    BlockType block = BlockRegistry.getBlock(blocks[getIndex(x, y, z)]);

                    int wx = worldOffsetX + x;
                  int wz = worldOffsetZ + z;

                    if (isTransparent(x, y + 1, z))
                        addFace(vertices, indices, wx, y, wz, 0, block.getTexture(0)); // top
                    if (isTransparent(x, y - 1, z))
                        addFace(vertices, indices, wx, y, wz, 1, block.getTexture(1)); // bottom
                    if (isTransparent(x, y, z + 1))
                        addFace(vertices, indices, wx, y, wz, 2, block.getTexture(2)); // front
                    if (isTransparent(x, y, z - 1))
                        addFace(vertices, indices, wx, y, wz, 3, block.getTexture(3)); // back
                    if (isTransparent(x - 1, y, z))
                        addFace(vertices, indices, wx, y, wz, 4, block.getTexture(4)); // left
                    if (isTransparent(x + 1, y, z))
                        addFace(vertices, indices, wx, y, wz, 5, block.getTexture(5)); // right
                }
            }
        }

        float[] vArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) vArray[i] = vertices.get(i);

        int[] iArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) iArray[i] = indices.get(i);

        mesh.updateMesh(vArray, iArray);
    }

    private void addFace(List<Float> v, List<Integer> idx, int x, int y, int z, int face, int tex) {
        int startIndex = v.size() / 7;

        float[][] pos = switch (face) {
            case 0 -> new float[][]{{0, 1, 1}, {1, 1, 1}, {1, 1, 0}, {0, 1, 0}}; // top
            case 1 -> new float[][]{{0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, 0, 1}}; // bottom
            case 2 -> new float[][]{{0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}}; // front
            case 3 -> new float[][]{{1, 0, 0}, {0, 0, 0}, {0, 1, 0}, {1, 1, 0}}; // back
            case 4 -> new float[][]{{0, 0, 0}, {0, 0, 1}, {0, 1, 1}, {0, 1, 0}}; // left
            case 5 -> new float[][]{{1, 0, 1}, {1, 0, 0}, {1, 1, 0}, {1, 1, 1}}; // right
            default -> throw new IllegalArgumentException();
        };

        float[][] uvs = {
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}
        };

        float[] cornerAOs = calculateFaceAO(x & 15, y, z & 15, face);

        for (int i = 0; i < 4; i++) {
            int uvIndex = i % 4;

            v.add(x + pos[i][0]);
            v.add(y + pos[i][1]);
            v.add(z + pos[i][2]);

            v.add(uvs[uvIndex][0]);
            v.add(uvs[uvIndex][1]);
            v.add((float) tex);

            v.add(cornerAOs[i]);
        }

        float aoBottomLeft = cornerAOs[0];
        float aoBottomRight = cornerAOs[1];
        float aoTopRight = cornerAOs[2];
        float aoTopLeft = cornerAOs[3];

        if (aoBottomLeft + aoTopRight < aoBottomRight + aoTopLeft) {
            idx.add(startIndex);
            idx.add(startIndex + 1);
            idx.add(startIndex + 3);

            idx.add(startIndex + 1);
            idx.add(startIndex + 2);
            idx.add(startIndex + 3);
        } else {
            idx.add(startIndex);
            idx.add(startIndex + 1);
            idx.add(startIndex + 2);

            idx.add(startIndex + 2);
            idx.add(startIndex + 3);
            idx.add(startIndex);
        }
    }

    private float getAOValue(boolean side1, boolean side2, boolean corner) {
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

    private float[] calculateFaceAO(float x, float y, float z, int face) {
        float[] aos = new float[4];

        int bx = (int) Math.floor(x);
        int by = (int) Math.floor(y);
        int bz = (int) Math.floor(z);

        switch (face) {
            case 0 -> { // TOP Face (y + 1)
                boolean s1 = !isTransparent(bx - 1, by + 1, bz);
                boolean s2 = !isTransparent(bx + 1, by + 1, bz);
                boolean s3 = !isTransparent(bx, by + 1, bz - 1);
                boolean s4 = !isTransparent(bx, by + 1, bz + 1);

                aos[0] = getAOValue(s1, s4, !isTransparent(bx - 1, by + 1, bz + 1)); // BL
                aos[1] = getAOValue(s2, s4, !isTransparent(bx + 1, by + 1, bz + 1)); // BR
                aos[2] = getAOValue(s2, s3, !isTransparent(bx + 1, by + 1, bz - 1)); // TR
                aos[3] = getAOValue(s1, s3, !isTransparent(bx - 1, by + 1, bz - 1)); // TL
            }
            case 1 -> { // BOTTOM Face (y - 1)
                boolean s1 = !isTransparent(bx - 1, by - 1, bz);
                boolean s2 = !isTransparent(bx + 1, by - 1, bz);
                boolean s3 = !isTransparent(bx, by - 1, bz - 1);
                boolean s4 = !isTransparent(bx, by - 1, bz + 1);

                aos[0] = getAOValue(s1, s3, !isTransparent(bx - 1, by - 1, bz - 1));
                aos[1] = getAOValue(s2, s3, !isTransparent(bx + 1, by - 1, bz - 1));
                aos[2] = getAOValue(s2, s4, !isTransparent(bx + 1, by - 1, bz + 1));
                aos[3] = getAOValue(s1, s4, !isTransparent(bx - 1, by - 1, bz + 1));
            }
            case 2 -> { // FRONT Face (z + 1)
                boolean s1 = !isTransparent(bx - 1, by, bz + 1);
                boolean s2 = !isTransparent(bx + 1, by, bz + 1);
                boolean s3 = !isTransparent(bx, by - 1, bz + 1);
                boolean s4 = !isTransparent(bx, by + 1, bz + 1);

                aos[0] = getAOValue(s1, s3, !isTransparent(bx - 1, by - 1, bz + 1));
                aos[1] = getAOValue(s2, s3, !isTransparent(bx + 1, by - 1, bz + 1));
                aos[2] = getAOValue(s2, s4, !isTransparent(bx + 1, by + 1, bz + 1));
                aos[3] = getAOValue(s1, s4, !isTransparent(bx - 1, by + 1, bz + 1));
            }
            case 3 -> { // BACK Face (z - 1)
                boolean s1 = !isTransparent(bx - 1, by, bz - 1);
                boolean s2 = !isTransparent(bx + 1, by, bz - 1);
                boolean s3 = !isTransparent(bx, by - 1, bz - 1);
                boolean s4 = !isTransparent(bx, by + 1, bz - 1);

                aos[0] = getAOValue(s2, s3, !isTransparent(bx + 1, by - 1, bz - 1));
                aos[1] = getAOValue(s1, s3, !isTransparent(bx - 1, by - 1, bz - 1));
                aos[2] = getAOValue(s1, s4, !isTransparent(bx - 1, by + 1, bz - 1));
                aos[3] = getAOValue(s2, s4, !isTransparent(bx + 1, by + 1, bz - 1));
            }
            case 4 -> { // LEFT Face (x - 1)
                boolean s1 = !isTransparent(bx - 1, by, bz - 1);
                boolean s2 = !isTransparent(bx - 1, by, bz + 1);
                boolean s3 = !isTransparent(bx - 1, by - 1, bz);
                boolean s4 = !isTransparent(bx - 1, by + 1, bz);

                aos[0] = getAOValue(s1, s3, !isTransparent(bx - 1, by - 1, bz - 1));
                aos[1] = getAOValue(s2, s3, !isTransparent(bx - 1, by - 1, bz + 1));
                aos[2] = getAOValue(s2, s4, !isTransparent(bx - 1, by + 1, bz + 1));
                aos[3] = getAOValue(s1, s4, !isTransparent(bx - 1, by + 1, bz - 1));
            }
            case 5 -> { // RIGHT Face (x + 1)
                boolean s1 = !isTransparent(bx + 1, by, bz - 1);
                boolean s2 = !isTransparent(bx + 1, by, bz + 1);
                boolean s3 = !isTransparent(bx + 1, by - 1, bz);
                boolean s4 = !isTransparent(bx + 1, by + 1, bz);

                aos[0] = getAOValue(s2, s3, !isTransparent(bx + 1, by - 1, bz + 1));
                aos[1] = getAOValue(s1, s3, !isTransparent(bx + 1, by - 1, bz - 1));
                aos[2] = getAOValue(s1, s4, !isTransparent(bx + 1, by + 1, bz - 1));
                aos[3] = getAOValue(s2, s4, !isTransparent(bx + 1, by + 1, bz + 1));
            }
        }
        return aos;
    }

    private int getIndex(int x, int y, int z) {
        return (z * SIZE_X * SIZE_Y) + (y * SIZE_X) + x;
    }

    public short getBlock(int x, int y, int z) {
        return blocks[getIndex(x, y, z)];
    }

    public void setBlock(int x, int y, int z, short block) {
        blocks[getIndex(x, y, z)] = block;
    }

    public boolean isTransparent(int x, int y, int z) {
        if (y < 0 || y >= SIZE_Y) return true;
        if (x < 0 || x >= SIZE_X || z < 0 || z >= SIZE_X) return getBlockInterchunk(x, y, z) == BlockTypes.AIR;
        return blocks[getIndex(x, y, z)] == BlockTypes.AIR;
    }

    public short getBlockInterchunk(int x, int y, int z) {
        Chunk chunk = NanoCraft.WORLD.getChunk(worldPos.offset(Math.floorDiv(x, SIZE_X), Math.floorDiv(z, SIZE_Z)));
        if (chunk == null) return BlockTypes.NULL;
        return chunk.getBlock(Math.floorMod(x, SIZE_X), y, Math.floorMod(z, SIZE_Z));
    }

    public short getBlockChunkSafe(int x, int y, int z) {
        if (x < 0 || x >= SIZE_X || z < 0 || z >= SIZE_X) return BlockTypes.NULL;
        return blocks[getIndex(x, y, z)];
    }

    public void setBlockChunkSafe(int x, int y, int z, short block) {
        if (x < 0 || x >= SIZE_X || z < 0 || z >= SIZE_X) return;
        blocks[getIndex(x, y, z)] = block;
    }

    public void render() {
        if (mesh != null) mesh.render();
    }

    public void cleanup() {
        if (mesh != null) mesh.cleanup();
    }
}