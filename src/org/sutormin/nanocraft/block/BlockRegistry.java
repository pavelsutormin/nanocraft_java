package org.sutormin.nanocraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockRegistry {

    public static final String NAMESPACE = "minecraft";

    private static final List<BlockType> blockTypes = new ArrayList<>();
    private static final Map<String,BlockType> byName = new HashMap<>();
    
    public static BlockType add(String name) {
        BlockType block = new BlockType(blockTypes.size(), NAMESPACE+":"+name);
        blockTypes.add(block);
        byName.put(NAMESPACE+":"+name,block);
        return block;
    }

    public static BlockType getBlock(char id) {
        return blockTypes.get(id);
    }

    public static BlockType getBlockByName(String name){return byName.get(name);}
}