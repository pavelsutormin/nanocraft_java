package org.sutormin.nanocraft.networking.packets.login.s2c;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.config.c2s.ClientInformation;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.networking.packets.login.c2s.LoginAcknowledged;

public class LoginSuccess implements S2CPacket {
    private final Channel channel;

    public LoginSuccess(Channel channel) {
        this.channel = channel;
    }
    public void read(ByteBuf data) {
        ByteBuf buf = channel.alloc().buffer();
        LoginAcknowledged.make(buf);
        channel.write(buf);
        ByteBuf buf2 = channel.alloc().buffer();
        ClientInformation.make(buf2);
        channel.writeAndFlush(buf2);
        Networking.networkPhase = NetworkPhase.CONFIG;
    }
}

