package org.sutormin.nanocraft.networking.coders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.PacketList;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketIOer {

  public static void writePacket(ByteBuf buf, int id, ByteBuf data) {
    VarCoder.writeVarInt(
        buf,
        VarCoder.lenVarInt(id) + data.readableBytes()
    );

    VarCoder.writeVarInt(buf, id);
    buf.writeBytes(data);
  }

  public static void readPacket(ByteBuf buf, Channel channel) {
    int length = VarCoder.readVarInt(buf);
    int packetId = VarCoder.readVarInt(buf);

    System.out.printf(
        "Packet Received -> Length: %d, ID: 0x%02X%n",
        length,
        packetId
    );

    System.out.printf(
        "S2C: phase=%s id=0x%02X length=%d%n",
        Networking.networkPhase,
        packetId,
        length
    );

    S2CPacket packet = PacketList.getS2CPacket(
        packetId,
        Networking.networkPhase,
        channel
    );
    if (packet == null) {System.out.printf("UNKNOWN S2C: phase=%s id=0x%02X length=%d%n", Networking.networkPhase, packetId,length); return;}
    packet.read(buf);
  }

  public static void writeCompressedPacket(
      ByteBuf buf,
      int id,
      ByteBuf data,
      int threshold
  ) {
    ByteBuf packetData = Unpooled.buffer();

    // Build the normal uncompressed packet first:
    // [Packet ID][Packet Data]
    VarCoder.writeVarInt(packetData, id);
    packetData.writeBytes(data);

    try {
      int uncompressedLength = packetData.readableBytes();

      ByteBuf compressedData;

      if (uncompressedLength < threshold) {
        /*
         * Packet is below compression threshold:
         *
         * [Packet Length]
         * [Data Length = 0]
         * [Packet ID]
         * [Packet Data]
         */

        int dataLengthSize = VarCoder.lenVarInt(0);

        VarCoder.writeVarInt(
            buf,
            dataLengthSize + uncompressedLength
        );

        VarCoder.writeVarInt(buf, 0);
        buf.writeBytes(packetData);

      } else {
        /*
         * Packet is compressed:
         *
         * [Packet Length]
         * [Data Length = uncompressed length]
         * [Compressed packet bytes]
         */

        byte[] input = new byte[uncompressedLength];
        packetData.readBytes(input);

        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        compressedData = Unpooled.buffer();

        byte[] output = new byte[1024];

        while (!deflater.finished()) {
          int written = deflater.deflate(output);
          compressedData.writeBytes(output, 0, written);
        }

        deflater.end();

        int packetLength =
            VarCoder.lenVarInt(uncompressedLength)
                + compressedData.readableBytes();

        VarCoder.writeVarInt(buf, packetLength);

        // Data Length = ORIGINAL uncompressed size
        VarCoder.writeVarInt(buf, uncompressedLength);

        buf.writeBytes(compressedData);

        compressedData.release();
      }

    } finally {
      packetData.release();
    }
  }

  public static void readCompressedPacket(
      ByteBuf buf,
      Channel channel
  ) {
    // Outer packet length
    int packetLength = VarCoder.readVarInt(buf);

    // Length after decompression.
    // 0 means this packet was not compressed.
    int dataLength = VarCoder.readVarInt(buf);

    ByteBuf packetData;

    if (dataLength == 0) {

      // Packet wasn't compressed.
      packetData = buf.readSlice(
          packetLength - VarCoder.lenVarInt(0)
      );

    } else {

      // Read the compressed bytes.
      int compressedLength =
          packetLength - VarCoder.lenVarInt(dataLength);

      byte[] compressed = new byte[compressedLength];
      buf.readBytes(compressed);

      byte[] decompressed = new byte[dataLength];

      Inflater inflater = new Inflater();
      inflater.setInput(compressed);

      try {
        int result = inflater.inflate(decompressed);

        if (result != dataLength) {
          throw new RuntimeException(
              "Decompression length mismatch: expected "
                  + dataLength
                  + ", got "
                  + result
          );
        }

      } catch (Exception e) {
        throw new RuntimeException(
            "Failed to decompress Minecraft packet",
            e
        );
      } finally {
        inflater.end();
      }

      packetData = Unpooled.wrappedBuffer(decompressed);
    }

    try {
      int packetId = VarCoder.readVarInt(packetData);

      /*System.out.printf(
          "Compressed Packet Received -> Length: %d, " +
              "Uncompressed Length: %d, ID: 0x%02X%n",
          packetLength,
          dataLength,
          packetId
      );

      System.out.printf(
          "S2C: phase=%s id=0x%02X%n",
          Networking.networkPhase,
          packetId
      );*/

      S2CPacket packet = PacketList.getS2CPacket(
              packetId,
              Networking.networkPhase,
              channel
      );
      if (packet == null) {System.out.printf("UNKNOWN S2C: phase=%s id=0x%02X length=%d%n", Networking.networkPhase, packetId,dataLength); return;}

      packet.read(packetData);

    } finally {
      // Only release wrapped decompressed buffers.
      // A readSlice() is owned by the original buffer.
      if (dataLength != 0) {
        packetData.release();
      }
    }
  }
}