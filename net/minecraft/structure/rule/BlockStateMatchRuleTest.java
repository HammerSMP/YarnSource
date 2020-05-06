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
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;

public class BlockStateMatchRuleTest
extends RuleTest {
    private final BlockState blockState;

    public BlockStateMatchRuleTest(BlockState arg) {
        this.blockState = arg;
    }

    public <T> BlockStateMatchRuleTest(Dynamic<T> dynamic) {
        this(BlockState.deserialize(dynamic.get("blockstate").orElseEmptyMap()));
    }

    @Override
    public boolean test(BlockState arg, Random random) {
        return arg == this.blockState;
    }

    @Override
    protected RuleTestType getType() {
        return RuleTestType.BLOCKSTATE_MATCH;
    }

    @Override
    protected <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("blockstate"), (Object)BlockState.serialize(dynamicOps, this.blockState).getValue())));
    }
}

