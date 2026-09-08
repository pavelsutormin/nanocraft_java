package org.sutormin.nanocraft.networking.packets.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.config.C2SClientInformation;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class S2CLoginSuccess implements S2CPacket {
    private final Channel channel;

    public S2CLoginSuccess(Channel channel) {
        this.channel = channel;
    }
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        C2SLoginAcknowledged.make(buf);
        channel.write(buf);
        ByteBuf buf2 = channel.alloc().buffer();
        C2SClientInformation.make(buf2);
        channel.writeAndFlush(buf2);
        Networking.networkPhase = NetworkPhase.CONFIG;
    }
}

