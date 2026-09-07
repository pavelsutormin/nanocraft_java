package org.sutormin.nanocraft.networking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.sutormin.nanocraft.Options;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.packets.login.c2s.Handshake;
import org.sutormin.nanocraft.networking.packets.login.c2s.LoginStart;

public class NettyClient extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf packet = Unpooled.buffer();

        Handshake.make(
                packet,
                "127.0.0.1",
                25565
        );

        ctx.write(packet);

        ByteBuf packet2 = Unpooled.buffer();

        LoginStart.make(
                packet2,
                Options.PLAYER_USERNAME,
                Options.PLAYER_UUID
        );

        ctx.writeAndFlush(packet2);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf in) {
            try {
                PacketIO.read(in, ctx.channel());
            } finally {
                in.release();
            }
        }
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {
        System.err.println(
                "[Client] Connection dropped or encountered an error:"
        );

        cause.printStackTrace();
        ctx.close();
    }
}