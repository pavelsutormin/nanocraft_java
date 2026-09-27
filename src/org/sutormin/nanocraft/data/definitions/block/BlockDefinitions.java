package org.sutormin.nanocraft.data.definitions.block;

import org.sutormin.nanocraft.data.registry.Registry;
import org.sutormin.nanocraft.data.types.Block;

import static org.sutormin.nanocraft.data.definitions.block.BlockProperties.*;
import static org.sutormin.nanocraft.data.definitions.block.BlockSpec.block;
import static org.sutormin.nanocraft.data.types.Block.RenderLayer.*;

/**
 * All 1286 blocks / 35723 block states of Minecraft 26.3.
 * Every state is registered as its own Block, named like "oak_stairs[facing=north,half=top,...]".
 *
 * Split into several methods because a single Java method is limited to 64 KB of bytecode.
 *
 * Shape names referenced (Registries.BLOCK_SHAPE must contain these):
 *  air, amethyst_cluster, anvil, azalea, bamboo, banner, bed, bell, big_dripleaf, big_dripleaf_stem, 
 *  brewing_stand, button, cactus, cake, campfire, candle, candle_cake, carpet, cauldron, chain, chest, 
 *  chorus_flower, chorus_plant, cocoa, comparator, composter, conduit, copper_golem_statue, coral_fan, 
 *  coral_wall_fan, crop, cross, daylight_detector, decorated_pot, dirt_path, door, double_cross, dragon_egg, 
 *  dried_ghast, enchanting_table, end_gateway, end_portal, end_portal_frame, end_rod, farmland, fence, 
 *  fence_gate, fire, flower_pot, frogspawn, full, glow_lichen, grindstone, hanging_sign, head, heavy_core, 
 *  hopper, ladder, lantern, large_amethyst_bud, lectern, lever, lightning_rod, lily_pad, liquid, 
 *  medium_amethyst_bud, moving_piston, nether_portal, pane, petals, pointed_dripstone, pressure_plate, rail, 
 *  redstone_wire, repeater, resin_clump, scaffolding, sculk_sensor, sculk_shrieker, sculk_vein, sea_pickle, 
 *  shelf, shulker_box, sign, slab_bottom, slab_top, small_amethyst_bud, small_dripleaf, sniffer_egg, snow, 
 *  spore_blossom, stairs, stonecutter, sulfur_spike, test_instance_block, torch, trapdoor, tripwire, 
 *  tripwire_hook, turtle_egg, vine, wall, wall_banner, wall_hanging_sign, wall_head, wall_sign, wall_torch
 */
public class BlockDefinitions {

    public static void define(Registry<Block> reg) {
        reg.addNew("null");
        reg.addNew("not_found").setTexture("null");
        part0(reg);
        part1(reg);
        part2(reg);
        part3(reg);
        part4(reg);
        part5(reg);
        part6(reg);
        part7(reg);
        part8(reg);
        part9(reg);
        part10(reg);
        part11(reg);
        part12(reg); // 26.3
    }

