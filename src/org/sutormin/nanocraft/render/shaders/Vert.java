package org.sutormin.nanocraft.render.shaders;

public class Vert {
  public static final String VERTEX_SHADER = """
        #version 330 core

        layout (location = 0) in uvec2 aPacked;

        out vec3 TexCoord;
        out vec3 FragPosView;
        out float vAO;

        uniform mat4 uProjection;
        uniform mat4 uView;
        uniform vec2 uChunkOffset;

        void main() {
            uint lo = aPacked.x;
            uint hi = aPacked.y;

            // X: bits 0-11
            uint x = lo & 0xFFFu;

            // Y: bits 12-27
            uint y = (lo >> 12u) & 0xFFFFu;

            // Z: bits 28-39
            uint z = (lo >> 28u) | ((hi & 0xFFu) << 4u);

            // UV: bits 40-41
            uint uv = (hi >> 8u) & 0x3u;

            // Texture array layer: bits 42-57
            uint texture = (hi >> 10u) & 0xFFFFu;

            // AO: bits 58-59
            uint ao = (hi >> 26u) & 0x3u;

            // Coordinates are stored at 1/128 block precision
            vec3 localPos = vec3(x, y, z) / 128.0;

            // Convert chunk-local position to world position
            vec3 worldPos = localPos + vec3(
                  uChunkOffset.x,
                  0.0,
                  uChunkOffset.y
              );

            // World -> view space
            vec4 viewPos = uView * vec4(worldPos, 1.0);

            FragPosView = viewPos.xyz;
            gl_Position = uProjection * viewPos;

            // UV + texture-array layer
            uint uBit = (uv & 1u) ^ ((uv >> 1u) & 1u);
            uint vBit = (uv >> 1u) & 1u;
            
            TexCoord = vec3(float(uBit), float(vBit), float(texture));

            // AO: 0 -> 1.0, 1 -> 0.8, 2 -> 0.6, 3 -> 0.4
            vAO = float(ao) * 0.2 + 0.4;
        }
    """;
}