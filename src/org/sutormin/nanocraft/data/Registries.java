package org.sutormin.nanocraft.data;

import org.sutormin.nanocraft.data.definitions.block.BlockDefinitions;
import org.sutormin.nanocraft.data.definitions.BlockShapeDefinitions;
import org.sutormin.nanocraft.data.registry.Registry;
import org.sutormin.nanocraft.data.types.Block;
import org.sutormin.nanocraft.data.types.BlockShape;

public class Registries {
    public static Registry<Block> BLOCK = new Registry<Block>(Block::new);
    public static Registry<BlockShape> BLOCK_SHAPE = new Registry<BlockShape>(BlockShape::new);

    public static void defineAll(){
        System.out.println("Registering blocks!");
        BlockDefinitions.define(Registries.BLOCK);
        System.out.println("Registering block shapes!");
        BlockShapeDefinitions.define(Registries.BLOCK_SHAPE);
    }
}
