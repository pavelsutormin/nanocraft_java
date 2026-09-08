package org.sutormin.nanocraft.render.shaders;

public class Vert {
  public static final String VERTEX_SHADER = """
        #version 330 core
        layout (location = 0) in vec3 aPos;
        layout (location = 1) in vec3 aTexCoord;
        layout (location = 2) in float aAO;

        out vec3 TexCoord;
        out vec3 FragPosView;
        out float vAO;

        uniform mat4 uProjection;
        uniform mat4 uView;

        void main() {
            vec4 viewPos = uView * vec4(aPos, 1.0);
            FragPosView = viewPos.xyz;
            gl_Position = uProjection * viewPos;
            TexCoord = aTexCoord;
            vAO = aAO;
        }
    """;
}
