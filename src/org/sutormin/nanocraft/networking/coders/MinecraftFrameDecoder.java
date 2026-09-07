package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MinecraftFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(
            ChannelHandlerContext ctx,
            ByteBuf in,
            List<Object> out
    ) {
        in.markReaderIndex();

        // We need to read the packet length without permanently
        // advancing the buffer if the VarInt is incomplete.
        int packetLength;

        try {
            packetLength = VarCoder.readVarInt(in);
        } catch (IndexOutOfBoundsException e) {
            in.resetReaderIndex();
            return;
        }

        // The length itself is not included in packetLength.
        if (in.readableBytes() < packetLength) {
            in.resetReaderIndex();
            return;
        }

        // Include the packet-length VarInt in the output because
        // PacketIO.read() currently expects to read it itself.
        in.resetReaderIndex();

        int frameLength = VarCoder.lenVarInt(packetLength) + packetLength;

        out.add(in.readRetainedSlice(frameLength));
    }
}