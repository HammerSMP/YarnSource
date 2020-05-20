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

public class BastionUnitsData {
    public static void init() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)BastionData.PROCESSOR_RULE)));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/base"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/air_base", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/center_pieces"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/center_pieces/center_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/center_pieces/center_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/center_pieces/center_2", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/pathways"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/pathways/pathway_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/pathways/pathway_wall_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/walls/wall_bases"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/walls/wall_base", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/walls/connected_wall", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/stages/stage_0"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_0_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_0_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_0_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_0_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/stages/stage_1"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_1_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_1_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_1_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_1_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/stages/rot/stage_1"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/rot/stage_1_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/stages/stage_2"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_2_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_2_1", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/stages/stage_3"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_3_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_3_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_3_2", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/stages/stage_3_3", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/fillers/stage_0"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/fillers/stage_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/edges"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/edges/edge_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/wall_units"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/wall_units/unit_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/edge_wall_units"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/wall_units/edge_0_large", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/ramparts"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/ramparts/ramparts_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/ramparts/ramparts_1", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/units/ramparts/ramparts_2", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/large_ramparts"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/ramparts/ramparts_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/units/rampart_plates"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/units/rampart_plates/plate_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
    }
}

