package org.sutormin.nanocraft.world.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL30.*;
import static org.sutormin.nanocraft.NanoCraft.SHADER;

public class Mesh {
    public static final int CHARS_PER_VERTEX = 7;
    private static final int STRIDE = CHARS_PER_VERTEX * Character.BYTES; // 14 bytes

    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private int indexCount;
    public boolean generated = false;
    private int chunkX;
    private int chunkZ;

    // back-to-front sorting, for translucent meshes only (see sortFor)
    private int[] indices;    // the index buffer as built: each face's triangles together
    private int[] faceStart;  // where each face's indices start in indices, plus the end
    private float[] centers;  // x, y, z of each face's center, chunk-local blocks
    private int[] sorted;     // indices in the current draw order
    private long[] sortKeys;
    private float sortedX = Float.NaN, sortedY, sortedZ;

    public Mesh() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);

        // attribute 0: gx, gy, gz, uv (chars 0-3)
        glVertexAttribIPointer(0, 4, GL_UNSIGNED_SHORT, STRIDE, 0L);
        glEnableVertexAttribArray(0);

        // attribute 1: ao, texture layer, flags (chars 4-6, byte offset 8)
        glVertexAttribIPointer(1, 3, GL_UNSIGNED_SHORT, STRIDE, 4L * Character.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Vertex and index data copied into native buffers, ready for {@link #upload}.
     * Preparing needs no GL context, so it can run on a worker thread.
     */
    public record Prepared(ByteBuffer vertices, IntBuffer indices, int indexCount,
                           int[] sortIndices, int[] faceStart, float[] centers) {
        Prepared(ByteBuffer vertices, IntBuffer indices, int indexCount) {
            this(vertices, indices, indexCount, null, null, null);
        }

        /** Frees the native buffers; {@link #upload} does this for you. */
        public void free() {
            if (vertices != null) MemoryUtil.memFree(vertices);
            if (indices != null) MemoryUtil.memFree(indices);
        }
    }

    /**
     * @param vertices  interleaved vertex data, 7 chars per vertex
     * @param charCount number of chars actually written (vertexCount * 7)
     */
    public static Prepared prepare(char[] vertices, int charCount, int[] indices, int iCount) {
        if (charCount % CHARS_PER_VERTEX != 0) {
            throw new IllegalStateException("charCount " + charCount + " is not a multiple of " + CHARS_PER_VERTEX);
        }
        int verts = charCount / CHARS_PER_VERTEX;
        for (int k = 0; k < iCount; k++) {
            if (indices[k] < 0 || indices[k] >= verts) {
                throw new IllegalStateException("index " + indices[k] + " at " + k + " but only " + verts + " vertices");
            }
        }

        if (charCount == 0 || iCount == 0) {
            return new Prepared(null, null, 0);
        }

        // Vertex buffer: raw bytes, filled through a native-order char view
        ByteBuffer vBuffer = MemoryUtil.memAlloc(charCount * Character.BYTES);
        vBuffer.order(ByteOrder.nativeOrder());
        vBuffer.asCharBuffer().put(vertices, 0, charCount); // doesn't move vBuffer's position

        IntBuffer iBuffer = MemoryUtil.memAllocInt(iCount);
        iBuffer.put(indices, 0, iCount).flip();

        return new Prepared(vBuffer, iBuffer, iCount);
    }

    /**
     * Like {@link #prepare}, but also keeps what {@link #sortFor} needs to draw the faces back to
     * front. For blended (translucent) meshes: their faces don't write depth, so the draw order
     * decides which one ends up in front. Whole faces are sorted by their center, like vanilla sorts
     * quads: sorting single triangles can put half of a face in front of a neighbor and half behind.
     */
    public static Prepared prepareSorted(char[] vertices, int charCount, int[] indices, int iCount) {
        Prepared p = prepare(vertices, charCount, indices, iCount);
        if (p.indexCount() == 0) return p;

        // A face's triangles only use that face's vertices, which come right after the previous
        // face's, so a triangle starts a new face when all its vertices are past the current one's.
        int[] starts = new int[iCount / 3 + 1];
        int[] firstVertex = new int[iCount / 3 + 1];
        int faces = 0, lastVertex = -1;
        for (int k = 0; k < iCount; k += 3) {
            int lo = Math.min(indices[k], Math.min(indices[k + 1], indices[k + 2]));
            int hi = Math.max(indices[k], Math.max(indices[k + 1], indices[k + 2]));
            if (lo > lastVertex) {
                starts[faces] = k;
                firstVertex[faces++] = lo;
            }
            lastVertex = Math.max(lastVertex, hi);
        }
        starts[faces] = iCount;
        firstVertex[faces] = charCount / CHARS_PER_VERTEX;

        float[] centers = new float[faces * 3];
        for (int f = 0; f < faces; f++) {
            int n = firstVertex[f + 1] - firstVertex[f];
            float x = 0, y = 0, z = 0;
            for (int v = firstVertex[f] * CHARS_PER_VERTEX; v < firstVertex[f + 1] * CHARS_PER_VERTEX; v += CHARS_PER_VERTEX) {
                x += vertices[v];
                y += vertices[v + 1];
                z += vertices[v + 2];
            }
            float scale = 1f / (n * 128f); // positions are 1/128 block
            centers[f * 3] = x * scale;
            centers[f * 3 + 1] = y * scale;
            centers[f * 3 + 2] = z * scale;
        }
        return new Prepared(p.vertices(), p.indices(), iCount, Arrays.copyOf(indices, iCount),
                Arrays.copyOf(starts, faces + 1), centers);
    }

    /** Uploads prepared data and frees its buffers. GL thread only. */
    public void upload(Prepared data) {
        generated = true;
        indexCount = data.indexCount();
        indices = data.sortIndices();
        faceStart = data.faceStart();
        centers = data.centers();
        sorted = indices == null ? null : new int[indexCount];
        sortKeys = indices == null ? null : new long[faceStart.length - 1];
        sortedX = Float.NaN; // sort before the first draw
        if (indexCount == 0) return;

        try {
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, data.vertices(), GL_DYNAMIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, data.indices(), GL_DYNAMIC_DRAW);
        } finally {
            data.free();
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Reorders the faces farthest first from the camera (render coordinates), like vanilla does
     * for translucent sections. Only meshes from {@link #prepareSorted}; skipped until the camera has
     * moved {@code minMove} blocks since the last sort. GL thread only.
     */
    public void sortFor(float camX, float camY, float camZ, float minMove) {
        if (indices == null || indexCount == 0) return;
        float mx = camX - sortedX, my = camY - sortedY, mz = camZ - sortedZ;
        if (mx * mx + my * my + mz * mz < minMove * minMove) return; // false for NaN: always sorts
        sortedX = camX;
        sortedY = camY;
        sortedZ = camZ;

        float lx = camX - chunkX * 16f, lz = camZ - chunkZ * 16f;
        int faces = sortKeys.length;
        for (int f = 0; f < faces; f++) {
            float dx = centers[f * 3] - lx, dy = centers[f * 3 + 1] - camY, dz = centers[f * 3 + 2] - lz;
            // distance bits of a positive float sort like the float; negated for farthest first
            sortKeys[f] = ((long) ~Float.floatToRawIntBits(dx * dx + dy * dy + dz * dz) << 32) | f;
        }
        Arrays.sort(sortKeys);
        int k = 0;
        for (long key : sortKeys) {
            int f = (int) key;
            int n = faceStart[f + 1] - faceStart[f];
            System.arraycopy(indices, faceStart[f], sorted, k, n);
            k += n;
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, sorted);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void setPos(int x, int z) {
        chunkX = x;
        chunkZ = z;
    }

    public void render() {
        if (indexCount == 0) return;
        glBindVertexArray(vaoId);
        SHADER.setUniform("uChunkOffset", chunkX * 16.0f, chunkZ * 16.0f);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
    }
}