package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

public class Encryptor extends ByteToMessageDecoder {
    private final Cipher cipher;

    public Encryptor(byte[] sharedSecret) {
        try {
            SecretKeySpec key = new SecretKeySpec(sharedSecret, "AES");
            IvParameterSpec iv = new IvParameterSpec(sharedSecret);

            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Minecraft encryption decoder", e);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!in.isReadable()) {
            return;
        }

        int length = in.readableBytes();

        byte[] encrypted = new byte[length];
        in.readBytes(encrypted);

        try {
            byte[] decrypted = cipher.update(encrypted);

            if (decrypted != null && decrypted.length > 0) {
                out.add(ctx.alloc().buffer(decrypted.length)
                    .writeBytes(decrypted));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt Minecraft packet data", e);
        }
    }
}