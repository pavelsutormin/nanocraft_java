package org.sutormin.nanocraft.networking.packets.config.s2c;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.networking.packets.config.c2s.KnownPacksResponse;

public class KnownPacksChallenge implements S2CPacket {
    private final Channel channel;

    public KnownPacksChallenge(Channel channel) {
        this.channel = channel;
    }
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        KnownPacksResponse.make(buf, data);
        channel.writeAndFlush(buf);
    }
}
