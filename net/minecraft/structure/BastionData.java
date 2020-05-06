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
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

public class BastionData {
    public static final StructureProcessorRule PROCESSOR_RULE = new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.BLACKSTONE, 0.01f), AlwaysTrueRuleTest.INSTANCE, Blocks.GILDED_BLACKSTONE.getDefaultState());

    public static void init() {
    }

    static {
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/mobs/piglin"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/melee_piglin"), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/sword_piglin"), (Object)4), (Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/crossbow_piglin"), (Object)4), (Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/empty"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/mobs/hoglin"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/hoglin"), (Object)2), (Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/empty"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/blocks/gold"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/blocks/air"), (Object)3), (Object)Pair.of((Object)new SinglePoolElement("bastion/blocks/gold"), (Object)1)), StructurePool.Projection.RIGID));
        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(new Identifier("bastion/mobs/piglin_melee"), new Identifier("empty"), (List<Pair<StructurePoolElement, Integer>>)ImmutableList.of((Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/melee_piglin_always"), (Object)1), (Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/melee_piglin"), (Object)5), (Object)Pair.of((Object)new SinglePoolElement("bastion/mobs/sword_piglin"), (Object)1)), StructurePool.Projection.RIGID));
    }
}

