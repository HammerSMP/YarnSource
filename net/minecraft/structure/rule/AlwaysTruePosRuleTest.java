/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;

public class AlwaysTruePosRuleTest
extends PosRuleTest {
    public static final AlwaysTruePosRuleTest INSTANCE = new AlwaysTruePosRuleTest();

    private AlwaysTruePosRuleTest() {
    }

    @Override
    public boolean test(BlockPos arg, BlockPos arg2, BlockPos arg3, Random random) {
        return true;
    }

    @Override
    protected PosRuleTestType getType() {
        return PosRuleTestType.ALWAYS_TRUE;
    }

    @Override
    protected <T> Dynamic<T> serializeContents(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.emptyMap());
    }
}

