package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.data.types.BlockShape;
import org.sutormin.nanocraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.sutormin.nanocraft.world.Direction.opposite;

/**
 * Face geometry helpers and the face-pair cull cache used by {@link Chunk}
 * during meshing.
 *
 * <p>Whether one face is fully hidden by another is a pure function of
 * their geometry (vertices + direction) -- it never depends on where in the
 * world the two blocks actually sit. So instead of recomputing footprints
 * for every single block instance in the world, the result is computed
 * once per unique (face, face) pair and cached forever. Faces are shared,
 * immutable objects owned by their registered {@link BlockShape}, so
 * identity is stable and safe to key on. Both caches are static (shared
 * across every {@link Chunk}) and backed by {@link ConcurrentHashMap} since
 * mesh building often happens on worker threads; {@code computeIfAbsent}
 * guarantees each pair/face is computed exactly once even under concurrent
 * access.
 */
public final class FaceCullCache {
    private FaceCullCache() {}

    // ------------------------------------------------------------------
    // Per-direction face basis
    //
    // Each of the 6 axis-aligned directions is described by a unit normal
    // (n) and two unit tangent axes (u, v). This is the only thing
    // hardcoded per-direction; it's used both for culling (which neighbor
    // to test) and, by Chunk, for AO sampling and vertex placement.
    // ------------------------------------------------------------------

    public record FaceBasis(int nx, int ny, int nz, int ux, int uy, int uz, int vx, int vy, int vz) {}

    // indexed by Direction.ordinal(); built once so meshing doesn't allocate a basis per face
    private static final FaceBasis[] BASES = new FaceBasis[Direction.values().length];
    static {
        BASES[Direction.UP.ordinal()]    = new FaceBasis(0, 1, 0,  0, 0, 1,  1, 0, 0);
        BASES[Direction.DOWN.ordinal()]  = new FaceBasis(0, -1, 0, 1, 0, 0,  0, 0, 1);
        // Minecraft convention, matching the .shp models: north is -Z, south is +Z
        BASES[Direction.NORTH.ordinal()] = new FaceBasis(0, 0, -1, 0, 1, 0,  1, 0, 0); // -Z
        BASES[Direction.SOUTH.ordinal()] = new FaceBasis(0, 0, 1,  1, 0, 0,  0, 1, 0); // +Z
        BASES[Direction.WEST.ordinal()]  = new FaceBasis(-1, 0, 0, 0, 0, 1,  0, 1, 0); // -X
        BASES[Direction.EAST.ordinal()]  = new FaceBasis(1, 0, 0,  0, 1, 0,  0, 0, 1); // +X
    }

    public static FaceBasis basisOf(Direction dir) {
        return BASES[dir.ordinal()];
    }



    // ------------------------------------------------------------------
    // Footprints
    // ------------------------------------------------------------------

    /**
     * The 2D footprint a face leaves on its normal axis' boundary plane:
     * where along the normal axis it sits ({@code normalCoord}, 0..128),
     * and its extent along the two tangent axes. Two faces on opposite
     * sides of a block boundary can only touch each other if they both
     * report {@link #touchesOwnBoundary()}; one fully hides the other only
     * if it {@link #covers(Footprint)} it.
     */
    private record Footprint(boolean normalPositive, int normalCoord,
                             int minA, int maxA, int minB, int maxB) {
        boolean touchesOwnBoundary() {
            return normalPositive ? normalCoord >= 128 : normalCoord <= 0;
        }

        boolean covers(Footprint other) {
            return minA <= other.minA && maxA >= other.maxA
                    && minB <= other.minB && maxB >= other.maxB;
        }
    }

    private static Footprint computeFootprint(List<BlockShape.Vertex> verts, BlockShape.Face face, FaceBasis basis) {
        int[] idx = face.vertices();
        int minA = Integer.MAX_VALUE, maxA = Integer.MIN_VALUE;
        int minB = Integer.MAX_VALUE, maxB = Integer.MIN_VALUE;
        int normalCoord = 0;

        for (int i : idx) {
            BlockShape.Vertex v = verts.get(i);
            int a, b;
            // Project onto whichever two axes aren't the face's normal axis.
            if (basis.nx() != 0) { normalCoord = v.x(); a = v.y(); b = v.z(); }
            else if (basis.ny() != 0) { normalCoord = v.y(); a = v.x(); b = v.z(); }
            else { normalCoord = v.z(); a = v.x(); b = v.y(); }

            if (a < minA) minA = a;
            if (a > maxA) maxA = a;
            if (b < minB) minB = b;
            if (b > maxB) maxB = b;
        }

        boolean positive = basis.nx() > 0 || basis.ny() > 0 || basis.nz() > 0;
        return new Footprint(positive, normalCoord, minA, maxA, minB, maxB);
    }

    // ------------------------------------------------------------------
    // Per-shape info
    //
    // Computed once per shape and stored on it, so the mesher can answer
    // the two hottest questions with an array read instead of a map lookup:
    // does face i touch the block boundary, and does this shape completely
    // cover side d (true for every side of a full cube)? A neighbor that
    // fully covers the facing side hides any boundary face, whatever its
    // footprint, so the face-pair cache below is only needed otherwise.
    // ------------------------------------------------------------------

    public record ShapeInfo(boolean[] faceTouchesBoundary, boolean[] fullSide) {
        /** Covers all six sides completely: a full cube. */
        public boolean fullCube() {
            for (boolean side : fullSide) if (!side) return false;
            return true;
        }
    }

    public static ShapeInfo infoOf(BlockShape shape) {
        ShapeInfo info = shape.getCullInfo();
        if (info == null) {
            // benign race: every thread computes the same value
            info = analyze(shape);
            shape.setCullInfo(info);
        }
        return info;
    }

