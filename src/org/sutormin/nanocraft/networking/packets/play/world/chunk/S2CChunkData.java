package org.sutormin.nanocraft.networking.packets.play.world.chunk;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.ChunkLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * "Chunk Data and Update Light" (S2C, PLAY) for protocol 776 / Minecraft 26.2.
 *
 * Wire layout:
 *   Int    chunkX
 *   Int    chunkZ
 *   Prefixed array of heightmaps: VarInt type, prefixed array of long
 *   VarInt size
 *   Bytes  data      -> chunk sections, not length prefixed, bottom to top
 *   VarInt blockEntityCount, then each block entity
 *   Light data       -> 4 bitsets + 2 arrays of 2048-byte nibble arrays
 *
 * Two things here are specific to 26.x and will break on 1.21.11 and older:
 * the fluid count field in each section, and the missing length prefix on
 * paletted container data arrays (dropped in 1.21.5).
 */
public class S2CChunkData implements S2CPacket {

    private static final int BLOCK_ENTRIES = 4096; // 16 * 16 * 16
    private static final int BIOME_ENTRIES = 64;   // 4 * 4 * 4

    private static final int MAX_BLOCK_INDIRECT_BITS = 8;
    private static final int MAX_BIOME_INDIRECT_BITS = 3;

    /** Smallest possible section: 2 + 2 + 1 + 1 + 1 + 1 bytes. */
    private static final int MIN_SECTION_BYTES = 8;

    /** Lowest block Y of the dimension. Overworld = -64, Nether/End = 0. */
    public static int minY = -64;

    public int chunkX;
    public int chunkZ;
    public Section[] sections = new Section[0];
    public final List<BlockEntity> blockEntities = new ArrayList<>();

    @Override
    public void read(ByteBuf buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();

        readHeightmaps(buf);

        // readSlice advances buf past the whole blob, so trailing padding
        // (the server sometimes over-reports Size) can't desync what follows.
        int size = VarCoder.readVarInt(buf);
        readSections(buf.readSlice(size));

        readBlockEntities(buf);
        readLight(buf);


        ChunkLoader.submit(this);

        /*System.out.printf(
                "Chunk (%d, %d): %d sections, %d block entities%n",
                chunkX,
                chunkZ,
                sections.length,
                blockEntities.size()
        );*/
    }

    /**
     * Block state id at chunk-local x/z (0..15) and absolute y.
     * Returns 0 (air) for a y outside this chunk column.
     */
    public int getBlockStateId(int x, int y, int z) {
        int sectionIndex = (y - minY) >> 4;

        if (sectionIndex < 0 || sectionIndex >= sections.length) {
            return 0;
        }

        int localY = y & 15;
        return sections[sectionIndex].blockStates.get(
                (localY << 8) | (z << 4) | x
        );
    }

    /** Biome id for the 4x4x4 region containing the given block. */
    public int getBiomeId(int x, int y, int z) {
        int sectionIndex = (y - minY) >> 4;

        if (sectionIndex < 0 || sectionIndex >= sections.length) {
            return 0;
        }

        int localY = (y & 15) >> 2;
        return sections[sectionIndex].biomes.get(
                (localY << 4) | ((z >> 2) << 2) | (x >> 2)
        );
    }

    // ------------------------------------------------------------------
    // Sections
    // ------------------------------------------------------------------

    /**
     * Sections are packed back to back with no count field. Reading until the
     * blob runs out avoids needing the dimension height up front; the overworld
     * gives 24 sections and accounts for every byte.
     */
    private void readSections(ByteBuf data) {
        List<Section> list = new ArrayList<>();

        while (data.readableBytes() >= MIN_SECTION_BYTES) {
            Section section = new Section();

            section.blockCount = data.readShort();
            section.fluidCount = data.readShort(); // added in 26.x
            section.blockStates = readPalettedContainer(
                    data,
                    MAX_BLOCK_INDIRECT_BITS,
                    BLOCK_ENTRIES
            );
            section.biomes = readPalettedContainer(
                    data,
                    MAX_BIOME_INDIRECT_BITS,
                    BIOME_ENTRIES
            );

            list.add(section);
        }

        sections = list.toArray(new Section[0]);
    }

