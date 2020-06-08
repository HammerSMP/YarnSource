/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;

public class RandomBlockStateMatchRuleTest
extends RuleTest {
    public static final Codec<RandomBlockStateMatchRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("block_state").forGetter(arg -> arg.blockState), (App)Codec.FLOAT.fieldOf("probability").forGetter(arg -> Float.valueOf(arg.probability))).apply((Applicative)instance, RandomBlockStateMatchRuleTest::new));
    private final BlockState blockState;
    private final float probability;

    public RandomBlockStateMatchRuleTest(BlockState arg, float f) {
        this.blockState = arg;
        this.probability = f;
    }

    @Override
    public boolean test(BlockState arg, Random random) {
        return arg == this.blockState && random.nextFloat() < this.probability;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.RANDOM_BLOCKSTATE_MATCH;
    }
}

