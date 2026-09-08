package org.sutormin.nanocraft.networking.packets.play.world.block;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.NanoCraft;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.BlockStateMapper;
import org.sutormin.nanocraft.world.ChunkLoader;

public class S2CBlockUpdate implements S2CPacket {

  @Override
  public void read(ByteBuf buf) {
    long encoded = buf.readLong();

    int x = (int) (encoded >> 38);
    int y = (int) (encoded << 52 >> 52); // sign-extend the low 12 bits
    int z = (int) (encoded << 26 >> 38);

    int blockStateId = VarCoder.readVarInt(buf);
    char type = BlockStateMapper.map(blockStateId);

    // Vanilla y is -64..319; array y is world y + 64 (see ChunkLoader docs).
    ChunkLoader.submitBlockChange(x, y + 64, z, type);
  }
}