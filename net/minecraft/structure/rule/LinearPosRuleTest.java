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
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosRuleTest
extends PosRuleTest {
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

    public <T> LinearPosRuleTest(Dynamic<T> dynamic) {
        this(dynamic.get("min_chance").asFloat(0.0f), dynamic.get("max_chance").asFloat(0.0f), dynamic.get("min_dist").asInt(0), dynamic.get("max_dist").asInt(0));
    }

    @Override
    public boolean test(BlockPos arg, BlockPos arg2, BlockPos arg3, Random random) {
        int i = arg2.getManhattanDistance(arg3);
        float f = random.nextFloat();
        return (double)f <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType getType() {
        return PosRuleTestType.LINEAR_POS;
    }

    @Override
    protected <T> Dynamic<T> serializeContents(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("min_chance"), (Object)dynamicOps.createFloat(this.minChance), (Object)dynamicOps.createString("max_chance"), (Object)dynamicOps.createFloat(this.maxChance), (Object)dynamicOps.createString("min_dist"), (Object)dynamicOps.createFloat((float)this.minDistance), (Object)dynamicOps.createString("max_dist"), (Object)dynamicOps.createFloat((float)this.maxDistance))));
    }
}

