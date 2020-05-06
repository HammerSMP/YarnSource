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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class AxisAlignedLinearPosRuleTest
extends PosRuleTest {
    private final float minChance;
    private final float maxChance;
    private final int minDistance;
    private final int maxDistance;
    private final Direction.Axis axis;

    public AxisAlignedLinearPosRuleTest(float f, float g, int i, int j, Direction.Axis arg) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        }
        this.minChance = f;
        this.maxChance = g;
        this.minDistance = i;
        this.maxDistance = j;
        this.axis = arg;
    }

    public <T> AxisAlignedLinearPosRuleTest(Dynamic<T> dynamic) {
        this(dynamic.get("min_chance").asFloat(0.0f), dynamic.get("max_chance").asFloat(0.0f), dynamic.get("min_dist").asInt(0), dynamic.get("max_dist").asInt(0), Direction.Axis.fromName(dynamic.get("axis").asString("y")));
    }

    @Override
    public boolean test(BlockPos arg, BlockPos arg2, BlockPos arg3, Random random) {
        Direction lv = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        float f = Math.abs((arg2.getX() - arg3.getX()) * lv.getOffsetX());
        float g = Math.abs((arg2.getY() - arg3.getY()) * lv.getOffsetY());
        float h = Math.abs((arg2.getZ() - arg3.getZ()) * lv.getOffsetZ());
        int i = (int)(f + g + h);
        float j = random.nextFloat();
        return (double)j <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.getLerpProgress(i, this.minDistance, this.maxDistance));
    }

    @Override
    protected PosRuleTestType getType() {
        return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS;
    }

    @Override
    protected <T> Dynamic<T> serializeContents(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("min_chance"), (Object)dynamicOps.createFloat(this.minChance), (Object)dynamicOps.createString("max_chance"), (Object)dynamicOps.createFloat(this.maxChance), (Object)dynamicOps.createString("min_dist"), (Object)dynamicOps.createFloat((float)this.minDistance), (Object)dynamicOps.createString("max_dist"), (Object)dynamicOps.createFloat((float)this.maxDistance), (Object)dynamicOps.createString("axis"), (Object)dynamicOps.createString(this.axis.getName()))));
    }
}