    private static ShapeInfo analyze(BlockShape shape) {
        List<BlockShape.Face> faces = shape.getFaces();
        boolean[] touches = new boolean[faces.size()];
        int directions = Direction.values().length;
        // per side, which 1/16-block cells the shape's faces on that side cover, together: a side made of
        // several faces (the back of stairs: lower step + upper part) counts as covered like one face
        boolean[][] covered = new boolean[directions][COVER_CELLS * COVER_CELLS];

        for (int i = 0; i < faces.size(); i++) {
            BlockShape.Face face = faces.get(i);
            Footprint fp = computeFootprint(shape.getVertices(), face, basisOf(face.dir()));
            touches[i] = fp.touchesOwnBoundary();
            if (!touches[i]) continue;
            boolean[] cells = covered[face.dir().ordinal()];
            for (int a = 0; a < COVER_CELLS; a++) {
                int ca = a * 128 / COVER_CELLS + 64 / COVER_CELLS; // cell center
                if (ca < fp.minA() || ca > fp.maxA()) continue;
                for (int b = 0; b < COVER_CELLS; b++) {
                    int cb = b * 128 / COVER_CELLS + 64 / COVER_CELLS;
                    if (cb >= fp.minB() && cb <= fp.maxB()) cells[a * COVER_CELLS + b] = true;
                }
            }
        }

        boolean[] fullSide = new boolean[directions];
        for (int d = 0; d < directions; d++) {
            fullSide[d] = true;
            for (boolean cell : covered[d]) if (!cell) { fullSide[d] = false; break; }
        }
        return new ShapeInfo(touches, fullSide);
    }

    private static final int COVER_CELLS = 16;

    // ------------------------------------------------------------------
    // Caches
    // ------------------------------------------------------------------

    private record FacePair(BlockShape.Face a, BlockShape.Face b) {}

    private static final Map<FacePair, Boolean> CULL_CACHE = new ConcurrentHashMap<>();

    // Whether a face touches its own block's boundary is also a pure,
    // neighbor-independent fact -- cache it separately so a face that could
    // never be culled (e.g. anything sitting mid-block) short-circuits
    // before Chunk even looks up the neighbor.
    private static final Map<BlockShape.Face, Boolean> BOUNDARY_CACHE = new ConcurrentHashMap<>();

    public static boolean touchesOwnBoundary(BlockShape.Face face, List<BlockShape.Vertex> verts) {
        return BOUNDARY_CACHE.computeIfAbsent(face,
                f -> computeFootprint(verts, f, basisOf(f.dir())).touchesOwnBoundary());
    }

    private static boolean cullsAgainst(BlockShape.Face a, List<BlockShape.Vertex> vertsA,
                                        BlockShape.Face b, List<BlockShape.Vertex> vertsB) {
        return CULL_CACHE.computeIfAbsent(new FacePair(a, b), k -> computeCulls(a, vertsA, b, vertsB));
    }

    private static boolean computeCulls(BlockShape.Face a, List<BlockShape.Vertex> vertsA,
                                        BlockShape.Face b, List<BlockShape.Vertex> vertsB) {
        if (b.dir() != opposite(a.dir())) return false;
        if (!touchesOwnBoundary(a, vertsA)) return false;
        if (!touchesOwnBoundary(b, vertsB)) return false;

        Footprint fa = computeFootprint(vertsA, a, basisOf(a.dir()));
        Footprint fb = computeFootprint(vertsB, b, basisOf(b.dir()));
        return fb.covers(fa);
    }

    /**
     * Checks whether {@code face} (with vertices {@code verts}) is hidden
     * by any of {@code neighborShape}'s faces on the opposite side. This is
     * the main entry point {@link Chunk} calls once it has already
     * confirmed {@code face} touches its own boundary and located a
     * non-null neighbor shape.
     */
    public static boolean occludes(BlockShape.Face face, List<BlockShape.Vertex> verts, BlockShape neighborShape) {
        Direction opposite = opposite(face.dir());
        if (infoOf(neighborShape).fullSide()[opposite.ordinal()]) return true; // caller checked face touches its boundary
        List<BlockShape.Vertex> neighborVerts = neighborShape.getVertices();

        for (BlockShape.Face candidate : neighborShape.getFaces()) {
            if (candidate.dir() != opposite) continue;
            if (cullsAgainst(face, verts, candidate, neighborVerts)) return true;
        }

        return false;
    }

    /**
     * Eagerly fills the cull cache for every (face, opposite-direction face)
     * pair across the given shapes. Purely a warm-up -- the cache fills
     * itself in correctly on demand without this -- but calling it once
     * right after all BlockShapes are registered (world/game load) means
     * the very first meshes built don't pay for any cache misses. Assumes
     * shape geometry doesn't change after registration; call
     * {@link #clear()} first if it does.
     */
    public static void preload(Collection<BlockShape> shapes) {
        record Entry(BlockShape.Face face, List<BlockShape.Vertex> verts) {}

        List<Entry> all = new ArrayList<>();
        for (BlockShape shape : shapes) {
            for (BlockShape.Face f : shape.getFaces()) {
                all.add(new Entry(f, shape.getVertices()));
            }
        }

        for (Entry a : all) {
            for (Entry b : all) {
                if (b.face().dir() == opposite(a.face().dir())) {
                    cullsAgainst(a.face(), a.verts(), b.face(), b.verts());
                }
            }
        }
    }

    public static void clear() {
        CULL_CACHE.clear();
        BOUNDARY_CACHE.clear();
    }
}