    private static void part0(Registry<Block> reg) {
        block("air", "air").layer(INVISIBLE).opacity(0).noCollision().register(reg);
        block("stone", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("granite", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("polished_granite", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("diorite", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("polished_diorite", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("andesite", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("polished_andesite", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("grass_block", "full", SNOWY).hardness(0.6f).tool("shovel").sound("grass").register(reg);
        block("dirt", "full").hardness(0.5f).tool("shovel").sound("gravel").register(reg);
        block("coarse_dirt", "full").hardness(0.5f).tool("shovel").sound("gravel").register(reg);
        block("podzol", "full", SNOWY).hardness(0.5f).tool("shovel").sound("gravel").register(reg);
        block("cobblestone", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("oak_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("spruce_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("birch_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("jungle_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("acacia_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("cherry_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("dark_oak_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("pale_oak_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("pale_oak_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("mangrove_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("bamboo_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("bamboo_mosaic", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("oak_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("spruce_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("birch_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jungle_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("acacia_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cherry_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dark_oak_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pale_oak_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("mangrove_propagule", "cross", AGE_4, HANGING, STAGE, WATERLOGGED).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bedrock", "full").unbreakable().register(reg);
        block("water", "liquid", LEVEL).hardness(100.0f).layer(TRANSLUCENT).opacity(1).noCollision().liquid().register(reg);
        block("lava", "liquid", LEVEL).hardness(100.0f).layer(CUTOUT).opacity(1).light(15).noCollision().liquid().register(reg);
        block("sand", "full").hardness(0.5f).tool("shovel").sound("sand").register(reg);
        block("suspicious_sand", "full", DUSTED).hardness(0.25f).tool("shovel").sound("sand").register(reg);
        block("red_sand", "full").hardness(0.5f).tool("shovel").sound("sand").register(reg);
        block("gravel", "full").hardness(0.6f).tool("shovel").sound("gravel").register(reg);
        block("suspicious_gravel", "full", DUSTED).hardness(0.25f).tool("shovel").sound("gravel").register(reg);
        block("gold_ore", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("deepslate_gold_ore", "full").hardness(4.5f).tool("pickaxe").sound("metal").register(reg);
        block("iron_ore", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("deepslate_iron_ore", "full").hardness(4.5f).tool("pickaxe").sound("metal").register(reg);
        block("coal_ore", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("deepslate_coal_ore", "full").hardness(4.5f).tool("pickaxe").register(reg);
        block("nether_gold_ore", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("oak_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("spruce_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("birch_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("jungle_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("acacia_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("cherry_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("dark_oak_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("pale_oak_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("mangrove_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("mangrove_roots", "full", WATERLOGGED).hardness(0.7f).tool("axe").sound("wood").layer(CUTOUT).opacity(1).register(reg);
        block("muddy_mangrove_roots", "full", AXIS).hardness(0.7f).tool("shovel").register(reg);
        block("bamboo_block", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_spruce_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_birch_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_jungle_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_acacia_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_cherry_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_dark_oak_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_pale_oak_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_oak_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_mangrove_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_bamboo_block", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("oak_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("spruce_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("birch_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("jungle_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("acacia_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("cherry_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("dark_oak_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("mangrove_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_oak_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_spruce_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_birch_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_jungle_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_acacia_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_cherry_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_dark_oak_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_pale_oak_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_mangrove_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("oak_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("spruce_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("birch_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("jungle_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("acacia_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("cherry_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("dark_oak_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("pale_oak_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("mangrove_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("azalea_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("flowering_azalea_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);
        block("sponge", "full").hardness(0.6f).tool("hoe").sound("grass").register(reg);
    }

    private static void part1(Registry<Block> reg) {
        block("wet_sponge", "full").hardness(0.6f).tool("hoe").sound("grass").register(reg);
        block("glass", "full").hardness(0.3f).sound("glass").layer(CUTOUT).opacity(0).register(reg);
        block("lapis_ore", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("deepslate_lapis_ore", "full").hardness(4.5f).tool("pickaxe").register(reg);
        block("lapis_block", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("dispenser", "full", FACING, TRIGGERED).hardness(3.5f).tool("pickaxe").register(reg);
        block("sandstone", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("chiseled_sandstone", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("cut_sandstone", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("note_block", "full", INSTRUMENT, NOTE, POWERED).hardness(0.8f).tool("axe").sound("wood").register(reg);
        block("white_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("orange_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("magenta_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("light_blue_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("yellow_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("lime_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("pink_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("gray_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("light_gray_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("cyan_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("purple_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("blue_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("brown_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("green_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("red_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("black_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);
        block("powered_rail", "rail", POWERED, RAIL_SHAPE_STRAIGHT, WATERLOGGED).hardness(0.7f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("detector_rail", "rail", POWERED, RAIL_SHAPE_STRAIGHT, WATERLOGGED).hardness(0.7f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("sticky_piston", "full", EXTENDED, FACING).hardness(1.5f).tool("pickaxe").register(reg);
        block("cobweb", "cross").hardness(4.0f).tool("sword").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("short_grass", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("fern", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dead_bush", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bush", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("short_dry_grass", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("tall_dry_grass", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("seagrass", "cross").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("tall_seagrass", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("piston", "full", EXTENDED, FACING).hardness(1.5f).tool("pickaxe").register(reg);
        block("piston_head", "head", PISTON_TYPE, FACING, SHORT).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("white_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("orange_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("magenta_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("light_blue_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("yellow_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("lime_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("pink_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("gray_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("light_gray_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("cyan_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("purple_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("blue_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("brown_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("green_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("red_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("black_wool", "full").hardness(0.8f).tool("shears").sound("wool").register(reg);
        block("moving_piston", "moving_piston", PISTON_TYPE, FACING).unbreakable().layer(INVISIBLE).opacity(0).noCollision().register(reg);
        block("dandelion", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("golden_dandelion", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("torchflower", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("poppy", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("blue_orchid", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("allium", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("azure_bluet", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("red_tulip", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("orange_tulip", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("white_tulip", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pink_tulip", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("oxeye_daisy", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cornflower", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("wither_rose", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("lily_of_the_valley", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("brown_mushroom", "cross").sound("grass").layer(CUTOUT).opacity(0).light(1).noCollision().register(reg);
        block("red_mushroom", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("gold_block", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("iron_block", "full").hardness(5.0f).tool("pickaxe").sound("metal").register(reg);
        block("bricks", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("tnt", "full", UNSTABLE).register(reg);
        block("bookshelf", "full").hardness(1.5f).tool("axe").sound("wood").register(reg);
        block("chiseled_bookshelf", "full", HORIZONTAL_FACING, SLOT_0_OCCUPIED, SLOT_1_OCCUPIED, SLOT_2_OCCUPIED, SLOT_3_OCCUPIED, SLOT_4_OCCUPIED, SLOT_5_OCCUPIED).hardness(1.5f).tool("axe").sound("wood").register(reg);
        block("acacia_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("birch_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("cherry_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("crimson_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("dark_oak_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("jungle_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("mangrove_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("oak_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("pale_oak_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("spruce_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("warped_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("mossy_cobblestone", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("obsidian", "full").hardness(50.0f).tool("pickaxe").register(reg);
        block("torch", "torch").layer(CUTOUT).opacity(0).light(14).noCollision().register(reg);
        block("wall_torch", "wall_torch", HORIZONTAL_FACING).layer(CUTOUT).opacity(0).light(14).noCollision().register(reg);
        block("fire", "fire", AGE_15, EAST, NORTH, SOUTH, UP, WEST).layer(CUTOUT).opacity(0).light(15).noCollision().register(reg);
        block("soul_fire", "fire").layer(CUTOUT).opacity(0).light(10).noCollision().register(reg);
        block("spawner", "full").hardness(5.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("creaking_heart", "full", AXIS, CREAKING_HEART_STATE, NATURAL).hardness(10.0f).tool("axe").sound("wood").register(reg);
    }

    private static void part2(Registry<Block> reg) {
        block("oak_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(2.5f).tool("axe").sound("wood").opacity(0).register(reg);
        block("redstone_wire", "redstone_wire", EAST_REDSTONE, NORTH_REDSTONE, POWER, SOUTH_REDSTONE, WEST_REDSTONE).layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("diamond_ore", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("deepslate_diamond_ore", "full").hardness(4.5f).tool("pickaxe").register(reg);
        block("diamond_block", "full").hardness(5.0f).tool("pickaxe").register(reg);
        block("crafting_table", "full").hardness(2.5f).tool("axe").sound("wood").register(reg);
        block("wheat", "crop", AGE_7).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("farmland", "farmland", MOISTURE).hardness(0.6f).tool("shovel").sound("gravel").opacity(0).register(reg);
        block("furnace", "full", HORIZONTAL_FACING, LIT).hardness(3.5f).tool("pickaxe").light(13).register(reg);
        block("oak_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("spruce_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("birch_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("acacia_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cherry_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jungle_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dark_oak_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pale_oak_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("mangrove_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bamboo_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("oak_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("ladder", "ladder", HORIZONTAL_FACING, WATERLOGGED).hardness(0.4f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).climbable().register(reg);
        block("rail", "rail", RAIL_SHAPE, WATERLOGGED).hardness(0.7f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cobblestone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("oak_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("spruce_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("birch_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("acacia_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cherry_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jungle_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dark_oak_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pale_oak_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("mangrove_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bamboo_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("oak_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("spruce_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("birch_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("acacia_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cherry_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jungle_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dark_oak_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pale_oak_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("crimson_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("mangrove_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bamboo_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("oak_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("spruce_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("birch_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("acacia_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("cherry_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("jungle_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("dark_oak_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("pale_oak_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("mangrove_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("crimson_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("warped_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("bamboo_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("lever", "lever", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("stone_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("iron_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oak_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("spruce_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("birch_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jungle_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("acacia_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cherry_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dark_oak_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pale_oak_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("mangrove_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bamboo_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("redstone_ore", "full", LIT).hardness(3.0f).tool("pickaxe").light(9).register(reg);
        block("deepslate_redstone_ore", "full", LIT).hardness(4.5f).tool("pickaxe").light(9).register(reg);
        block("redstone_torch", "torch", LIT).layer(CUTOUT).opacity(0).light(7).noCollision().register(reg);
        block("redstone_wall_torch", "wall_torch", HORIZONTAL_FACING, LIT).layer(CUTOUT).opacity(0).light(7).noCollision().register(reg);
        block("stone_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("snow", "snow", LAYERS).hardness(0.1f).tool("shovel").sound("snow").opacity(0).noCollision().register(reg);
        block("ice", "full").hardness(0.5f).tool("pickaxe").sound("glass").layer(TRANSLUCENT).opacity(1).friction(0.98f).register(reg);
        block("snow_block", "full").hardness(0.2f).tool("shovel").sound("snow").register(reg);
        block("cactus", "cactus", AGE_15).hardness(0.4f).opacity(0).register(reg);
        block("cactus_flower", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("clay", "full").hardness(0.6f).tool("shovel").sound("gravel").register(reg);
        block("sugar_cane", "cross", AGE_15).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jukebox", "full", HAS_RECORD).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("oak_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("netherrack", "full").hardness(0.4f).tool("pickaxe").register(reg);
        block("soul_sand", "full").hardness(0.5f).tool("shovel").sound("sand").speed(0.4f).register(reg);
        block("soul_soil", "full").hardness(0.5f).tool("shovel").sound("sand").register(reg);
        block("basalt", "full", AXIS).hardness(1.25f).tool("pickaxe").register(reg);
        block("polished_basalt", "full", AXIS).hardness(1.25f).tool("pickaxe").register(reg);
        block("soul_torch", "torch").layer(CUTOUT).opacity(0).light(10).noCollision().register(reg);
        block("soul_wall_torch", "wall_torch", HORIZONTAL_FACING).layer(CUTOUT).opacity(0).light(10).noCollision().register(reg);
        block("copper_torch", "torch").sound("metal").layer(CUTOUT).opacity(0).light(14).noCollision().register(reg);
        block("copper_wall_torch", "wall_torch", HORIZONTAL_FACING).sound("metal").layer(CUTOUT).opacity(0).light(14).noCollision().register(reg);
        block("glowstone", "full").hardness(0.3f).sound("glass").light(15).register(reg);
        block("nether_portal", "nether_portal", HORIZONTAL_AXIS).unbreakable().layer(TRANSLUCENT).opacity(0).light(11).noCollision().register(reg);
        block("carved_pumpkin", "full", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").register(reg);
        block("jack_o_lantern", "full", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("metal").light(15).register(reg);
        block("cake", "cake", BITES).hardness(0.5f).opacity(0).register(reg);
        block("repeater", "repeater", DELAY, HORIZONTAL_FACING, LOCKED, POWERED).opacity(0).register(reg);
    }

    private static void part3(Registry<Block> reg) {
        block("white_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("orange_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("magenta_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("light_blue_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("yellow_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("lime_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("pink_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("gray_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("light_gray_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("cyan_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("purple_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("blue_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("brown_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("green_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("red_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("black_stained_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("oak_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("spruce_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("birch_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("jungle_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("acacia_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("cherry_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("dark_oak_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("pale_oak_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("mangrove_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("bamboo_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("stone_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("mossy_stone_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("cracked_stone_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("chiseled_stone_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("packed_mud", "full").hardness(1.0f).tool("pickaxe").register(reg);
        block("mud_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("infested_stone", "full").hardness(0.75f).tool("pickaxe").register(reg);
        block("infested_cobblestone", "full").hardness(1.0f).tool("pickaxe").register(reg);
        block("infested_stone_bricks", "full").hardness(0.75f).tool("pickaxe").register(reg);
        block("infested_mossy_stone_bricks", "full").hardness(0.75f).tool("pickaxe").register(reg);
        block("infested_cracked_stone_bricks", "full").hardness(0.75f).tool("pickaxe").register(reg);
        block("infested_chiseled_stone_bricks", "full").hardness(0.75f).tool("pickaxe").register(reg);
        block("brown_mushroom_block", "full", DOWN, EAST, NORTH, SOUTH, UP, WEST).hardness(0.2f).tool("axe").sound("wood").register(reg);
        block("red_mushroom_block", "full", DOWN, EAST, NORTH, SOUTH, UP, WEST).hardness(0.2f).tool("axe").sound("wood").register(reg);
        block("mushroom_stem", "full", DOWN, EAST, NORTH, SOUTH, UP, WEST).hardness(0.2f).tool("axe").sound("wood").register(reg);
        block("iron_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oxidized_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_copper_bars", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("iron_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oxidized_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_copper_chain", "chain", AXIS, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(CUTOUT).opacity(0).register(reg);
        block("pumpkin", "full").hardness(1.0f).tool("axe").sound("wood").register(reg);
        block("melon", "full").hardness(1.0f).tool("axe").sound("wood").register(reg);
        block("attached_pumpkin_stem", "crop", HORIZONTAL_FACING).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("attached_melon_stem", "crop", HORIZONTAL_FACING).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pumpkin_stem", "crop", AGE_7).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("melon_stem", "crop", AGE_7).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("vine", "vine", EAST, NORTH, SOUTH, UP, WEST).hardness(0.2f).tool("axe").sound("grass").layer(CUTOUT).opacity(0).noCollision().climbable().register(reg);
        block("glow_lichen", "glow_lichen", DOWN, EAST, NORTH, SOUTH, UP, WATERLOGGED, WEST).hardness(0.2f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("resin_clump", "resin_clump", DOWN, EAST, NORTH, SOUTH, UP, WATERLOGGED, WEST).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("oak_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("stone_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("mud_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("mycelium", "full", SNOWY).hardness(0.6f).tool("shovel").sound("gravel").register(reg);
        block("lily_pad", "lily_pad").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("resin_block", "full").register(reg);
        block("resin_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("resin_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("resin_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("resin_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("chiseled_resin_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("nether_bricks", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("nether_brick_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("nether_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("nether_wart", "crop", AGE_3).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("enchanting_table", "enchanting_table").hardness(5.0f).tool("pickaxe").opacity(0).light(7).register(reg);
        block("brewing_stand", "brewing_stand", HAS_BOTTLE_0, HAS_BOTTLE_1, HAS_BOTTLE_2).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).light(1).register(reg);
        block("cauldron", "cauldron").hardness(2.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("water_cauldron", "cauldron", CAULDRON_LEVEL).hardness(2.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("lava_cauldron", "cauldron").hardness(2.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("powder_snow_cauldron", "cauldron", CAULDRON_LEVEL).hardness(2.0f).tool("pickaxe").sound("snow").layer(CUTOUT).opacity(0).register(reg);
        block("end_portal", "end_portal").unbreakable().layer(CUTOUT).opacity(0).light(15).noCollision().register(reg);
        block("end_portal_frame", "end_portal_frame", EYE, HORIZONTAL_FACING).unbreakable().opacity(0).light(1).register(reg);
        block("end_stone", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("dragon_egg", "dragon_egg").hardness(3.0f).layer(CUTOUT).opacity(0).light(1).register(reg);
        block("redstone_lamp", "full", LIT).hardness(0.3f).light(15).register(reg);
        block("cocoa", "cocoa", AGE_2, HORIZONTAL_FACING).hardness(0.2f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("sandstone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("pickaxe").opacity(0).register(reg);
        block("emerald_ore", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("deepslate_emerald_ore", "full").hardness(4.5f).tool("pickaxe").register(reg);
    }

    private static void part4(Registry<Block> reg) {
        block("ender_chest", "chest", HORIZONTAL_FACING, WATERLOGGED).hardness(22.5f).tool("pickaxe").opacity(0).light(7).register(reg);
        block("tripwire_hook", "tripwire_hook", ATTACHED, HORIZONTAL_FACING, POWERED).layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("tripwire", "tripwire", ATTACHED, DISARMED, EAST, NORTH, POWERED, SOUTH, WEST).layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("emerald_block", "full").hardness(5.0f).tool("pickaxe").register(reg);
        block("spruce_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("birch_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("jungle_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("command_block", "full", CONDITIONAL, FACING).unbreakable().register(reg);
        block("beacon", "full").hardness(3.0f).sound("glass").layer(CUTOUT).opacity(1).light(15).register(reg);
        block("cobblestone_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("mossy_cobblestone_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("flower_pot", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_torchflower", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_oak_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_spruce_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_birch_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_jungle_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_acacia_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_cherry_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_dark_oak_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_pale_oak_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("potted_mangrove_propagule", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_fern", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_dandelion", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_golden_dandelion", "flower_pot").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("potted_poppy", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_blue_orchid", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_allium", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_azure_bluet", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_red_tulip", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_orange_tulip", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_white_tulip", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_pink_tulip", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_oxeye_daisy", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_cornflower", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_lily_of_the_valley", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_wither_rose", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_red_mushroom", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_brown_mushroom", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_dead_bush", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_cactus", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("carrots", "crop", AGE_7).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("potatoes", "crop", AGE_7).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("oak_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("spruce_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("birch_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("jungle_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("acacia_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cherry_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dark_oak_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pale_oak_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("mangrove_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bamboo_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("skeleton_skull", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("skeleton_wall_skull", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("wither_skeleton_skull", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("wither_skeleton_wall_skull", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("zombie_head", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("zombie_wall_head", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("player_head", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("player_wall_head", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("creeper_head", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("creeper_wall_head", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("dragon_head", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("dragon_wall_head", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("piglin_head", "head", POWERED, ROTATION).hardness(1.0f).layer(CUTOUT).opacity(0).register(reg);
        block("piglin_wall_head", "wall_head", HORIZONTAL_FACING, POWERED).hardness(1.0f).opacity(0).register(reg);
        block("anvil", "anvil", HORIZONTAL_FACING).hardness(5.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("chipped_anvil", "anvil", HORIZONTAL_FACING).hardness(5.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("damaged_anvil", "anvil", HORIZONTAL_FACING).hardness(5.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("trapped_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(2.5f).tool("axe").sound("wood").opacity(0).register(reg);
        block("light_weighted_pressure_plate", "pressure_plate", POWER).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("heavy_weighted_pressure_plate", "pressure_plate", POWER).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("comparator", "comparator", HORIZONTAL_FACING, COMPARATOR_MODE, POWERED).opacity(0).register(reg);
        block("daylight_detector", "daylight_detector", INVERTED, POWER).hardness(0.2f).tool("axe").sound("wood").opacity(0).register(reg);
        block("redstone_block", "full").hardness(5.0f).tool("pickaxe").register(reg);
        block("nether_quartz_ore", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("hopper", "hopper", ENABLED, HOPPER_FACING).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("quartz_block", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("chiseled_quartz_block", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("quartz_pillar", "full", AXIS).hardness(0.8f).tool("pickaxe").register(reg);
        block("quartz_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("pickaxe").opacity(0).register(reg);
        block("activator_rail", "rail", POWERED, RAIL_SHAPE_STRAIGHT, WATERLOGGED).hardness(0.7f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dropper", "full", FACING, TRIGGERED).hardness(3.5f).tool("pickaxe").register(reg);
        block("white_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("orange_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("magenta_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("light_blue_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("yellow_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("lime_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("pink_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("gray_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("light_gray_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("cyan_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("purple_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("blue_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("brown_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("green_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("red_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("black_terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
    }

    private static void part5(Registry<Block> reg) {
        block("white_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("orange_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("magenta_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("light_blue_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("yellow_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("lime_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("pink_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("gray_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("light_gray_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("cyan_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("purple_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("blue_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("brown_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("green_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("red_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("black_stained_glass_pane", "pane", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(0.3f).sound("glass").layer(TRANSLUCENT).opacity(0).register(reg);
        block("acacia_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("cherry_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("dark_oak_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("pale_oak_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("mangrove_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_mosaic_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("slime_block", "full").layer(TRANSLUCENT).opacity(1).friction(0.8f).register(reg);
        block("barrier", "full", WATERLOGGED).unbreakable().layer(INVISIBLE).opacity(0).register(reg);
        block("light", "air", LEVEL, WATERLOGGED).unbreakable().layer(INVISIBLE).opacity(0).light(15).noCollision().register(reg);
        block("iron_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(5.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("prismarine", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("prismarine_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("dark_prismarine", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("prismarine_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("prismarine_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("dark_prismarine_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("prismarine_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("prismarine_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("dark_prismarine_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("sea_lantern", "full").hardness(0.3f).sound("glass").light(15).register(reg);
        block("hay_block", "full", AXIS).hardness(0.5f).tool("hoe").sound("grass").register(reg);
        block("white_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("orange_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("magenta_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("light_blue_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("yellow_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("lime_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("pink_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("gray_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("light_gray_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("cyan_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("purple_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("blue_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("brown_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("green_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("red_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("black_carpet", "carpet").hardness(0.1f).sound("wool").opacity(0).register(reg);
        block("terracotta", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("coal_block", "full").hardness(5.0f).tool("pickaxe").register(reg);
        block("packed_ice", "full").hardness(0.5f).tool("pickaxe").sound("glass").friction(0.98f).register(reg);
        block("sunflower", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("lilac", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("rose_bush", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("peony", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("tall_grass", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("large_fern", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("white_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("orange_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("magenta_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("light_blue_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("yellow_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("lime_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pink_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("gray_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("light_gray_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cyan_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("purple_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("blue_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("brown_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("green_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("red_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("black_banner", "banner", ROTATION).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("white_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("orange_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("magenta_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("light_blue_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("yellow_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("lime_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pink_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("gray_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("light_gray_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("cyan_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("purple_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("blue_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("brown_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("green_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("red_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("black_wall_banner", "wall_banner", HORIZONTAL_FACING).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("red_sandstone", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("chiseled_red_sandstone", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("cut_red_sandstone", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("red_sandstone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("pickaxe").opacity(0).register(reg);
        block("oak_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
    }

    private static void part6(Registry<Block> reg) {
        block("spruce_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("birch_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("jungle_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("acacia_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("cherry_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("dark_oak_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("pale_oak_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("mangrove_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_mosaic_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("stone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_stone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("sandstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("cut_sandstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("petrified_oak_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("cobblestone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("stone_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("mud_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("nether_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("quartz_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("red_sandstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("cut_red_sandstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("purpur_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_stone", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("smooth_sandstone", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("smooth_quartz", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("smooth_red_sandstone", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("spruce_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("birch_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("jungle_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("acacia_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("cherry_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("dark_oak_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("pale_oak_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("mangrove_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("spruce_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("birch_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("jungle_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("acacia_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("cherry_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("dark_oak_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("pale_oak_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("mangrove_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("bamboo_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("spruce_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("birch_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("jungle_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("acacia_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("cherry_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("dark_oak_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("pale_oak_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("mangrove_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("bamboo_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("end_rod", "end_rod", FACING).layer(CUTOUT).opacity(0).light(14).register(reg);
        block("chorus_plant", "chorus_plant", DOWN, EAST, NORTH, SOUTH, UP, WEST).hardness(0.4f).tool("axe").sound("wood").layer(CUTOUT).opacity(1).register(reg);
        block("chorus_flower", "chorus_flower", AGE_5).hardness(0.4f).tool("axe").sound("wood").layer(CUTOUT).opacity(1).register(reg);
        block("purpur_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("purpur_pillar", "full", AXIS).hardness(1.5f).tool("pickaxe").register(reg);
        block("purpur_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("end_stone_bricks", "full").hardness(3.0f).tool("pickaxe").register(reg);
        block("torchflower_crop", "crop", AGE_1).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("pitcher_crop", "crop", AGE_4, DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("pitcher_plant", "double_cross", DOUBLE_BLOCK_HALF).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("beetroots", "crop", AGE_3).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("dirt_path", "dirt_path").hardness(0.65f).tool("shovel").sound("gravel").opacity(0).register(reg);
        block("end_gateway", "end_gateway").unbreakable().layer(CUTOUT).opacity(1).light(15).noCollision().register(reg);
        block("repeating_command_block", "full", CONDITIONAL, FACING).unbreakable().register(reg);
        block("chain_command_block", "full", CONDITIONAL, FACING).unbreakable().sound("metal").register(reg);
        block("frosted_ice", "full", AGE_3).hardness(0.5f).sound("glass").layer(TRANSLUCENT).opacity(1).friction(0.98f).register(reg);
        block("magma_block", "full").hardness(0.5f).tool("pickaxe").light(3).register(reg);
        block("nether_wart_block", "full").hardness(1.0f).tool("hoe").register(reg);
        block("red_nether_bricks", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("bone_block", "full", AXIS).hardness(2.0f).tool("pickaxe").register(reg);
        block("structure_void", "air").layer(INVISIBLE).opacity(0).noCollision().register(reg);
        block("observer", "full", FACING, POWERED).hardness(3.0f).tool("pickaxe").register(reg);
        block("shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("white_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("orange_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("magenta_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("light_blue_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("yellow_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("lime_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("pink_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("gray_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("light_gray_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("cyan_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("purple_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("blue_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("brown_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("green_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("red_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("black_shulker_box", "shulker_box", FACING).hardness(2.0f).tool("pickaxe").layer(CUTOUT).opacity(1).register(reg);
        block("white_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("orange_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("magenta_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("light_blue_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("yellow_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("lime_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
    }

    private static void part7(Registry<Block> reg) {
        block("pink_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("gray_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("light_gray_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("cyan_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("purple_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("blue_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("brown_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("green_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("red_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("black_glazed_terracotta", "full", HORIZONTAL_FACING).hardness(1.4f).tool("pickaxe").register(reg);
        block("white_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("orange_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("magenta_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("light_blue_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("yellow_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("lime_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("pink_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("gray_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("light_gray_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("cyan_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("purple_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("blue_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("brown_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("green_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("red_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("black_concrete", "full").hardness(1.8f).tool("pickaxe").register(reg);
        block("white_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("orange_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("magenta_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("light_blue_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("yellow_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("lime_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("pink_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("gray_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("light_gray_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("cyan_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("purple_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("blue_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("brown_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("green_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("red_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("black_concrete_powder", "full").hardness(0.5f).tool("shovel").register(reg);
        block("kelp", "cross", AGE_25).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("kelp_plant", "cross").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dried_kelp_block", "full").hardness(0.5f).tool("hoe").sound("grass").register(reg);
        block("turtle_egg", "turtle_egg", EGGS, HATCH).hardness(0.5f).layer(CUTOUT).opacity(0).register(reg);
        block("sniffer_egg", "sniffer_egg", HATCH).hardness(0.5f).layer(CUTOUT).opacity(0).register(reg);
        block("dried_ghast", "dried_ghast", HORIZONTAL_FACING, HYDRATION, WATERLOGGED).layer(CUTOUT).opacity(0).register(reg);
        block("dead_tube_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("dead_brain_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("dead_bubble_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("dead_fire_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("dead_horn_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("tube_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("brain_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("bubble_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("fire_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("horn_coral_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("dead_tube_coral", "cross", WATERLOGGED).tool("pickaxe").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_brain_coral", "cross", WATERLOGGED).tool("pickaxe").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_bubble_coral", "cross", WATERLOGGED).tool("pickaxe").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_fire_coral", "cross", WATERLOGGED).tool("pickaxe").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_horn_coral", "cross", WATERLOGGED).tool("pickaxe").sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("tube_coral", "cross", WATERLOGGED).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("brain_coral", "cross", WATERLOGGED).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("bubble_coral", "cross", WATERLOGGED).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("fire_coral", "cross", WATERLOGGED).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("horn_coral", "cross", WATERLOGGED).sound("grass").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_tube_coral_fan", "coral_fan", WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_brain_coral_fan", "coral_fan", WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_bubble_coral_fan", "coral_fan", WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_fire_coral_fan", "coral_fan", WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_horn_coral_fan", "coral_fan", WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("tube_coral_fan", "coral_fan", WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("brain_coral_fan", "coral_fan", WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("bubble_coral_fan", "coral_fan", WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("fire_coral_fan", "coral_fan", WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("horn_coral_fan", "coral_fan", WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_tube_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_brain_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_bubble_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_fire_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("dead_horn_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).tool("pickaxe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("tube_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("brain_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("bubble_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("fire_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("horn_coral_wall_fan", "coral_wall_fan", HORIZONTAL_FACING, WATERLOGGED).layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("sea_pickle", "sea_pickle", PICKLES, WATERLOGGED).layer(CUTOUT).opacity(1).light(6).register(reg);
        block("blue_ice", "full").hardness(2.8f).tool("pickaxe").sound("glass").friction(0.989f).register(reg);
        block("conduit", "conduit", WATERLOGGED).hardness(3.0f).tool("pickaxe").layer(CUTOUT).opacity(1).light(15).register(reg);
        block("bamboo_sapling", "cross").hardness(1.0f).tool("sword").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("bamboo", "bamboo", AGE_1, LEAVES, STAGE).hardness(1.0f).tool("sword").layer(CUTOUT).opacity(0).register(reg);
        block("potted_bamboo", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("void_air", "air").layer(INVISIBLE).opacity(0).noCollision().register(reg);
        block("cave_air", "air").layer(INVISIBLE).opacity(0).noCollision().register(reg);
        block("bubble_column", "liquid", DRAG).layer(TRANSLUCENT).opacity(1).noCollision().liquid().register(reg);
        block("polished_granite_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_red_sandstone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("mossy_stone_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
    }

    private static void part8(Registry<Block> reg) {
        block("polished_diorite_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("mossy_cobblestone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("end_stone_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").opacity(0).register(reg);
        block("stone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_sandstone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_quartz_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("granite_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("andesite_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("red_nether_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("polished_andesite_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("diorite_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_granite_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_red_sandstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("mossy_stone_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_diorite_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("mossy_cobblestone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("end_stone_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_sandstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("smooth_quartz_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("granite_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("andesite_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("red_nether_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("polished_andesite_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("diorite_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("prismarine_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("red_sandstone_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(0.8f).tool("pickaxe").opacity(0).register(reg);
        block("mossy_stone_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("granite_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("stone_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("mud_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("nether_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("andesite_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("red_nether_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("sandstone_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(0.8f).tool("pickaxe").opacity(0).register(reg);
        block("end_stone_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(3.0f).tool("pickaxe").opacity(0).register(reg);
        block("diorite_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("scaffolding", "scaffolding", BOTTOM, SCAFFOLD_DISTANCE, WATERLOGGED).layer(CUTOUT).opacity(0).climbable().register(reg);
        block("loom", "full", HORIZONTAL_FACING).hardness(2.5f).tool("axe").sound("wood").register(reg);
        block("barrel", "full", FACING, OPEN).hardness(2.5f).tool("axe").sound("wood").register(reg);
        block("smoker", "full", HORIZONTAL_FACING, LIT).hardness(3.5f).tool("pickaxe").light(13).register(reg);
        block("blast_furnace", "full", HORIZONTAL_FACING, LIT).hardness(3.5f).tool("pickaxe").light(13).register(reg);
        block("cartography_table", "full").hardness(2.5f).tool("axe").sound("wood").register(reg);
        block("fletching_table", "full").hardness(2.5f).tool("axe").sound("wood").register(reg);
        block("grindstone", "grindstone", FACE, HORIZONTAL_FACING).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("lectern", "lectern", HORIZONTAL_FACING, HAS_BOOK, POWERED).hardness(2.5f).tool("axe").sound("wood").opacity(0).register(reg);
        block("smithing_table", "full").hardness(2.5f).tool("axe").sound("wood").register(reg);
        block("stonecutter", "stonecutter", HORIZONTAL_FACING).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("bell", "bell", ATTACHMENT, HORIZONTAL_FACING, POWERED).hardness(5.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("soul_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(10).register(reg);
        block("copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("exposed_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("weathered_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("oxidized_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("waxed_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("waxed_exposed_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("waxed_weathered_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("waxed_oxidized_copper_lantern", "lantern", HANGING, WATERLOGGED).hardness(3.5f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("campfire", "campfire", HORIZONTAL_FACING, LIT, SIGNAL_FIRE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).light(15).register(reg);
        block("soul_campfire", "campfire", HORIZONTAL_FACING, LIT, SIGNAL_FIRE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).light(10).register(reg);
        block("sweet_berry_bush", "cross", AGE_3).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_stem", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_warped_stem", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("warped_hyphae", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_warped_hyphae", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("warped_nylium", "full").hardness(0.4f).tool("pickaxe").sound("wood").register(reg);
        block("warped_fungus", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_wart_block", "full").hardness(1.0f).tool("hoe").sound("wood").register(reg);
        block("warped_roots", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("nether_sprouts", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("crimson_stem", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_crimson_stem", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("crimson_hyphae", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("stripped_crimson_hyphae", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("crimson_nylium", "full").hardness(0.4f).tool("pickaxe").sound("wood").register(reg);
        block("crimson_fungus", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("shroomlight", "full").hardness(1.0f).tool("hoe").light(15).register(reg);
        block("weeping_vines", "cross", AGE_25).sound("grass").layer(CUTOUT).opacity(0).noCollision().climbable().register(reg);
        block("weeping_vines_plant", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().climbable().register(reg);
        block("twisting_vines", "cross", AGE_25).sound("grass").layer(CUTOUT).opacity(0).noCollision().climbable().register(reg);
        block("twisting_vines_plant", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().climbable().register(reg);
        block("crimson_roots", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("crimson_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("warped_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);
        block("crimson_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("warped_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("crimson_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("crimson_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("warped_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("crimson_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("warped_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("crimson_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("warped_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("crimson_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("warped_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);
        block("crimson_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("crimson_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
    }

    private static void part9(Registry<Block> reg) {
        block("warped_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);
        block("crimson_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("crimson_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("warped_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("structure_block", "full", STRUCTURE_BLOCK_MODE).unbreakable().register(reg);
        block("jigsaw", "full", ORIENTATION).unbreakable().register(reg);
        block("test_block", "full", TEST_BLOCK_MODE).unbreakable().register(reg);
        block("test_instance_block", "test_instance_block").unbreakable().layer(CUTOUT).opacity(1).register(reg);
        block("composter", "composter", COMPOSTER_LEVEL).hardness(0.6f).tool("axe").sound("wood").opacity(0).register(reg);
        block("target", "full", POWER).hardness(0.5f).tool("hoe").register(reg);
        block("bee_nest", "full", HORIZONTAL_FACING, HONEY_LEVEL).hardness(0.3f).tool("axe").sound("wood").register(reg);
        block("beehive", "full", HORIZONTAL_FACING, HONEY_LEVEL).hardness(0.6f).tool("axe").sound("wood").register(reg);
        block("honey_block", "full").layer(TRANSLUCENT).opacity(1).speed(0.4f).register(reg);
        block("honeycomb_block", "full").hardness(0.6f).register(reg);
        block("netherite_block", "full").hardness(50.0f).tool("pickaxe").sound("metal").register(reg);
        block("ancient_debris", "full").hardness(30.0f).tool("pickaxe").register(reg);
        block("crying_obsidian", "full").hardness(50.0f).tool("pickaxe").light(10).register(reg);
        block("respawn_anchor", "full", CHARGES).hardness(50.0f).tool("pickaxe").register(reg);
        block("potted_crimson_fungus", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_warped_fungus", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_crimson_roots", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_warped_roots", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("lodestone", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("blackstone", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("blackstone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("blackstone_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("blackstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("polished_blackstone", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("polished_blackstone_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("cracked_polished_blackstone_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("chiseled_polished_blackstone", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("polished_blackstone_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("polished_blackstone_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_blackstone_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("gilded_blackstone", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("polished_blackstone_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("polished_blackstone_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("polished_blackstone_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("polished_blackstone_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("pickaxe").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("polished_blackstone_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(2.0f).tool("pickaxe").opacity(0).register(reg);
        block("chiseled_nether_bricks", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("cracked_nether_bricks", "full").hardness(2.0f).tool("pickaxe").register(reg);
        block("quartz_bricks", "full").hardness(0.8f).tool("pickaxe").register(reg);
        block("candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("white_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("orange_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("magenta_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("light_blue_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("yellow_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("lime_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("pink_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("gray_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("light_gray_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("cyan_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("purple_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("blue_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("brown_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("green_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("red_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("black_candle", "candle", CANDLES, LIT, WATERLOGGED).hardness(0.1f).layer(CUTOUT).opacity(0).register(reg);
        block("candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("white_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("orange_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("magenta_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("light_blue_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("yellow_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("lime_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("pink_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("gray_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("light_gray_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("cyan_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("purple_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("blue_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("brown_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("green_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("red_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("black_candle_cake", "candle_cake", LIT).hardness(0.5f).opacity(0).register(reg);
        block("amethyst_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("budding_amethyst", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("amethyst_cluster", "amethyst_cluster", FACING, WATERLOGGED).hardness(1.5f).tool("pickaxe").layer(CUTOUT).opacity(0).light(5).register(reg);
        block("large_amethyst_bud", "large_amethyst_bud", FACING, WATERLOGGED).hardness(1.5f).tool("pickaxe").layer(CUTOUT).opacity(0).light(4).register(reg);
        block("medium_amethyst_bud", "medium_amethyst_bud", FACING, WATERLOGGED).hardness(1.5f).tool("pickaxe").layer(CUTOUT).opacity(0).light(2).register(reg);
        block("small_amethyst_bud", "small_amethyst_bud", FACING, WATERLOGGED).hardness(1.5f).tool("pickaxe").layer(CUTOUT).opacity(0).light(1).register(reg);
        block("tuff", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("tuff_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("tuff_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("tuff_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_tuff", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("polished_tuff_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_tuff_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_tuff_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("chiseled_tuff", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("tuff_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("tuff_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("tuff_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("tuff_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);
        block("chiseled_tuff_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("calcite", "full").hardness(0.75f).tool("pickaxe").register(reg);
        block("tinted_glass", "full").hardness(0.3f).sound("glass").layer(TRANSLUCENT).register(reg);
    }

    private static void part10(Registry<Block> reg) {
        block("powder_snow", "full").hardness(0.25f).sound("snow").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("sculk_sensor", "sculk_sensor", POWER, SCULK_SENSOR_PHASE, WATERLOGGED).hardness(1.5f).tool("hoe").opacity(0).light(1).register(reg);
        block("calibrated_sculk_sensor", "sculk_sensor", HORIZONTAL_FACING, POWER, SCULK_SENSOR_PHASE, WATERLOGGED).hardness(1.5f).tool("hoe").opacity(0).light(1).register(reg);
        block("sculk", "full").hardness(0.2f).tool("hoe").register(reg);
        block("sculk_vein", "sculk_vein", DOWN, EAST, NORTH, SOUTH, UP, WATERLOGGED, WEST).hardness(0.2f).tool("hoe").layer(CUTOUT).opacity(1).noCollision().register(reg);
        block("sculk_catalyst", "full", BLOOM).hardness(3.0f).tool("hoe").light(6).register(reg);
        block("sculk_shrieker", "sculk_shrieker", CAN_SUMMON, SHRIEKING, WATERLOGGED).hardness(3.0f).tool("hoe").opacity(1).register(reg);
        block("copper_block", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("exposed_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("weathered_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("oxidized_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("copper_ore", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("deepslate_copper_ore", "full").hardness(4.5f).tool("pickaxe").sound("metal").register(reg);
        block("oxidized_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("weathered_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("exposed_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("oxidized_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("weathered_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("exposed_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_oxidized_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_weathered_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_exposed_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_chiseled_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("oxidized_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("weathered_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("exposed_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("oxidized_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("weathered_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("exposed_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_copper_block", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_weathered_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_exposed_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_oxidized_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_oxidized_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_weathered_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_exposed_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_cut_copper", "full").hardness(3.0f).tool("pickaxe").sound("metal").register(reg);
        block("waxed_oxidized_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_weathered_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_exposed_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_cut_copper_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_oxidized_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_weathered_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_exposed_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_cut_copper_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oxidized_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_copper_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oxidized_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_copper_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oxidized_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_copper_grate", "full", WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(15).register(reg);
        block("exposed_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(12).register(reg);
        block("weathered_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(8).register(reg);
        block("oxidized_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(4).register(reg);
        block("waxed_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(15).register(reg);
        block("waxed_exposed_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(12).register(reg);
        block("waxed_weathered_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(8).register(reg);
        block("waxed_oxidized_copper_bulb", "full", LIT, POWERED).hardness(3.0f).tool("pickaxe").sound("metal").light(4).register(reg);
        block("copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("exposed_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("weathered_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("oxidized_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_exposed_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_weathered_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("waxed_oxidized_copper_chest", "chest", CHEST_TYPE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").opacity(0).register(reg);
        block("copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("oxidized_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_copper_golem_statue", "copper_golem_statue", COPPER_GOLEM_POSE, HORIZONTAL_FACING, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("exposed_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("weathered_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
    }

    private static void part11(Registry<Block> reg) {
        block("oxidized_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_exposed_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_weathered_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("waxed_oxidized_lightning_rod", "lightning_rod", FACING, POWERED, WATERLOGGED).hardness(3.0f).tool("pickaxe").sound("metal").layer(CUTOUT).opacity(0).register(reg);
        block("pointed_dripstone", "pointed_dripstone", THICKNESS, VERTICAL_DIRECTION, WATERLOGGED).hardness(1.5f).tool("pickaxe").layer(CUTOUT).opacity(0).register(reg);
        block("dripstone_block", "full").hardness(1.5f).tool("pickaxe").register(reg);
        block("cave_vines", "cross", AGE_25, BERRIES).sound("grass").layer(CUTOUT).opacity(0).light(14).noCollision().climbable().register(reg);
        block("cave_vines_plant", "cross", BERRIES).sound("grass").layer(CUTOUT).opacity(0).light(14).noCollision().climbable().register(reg);
        block("spore_blossom", "spore_blossom").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("azalea", "azalea").layer(CUTOUT).opacity(0).register(reg);
        block("flowering_azalea", "azalea").layer(CUTOUT).opacity(0).register(reg);
        block("moss_carpet", "carpet").hardness(0.1f).tool("hoe").sound("grass").opacity(0).register(reg);
        block("pink_petals", "petals", HORIZONTAL_FACING, FLOWER_AMOUNT).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("wildflowers", "petals", HORIZONTAL_FACING, FLOWER_AMOUNT).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("leaf_litter", "petals", HORIZONTAL_FACING, SEGMENT_AMOUNT).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("moss_block", "full").hardness(0.1f).tool("hoe").sound("grass").register(reg);
        block("big_dripleaf", "big_dripleaf", HORIZONTAL_FACING, TILT, WATERLOGGED).hardness(0.1f).tool("axe").sound("wood").opacity(0).register(reg);
        block("big_dripleaf_stem", "big_dripleaf_stem", HORIZONTAL_FACING, WATERLOGGED).hardness(0.1f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("small_dripleaf", "small_dripleaf", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, WATERLOGGED).layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("hanging_roots", "cross", WATERLOGGED).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("rooted_dirt", "full").hardness(0.5f).tool("shovel").sound("gravel").register(reg);
        block("mud", "full").hardness(0.5f).tool("shovel").sound("gravel").register(reg);
        block("deepslate", "full", AXIS).hardness(3.0f).tool("pickaxe").register(reg);
        block("cobbled_deepslate", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("cobbled_deepslate_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("cobbled_deepslate_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("cobbled_deepslate_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_deepslate", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("polished_deepslate_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_deepslate_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("polished_deepslate_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("deepslate_tiles", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("deepslate_tile_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("deepslate_tile_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("deepslate_tile_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("deepslate_bricks", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("deepslate_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("deepslate_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("deepslate_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(3.5f).tool("pickaxe").opacity(0).register(reg);
        block("chiseled_deepslate", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("cracked_deepslate_bricks", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("cracked_deepslate_tiles", "full").hardness(3.5f).tool("pickaxe").register(reg);
        block("infested_deepslate", "full", AXIS).hardness(1.5f).tool("pickaxe").register(reg);
        block("smooth_basalt", "full").hardness(1.25f).tool("pickaxe").register(reg);
        block("raw_iron_block", "full").hardness(5.0f).tool("pickaxe").sound("metal").register(reg);
        block("raw_copper_block", "full").hardness(5.0f).tool("pickaxe").sound("metal").register(reg);
        block("raw_gold_block", "full").hardness(5.0f).tool("pickaxe").sound("metal").register(reg);
        block("potted_azalea_bush", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_flowering_azalea_bush", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("ochre_froglight", "full", AXIS).hardness(0.3f).light(15).register(reg);
        block("verdant_froglight", "full", AXIS).hardness(0.3f).light(15).register(reg);
        block("pearlescent_froglight", "full", AXIS).hardness(0.3f).light(15).register(reg);
        block("frogspawn", "frogspawn").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("reinforced_deepslate", "full").hardness(55.0f).register(reg);
        block("decorated_pot", "decorated_pot", CRACKED, HORIZONTAL_FACING, WATERLOGGED).layer(CUTOUT).opacity(0).register(reg);
        block("crafter", "full", CRAFTING, ORIENTATION, TRIGGERED).hardness(1.5f).tool("pickaxe").register(reg);
        block("trial_spawner", "full", OMINOUS, TRIAL_SPAWNER_STATE).hardness(50.0f).layer(CUTOUT).opacity(1).register(reg);
        block("vault", "full", HORIZONTAL_FACING, OMINOUS, VAULT_STATE).hardness(50.0f).layer(CUTOUT).opacity(1).light(6).register(reg);
        block("heavy_core", "heavy_core", WATERLOGGED).hardness(10.0f).tool("pickaxe").opacity(0).register(reg);
        block("pale_moss_block", "full").hardness(0.1f).tool("hoe").sound("grass").register(reg);
        block("pale_moss_carpet", "carpet", BOTTOM, EAST_WALL, NORTH_WALL, SOUTH_WALL, WEST_WALL).hardness(0.1f).tool("hoe").sound("grass").layer(CUTOUT).opacity(0).register(reg);
        block("pale_hanging_moss", "cross", TIP).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("open_eyeblossom", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("closed_eyeblossom", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);
        block("potted_open_eyeblossom", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("potted_closed_eyeblossom", "flower_pot").layer(CUTOUT).opacity(0).register(reg);
        block("firefly_bush", "cross").sound("grass").layer(CUTOUT).opacity(0).light(2).noCollision().register(reg);
        block("chiseled_cinnabar", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("chiseled_sulfur", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("cinnabar_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_cinnabar", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_cinnabar_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_cinnabar_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_cinnabar_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_sulfur", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_sulfur_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_sulfur_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("polished_sulfur_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("potent_sulfur", "full", POTENT_SULFUR_STATE).hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_brick_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_brick_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_brick_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_bricks", "full").hardness(1.5f).tool("pickaxe").register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_spike", "sulfur_spike", THICKNESS, VERTICAL_DIRECTION, WATERLOGGED).hardness(1.5f).tool("pickaxe").layer(CUTOUT).opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
        block("sulfur_wall", "wall", EAST_WALL, NORTH_WALL, SOUTH_WALL, UP, WATERLOGGED, WEST_WALL).hardness(1.5f).tool("pickaxe").opacity(0).register(reg);  // new in 26.2: hardness/tool/sound estimated
    }

    private static void part12(Registry<Block> reg) {
        block("black_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("black_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("black_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("black_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("blue_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("blue_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("blue_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("blue_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("brown_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("brown_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("brown_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("brown_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("cyan_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("cyan_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("cyan_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("cyan_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("gray_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("gray_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("gray_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("gray_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("green_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("green_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("green_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("green_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_blue_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_blue_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_blue_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_blue_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_gray_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_gray_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_gray_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("light_gray_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("lime_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("lime_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("lime_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("lime_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("magenta_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("magenta_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("magenta_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("magenta_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("orange_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("orange_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("orange_poplar_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("orange_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("orange_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("pink_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("pink_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("pink_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("pink_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_button", "button", FACE, HORIZONTAL_FACING, POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_door", "door", HORIZONTAL_FACING, DOUBLE_BLOCK_HALF, HINGE, OPEN, POWERED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_fence", "fence", EAST, NORTH, SOUTH, WATERLOGGED, WEST).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_fence_gate", "fence_gate", HORIZONTAL_FACING, IN_WALL, OPEN, POWERED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_hanging_sign", "hanging_sign", ATTACHED, ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_planks", "full").hardness(2.0f).tool("axe").sound("wood").register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_pressure_plate", "pressure_plate", POWERED).hardness(0.5f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_sapling", "cross", STAGE).sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_shelf", "shelf", HORIZONTAL_FACING, POWERED, SIDE_CHAIN, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_sign", "sign", ROTATION, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(2.0f).tool("axe").sound("wood").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_trapdoor", "trapdoor", HORIZONTAL_FACING, HALF, OPEN, POWERED, WATERLOGGED).hardness(3.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_wall_hanging_sign", "wall_hanging_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_wall_sign", "wall_sign", HORIZONTAL_FACING, WATERLOGGED).hardness(1.0f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("poplar_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);  // new in 26.3: copied from a similar block or estimated
        block("potted_poplar_sapling", "cross").sound("grass").layer(CUTOUT).opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("purple_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("purple_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("purple_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("purple_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("red_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("red_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("red_poplar_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("red_shrub", "cross").sound("grass").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("red_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("red_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("shelf_mushroom", "shelf_mushroom", AGE_1, HORIZONTAL_FACING).hardness(0.2f).tool("axe").sound("wood").layer(CUTOUT).opacity(0).noCollision().register(reg);  // new in 26.3: copied from a similar block or estimated
        block("straw_bed", "bed", HORIZONTAL_FACING, OCCUPIED, PART).hardness(0.2f).layer(CUTOUT).opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("stripped_poplar_log", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);  // new in 26.3: copied from a similar block or estimated
        block("stripped_poplar_wood", "full", AXIS).hardness(2.0f).tool("axe").sound("wood").register(reg);  // new in 26.3: copied from a similar block or estimated
        block("white_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("white_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("white_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("white_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("yellow_concrete_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("yellow_concrete_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(1.8f).tool("pickaxe").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("yellow_poplar_leaves", "full", DISTANCE, PERSISTENT, WATERLOGGED).hardness(0.2f).tool("hoe").sound("grass").layer(CUTOUT).opacity(1).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("yellow_wool_slab", "slab", SLAB_TYPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
        block("yellow_wool_stairs", "stairs", HORIZONTAL_FACING, HALF, STAIRS_SHAPE, WATERLOGGED).hardness(0.8f).tool("shears").sound("wool").opacity(0).register(reg);  // new in 26.3: copied from a similar block or estimated
    }
}
