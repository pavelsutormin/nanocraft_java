package org.sutormin.nanocraft.networking.packets.play.player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

/**
 * Sent at the end of every client tick (20 per second), with no data. Since 26.3 the server allows at
 * most one position update between two of these and disconnects for more.
 */
public class C2SClientTickEnd implements C2SPacket {
  public static int ID = 13;

  public static void make(ByteBuf buf) {
    ByteBuf packet = Unpooled.buffer();
    PacketIO.write(buf, ID, packet);
    packet.release();
  }
}
