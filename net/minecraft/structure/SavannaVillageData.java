/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;

public class SavannaVillageData {
    public static void init() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_PLANKS, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_STAIRS, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_LOG, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ACACIA_WOOD, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.ORANGE_TERRACOTTA, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.YELLOW_TERRACOTTA, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.RED_TERRACOTTA, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new BlockStateMatchRuleTest((BlockState)((BlockState)Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)), (Object[])new StructureProcessorRule[]{new StructureProcessorRule(new BlockStateMatchRuleTest((BlockState)((BlockState)Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true)), new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState())})));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/town_centers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/town_centers/savanna_meeting_point_1"), (Object)100), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/town_centers/savanna_meeting_point_2"), (Object)50), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/town_centers/savanna_meeting_point_3"), (Object)150), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/town_centers/savanna_meeting_point_4"), (Object)150), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_3", (List<StructureProcessor>)immutableList), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/town_centers/savanna_meeting_point_4", (List<StructureProcessor>)immutableList), (Object)3)), StructurePool.Projection.RIGID));
        ImmutableList immutableList2 = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.ACACIA_PLANKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GRASS_PATH, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/streets"), new Identifier("village/savanna/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/corner_01", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/corner_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_02", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_04", (List<StructureProcessor>)immutableList2), (Object)7), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_05", (List<StructureProcessor>)immutableList2), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_06", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_08", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_09", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_10", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/straight_11", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/crossroad_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/crossroad_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/crossroad_04", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/crossroad_05", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/crossroad_06", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/crossroad_07", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/split_01", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/split_02", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/streets/turn_01", (List<StructureProcessor>)immutableList2), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/zombie/streets"), new Identifier("village/savanna/zombie/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/corner_01", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/corner_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_02", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_04", (List<StructureProcessor>)immutableList2), (Object)7), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_05", (List<StructureProcessor>)immutableList2), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_06", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_08", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_09", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_10", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/straight_11", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/crossroad_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/crossroad_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/crossroad_04", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/crossroad_05", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/crossroad_06", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/crossroad_07", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/split_01", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/split_02", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/streets/turn_01", (List<StructureProcessor>)immutableList2), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        ImmutableList immutableList3 = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/houses"), new Identifier("village/savanna/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_2"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_3"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_4"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_5"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_6"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_7"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_house_8"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_medium_house_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_medium_house_2"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_butchers_shop_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_butchers_shop_2"), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_tool_smith_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_fletcher_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_shepherd_1"), (Object)7), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_armorer_1"), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_fisher_cottage_1"), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_tannery_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_cartographer_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_library_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_mason_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_weaponsmith_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_weaponsmith_2"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_temple_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_temple_2"), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_large_farm_1", (List<StructureProcessor>)immutableList3), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_large_farm_2", (List<StructureProcessor>)immutableList3), (Object)6), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_farm", (List<StructureProcessor>)immutableList3), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_animal_pen_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_animal_pen_2"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_animal_pen_3"), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)5)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/zombie/houses"), new Identifier("village/savanna/zombie/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_3", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_4", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_5", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_6", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_7", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_small_house_8", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_medium_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_medium_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_butchers_shop_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_butchers_shop_2", (List<StructureProcessor>)immutableList), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_tool_smith_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_fletcher_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_shepherd_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_armorer_1", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_fisher_cottage_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_tannery_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_cartographer_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_library_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_mason_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_weaponsmith_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_weaponsmith_2", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_temple_1", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_temple_2", (List<StructureProcessor>)immutableList), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_large_farm_1", (List<StructureProcessor>)immutableList), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_large_farm_2", (List<StructureProcessor>)immutableList), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_small_farm", (List<StructureProcessor>)immutableList), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/savanna/houses/savanna_animal_pen_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_animal_pen_2", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/houses/savanna_animal_pen_3", (List<StructureProcessor>)immutableList), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)5)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/terminators"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_01", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_03", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_04", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/terminators/terminator_05", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/zombie/terminators"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_01", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_03", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_04", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/terminators/terminator_05", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/trees"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG)), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/savanna_lamp_post_01"), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.HAY_PILE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.MELON_PILE_CONFIG)), (Object)1), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)4)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/zombie/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/savanna_lamp_post_01", (List<StructureProcessor>)immutableList), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.ACACIA_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.HAY_PILE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.MELON_PILE_CONFIG)), (Object)1), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)4)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/villagers/baby"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/savanna/zombie/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/savanna/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
    }
}

