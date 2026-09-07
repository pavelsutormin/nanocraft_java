package org.sutormin.nanocraft.networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.sutormin.nanocraft.networking.coders.MinecraftFrameDecoder;

public class Networking {

    public static NetworkPhase networkPhase = NetworkPhase.HANDSHAKE;
    public static int compressionThreshold = -1;

    private static EventLoopGroup group;

    public static void init() {
        String host = "127.0.0.1";
        int port = 25565;

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
                                new MinecraftFrameDecoder()
                        );

                        ch.pipeline().addLast(
                                "client",
                                new NettyClient()
                        );
                    }
                });

        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("Connected!");
            } else {
                System.err.println("Connection failed");
                future.cause().printStackTrace();
            }
        });
    }
}