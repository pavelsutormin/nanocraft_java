package org.sutormin.nanocraft.networking.packets.play.player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class C2SConfirmTeleport implements C2SPacket {
  public static short ID = 0;
  /**
   * Since 26.3 the confirmation also carries where the client ended up after the teleport (world
   * position and Minecraft yaw/pitch); the server uses it and disconnects on invalid values.
   */
  public static void make(ByteBuf buf, int id, double x, double y, double z, float yaw, float pitch) {
    ByteBuf packet = Unpooled.buffer();
    VarCoder.writeVarInt(packet,id);
    packet.writeDouble(x);
    packet.writeDouble(y);
    packet.writeDouble(z);
    packet.writeFloat(yaw);
    packet.writeFloat(pitch);
    PacketIO.write(buf, ID, packet);
    packet.release();
  }
}
