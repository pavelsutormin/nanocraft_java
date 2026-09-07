package org.sutormin.nanocraft.networking.packets.login.c2s;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class Handshake implements C2SPacket {
    public static short ID = 0;
    public static short PROTOCOL = 776;
    public static void make(ByteBuf buf, String addr, int port) {
        ByteBuf packet = Unpooled.buffer();
        VarCoder.writeVarInt(packet, PROTOCOL);
        VarCoder.writeString(packet, addr);
        packet.writeShort((short) port);
        VarCoder.writeVarInt(packet, 2);
        PacketIO.write(buf, ID, packet);
        packet.release();
    }
}
