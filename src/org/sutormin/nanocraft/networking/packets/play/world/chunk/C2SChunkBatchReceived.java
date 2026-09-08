package org.sutormin.nanocraft.networking.packets.play.world.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.Options;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class C2SChunkBatchReceived implements C2SPacket {
  public static int ID = 11;
  public static void make(ByteBuf buf){
    ByteBuf packet = Unpooled.buffer();
    packet.writeFloat(Options.RECEIVE_CHUNKS_PER_TICK);
    PacketIO.write(buf,ID,packet);
    packet.release();
  }
}
