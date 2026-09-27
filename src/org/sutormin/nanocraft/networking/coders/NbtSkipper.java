package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Reads past network NBT (a nameless root tag, as sent since 1.20.2) without decoding it. */
public final class NbtSkipper {
    private NbtSkipper() {}

    public static void skipRoot(ByteBuf buf) {
        int type = buf.readUnsignedByte();
        if (type != 0) skipPayload(buf, type);
    }

    /**
     * Reads a root compound, keeping only its top-level int tags (e.g. a dimension type's min_y and
     * height) and skipping everything else. Empty if the root isn't a compound.
     */
    public static Map<String, Integer> readRootInts(ByteBuf buf) {
        Map<String, Integer> ints = new HashMap<>();
        int type = buf.readUnsignedByte();
        if (type != 10) {
            if (type != 0) skipPayload(buf, type);
            return ints;
        }
        int child;
        while ((child = buf.readUnsignedByte()) != 0) {
            String name = buf.readCharSequence(buf.readUnsignedShort(), StandardCharsets.UTF_8).toString();
            if (child == 3) ints.put(name, buf.readInt());
            else skipPayload(buf, child);
        }
        return ints;
    }

    private static void skipPayload(ByteBuf buf, int type) {
        switch (type) {
            case 1 -> buf.skipBytes(1);                          // byte
            case 2 -> buf.skipBytes(2);                          // short
            case 3, 5 -> buf.skipBytes(4);                       // int, float
            case 4, 6 -> buf.skipBytes(8);                       // long, double
            case 7 -> buf.skipBytes(buf.readInt());              // byte array
            case 8 -> buf.skipBytes(buf.readUnsignedShort());    // string
            case 9 -> {                                          // list
                int elementType = buf.readUnsignedByte();
                int length = buf.readInt();
                if (elementType != 0) for (int i = 0; i < length; i++) skipPayload(buf, elementType);
            }
            case 10 -> {                                         // compound
                int child;
                while ((child = buf.readUnsignedByte()) != 0) {
                    buf.skipBytes(buf.readUnsignedShort());      // name
                    skipPayload(buf, child);
                }
            }
            case 11 -> buf.skipBytes(buf.readInt() * 4);         // int array
            case 12 -> buf.skipBytes(buf.readInt() * 8);         // long array
            default -> throw new IllegalStateException("Unknown NBT tag type " + type);
        }
    }
}
