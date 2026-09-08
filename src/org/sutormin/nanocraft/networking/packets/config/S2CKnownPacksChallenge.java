package org.sutormin.nanocraft.networking.packets.config;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class S2CKnownPacksChallenge implements S2CPacket {
    private final Channel channel;

    public S2CKnownPacksChallenge(Channel channel) {
        this.channel = channel;
    }
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        C2SKnownPacksResponse.make(buf, data);
        channel.writeAndFlush(buf);
    }
}
