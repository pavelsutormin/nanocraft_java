package org.sutormin.nanocraft.networking.packets.config;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class C2SKnownPacksResponse implements C2SPacket {
    public static short ID = 7;
    public static void make(ByteBuf buf, ByteBuf data) {
        PacketIO.write(buf, ID, data);
    }
}
