package org.sutormin.nanocraft.networking.packets.play.world.block;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.BlockStateMapper;
import org.sutormin.nanocraft.world.Dimension;
import org.sutormin.nanocraft.world.chunk.ChunkLoader;

/**
 * Several block changes inside one 16x16x16 section (TNT, flowing water, pistons, ...), sent instead
 * of separate block updates.
 *
 * Section position: a long with x in the top 22 bits, z in the next 22 and y in the low 20 (all signed).
 * Then a VarInt count and one VarLong per block: state id << 12 | x << 8 | z << 4 | y, with x/y/z the
 * block's position inside the section.
 */
public class S2CSectionBlocksUpdate implements S2CPacket {
  @Override
  public void read(ByteBuf buf) {
    long section = buf.readLong();
    int sectionX = (int) (section >> 42);
    int sectionY = (int) (section << 44 >> 44);
    int sectionZ = (int) (section << 22 >> 42);

    int count = VarCoder.readVarInt(buf);
    for (int i = 0; i < count; i++) {
      long entry = VarCoder.readVarLong(buf);
      int blockStateId = (int) (entry >>> 12);
      int x = sectionX * 16 + (int) ((entry >> 8) & 15);
      int z = sectionZ * 16 + (int) ((entry >> 4) & 15);
      int y = sectionY * 16 + (int) (entry & 15);
      // chunk arrays start at the dimension's lowest y (see ChunkLoader docs)
      ChunkLoader.submitBlockChange(x, Dimension.toRow(y), z, BlockStateMapper.map(blockStateId));
    }
  }
}
