package org.sutormin.nanocraft.networking.packets.play.world.chunk;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.ChunkLoader;
import org.sutormin.nanocraft.world.ChunkPos;

public class S2CUnloadChunk implements S2CPacket {

  @Override
  public void read(ByteBuf buf) {
    int chunkZ = buf.readInt();
    int chunkX = buf.readInt();
    ChunkLoader.submitUnload(new ChunkPos(chunkX,chunkZ));
  }
}
