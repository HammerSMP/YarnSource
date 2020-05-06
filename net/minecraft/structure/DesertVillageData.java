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
import net.minecraft.block.Blocks;
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
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;

public class DesertVillageData {
    public static void init() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE, 0.08f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.CUT_SANDSTONE, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.TERRACOTTA, 0.08f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_STAIRS, 0.08f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.SMOOTH_SANDSTONE_SLAB, 0.08f), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/town_centers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/town_centers/desert_meeting_point_1"), (Object)98), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/town_centers/desert_meeting_point_2"), (Object)98), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/town_centers/desert_meeting_point_3"), (Object)49), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/town_centers/desert_meeting_point_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/streets"), new Identifier("village/desert/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/corner_01"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/corner_02"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/straight_01"), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/straight_02"), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/straight_03"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/crossroad_01"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/crossroad_02"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/crossroad_03"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/square_01"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/square_02"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/streets/turn_01"), (Object)3)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/zombie/streets"), new Identifier("village/desert/zombie/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/corner_01"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/corner_02"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/straight_01"), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/straight_02"), (Object)4), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/straight_03"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/crossroad_01"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/crossroad_02"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/crossroad_03"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/square_01"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/square_02"), (Object)3), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/streets/turn_01"), (Object)3)), StructurePool.Projection.TERRAIN_MATCHING));
        ImmutableList immutableList2 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2f), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.MELON_STEM.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/houses"), new Identifier("village/desert/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_2"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_3"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_4"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_5"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_6"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_7"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_small_house_8"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_medium_house_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_medium_house_2"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_butcher_shop_1"), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_tool_smith_1"), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_fletcher_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_shepherd_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_armorer_1"), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_fisher_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_tannery_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_cartographer_house_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_library_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_mason_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_weaponsmith_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_temple_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_temple_2"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_large_farm_1", (List<StructureProcessor>)immutableList2), (Object)11), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_farm_1", (List<StructureProcessor>)immutableList2), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_farm_2", (List<StructureProcessor>)immutableList2), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_animal_pen_1"), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_animal_pen_2"), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)5)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/zombie/houses"), new Identifier("village/desert/zombie/terminators"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_3", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_4", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_5", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_6", (List<StructureProcessor>)immutableList), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_7", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_small_house_8", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_medium_house_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/houses/desert_medium_house_2", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_butcher_shop_1", (List<StructureProcessor>)immutableList), (Object)2), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_tool_smith_1", (List<StructureProcessor>)immutableList), (Object)2), (Object[])new Pair[]{new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_fletcher_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_shepherd_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_armorer_1", (List<StructureProcessor>)immutableList), (Object)1), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_fisher_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_tannery_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_cartographer_house_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_library_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_mason_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_weaponsmith_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_temple_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_temple_2", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_large_farm_1", (List<StructureProcessor>)immutableList), (Object)7), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_farm_1", (List<StructureProcessor>)immutableList), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_farm_2", (List<StructureProcessor>)immutableList), (Object)4), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_animal_pen_1", (List<StructureProcessor>)immutableList), (Object)2), new Pair((Object)new LegacySinglePoolElement("village/desert/houses/desert_animal_pen_2", (List<StructureProcessor>)immutableList), (Object)2), Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)5)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/terminators"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/terminators/terminator_01"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/terminators/terminator_02"), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/zombie/terminators"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/terminators/terminator_01"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/terminators/terminator_02"), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/desert_lamp_1"), (Object)10), (Object)new Pair((Object)new FeaturePoolElement(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.CACTUS_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.HAY_PILE_CONFIG)), (Object)4), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)10)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/zombie/decor"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/desert_lamp_1", (List<StructureProcessor>)immutableList), (Object)10), (Object)new Pair((Object)new FeaturePoolElement(Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.CACTUS_CONFIG)), (Object)4), (Object)new Pair((Object)new FeaturePoolElement(Feature.BLOCK_PILE.configure(DefaultBiomeFeatures.HAY_PILE_CONFIG)), (Object)4), (Object)Pair.of((Object)EmptyPoolElement.INSTANCE, (Object)10)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/villagers/baby"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("village/desert/zombie/villagers"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/villagers/nitwit"), (Object)1), (Object)new Pair((Object)new LegacySinglePoolElement("village/desert/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
    }
}

