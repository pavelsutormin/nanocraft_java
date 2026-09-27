package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.Options;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.PacketList;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketIO {
    public static void read(ByteBuf buf, Channel channel){
        if (Networking.compressionThreshold >= 0) {
            readCompressedPacket(buf, channel);
        } else {
            readPacket(buf, channel);
        }
    }
    
    public static void write(ByteBuf out, int packetId, ByteBuf data){
        if (Options.DEBUG_LOG_C2S_PACKETS) {
            System.out.printf("[C2S] %s 0x%02X %s (%d bytes)%n", Networking.networkPhase, packetId, callerName(),
                    data.readableBytes());
        }
        if (Networking.compressionThreshold >= 0) {
            writeCompressedPacket(
                out,
                packetId,
                data,
                Networking.compressionThreshold
            );
        } else {
            writePacket(out, packetId, data);
        }
    }
    /** The packet class that called {@link #write}: each C2S packet writes itself from its make(). */
    private static String callerName() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames.map(StackWalker.StackFrame::getDeclaringClass)
                        .filter(c -> c != PacketIO.class)
                        .findFirst()
                        .map(Class::getSimpleName)
                        .orElse("?"));
    }

    /** Hands a server packet's data (after its id) to the packet class for its id, if there is one. */
    private static void dispatch(int packetId, ByteBuf data, Channel channel) {
        S2CPacket packet = PacketList.getS2CPacket(packetId, Networking.networkPhase, channel);
        if (packet == null) {
            if (Options.DEBUG_LOG_UNKNOWN_S2C_PACKETS) {
                System.out.printf("[S2C] %s 0x%02X unknown, skipped (%d bytes)%n", Networking.networkPhase, packetId,
                        data.readableBytes());
            }
            return;
        }
        if (Options.DEBUG_LOG_KNOWN_S2C_PACKETS) {
            System.out.printf("[S2C] %s 0x%02X %s (%d bytes)%n", Networking.networkPhase, packetId,
                    packet.getClass().getSimpleName(), data.readableBytes());
        }
        packet.read(data);
    }

    private static void writePacket(ByteBuf buf, int id, ByteBuf data) {
        VarCoder.writeVarInt(
            buf,
            VarCoder.lenVarInt(id) + data.readableBytes()
        );

        VarCoder.writeVarInt(buf, id);
        buf.writeBytes(data);
    }

    private static void readPacket(ByteBuf buf, Channel channel) {
        int length = VarCoder.readVarInt(buf);
        int packetId = VarCoder.readVarInt(buf);
        dispatch(packetId, buf.readSlice(length - VarCoder.lenVarInt(packetId)), channel);
    }

    private static void writeCompressedPacket(
        ByteBuf buf,
        int id,
        ByteBuf data,
        int threshold
    ) {
        ByteBuf packetData = Unpooled.buffer();

        // Build the normal uncompressed packet first:
        // [Packet ID][Packet Data]
        VarCoder.writeVarInt(packetData, id);
        packetData.writeBytes(data);

        try {
            int uncompressedLength = packetData.readableBytes();

            ByteBuf compressedData;

            if (uncompressedLength < threshold) {
                /*
                 * Packet is below compression threshold:
                 *
                 * [Packet Length]
                 * [Data Length = 0]
                 * [Packet ID]
                 * [Packet Data]
                 */

                int dataLengthSize = VarCoder.lenVarInt(0);

                VarCoder.writeVarInt(
                    buf,
                    dataLengthSize + uncompressedLength
                );

                VarCoder.writeVarInt(buf, 0);
                buf.writeBytes(packetData);

            } else {
                /*
                 * Packet is compressed:
                 *
                 * [Packet Length]
                 * [Data Length = uncompressed length]
                 * [Compressed packet bytes]
                 */

                byte[] input = new byte[uncompressedLength];
                packetData.readBytes(input);

                Deflater deflater = new Deflater();
                deflater.setInput(input);
                deflater.finish();

                compressedData = Unpooled.buffer();

                byte[] output = new byte[1024];

                while (!deflater.finished()) {
                    int written = deflater.deflate(output);
                    compressedData.writeBytes(output, 0, written);
                }

                deflater.end();

                int packetLength =
                    VarCoder.lenVarInt(uncompressedLength)
                        + compressedData.readableBytes();

                VarCoder.writeVarInt(buf, packetLength);

                // Data Length = ORIGINAL uncompressed size
                VarCoder.writeVarInt(buf, uncompressedLength);

                buf.writeBytes(compressedData);

                compressedData.release();
            }

        } finally {
            packetData.release();
        }
    }

    private static void readCompressedPacket(
        ByteBuf buf,
        Channel channel
    ) {
        // Outer packet length
        int packetLength = VarCoder.readVarInt(buf);

        // Length after decompression.
        // 0 means this packet was not compressed.
        int dataLength = VarCoder.readVarInt(buf);

        ByteBuf packetData;

        if (dataLength == 0) {

            // Packet wasn't compressed.
            packetData = buf.readSlice(
                packetLength - VarCoder.lenVarInt(0)
            );

        } else {

            // Read the compressed bytes.
            int compressedLength =
                packetLength - VarCoder.lenVarInt(dataLength);

            byte[] compressed = new byte[compressedLength];
            buf.readBytes(compressed);

            byte[] decompressed = new byte[dataLength];

            Inflater inflater = new Inflater();
            inflater.setInput(compressed);

            try {
                int result = inflater.inflate(decompressed);

                if (result != dataLength) {
                    throw new RuntimeException(
                        "Decompression length mismatch: expected "
                            + dataLength
                            + ", got "
                            + result
                    );
                }

            } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to decompress Minecraft packet",
                    e
                );
            } finally {
                inflater.end();
            }

            packetData = Unpooled.wrappedBuffer(decompressed);
        }

        try {
            int packetId = VarCoder.readVarInt(packetData);
            dispatch(packetId, packetData, channel);

        } finally {
            // Only release wrapped decompressed buffers.
            // A readSlice() is owned by the original buffer.
            if (dataLength != 0) {
                packetData.release();
            }
        }
    }
}
