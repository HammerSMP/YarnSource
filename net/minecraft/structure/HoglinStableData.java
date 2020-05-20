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
import net.minecraft.structure.BastionData;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

public class HoglinStableData {
    public static void init() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)BastionData.PROCESSOR_RULE)));
        ImmutableList immutableList2 = ImmutableList.of((Object)new RuleStructureProcessor((List<StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.1f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)BastionData.PROCESSOR_RULE)));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/origin"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/air_base", (List<StructureProcessor>)ImmutableList.of()), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/starting_pieces"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_3", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/starting_stairs_4", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/mirrored_starting_pieces"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_0_mirrored", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_1_mirrored", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_2_mirrored", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_3_mirrored", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/starting_pieces/stairs_4_mirrored", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/wall_bases"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/walls/wall_base", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/walls"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/walls/side_wall_0", (List<StructureProcessor>)immutableList2), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/walls/side_wall_1", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/stairs"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_0_mirrored", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_3", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_1_4", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_3", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_2_4", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_0", (List<StructureProcessor>)immutableList), (Object)1), (Object[])new Pair[]{Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_1", (List<StructureProcessor>)immutableList), (Object)1), Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_2", (List<StructureProcessor>)immutableList), (Object)1), Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_3", (List<StructureProcessor>)immutableList), (Object)1), Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/stairs/stairs_3_4", (List<StructureProcessor>)immutableList), (Object)1)}), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/small_stables/inner"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/inner_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/small_stables/outer"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/small_stables/outer_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/large_stables/inner"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/inner_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/large_stables/outer"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/large_stables/outer_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/posts"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/posts/stair_post", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/posts/end_post", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/ramparts"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/ramparts/ramparts_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/ramparts/ramparts_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/ramparts/ramparts_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/rampart_plates"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/rampart_plates/rampart_plate_1", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/hoglin_stable/connectors"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/hoglin_stable/connectors/end_post_connector", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
    }
}
