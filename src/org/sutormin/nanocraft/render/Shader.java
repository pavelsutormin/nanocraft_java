package org.sutormin.nanocraft.render;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader {
    private final int programId;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public Shader(String vertexSource, String fragmentSource) {
        int vertShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        programId = glCreateProgram();
        glAttachShader(programId, vertShader);
        glAttachShader(programId, fragShader);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("[ERROR] couldn't link shader program: " + glGetProgramInfoLog(programId));
        }

        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
    }

    private int compileShader(int type, String source) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("[ERROR] couldn't compile " + type + " shader: " + glGetShaderInfoLog(shaderId));
        }
        return shaderId;
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void createUniform(String uniformName) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location < 0) {
            throw new RuntimeException("[ERROR] uniform '" + uniformName + "' not found");
        }
        uniforms.put(uniformName, location);
    }

    public void setUniform(String uniformName, Matrix4f matrix) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            glUniformMatrix4fv(uniforms.get(uniformName), false, buffer);
        }
    }

    public void setUniform(String uniformName, float x, float y) {
        glUniform2f(uniforms.get(uniformName), x, y);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}