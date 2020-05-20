/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;

public class AlwaysTruePosRuleTest
extends PosRuleTest {
    public static final Codec<AlwaysTruePosRuleTest> field_25006 = Codec.unit(() -> INSTANCE);
    public static final AlwaysTruePosRuleTest INSTANCE = new AlwaysTruePosRuleTest();

    private AlwaysTruePosRuleTest() {
    }

    @Override
    public boolean test(BlockPos arg, BlockPos arg2, BlockPos arg3, Random random) {
        return true;
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.ALWAYS_TRUE;
    }
}

