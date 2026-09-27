package org.sutormin.nanocraft;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.sutormin.nanocraft.data.Registries;
import org.sutormin.nanocraft.data.definitions.BlockShapeDefinitions;
import org.sutormin.nanocraft.data.quickaccess.QuickAccessBlocks;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.play.player.C2SClientTickEnd;
import org.sutormin.nanocraft.render.Shader;
import org.sutormin.nanocraft.world.Dimension;
import org.sutormin.nanocraft.resources.block.BlockDefinitionParser;
import org.sutormin.nanocraft.resources.block.BlockShapeParser;
import org.sutormin.nanocraft.resources.texture.Texture;
import org.sutormin.nanocraft.resources.texture.Textures;
import org.sutormin.nanocraft.world.BlockStateMapper;
import org.sutormin.nanocraft.world.chunk.ChunkPos;
import org.sutormin.nanocraft.world.World;
import org.sutormin.nanocraft.world.biome.BiomeTint;
import org.sutormin.nanocraft.world.render.WorldShaders;

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
        glDepthFunc(GL_LEQUAL); // overlay faces (grass sides) lie exactly on the face below them
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




        System.out.println("Loading block definitions!");
        BlockDefinitionParser.loadFromIndex();
        System.out.println("Loading block shapes!");
        BlockShapeParser.loadFromIndex();

        System.out.println("Loading registries!");
        Registries.defineAll();
        QuickAccessBlocks.loadFromRegistry();

        System.out.println("Loading biome colors!");
        BiomeTint.load();

        System.out.println("Loading blockstate map!");
        BlockStateMapper.load();

        System.out.println("Loading textures!");
        Textures.loadTextures();

        SHADER = new Shader(WorldShaders.WORLD_VERTEX_SHADER, WorldShaders.WORLD_FRAGMENT_SHADER);
        SHADER.createUniform("uProjection");
        SHADER.createUniform("uView");
        SHADER.createUniform("uChunkOffset");
        SHADER.createUniform("uAlphaCutoff");

        WORLD = new World();

        Networking.init();
    }

    private void loop() {
        Matrix4f projection = new Matrix4f().perspective(
                (float) Math.toRadians(60.0f), (float) width / height, 0.1f, 1000.0f
        );

        long lastTime = System.nanoTime();
        float clientTickTime = 0.0f;
        long fpsStart = lastTime;
        int frames = 0;
        while (!glfwWindowShouldClose(window)) {
            long now = System.nanoTime();
            float deltaTime = (now - lastTime) / 1000000000.0f;
            lastTime = now;

            frames++;
            if (now - fpsStart >= 1_000_000_000L) {
                if (Options.DEBUG_SHOW_FPS) {
                    glfwSetWindowTitle(window, String.format("NanoCraft | %d FPS | %d chunks | %.1f %.1f %.1f",
                            frames, WORLD.chunkCount(), CAMERA.getX(), CAMERA.getY() + Dimension.minY(), CAMERA.getZ()));
                }
                frames = 0;
                fpsStart = now;
            }

            // fixed 20 Hz client ticks for the server, like vanilla, however fast frames are drawn
            clientTickTime += deltaTime;
            if (clientTickTime > 0.25f) clientTickTime = CLIENT_TICK; // after a stall, don't send a burst
            while (clientTickTime >= CLIENT_TICK) {
                clientTick();
                clientTickTime -= CLIENT_TICK;
            }

            WORLD.tick();

            processInput(deltaTime);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Textures.BLOCK.bind();
            SHADER.bind();
            SHADER.setUniform("uProjection", projection);
            SHADER.setUniform("uView", CAMERA.getViewMatrix());

            if (Options.DEBUG_WIREFRAME) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            SHADER.setUniform("uAlphaCutoff", 0.5f);
            WORLD.renderChunks();

            // Translucent pass: blended over the opaque scene, depth-tested but not written,
            // so translucent faces don't hide each other. Pushed slightly back in depth so a solid
            // face lying in the same plane (the side of waterlogged stairs) always wins instead of
            // flickering against the water.
            SHADER.setUniform("uAlphaCutoff", 0.004f);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDepthMask(false);
            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, 1.0f);
            WORLD.renderTranslucent();
            glDisable(GL_POLYGON_OFFSET_FILL);
            glDepthMask(true);
            glDisable(GL_BLEND);
            if (Options.DEBUG_WIREFRAME) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

            Textures.BLOCK.unbind();
            SHADER.unbind();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private static final float CLIENT_TICK = 1.0f / 20.0f;

    /**
     * One client tick: at most one position update, then "tick end". Since 26.3 the server disconnects
     * clients that send more than one position between two tick ends.
     */
    private void clientTick() {
        if (Networking.networkPhase != NetworkPhase.PLAY) return;
        CAMERA.sendPositionIfMoved();
        ByteBuf buf = Unpooled.buffer();
        C2SClientTickEnd.make(buf);
        Networking.sendPacket(buf);
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