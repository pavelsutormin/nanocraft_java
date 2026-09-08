package org.sutormin.nanocraft.networking.packets.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.coders.Encryptor;
import org.sutormin.nanocraft.networking.coders.Decryptor;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class S2CEncryptionRequest implements S2CPacket {
    private final Channel channel;

    public S2CEncryptionRequest(Channel channel) {
        this.channel = channel;
    }


    public void read(ByteBuf data) {
        String serverId = VarCoder.readString(data);

        int publicKeyLength = VarCoder.readVarInt(data);
        byte[] publicKeyBytes = new byte[publicKeyLength];
        data.readBytes(publicKeyBytes);

        int verifyTokenLength = VarCoder.readVarInt(data);
        byte[] verifyToken = new byte[verifyTokenLength];
        data.readBytes(verifyToken);

        try {
            // Minecraft sends the public key as X.509 SubjectPublicKeyInfo DER.
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey publicKey = keyFactory.generatePublic(
                    new X509EncodedKeySpec(publicKeyBytes)
            );

            // Generate Minecraft's 128-bit shared secret.
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();

            byte[] sharedSecret = secretKey.getEncoded();

            // Immediately send Encryption Response.
            ByteBuf out = channel.alloc().buffer();

            C2SEncryptionResponse.make(
                    out,
                    publicKey,
                    sharedSecret,
                    verifyToken
            );

            channel.writeAndFlush(out);

            channel.pipeline().addFirst(
                    "decrypt",
                    new Encryptor(sharedSecret)
            );

            channel.pipeline().addLast(
                    "encrypt",
                    new Decryptor(sharedSecret)
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Encryption Request", e);
        }
    }
}