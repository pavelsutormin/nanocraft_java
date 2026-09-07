package org.sutormin.nanocraft.networking.packets.config.s2c;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.networking.packets.config.c2s.FinishConfigurationAcknowledged;

public class FinishConfiguration implements S2CPacket {
    private final Channel channel;

    public FinishConfiguration(Channel channel) {
        this.channel = channel;
    }
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        FinishConfigurationAcknowledged.make(buf);
        channel.writeAndFlush(buf);
        Networking.networkPhase = NetworkPhase.PLAY;
    }
}
