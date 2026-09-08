package org.sutormin.nanocraft.networking.packets.misc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class S2CKeepAlive implements S2CPacket {
    private final Channel channel;

    public S2CKeepAlive(Channel channel) {
        this.channel = channel;
    }
    @Override
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        C2SKeepAlive.make(buf, data.readLong());
        channel.writeAndFlush(buf);
    }
}
