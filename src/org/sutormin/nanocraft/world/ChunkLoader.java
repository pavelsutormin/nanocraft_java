package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.block.BlockTypes;
import org.sutormin.nanocraft.networking.packets.play.s2c.ChunkData;

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
 * Vertical alignment: the vanilla overworld runs from y = -64 to y = 319, and
 * Chunk.SIZE_Y is 384, so section s covers array rows s * 16 to s * 16 + 15
 * with no gaps. Array y is world y plus 64.
 */
public final class ChunkLoader {

    public static final int SECTION_SIZE = 16;
    public static final int ENTRIES_PER_SECTION = 4096;

    private static final Queue<Pending> PENDING = new ConcurrentLinkedQueue<>();

    public record Pending(ChunkPos pos, short[] blocks) {
    }

    private ChunkLoader() {
    }

    /** Call from ChunkData.read, on the Netty thread. */
    public static void submit(ChunkData data) {
        PENDING.add(new Pending(
                new ChunkPos(data.chunkX, data.chunkZ),
                toBlocks(data)
        ));
    }

    /** Call from the main loop, on the GL thread. Returns null when empty. */
    public static Pending poll() {
        return PENDING.poll();
    }

    public static int pendingCount() {
        return PENDING.size();
    }

    public static short[] toBlocks(ChunkData data) {
        short[] blocks = new short[Chunk.SIZE_X * Chunk.SIZE_Y * Chunk.SIZE_Z];

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

    private static void writeSection(
            short[] blocks,
            ChunkData.PalettedContainer states,
            int baseY
    ) {
        // Single valued section, overwhelmingly the common case: whole
        // 16x16x16 is one block, usually air or stone.
        if (states.bitsPerEntry == 0) {
            short type = BlockStateMapper.map(states.palette[0]);

            if (type == BlockTypes.AIR) {
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
        short[] mapped = null;

        if (states.palette != null) {
            mapped = new short[states.palette.length];

            for (int i = 0; i < mapped.length; i++) {
                mapped[i] = BlockStateMapper.map(states.palette[i]);
            }
        }

        for (int y = 0; y < SECTION_SIZE; y++) {
            for (int z = 0; z < Chunk.SIZE_Z; z++) {
                for (int x = 0; x < Chunk.SIZE_X; x++) {
                    // Vanilla packs x fastest, then z, then y.
                    int raw = states.rawGet((y << 8) | (z << 4) | x);

                    short type;

                    if (mapped == null) {
                        // Global palette: raw is the state id itself.
                        type = BlockStateMapper.map(raw);
                    } else if (raw < mapped.length) {
                        type = mapped[raw];
                    } else {
                        // Out of range means the section was misparsed.
                        type = BlockTypes.STONE;
                    }

                    if (type != BlockTypes.AIR) {
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