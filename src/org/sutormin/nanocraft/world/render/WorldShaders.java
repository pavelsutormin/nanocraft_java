package org.sutormin.nanocraft.world.render;

public class WorldShaders {
    public static final String WORLD_VERTEX_SHADER = """
    #version 330 core

    layout (location = 0) in uvec4 aPosUv;  // gx, gy, gz, uv
    layout (location = 1) in uvec3 aAoTex;  // ao, texture layer, flags

    out vec3 TexCoord;
    out vec3 FragPosView;
    out float vAO;
    flat out uint vSolid; // 1: no alpha cutout (e.g. opaque "fast" leaves)
    out vec3 vTint;       // biome color the texture is multiplied by (white = none)

    uniform mat4 uProjection;
    uniform mat4 uView;
    uniform vec2 uChunkOffset;

    void main() {
        // Positions are chunk-local at 1/128 block precision
        vec3 localPos = vec3(aPosUv.xyz) / 128.0;
        vec3 worldPos = localPos + vec3(uChunkOffset.x, 0.0, uChunkOffset.y);

        vec4 viewPos = uView * vec4(worldPos, 1.0);
        FragPosView = viewPos.xyz;
        gl_Position = uProjection * viewPos;

        // uv was packed as u * 129 + v, with u and v in 0..128
        uint u = aPosUv.w / 129u;
        uint v = aPosUv.w % 129u;

        // texture layer was split into two 16-bit halves
        uint layer = aAoTex.y;
        vSolid = aAoTex.z & 1u;
        uint tint = aAoTex.z >> 1u; // 5-bit RGB, 0 = untinted
        vTint = tint == 0u ? vec3(1.0)
                : vec3(float(tint & 31u), float((tint >> 5u) & 31u), float((tint >> 10u) & 31u)) / 31.0;

        TexCoord = vec3(float(u) / 128.0, float(v) / 128.0, float(layer));

        // ao was stored as brightness * 65535
        vAO = float(aAoTex.x) / 65535.0;
    }
""";
    public static final String WORLD_FRAGMENT_SHADER = """
    #version 330 core
    in vec3 TexCoord;
    in vec3 FragPosView;
    in float vAO;
    flat in uint vSolid;
    in vec3 vTint;
    out vec4 FragColor;

    uniform sampler2DArray uTexture;
    // pixels with less alpha than this are discarded: 0.5 for the opaque/cutout pass,
    // near 0 for the blended translucent pass
    uniform float uAlphaCutoff;
    //uniform vec3 uFogColor;
    //uniform float uFogNear;
    //uniform float uFogFar;

    void main() {
        // Alpha test on the full-size texture, not the mipmap: averaged alpha in small mips
        // would make leaves and glass frames thin out and vanish with distance.
        // Solid-texture faces keep every pixel: see-through ones show their stored color.
        if (vSolid == 0u && textureLod(uTexture, TexCoord, 0.0).a < uAlphaCutoff) discard;
        vec4 texColor = texture(uTexture, TexCoord);

        // fog (optional)
        //float dist = length(FragPosView);
        //float fogFactor = clamp((uFogFar - dist) / (uFogFar - uFogNear), 0.0, 1.0);
        //vec3 finalColor = mix(uFogColor, texColor.rgb * vAO, fogFactor);
        //FragColor = vec4(TexCoord.xy, 0.0, 1.0);
        //FragColor = vec4(1.0, 0.0, 1.0, 1.0);
        // solid-texture faces are fully opaque (alpha 0 would let a compositing window manager show through)
        FragColor = vec4(texColor.rgb * vTint * vAO, vSolid == 1u ? 1.0 : texColor.a);
    }
  """;
}