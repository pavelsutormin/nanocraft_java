package org.sutormin.nanocraft.networking.packets.play.player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class C2SConfirmTeleport implements C2SPacket {
  public static short ID = 0;
  public static void make(ByteBuf buf, int id) {
    ByteBuf packet = Unpooled.buffer();
    VarCoder.writeVarInt(packet,id);
    PacketIO.write(buf, ID, packet);
    packet.release();
  }
}
