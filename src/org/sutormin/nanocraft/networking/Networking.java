package org.sutormin.nanocraft.networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.sutormin.nanocraft.Options;
import org.sutormin.nanocraft.networking.coders.Defragmentor;

public class Networking {

    public static NetworkPhase networkPhase = NetworkPhase.HANDSHAKE;
    public static int compressionThreshold = -1;

    private static EventLoopGroup group;
    private static Channel channel;

    public static void init() {
        String host = Options.SERVER_IP;
        int port = Options.PORT;

        group = new MultiThreadIoEventLoopGroup(
            NioIoHandler.newFactory()
        );

        Bootstrap bootstrap = new Bootstrap();

        bootstrap
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(
                        "frame",
                        new Defragmentor()
                    );

                    ch.pipeline().addLast(
                        "client",
                        new NettyClient()
                    );
                }
            });

        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                channel = ((io.netty.channel.ChannelFuture) future).channel();

                System.out.println("Connected!");
            } else {
                System.err.println("Connection failed");
                future.cause().printStackTrace();
            }
        });
    }

    public static void sendPacket(ByteBuf buf) {
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("Not connected");
        }

        channel.writeAndFlush(buf);
    }
}