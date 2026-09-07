package org.sutormin.nanocraft;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.sutormin.nanocraft.block.BlockTypes;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.render.Shader;
import org.sutormin.nanocraft.resources.Texture;
import org.sutormin.nanocraft.resources.Textures;
import org.sutormin.nanocraft.world.ChunkLoader;
import org.sutormin.nanocraft.world.ChunkPos;
import org.sutormin.nanocraft.world.World;

import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class NanoCraft {
    private long window;
    private final int width = 1024;
    private final int height = 576;

    private Texture texture;
    private Shader shader;
    public static World WORLD;
    public static final Camera CAMERA = new Camera();

    private double lastMouseX = width / 2.0;
    private double lastMouseY = height / 2.0;
    private boolean firstMouse = true;

    private ChunkPos lastCameraChunkPos = null;

    long lastLog;

    private static final String VERTEX_SHADER = """
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

    private static final String FRAGMENT_SHADER = """
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


    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new RuntimeException("[ERROR] failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_X11);

        window = glfwCreateWindow(width, height, "NanoCraft", NULL, NULL);
        if (window == NULL) throw new RuntimeException("[ERROR] failed to create GLFW window");

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            if (firstMouse) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                firstMouse = false;
            }
            float xOffset = (float) (xpos - lastMouseX);
            float yOffset = (float) (ypos - lastMouseY);
            lastMouseX = xpos;
            lastMouseY = ypos;

            CAMERA.processMouseInput(xOffset, yOffset);
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0.623f, 0.734f, 0.785f, 1.0f);

        int[] framebufferWidth = new int[1];
        int[] framebufferHeight = new int[1];
        
        glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);

        glViewport(0, 0, framebufferWidth[0], framebufferHeight[0]);

        BlockTypes.define();

        Textures.loadTextures();


        shader = new Shader(VERTEX_SHADER, FRAGMENT_SHADER);
        shader.createUniform("uProjection");
        shader.createUniform("uView");

        WORLD = new World();

        Networking.init();
    }

    private void loop() {
        Matrix4f projection = new Matrix4f().perspective(
                (float) Math.toRadians(60.0f), (float) width / height, 0.1f, 1000.0f
        );

        long lastTime = System.nanoTime();
        while (!glfwWindowShouldClose(window)) {
            long now = System.nanoTime();
            float deltaTime = (now - lastTime) / 1000000000.0f;
            lastTime = now;
            WORLD.drainNetworkChunks(100);
            ChunkPos currentChunkPos = CAMERA.getChunkPos();
            if (!currentChunkPos.equals(lastCameraChunkPos)) {

               // WORLD.loadChunksAndUnloadAllOtherChunks(getChunksInRenderDistance(currentChunkPos, 8));
                lastCameraChunkPos = currentChunkPos;
            }

            if (now - lastLog > 1_000_000_000L) {
                System.out.printf("camera chunk %s | loaded %d | queued %d%n",
                        CAMERA.getChunkPos(), WORLD.chunkCount(), ChunkLoader.pendingCount());
                lastLog = now;
            }

            processInput(deltaTime);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Textures.BLOCK.bind();
            shader.bind();
            shader.setUniform("uProjection", projection);
            shader.setUniform("uView", CAMERA.getViewMatrix());

            WORLD.renderChunks();

            Textures.BLOCK.unbind();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void processInput(float dt) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }

        float forwardBack = 0.0f;
        float rightLeft = 0.0f;
        float upDown = 0.0f;
        float speed = 10.0f;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) forwardBack += 1.0f;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) forwardBack -= 1.0f;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) rightLeft += 1.0f;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) rightLeft -= 1.0f;
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) upDown += 1.0f;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) upDown -= 1.0f;

        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            speed = 30.0f;
        }

        CAMERA.updatePosition(forwardBack, rightLeft, upDown, speed, dt);
    }

    private List<ChunkPos> getChunksInRenderDistance(ChunkPos center, int renderDistance) {
        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                chunks.add(new ChunkPos(center.x() + x, center.z() + z));
            }
        }
        return chunks;
    }

    private void cleanup() {
        Textures.cleanup();
        WORLD.cleanup();
        shader.cleanup();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}