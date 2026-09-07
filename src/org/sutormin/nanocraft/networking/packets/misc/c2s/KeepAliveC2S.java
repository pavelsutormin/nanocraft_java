package org.sutormin.nanocraft.networking.packets.misc.c2s;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class KeepAliveC2S implements C2SPacket {
    public static short CONFIG_ID = 4;
    public static short PLAY_ID = 28;
    public static void make(ByteBuf buf, long id) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeLong(id);
        if (Networking.networkPhase == NetworkPhase.CONFIG) PacketIO.write(buf, CONFIG_ID, packet);
        if (Networking.networkPhase == NetworkPhase.PLAY) PacketIO.write(buf, PLAY_ID, packet);
        packet.release();
    }
}
