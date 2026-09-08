package org.sutormin.nanocraft.block;

public class BlockTypes {
    public static char NULL = 65535;
    public static char AIR;
    public static char BEDROCK;
    public static char DIRT;
    public static char GRASS;
    public static char SAND;
    public static char STONE;
    public static char OAK_LOG;
    public static char OAK_LEAVES;
    public static char OAK_PLANKS;
    public static char WATER;
    public static char IRON_ORE;
    public static char COPPER_ORE;
    public static char GOLD_ORE;
    public static char REDSTONE_ORE;
    public static char DIAMOND_ORE;

    public static void define(){
        AIR = BlockRegistry.add("air").getId();
        BEDROCK = BlockRegistry.add("bedrock").setTextures("bedrock.png").getId();
        DIRT = BlockRegistry.add("dirt").setTextures("dirt.png").getId();
        GRASS = BlockRegistry.add("grass")
            .setTextures(0b100000,"grass_block_top.png")
            .setTextures(0b001111,"grass_block_side.png")
            .setTextures(0b010000,"dirt.png").getId();
        SAND = BlockRegistry.add("sand").setTextures("sand.png").getId();
        STONE = BlockRegistry.add("stone").setTextures("stone.png").getId();
        OAK_LOG = BlockRegistry.add("oak_log")
            .setTextures(0b110000,"oak_log_top.png")
            .setTextures(0b001111,"oak_log.png").getId();
        OAK_LEAVES = BlockRegistry.add("oak_leaves").setTextures("oak_leaves.png").getId();
        OAK_PLANKS = BlockRegistry.add("oak_planks").setTextures("oak_planks.png").getId();
        IRON_ORE = BlockRegistry.add("iron_ore").setTextures("iron_ore.png").getId();
        COPPER_ORE = BlockRegistry.add("copper_ore").setTextures("copper_ore.png").getId();
        GOLD_ORE = BlockRegistry.add("gold_ore").setTextures("gold_ore.png").getId();
        REDSTONE_ORE = BlockRegistry.add("redstone_ore").setTextures("redstone_ore.png").getId();
        DIAMOND_ORE = BlockRegistry.add("diamond_ore").setTextures("diamond_ore.png").getId();
        WATER = BlockRegistry.add("water").setTextures("water.png").getId();
    }
}
