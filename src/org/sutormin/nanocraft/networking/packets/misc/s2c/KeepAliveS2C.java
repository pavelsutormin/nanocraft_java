package org.sutormin.nanocraft.networking.packets.misc.s2c;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.networking.packets.misc.c2s.KeepAliveC2S;

public class KeepAliveS2C implements S2CPacket {
    private final Channel channel;

    public KeepAliveS2C(Channel channel) {
        this.channel = channel;
    }
    @Override
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        KeepAliveC2S.make(buf, data.readLong());
        channel.writeAndFlush(buf);
    }
}
