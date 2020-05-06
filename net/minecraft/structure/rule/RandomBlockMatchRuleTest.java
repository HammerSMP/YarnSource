/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RandomBlockMatchRuleTest
extends RuleTest {
    private final Block block;
    private final float probability;

    public RandomBlockMatchRuleTest(Block arg, float f) {
        this.block = arg;
        this.probability = f;
    }

    public <T> RandomBlockMatchRuleTest(Dynamic<T> dynamic) {
        this(Registry.BLOCK.get(new Identifier(dynamic.get("block").asString(""))), dynamic.get("probability").asFloat(1.0f));
    }

    @Override
    public boolean test(BlockState arg, Random random) {
        return arg.isOf(this.block) && random.nextFloat() < this.probability;
    }

    @Override
    protected RuleTestType getType() {
        return RuleTestType.RANDOM_BLOCK_MATCH;
    }

    @Override
    protected <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("block"), (Object)dynamicOps.createString(Registry.BLOCK.getId(this.block).toString()), (Object)dynamicOps.createString("probability"), (Object)dynamicOps.createFloat(this.probability))));
    }
}

