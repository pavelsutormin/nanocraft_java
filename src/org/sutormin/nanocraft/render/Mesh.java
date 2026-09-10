package org.sutormin.nanocraft.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.sutormin.nanocraft.NanoCraft.SHADER;

public class Mesh {
    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private int vertexCount;
    public boolean generated = false;
    private int chunkX;
    private int chunkZ;

    public Mesh() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        //updateMesh(vertices,indices);

        int stride = Long.BYTES; // 8 bytes

        glVertexAttribIPointer(
            0,
            2,
            GL_UNSIGNED_INT,
            stride,
            0
        );
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void updateMesh(long[] vertices, int vCount, int[] indices, int iCount){
        generated = true;

        if (vCount == 0 || iCount == 0) {
            this.vertexCount = 0;
            return;
        }

        vertexCount = iCount;

        long vBytes = (long) vCount * Long.BYTES;
        long iBytes = (long) iCount * Integer.BYTES;

// Vertex buffer
        LongBuffer vBuffer = MemoryUtil.memAllocLong(vCount);
        vBuffer.put(vertices, 0, vCount).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vBuffer, GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(vBuffer);

// Index buffer
        IntBuffer iBuffer = MemoryUtil.memAllocInt(iCount);
        iBuffer.put(indices, 0, iCount).flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(iBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void setPos(int x, int z){
        chunkX = x;
        chunkZ = z;
    }

    public void render() {
        glBindVertexArray(vaoId);
        SHADER.setUniform("uChunkOffset", chunkX * 16.0f, chunkZ * 16.0f);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
    }
}