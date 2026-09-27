package org.sutormin.nanocraft.networking.packets.play;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.Dimension;
import org.sutormin.nanocraft.world.biome.BiomeTint;
import org.sutormin.nanocraft.world.chunk.ChunkLoader;

/**
 * Join game ("login" in play) and respawn packets: the dimension (for the world height) and the
 * hashed seed (for biome lookups) are used. Both end in the same spawn info (dimension type,
 * dimension name, hashed seed, ...); the join packet has its own fields first.
 */
public class S2CJoinGame implements S2CPacket {
    private final boolean respawn;

    public S2CJoinGame(boolean respawn) {
        this.respawn = respawn;
    }

    @Override
    public void read(ByteBuf buf) {
        if (!respawn) {
            buf.readInt();                                    // player entity id
            buf.readBoolean();                                // hardcore
            int levels = VarCoder.readVarInt(buf);            // dimension names
            for (int i = 0; i < levels; i++) VarCoder.readString(buf);
            VarCoder.readVarInt(buf);                         // max players
            VarCoder.readVarInt(buf);                         // view distance
            VarCoder.readVarInt(buf);                         // simulation distance
            buf.readBoolean();                                // reduced debug info
            buf.readBoolean();                                // show death screen
            buf.readBoolean();                                // limited crafting
        }
        int dimensionType = VarCoder.readVarInt(buf);
        String dimensionName = VarCoder.readString(buf);
        // The server doesn't unload the old dimension's chunks, so drop them all, like vanilla.
        // Clearing here, on the network thread, keeps it in order with the chunks that follow.
        if (Dimension.enter(dimensionType, dimensionName)) ChunkLoader.clear();
        BiomeTint.setZoomSeed(buf.readLong());                // hashed seed
    }
}
