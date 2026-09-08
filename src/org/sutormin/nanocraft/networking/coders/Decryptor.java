package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decryptor extends MessageToByteEncoder<ByteBuf> {
    private final Cipher cipher;

    public Decryptor(byte[] sharedSecret) {
        try {
            SecretKeySpec key = new SecretKeySpec(sharedSecret, "AES");
            IvParameterSpec iv = new IvParameterSpec(sharedSecret);

            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Minecraft encryption encoder", e);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int length = msg.readableBytes();

        byte[] plaintext = new byte[length];
        msg.readBytes(plaintext);

        try {
            byte[] encrypted = cipher.update(plaintext);

            if (encrypted != null) {
                out.writeBytes(encrypted);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt Minecraft packet data", e);
        }
    }
}