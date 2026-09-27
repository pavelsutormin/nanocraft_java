package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;


public final class VarCoder {
    public static void writeVarInt(ByteBuf buf, int value) {
        while ((value & ~0x7F) != 0) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value);
    }

    public static int lenVarInt(int value) {
        int x = 1;
        while ((value & ~0x7F) != 0) {
            value >>>= 7;
            x++;
        }
        return x;
    }

    /**
     * Throws IndexOutOfBoundsException if the buffer ends in the middle of the VarInt (readByte does),
     * which the frame decoder relies on to wait for the rest of a split packet length.
     */
    public static int readVarInt(ByteBuf buf) {
        int value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte = buf.readByte();
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x80) == 0) return value;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }
    }

    public static void writeVarLong(ByteBuf buf, long value) {
        while ((value & ~0x7F) != 0) {
            buf.writeByte((int) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        buf.writeByte((int) value);
    }

    public static long readVarLong(ByteBuf buf) {
        long value = 0;
        int position = 0;

        while (buf.isReadable()) {
            byte currentByte = buf.readByte();

            value |= (long) (currentByte & 0x7F) << position;

            if ((currentByte & 0x80) == 0) {
                return value;
            }

            position += 7;

            if (position >= 64) {
                throw new RuntimeException("VarLong is too big");
            }
        }

        throw new RuntimeException("Unexpected end of VarLong");
    }

    public static void writeString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
