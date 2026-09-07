package org.sutormin.nanocraft.networking.packets.types;

import io.netty.buffer.ByteBuf;

public interface S2CPacket {
    void read(ByteBuf buf);
}
