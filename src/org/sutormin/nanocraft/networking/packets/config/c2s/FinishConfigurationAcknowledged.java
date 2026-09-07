package org.sutormin.nanocraft.networking.packets.config.c2s;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class FinishConfigurationAcknowledged implements C2SPacket {
    public static int ID = 3;
    public static void make(ByteBuf buf){
        ByteBuf packet = Unpooled.buffer();
        PacketIO.write(buf,ID,packet);
        packet.release();
    }
}
