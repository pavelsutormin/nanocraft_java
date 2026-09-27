package org.sutormin.nanocraft.world.chunk;

import org.sutormin.nanocraft.data.Registries;
import org.sutormin.nanocraft.data.quickaccess.QuickAccessBlocks;
import org.sutormin.nanocraft.networking.packets.play.world.chunk.S2CChunkData;
import org.sutormin.nanocraft.world.BlockStateMapper;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Turns a decoded ChunkData packet into the flat block array Chunk renders.
 *
 * Decoding happens on the Netty thread; nothing here touches OpenGL. Finished
 * arrays go on a queue that the main loop drains, because Chunk's constructor
 * allocates a Mesh and buildMesh uploads buffers, both of which require the GL
 * context thread.
 *
 * Chunk batches:
 *   RECEIVING -> chunks decoded from the current server batch
 *   READY     -> chunks whose batch has been completed and may be rendered
 *
 * Vertical alignment: section s covers array rows s * 16 to s * 16 + 15, from the
 * dimension's lowest y up, so array y is world y minus Dimension.minY() (the
 * overworld's -64..319 fills all 384 rows, the Nether and End use the lowest 256).
 *
 * Generations: changing dimension calls clear(), which starts a new generation.
 * Everything queued carries the generation it was received in, and World drops
 * the old dimension's chunks once it sees a newer one.
 */
public final class ChunkLoader {

    public static final int SECTION_SIZE = 16;

    private static final Queue<Pending> RECEIVING = new ConcurrentLinkedQueue<>();
    private static final Queue<Pending> READY = new ConcurrentLinkedQueue<>();
    private static final Queue<ChunkPos> UNLOADS = new ConcurrentLinkedQueue<>();
    private static final Queue<BlockChange> BLOCK_CHANGES = new ConcurrentLinkedQueue<>();
    private static volatile int generation;

    public record Pending(ChunkPos pos, char[] blocks, char[] biomes, int generation) {
    }

    /** y is the chunk array row (world y - Dimension.minY()). */
    public record BlockChange(int x, int y, int z, char block, int generation) {
    }

    private ChunkLoader() {
    }

    /**
     * Call from ChunkData.read, on the Netty thread.
     *
     * Adds a decoded chunk to the current incoming batch.
     */
    public static void submit(S2CChunkData data) {
        RECEIVING.add(new Pending(
            new ChunkPos(data.chunkX, data.chunkZ),
            toBlocks(data),
            toBiomes(data),
            generation
        ));
    }

    public static void submitUnload(ChunkPos pos) {
        UNLOADS.add(pos);
    }

    public static void submitBlockChange(int x, int y, int z, char block) {
        BLOCK_CHANGES.add(new BlockChange(x, y, z, block, generation));
    }

    /**
     * Forgets everything queued and starts a new generation, when the player changes dimension.
     * Network thread, in order with the packets around it.
     */
    public static void clear() {
        generation++;
        RECEIVING.clear();
        READY.clear();
        UNLOADS.clear();
        BLOCK_CHANGES.clear();
    }

    /** The current generation; World clears its chunks when this moves past its own. */
    public static int generation() {
        return generation;
    }

    /**
     * Called when the server's chunk batch has finished.
     *
     * Verifies that the number of chunks received matches the server's
     * reported batch size, then moves the entire batch to READY.
     */
    public static boolean chunkBatchFinished(int chunks) {
        int received = RECEIVING.size();

        if (received != chunks) {
            System.err.printf(
                "[WARN] Chunk batch size mismatch: expected %d chunks, received %d%n",
                chunks,
                received
            );
            return false;
        }

        for (int i = 0; i < chunks; i++) {
            Pending pending = RECEIVING.poll();

            if (pending == null) {
                // Should be impossible because we checked the size above.
                System.err.printf(
                    "[WARN] Chunk batch unexpectedly emptied after %d/%d chunks%n",
                    i,
                    chunks
                );
                RECEIVING.clear();
                return false;
            }

            READY.add(pending);
        }
        return true;
    }

