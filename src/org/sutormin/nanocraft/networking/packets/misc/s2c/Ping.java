package org.sutormin.nanocraft.networking.packets.misc.s2c;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.networking.packets.misc.c2s.Pong;

public class Ping  implements S2CPacket {
    private final Channel channel;

    public Ping(Channel channel) {
        this.channel = channel;
    }
    @Override
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        Pong.make(buf, data.readInt());
        channel.writeAndFlush(buf);
    }
}
