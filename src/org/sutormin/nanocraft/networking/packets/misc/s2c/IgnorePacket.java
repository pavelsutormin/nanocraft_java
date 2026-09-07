package org.sutormin.nanocraft.networking.packets.misc.s2c;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class IgnorePacket implements S2CPacket {
    public IgnorePacket(String name){}
    public void read(ByteBuf data){}
}
