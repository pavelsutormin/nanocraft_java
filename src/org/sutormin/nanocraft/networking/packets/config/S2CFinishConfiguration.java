package org.sutormin.nanocraft.networking.packets.config;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class S2CFinishConfiguration implements S2CPacket {
    private final Channel channel;

    public S2CFinishConfiguration(Channel channel) {
        this.channel = channel;
    }
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        C2SFinishConfigurationAcknowledged.make(buf);
        channel.writeAndFlush(buf);
        Networking.networkPhase = NetworkPhase.PLAY;
    }
}
