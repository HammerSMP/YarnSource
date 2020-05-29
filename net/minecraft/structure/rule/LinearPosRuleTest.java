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
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosRuleTest
extends PosRuleTest {
    public static final Codec<LinearPosRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("min_chance").withDefault((Object)Float.valueOf(0.0f)).forGetter(arg -> Float.valueOf(arg.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").withDefault((Object)Float.valueOf(0.0f)).forGetter(arg -> Float.valueOf(arg.maxChance)), (App)Codec.INT.fieldOf("min_dist").withDefault((Object)0).forGetter(arg -> arg.minDistance), (App)Codec.INT.fieldOf("max_dist").withDefault((Object)0).forGetter(arg -> arg.maxDistance)).apply((Applicative)instance, LinearPosRuleTest::new));
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;

    public LinearPosRuleTest(float f, float g, int i, int j) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        }
        this.minChance = f;
        this.maxChance = g;
        this.minDistance = i;
        this.maxDistance = j;
    }

    @Override
    public boolean test(BlockPos arg, BlockPos arg2, BlockPos arg3, Random random) {
        int i = arg2.getManhattanDistance(arg3);
        float f = random.nextFloat();
        return (double)f <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.LINEAR_POS;
    }
}

