package org.sutormin.nanocraft.networking.packets.play.world.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.ChunkLoader;

public class S2CChunkBatchFinished implements S2CPacket {
  private final Channel channel;

  public S2CChunkBatchFinished(Channel channel) {
    this.channel = channel;
  }

  @Override
  public void read(ByteBuf buf) {
    int chunks = VarCoder.readVarInt(buf);
    if (ChunkLoader.chunkBatchFinished(chunks)) {
      ByteBuf packet = Unpooled.buffer();
      C2SChunkBatchReceived.make(packet);
      channel.writeAndFlush(packet);
    }
  }
}
