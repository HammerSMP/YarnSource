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
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;

public class SnowyVillageData {
    public static void init() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.LANTERN), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SPRUCE_PLANKS, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SPRUCE_SLAB, 0.4f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STRIPPED_SPRUCE_LOG, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STRIPPED_SPRUCE_WOOD, 0.05f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new BlockStateMatchRuleTest((BlockState)((BlockState)Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)), (Object)new StructureProcessorRule(new BlockStateMatchRuleTest((BlockState)((BlockState)Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, (BlockState)((BlockState)Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true)), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()), (Object[])new StructureProcessorRule[]{new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.8f), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState())})));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/town_centers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/town_centers/snowy_meeting_point_1"), (Object)100), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/town_centers/snowy_meeting_point_2"), (Object)50), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/town_centers/snowy_meeting_point_3"), (Object)150), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/town_centers/snowy_meeting_point_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/town_centers/snowy_meeting_point_2"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/town_centers/snowy_meeting_point_3"), (Object)3)), StructurePool.Projection.RIGID));
        ImmutableList immutableList2 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.SPRUCE_PLANKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GRASS_PATH, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.GRASS_BLOCK.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_BLOCK), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/streets"), new Identifier("village/snowy/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/corner_01", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/corner_02", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/corner_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/square_01", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/straight_01", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/straight_02", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/straight_03", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/straight_04", (List<StructureProcessor>)immutableList2), (Object)7), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/straight_06", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/straight_08", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/crossroad_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/crossroad_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/crossroad_04", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/crossroad_05", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/crossroad_06", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/streets/turn_01", (List<StructureProcessor>)immutableList2), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/zombie/streets"), new Identifier("village/snowy/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/corner_01", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/corner_02", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/corner_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/square_01", (List<StructureProcessor>)immutableList2), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/straight_01", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/straight_02", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/straight_03", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/straight_04", (List<StructureProcessor>)immutableList2), (Object)7), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/straight_06", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/straight_08", (List<StructureProcessor>)immutableList2), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/crossroad_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/crossroad_03", (List<StructureProcessor>)immutableList2), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/crossroad_04", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/crossroad_05", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/crossroad_06", (List<StructureProcessor>)immutableList2), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/streets/turn_01", (List<StructureProcessor>)immutableList2), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        ImmutableList immutableList3 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.8f), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/houses"), new Identifier("village/snowy/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_2"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_3"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_4"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_5"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_6"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_7"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_small_house_8"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_medium_house_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_medium_house_2"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_medium_house_3"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_butchers_shop_1"), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_butchers_shop_2"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_tool_smith_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_fletcher_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_shepherds_house_1"), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_armorer_house_1"), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_armorer_house_2"), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_fisher_cottage"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_tannery_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_cartographer_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_library_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_masons_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_masons_house_2"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_weapon_smith_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_temple_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_farm_1", (List<StructureProcessor>)immutableList3), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_farm_2", (List<StructureProcessor>)immutableList3), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_animal_pen_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_animal_pen_2"), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)6)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/zombie/houses"), new Identifier("village/snowy/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_3", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_4", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_5", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_6", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_7", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_small_house_8", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_medium_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_medium_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/houses/snowy_medium_house_3", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_butchers_shop_1", (List<StructureProcessor>)immutableList), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_butchers_shop_2", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_tool_smith_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_fletcher_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_shepherds_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_armorer_house_1", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_armorer_house_2", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_fisher_cottage", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_tannery_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_cartographer_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_library_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_masons_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_masons_house_2", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_weapon_smith_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_temple_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_farm_1", (List<StructureProcessor>)immutableList), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_farm_2", (List<StructureProcessor>)immutableList), (Object)3), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_animal_pen_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/snowy/houses/snowy_animal_pen_2", (List<StructureProcessor>)immutableList), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)6)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/terminators"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_01", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_02", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_03", (List<StructureProcessor>)immutableList2), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/plains/terminators/terminator_04", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/trees"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG)), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/snowy_lamp_post_01"), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/snowy_lamp_post_02"), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/snowy_lamp_post_03"), (Object)1), (Object)new Pair((Object)new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.SNOW_PILE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.BLUE_ICE_PILE_CONFIG)), (Object)1), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)9)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/zombie/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/snowy_lamp_post_01", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/snowy_lamp_post_02", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/snowy_lamp_post_03", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.SNOW_PILE_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.BLUE_ICE_PILE_CONFIG)), (Object)4), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)7)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/villagers/baby"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/snowy/zombie/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/snowy/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
    }
}
