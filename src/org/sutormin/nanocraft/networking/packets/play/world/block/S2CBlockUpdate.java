package org.sutormin.nanocraft.networking.packets.play.world.block;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.BlockStateMapper;
import org.sutormin.nanocraft.world.Dimension;
import org.sutormin.nanocraft.world.chunk.ChunkLoader;

public class S2CBlockUpdate implements S2CPacket {

  @Override
  public void read(ByteBuf buf) {
    long encoded = buf.readLong();

    int x = (int) (encoded >> 38);
    int y = (int) (encoded << 52 >> 52); // sign-extend the low 12 bits
    int z = (int) (encoded << 26 >> 38);

    int blockStateId = VarCoder.readVarInt(buf);
    char type = BlockStateMapper.map(blockStateId);

    // chunk arrays start at the dimension's lowest y (see ChunkLoader docs)
    ChunkLoader.submitBlockChange(x, Dimension.toRow(y), z, type);
  }
}