package org.sutormin.nanocraft.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private int vertexCount;
    public boolean generated = false;

    public Mesh() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        //updateMesh(vertices,indices);

        int stride = 7 * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void updateMesh(float[] vertices, int vCount, int[] indices, int iCount){
        generated = true;
        if (vCount == 0 || iCount == 0) {
            this.vertexCount = 0;
            return;
        }
        vertexCount = iCount;
        long vBytes = (long) vCount * Float.BYTES;
        long iBytes = (long) iCount * Integer.BYTES;

        FloatBuffer vBuffer = MemoryUtil.memAllocFloat(vCount);
        vBuffer.put(vertices, 0, vCount).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vBytes, GL_DYNAMIC_DRAW);
        glBufferData(GL_ARRAY_BUFFER, vBuffer, GL_DYNAMIC_DRAW);
        MemoryUtil.memFree(vBuffer);

        IntBuffer iBuffer = MemoryUtil.memAllocInt(iCount);
        iBuffer.put(indices, 0, iCount).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iBytes, GL_DYNAMIC_DRAW);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL_DYNAMIC_DRAW);
        MemoryUtil.memFree(iBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void render() {
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
    }
}