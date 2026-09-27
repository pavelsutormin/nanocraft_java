package org.sutormin.nanocraft.networking.packets.misc;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The server closing the connection, with a reason: JSON text during login, an NBT text component
 * during configuration and play. The reason is printed; the text inside the component is enough to
 * understand it (e.g. "Flying is not enabled on this server").
 */
public class S2CDisconnect implements S2CPacket {
    private final boolean jsonReason;

    public S2CDisconnect(boolean jsonReason) {
        this.jsonReason = jsonReason;
    }

    @Override
    public void read(ByteBuf buf) {
        String reason;
        if (jsonReason) {
            reason = VarCoder.readString(buf);
        } else {
            List<String> texts = new ArrayList<>();
            int type = buf.readUnsignedByte();
            if (type != 0) collectStrings(buf, type, texts);
            reason = String.join(" ", texts);
        }
        System.err.println("[Client] Disconnected by the server: " + reason);
    }

    /** Walks network NBT, keeping every string value (a text component's "text"/"translate"). */
    private static void collectStrings(ByteBuf buf, int type, List<String> out) {
        switch (type) {
            case 1 -> buf.skipBytes(1);
            case 2 -> buf.skipBytes(2);
            case 3, 5 -> buf.skipBytes(4);
            case 4, 6 -> buf.skipBytes(8);
            case 7 -> buf.skipBytes(buf.readInt());
            case 8 -> {
                byte[] bytes = new byte[buf.readUnsignedShort()];
                buf.readBytes(bytes);
                out.add(new String(bytes, StandardCharsets.UTF_8));
            }
            case 9 -> {
                int elementType = buf.readUnsignedByte();
                int length = buf.readInt();
                for (int i = 0; i < length; i++) collectStrings(buf, elementType, out);
            }
            case 10 -> {
                int child;
                while ((child = buf.readUnsignedByte()) != 0) {
                    buf.skipBytes(buf.readUnsignedShort()); // name
                    collectStrings(buf, child, out);
                }
            }
            case 11 -> buf.skipBytes(buf.readInt() * 4);
            case 12 -> buf.skipBytes(buf.readInt() * 8);
            default -> throw new IllegalStateException("Unknown NBT tag type " + type);
        }
    }
}