    /**
     * Call from the main loop, on the GL thread.
     *
     * Returns the next chunk whose batch has been completed.
     * Returns null when empty.
     */
    public static Pending poll() {
        return READY.poll();
    }

    public static ChunkPos pollUnload() {
        return UNLOADS.poll();
    }


    public static BlockChange pollBlockChange() {
        return BLOCK_CHANGES.poll();
    }

    /** Number of chunks waiting for the current batch to finish. */
    public static int receivingCount() {
        return RECEIVING.size();
    }

    /** Number of completed chunks waiting for the GL thread. */
    public static int readyCount() {
        return READY.size();
    }

    /** Number of chunks queued for unload. */
    public static int unloadCount() {
        return UNLOADS.size();
    }


    public static int blockChangeCount() {
        return BLOCK_CHANGES.size();
    }

    public static char[] toBlocks(S2CChunkData data) {
        char[] blocks = new char[Chunk.SIZE_X * Chunk.SIZE_Y * Chunk.SIZE_Z];

        int sectionCount = Math.min(
            data.sections.length,
            Chunk.SIZE_Y / SECTION_SIZE
        );

        for (int s = 0; s < sectionCount; s++) {
            writeSection(
                blocks,
                data.sections[s].blockStates,
                s * SECTION_SIZE
            );
        }

        return blocks;
    }

    /** Biome ids per 4x4x4 cell, laid out like {@link Chunk#getBiome}: (cellY * 4 + cellZ) * 4 + cellX. */
    public static char[] toBiomes(S2CChunkData data) {
        char[] biomes = new char[Chunk.BIOME_CELLS_Y * 16];
        int sectionCount = Math.min(data.sections.length, Chunk.SIZE_Y / SECTION_SIZE);
        for (int s = 0; s < sectionCount; s++) {
            S2CChunkData.PalettedContainer container = data.sections[s].biomes;
            if (container == null) continue;
            for (int i = 0; i < 64; i++) { // vanilla order: (y << 4) | (z << 2) | x
                biomes[s * 64 + i] = (char) container.get(i);
            }
        }
        return biomes;
    }

    private static void writeSection(
        char[] blocks,
        S2CChunkData.PalettedContainer states,
        int baseY
    ) {
        // Single valued section, overwhelmingly the common case: whole
        // 16x16x16 is one block, usually air or stone.
        if (states.bitsPerEntry == 0) {
            char type = BlockStateMapper.map(states.palette[0]);

            if (type == Registries.BLOCK.get("air").getId()) {
                return;
            }

            for (int y = 0; y < SECTION_SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE_Z; z++) {
                    for (int x = 0; x < Chunk.SIZE_X; x++) {
                        blocks[index(x, baseY + y, z)] = type;
                    }
                }
            }

            return;
        }

        // Indirect section: translate the palette once, then index into it.
        char[] mapped = null;

        if (states.palette != null) {
            mapped = new char[states.palette.length];

            for (int i = 0; i < mapped.length; i++) {
                mapped[i] = BlockStateMapper.map(states.palette[i]);
            }
        }

        for (int y = 0; y < SECTION_SIZE; y++) {
            for (int z = 0; z < Chunk.SIZE_Z; z++) {
                for (int x = 0; x < Chunk.SIZE_X; x++) {
                    // Vanilla packs x fastest, then z, then y.
                    int raw = states.rawGet((y << 8) | (z << 4) | x);

                    char type;

                    if (mapped == null) {
                        // Global palette: raw is the state id itself.
                        type = BlockStateMapper.map(raw);
                    } else if (raw < mapped.length) {
                        type = mapped[raw];
                    } else {
                        // Out of range means the section was misparsed.
                        type = QuickAccessBlocks.NULL;
                    }

                    if (type != QuickAccessBlocks.AIR) {
                        blocks[index(x, baseY + y, z)] = type;
                    }
                }
            }
        }
    }

    /** Must stay identical to Chunk.getIndex. */
    private static int index(int x, int y, int z) {
        return (z * Chunk.SIZE_X * Chunk.SIZE_Y) + (y * Chunk.SIZE_X) + x;
    }
}