    /**
     * @param maxIndirectBits 8 for block states, 3 for biomes. Above this the
     *                        container uses the global palette directly, which
     *                        for 26.2 block states means 17 bits per entry.
     * @param entryCount      4096 for block states, 64 for biomes.
     */
    private static PalettedContainer readPalettedContainer(
            ByteBuf buf,
            int maxIndirectBits,
            int entryCount
    ) {
        int bitsPerEntry = buf.readUnsignedByte();

        int[] palette;

        if (bitsPerEntry == 0) {
            // Single valued: the whole container is one id, no data array.
            palette = new int[]{VarCoder.readVarInt(buf)};

        } else if (bitsPerEntry <= maxIndirectBits) {
            // Indirect: entries are indices into this palette.
            int length = VarCoder.readVarInt(buf);
            palette = new int[length];

            for (int i = 0; i < length; i++) {
                palette[i] = VarCoder.readVarInt(buf);
            }

        } else {
            // Global: entries are registry ids already, no palette sent.
            palette = null;
        }

        // Since 1.21.5 the long count is computed, not sent.
        int longCount = longsFor(bitsPerEntry, entryCount);
        long[] data = new long[longCount];

        for (int i = 0; i < longCount; i++) {
            data[i] = buf.readLong();
        }

        return new PalettedContainer(bitsPerEntry, palette, data);
    }

    /** ceil(entryCount / floor(64 / bitsPerEntry)), matching SimpleBitStorage. */
    private static int longsFor(int bitsPerEntry, int entryCount) {
        if (bitsPerEntry == 0) {
            return 0;
        }

        int valuesPerLong = 64 / bitsPerEntry;
        return (entryCount + valuesPerLong - 1) / valuesPerLong;
    }

    // ------------------------------------------------------------------
    // Heightmaps
    // ------------------------------------------------------------------

    /** Types: 1 = WORLD_SURFACE, 4 = MOTION_BLOCKING, 5 = ..._NO_LEAVES. */
    private void readHeightmaps(ByteBuf buf) {
        int count = VarCoder.readVarInt(buf);

        for (int i = 0; i < count; i++) {
            VarCoder.readVarInt(buf);                 // heightmap type
            int longCount = VarCoder.readVarInt(buf); // this one IS prefixed
            buf.skipBytes(longCount * 8);
        }
    }

    // ------------------------------------------------------------------
    // Block entities
    // ------------------------------------------------------------------

    private void readBlockEntities(ByteBuf buf) {
        blockEntities.clear();

        int count = VarCoder.readVarInt(buf);

        for (int i = 0; i < count; i++) {
            int packedXZ = buf.readUnsignedByte();

            BlockEntity entity = new BlockEntity();
            entity.x = (packedXZ >> 4) & 15;
            entity.z = packedXZ & 15;
            entity.y = buf.readShort();
            entity.type = VarCoder.readVarInt(buf);

            // NBT payload: skipped for now. Swap in a real NBT reader here
            // when you need sign text, chest contents, etc.
            skipNbt(buf);

            blockEntities.add(entity);
        }
    }

    // ------------------------------------------------------------------
    // Light
    // ------------------------------------------------------------------

    /**
     * Skipped rather than stored. Wrapped defensively because it is the last
     * thing in the packet: a mismatch here would otherwise bubble up to
     * exceptionCaught and close the connection.
     */
    private void readLight(ByteBuf buf) {
        try {
            skipBitSet(buf); // sky light mask
            skipBitSet(buf); // block light mask
            skipBitSet(buf); // empty sky light mask
            skipBitSet(buf); // empty block light mask

            skipLightArrays(buf); // sky light
            skipLightArrays(buf); // block light

        } catch (RuntimeException e) {
            System.out.printf(
                    "Chunk (%d, %d): light section not parsed (%s)%n",
                    chunkX,
                    chunkZ,
                    e
            );
        }
    }

