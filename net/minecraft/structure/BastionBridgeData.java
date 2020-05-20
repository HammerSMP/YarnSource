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

public class BastionBridgeData {
    public static void init() {
    }

    static {
        ImmutableList immutableList = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.4f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 0.01f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 1.0E-4f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)BastionData.PROCESSOR_RULE)));
        ImmutableList immutableList2 = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)BastionData.PROCESSOR_RULE)));
        ImmutableList immutableList3 = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.CHISELED_POLISHED_BLACKSTONE, 0.5f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GOLD_BLOCK, 0.6f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)BastionData.PROCESSOR_RULE)));
        ImmutableList immutableList4 = ImmutableList.of((Object)new RuleStructureProcessor((List<? extends StructureProcessorRule>)ImmutableList.of((Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, 0.3f), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState()), (Object)new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 1.0E-4f), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()))));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/start"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/starting_pieces/entrance_base", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/starting_pieces"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/starting_pieces/entrance", (List<StructureProcessor>)immutableList3), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/starting_pieces/entrance_face", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/bridge_pieces"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/bridge_pieces/bridge", (List<StructureProcessor>)immutableList4), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/legs"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/legs/leg_0", (List<StructureProcessor>)immutableList2), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/legs/leg_1", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/walls"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/walls/wall_base_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/walls/wall_base_1", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/ramparts"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/ramparts/rampart_0", (List<StructureProcessor>)immutableList), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/ramparts/rampart_1", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/rampart_plates"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/rampart_plates/plate_0", (List<StructureProcessor>)immutableList), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/bridge/connectors"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/connectors/back_bridge_top", (List<StructureProcessor>)immutableList2), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/bridge/connectors/back_bridge_bottom", (List<StructureProcessor>)immutableList2), (Object)1)), StructurePool.Projection.RIGID));
    }
}

