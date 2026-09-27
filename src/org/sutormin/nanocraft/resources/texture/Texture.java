package org.sutormin.nanocraft.resources.texture;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.sutormin.nanocraft.Main;
import org.sutormin.nanocraft.Options;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {
    private int texSize = 16;

    private int tex;

    private List<String> paths = new ArrayList<>();
    // path -> layer; missing files map to the fallback layer so they don't each take a layer
    private final Map<String, Integer> layers = new HashMap<>();
    private int fallback = -1;
    private int missingCount = 0;

    /** Registers the texture used for any path that doesn't exist. Call before addTexture. */
    public int setFallbackTexture(String path) {
        fallback = addTexture(path);
        return fallback;
    }

    public int addTexture(String path){
        Integer layer = layers.get(path);
        if (layer != null) return layer;

        if (fallback >= 0 && Main.class.getClassLoader().getResource(path) == null) {
            missingCount++;
            if (Options.DEBUG_LOG_MISSING_TEXTURES) System.out.println("[Client] Missing texture: " + path);
            layers.put(path, fallback);
            return fallback;
        }

        paths.add(path);
        layers.put(path, paths.size()-1);
        return paths.size()-1;
    }

    public void loadTextures() {
        int maxLayers = glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS);
        System.out.println("Texture array: " + paths.size() + " layers (max " + maxLayers + "), "
                + missingCount + " missing textures use the fallback");
        if (paths.size() > maxLayers) {
            throw new RuntimeException("Too many textures for one texture array: "
                    + paths.size() + " > " + maxLayers);
        }

        // No flip: block shape UVs put v=0 at the top of the image (Minecraft convention)
        stbi_set_flip_vertically_on_load(false);
        this.tex = glGenTextures();

        glBindTexture(GL_TEXTURE_2D_ARRAY, this.tex);

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_R, GL_REPEAT);

        int maxLevel = Integer.numberOfTrailingZeros(texSize); // 16x16 -> levels 0..4
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, maxLevel);

        for (int level = 0; level <= maxLevel; level++) {
            glTexImage3D(
                    GL_TEXTURE_2D_ARRAY,
                    level,
                    GL_RGBA,
                    texSize >> level,
                    texSize >> level,
                    paths.size(),
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    (ByteBuffer) null
            );
        }

        for (int i = 0; i < paths.size(); i++) {
            ByteBuffer image = null;
            ByteBuffer buf = null;
            ByteBuffer tile = null;

            try (MemoryStack stack = stackPush()) {
                InputStream is = Main.class
                        .getClassLoader()
                        .getResourceAsStream(paths.get(i));

                if (is == null) {
                    throw new RuntimeException("Texture not found: " + paths.get(i));
                }

                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                byte[] bytes = is.readAllBytes();

                buf = MemoryUtil.memAlloc(bytes.length);
                buf.put(bytes).flip();

                image = stbi_load_from_memory(
                        buf,
                        width,
                        height,
                        channels,
                        4
                );

                if (image == null) {
                    throw new RuntimeException(
                            "STB failed to load " + paths.get(i) +
                                    ": " + stbi_failure_reason()
                    );
                }

                int w = width.get(0);
                int h = height.get(0);

                if (w < texSize || h < texSize) {
                    throw new RuntimeException("Texture is " + w + "x" + h + ", needs at least " + texSize + "x" + texSize);
                }

                // Only the top-left texSize x texSize is used, e.g. the first frame of an animated strip
                tile = MemoryUtil.memAlloc(texSize * texSize * 4);
                for (int row = 0; row < texSize; row++) {
                    for (int col = 0; col < texSize * 4; col++) {
                        tile.put(row * texSize * 4 + col, image.get(row * w * 4 + col));
                    }
                }

                glTexSubImage3D(
                        GL_TEXTURE_2D_ARRAY,
                        0,
                        0,
                        0,
                        i,
                        texSize,
                        texSize,
                        1,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        tile
                );
                uploadMipmaps(tile, texSize, i, maxLevel);

            } catch (Exception e) {
                System.err.println(
                        "Error loading texture " + paths.get(i) + ":"
                );
                e.printStackTrace();

            } finally {
                if (image != null) {
                    stbi_image_free(image);
                }

                if (buf != null) {
                    MemoryUtil.memFree(buf);
                }

                if (tile != null) {
                    MemoryUtil.memFree(tile);
                }
            }
        }

        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    /**
     * Builds and uploads mip levels 1..maxLevel for one layer. Like glGenerateMipmap, each
     * pixel averages a 2x2 block, but colors are weighted by alpha so the (often black) color of
     * fully transparent pixels doesn't bleed dark fringes into leaves and glass at a distance.
     */
    private void uploadMipmaps(ByteBuffer image, int size, int layer, int maxLevel) {
        int[] src = new int[size * size * 4];
        for (int k = 0; k < src.length; k++) src[k] = image.get(k) & 0xFF;

        for (int level = 1; level <= maxLevel; level++) {
            int half = size / 2;
            int[] dst = new int[half * half * 4];
            for (int y = 0; y < half; y++) {
                for (int x = 0; x < half; x++) {
                    int r = 0, g = 0, b = 0, a = 0, rawR = 0, rawG = 0, rawB = 0;
                    for (int dy = 0; dy < 2; dy++) {
                        for (int dx = 0; dx < 2; dx++) {
                            int p = ((y * 2 + dy) * size + (x * 2 + dx)) * 4;
                            int pa = src[p + 3];
                            r += src[p] * pa; g += src[p + 1] * pa; b += src[p + 2] * pa; a += pa;
                            rawR += src[p]; rawG += src[p + 1]; rawB += src[p + 2];
                        }
                    }
                    int q = (y * half + x) * 4;
                    if (a > 0) {
                        dst[q] = r / a; dst[q + 1] = g / a; dst[q + 2] = b / a;
                    } else { // fully transparent: color is never visible, plain average is fine
                        dst[q] = rawR / 4; dst[q + 1] = rawG / 4; dst[q + 2] = rawB / 4;
                    }
                    dst[q + 3] = a / 4;
                }
            }

            ByteBuffer buf = MemoryUtil.memAlloc(dst.length);
            try {
                for (int v : dst) buf.put((byte) v);
                buf.flip();
                glTexSubImage3D(GL_TEXTURE_2D_ARRAY, level, 0, 0, layer, half, half, 1,
                        GL_RGBA, GL_UNSIGNED_BYTE, buf);
            } finally {
                MemoryUtil.memFree(buf);
            }
            src = dst;
            size = half;
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, this.tex);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    public void cleanup() {
        glDeleteTextures(this.tex);
    }
}