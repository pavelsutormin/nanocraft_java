package org.sutormin.nanocraft;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.sutormin.nanocraft.block.BlockTypes;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.render.Shader;
import org.sutormin.nanocraft.render.shaders.Frag;
import org.sutormin.nanocraft.render.shaders.Vert;
import org.sutormin.nanocraft.resources.Textures;
import org.sutormin.nanocraft.world.ChunkPos;
import org.sutormin.nanocraft.world.World;

import java.nio.IntBuffer;
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

    public static Shader SHADER;
    public static World WORLD;
    public static final Camera CAMERA = new Camera();

    private double lastMouseX = width / 2.0;
    private double lastMouseY = height / 2.0;
    private boolean firstMouse = true;

    //private ChunkPos lastCameraChunkPos = null;

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

        GLFW.glfwSetWindowSizeCallback(window, (windowHandle, width, height) -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);

                // Fetch the true pixel dimensions of the framebuffer
                GLFW.glfwGetFramebufferSize(windowHandle, pWidth, pHeight);

                // Update the viewport
                GL11.glViewport(0, 0, pWidth.get(0), pHeight.get(0));
            }
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0.623f, 0.734f, 0.785f, 1.0f);


        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Fetch the true pixel dimensions of the framebuffer
            GLFW.glfwGetFramebufferSize(window, pWidth, pHeight);

            // Update the viewport
            GL11.glViewport(0, 0, pWidth.get(0), pHeight.get(0));
        }

        BlockTypes.define();

        Textures.loadTextures();


        SHADER = new Shader(Vert.VERTEX_SHADER, Frag.FRAGMENT_SHADER);
        SHADER.createUniform("uProjection");
        SHADER.createUniform("uView");
        SHADER.createUniform("uChunkOffset");

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

            WORLD.tick();

            //ChunkLoader.poll();

            /*if (now - lastLog > 1_000_000_000L) {
                System.out.printf("camera chunk %s | loaded %d | queued %d%n",
                        CAMERA.getChunkPos(), WORLD.chunkCount(), ChunkLoader.pendingCount());
                lastLog = now;
            }*/

            processInput(deltaTime);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Textures.BLOCK.bind();
            SHADER.bind();
            SHADER.setUniform("uProjection", projection);
            SHADER.setUniform("uView", CAMERA.getViewMatrix());

            WORLD.renderChunks();

            Textures.BLOCK.unbind();
            SHADER.unbind();

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
        float speed = 1f;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) forwardBack += 1.0f;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) forwardBack -= 1.0f;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) rightLeft += 1.0f;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) rightLeft -= 1.0f;
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) upDown += 1.0f;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) upDown -= 1.0f;

        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            speed = 1.5f;
        }

        CAMERA.updatePosition(forwardBack, rightLeft, upDown, speed, dt);
        CAMERA.tick();
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
        SHADER.cleanup();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}