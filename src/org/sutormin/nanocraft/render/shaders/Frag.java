package org.sutormin.nanocraft.render.shaders;

public class Frag {
  public static final String FRAGMENT_SHADER = """
    #version 330 core
    in vec3 TexCoord;
    in vec3 FragPosView;
    in float vAO;
    out vec4 FragColor;

    uniform sampler2DArray uTexture;
    //uniform vec3 uFogColor;
    //uniform float uFogNear;
    //uniform float uFogFar;

    void main() {
        vec4 texColor = texture(uTexture, TexCoord);
        //if (texColor.a < 0.1) discard; // buggy bc of mipmaps

        // fog (optional)
        //float dist = length(FragPosView);
        //float fogFactor = clamp((uFogFar - dist) / (uFogFar - uFogNear), 0.0, 1.0);
        //vec3 finalColor = mix(uFogColor, texColor.rgb * vAO, fogFactor);
        
        FragColor = vec4(texColor.rgb * vAO, texColor.a);
    }
  """;
}
