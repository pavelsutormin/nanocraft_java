package org.sutormin.nanocraft.networking.packets.login.s2c;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class SetCompression implements S2CPacket {
    public void read(ByteBuf data) {
        Networking.compressionThreshold = VarCoder.readVarInt(data);
    }
}
