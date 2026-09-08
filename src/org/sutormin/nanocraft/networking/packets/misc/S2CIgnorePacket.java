package org.sutormin.nanocraft.networking.packets.misc;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class S2CIgnorePacket implements S2CPacket {
    public S2CIgnorePacket(String name){}
    public void read(ByteBuf data){}
}
