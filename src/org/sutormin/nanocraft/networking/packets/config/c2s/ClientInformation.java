package org.sutormin.nanocraft.networking.packets.config.c2s;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.Options;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

public class ClientInformation implements C2SPacket {
    public static short ID = 0;
    public static void make(ByteBuf out) {
        ByteBuf data = Unpooled.buffer();

        VarCoder.writeString(data, "en_US");  // Locale
        data.writeByte(Options.VIEW_DISTANCE);            // View Distance
        VarCoder.writeVarInt(data, 0);           // Chat Mode: enabled
        data.writeBoolean(true);                 // Chat Colors
        data.writeByte(0x7F);                    // Displayed Skin Parts: all
        VarCoder.writeVarInt(data, 1);           // Main Hand: right
        data.writeBoolean(false);                // Text filtering
        data.writeBoolean(true);                 // Allow server listings
        VarCoder.writeVarInt(data, 0);           // Particle Status: all

        PacketIO.write(out, ID, data);         // 0x00 in CONFIGURATION
        data.release();
    }
}
