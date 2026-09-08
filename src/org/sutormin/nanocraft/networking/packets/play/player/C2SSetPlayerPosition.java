package org.sutormin.nanocraft.networking.packets.play.player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class C2SSetPlayerPosition implements C2SPacket {
    public static short ID = 30;
    public static void make(ByteBuf buf, double x, double y, double z, byte flags) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeDouble(x);
        packet.writeDouble(y);
        packet.writeDouble(z);
        packet.writeByte(flags);
        PacketIO.write(buf, ID, packet);
        packet.release();
    }
}
