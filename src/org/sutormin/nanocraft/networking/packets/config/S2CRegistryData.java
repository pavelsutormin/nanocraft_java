package org.sutormin.nanocraft.networking.packets.config;

import io.netty.buffer.ByteBuf;
import org.sutormin.nanocraft.networking.coders.NbtSkipper;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;
import org.sutormin.nanocraft.world.Dimension;
import org.sutormin.nanocraft.world.biome.BiomeTint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * One registry's entries, in id order. Two registries are used: biomes (their order gives the biome
 * ids in chunk data) and dimension types (for the world height). Entries from packs we echoed back as
 * known come without data; others carry NBT.
 */
public class S2CRegistryData implements S2CPacket {
    @Override
    public void read(ByteBuf buf) {
        String registry = VarCoder.readString(buf);
        switch (registry) {
            case "minecraft:worldgen/biome" -> readBiomes(buf);
            case "minecraft:dimension_type" -> readDimensionTypes(buf);
            default -> {}
        }
    }

    private static void readBiomes(ByteBuf buf) {
        int count = VarCoder.readVarInt(buf);
        List<String> names = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            names.add(VarCoder.readString(buf));
            if (buf.readBoolean()) NbtSkipper.skipRoot(buf);
        }
        BiomeTint.setRegistry(names);
    }

    private static void readDimensionTypes(ByteBuf buf) {
        int count = VarCoder.readVarInt(buf);
        List<Dimension.Type> types = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String name = VarCoder.readString(buf);
            Dimension.Type type = Dimension.vanilla(name);
            if (buf.readBoolean()) {
                Map<String, Integer> ints = NbtSkipper.readRootInts(buf);
                type = new Dimension.Type(name, ints.getOrDefault("min_y", type.minY()),
                        ints.getOrDefault("height", type.height()));
            }
            types.add(type);
        }
        Dimension.setTypes(types);
    }
}
