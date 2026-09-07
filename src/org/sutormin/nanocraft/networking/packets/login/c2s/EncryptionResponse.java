package org.sutormin.nanocraft.networking.packets.login.c2s;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.sutormin.nanocraft.networking.coders.PacketIO;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.C2SPacket;

import javax.crypto.Cipher;
import java.security.PublicKey;

public class EncryptionResponse implements C2SPacket {
    public static short ID = 1;

    public static void make(ByteBuf buf, PublicKey publicKey, byte[] sharedSecret, byte[] verifyToken) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedSecret = cipher.doFinal(sharedSecret);
            byte[] encryptedToken = cipher.doFinal(verifyToken);

            ByteBuf packet = Unpooled.buffer();

            // Shared Secret
            VarCoder.writeVarInt(packet, encryptedSecret.length);
            packet.writeBytes(encryptedSecret);

            // Verify Token
            VarCoder.writeVarInt(packet, encryptedToken.length);
            packet.writeBytes(encryptedToken);

            PacketIO.write(buf, ID, packet);

            packet.release();
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt Encryption Response", e);
        }
    }
}