    private static void skipBitSet(ByteBuf buf) {
        int longCount = VarCoder.readVarInt(buf);
        skipBounded(buf, longCount * 8);
    }

    private static void skipLightArrays(ByteBuf buf) {
        int count = VarCoder.readVarInt(buf);

        for (int i = 0; i < count; i++) {
            int length = VarCoder.readVarInt(buf); // always 2048
            skipBounded(buf, length);
        }
    }

    private static void skipBounded(ByteBuf buf, int bytes) {
        buf.skipBytes(Math.min(bytes, buf.readableBytes()));
    }

    // ------------------------------------------------------------------
    // Minimal NBT skipper (network NBT: root tag carries no name)
    // ------------------------------------------------------------------

    private static void skipNbt(ByteBuf buf) {
        int type = buf.readUnsignedByte();

        if (type == 0) {
            return;
        }

        skipNbtPayload(buf, type);
    }

    private static void skipNbtPayload(ByteBuf buf, int type) {
        switch (type) {
            case 1 -> buf.skipBytes(1);                       // byte
            case 2 -> buf.skipBytes(2);                       // short
            case 3, 5 -> buf.skipBytes(4);                    // int, float
            case 4, 6 -> buf.skipBytes(8);                    // long, double
            case 7 -> buf.skipBytes(buf.readInt());           // byte array
            case 8 -> buf.skipBytes(buf.readUnsignedShort()); // string

            case 9 -> {                                       // list
                int elementType = buf.readUnsignedByte();
                int length = buf.readInt();

                if (elementType != 0) {
                    for (int i = 0; i < length; i++) {
                        skipNbtPayload(buf, elementType);
                    }
                }
            }

            case 10 -> {                                      // compound
                int childType;

                while ((childType = buf.readUnsignedByte()) != 0) {
                    buf.skipBytes(buf.readUnsignedShort());   // name
                    skipNbtPayload(buf, childType);
                }
            }

            case 11 -> buf.skipBytes(buf.readInt() * 4);      // int array
            case 12 -> buf.skipBytes(buf.readInt() * 8);      // long array

            default -> throw new IllegalStateException(
                    "Unknown NBT tag type: " + type
            );
        }
    }

    // ------------------------------------------------------------------
    // Data holders
    // ------------------------------------------------------------------

    public static final class Section {
        public int blockCount;
        public int fluidCount;
        public PalettedContainer blockStates;
        public PalettedContainer biomes;
    }

    public static final class BlockEntity {
        public int x;
        public int y;
        public int z;
        public int type;
    }

    public static final class PalettedContainer {
        public final int bitsPerEntry;
        public final int[] palette; // null means global palette
        public final long[] data;   // empty when bitsPerEntry == 0

        public PalettedContainer(int bitsPerEntry, int[] palette, long[] data) {
            this.bitsPerEntry = bitsPerEntry;
            this.palette = palette;
            this.data = data;
        }

        public int get(int index) {
            if (bitsPerEntry == 0) {
                return palette[0];
            }

            int value = rawGet(index);
            return palette == null ? value : palette[value];
        }

        /**
         * The stored entry before palette resolution: a local palette index
         * for indirect containers, a global id for direct ones. Lets callers
         * translate a section's palette once instead of per block.
         */
        public int rawGet(int index) {
            if (bitsPerEntry == 0) {
                return 0;
            }

            int valuesPerLong = 64 / bitsPerEntry;
            long packed = data[index / valuesPerLong];
            int shift = (index % valuesPerLong) * bitsPerEntry;

            return (int) ((packed >>> shift) & ((1L << bitsPerEntry) - 1));
        }
    }
}