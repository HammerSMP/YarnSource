/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    private final int heightRange;
    private final int crownHeight;

    public MegaPineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
        super(i, j, k, l, FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER);
        this.heightRange = m;
        this.crownHeight = n;
    }

    public <T> MegaPineFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0), dynamic.get("height_rand").asInt(0), dynamic.get("crown_height").asInt(0));
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        BlockPos lv = arg3.getCenter();
        int m = 0;
        for (int n = lv.getY() - j + l; n <= lv.getY() + l; ++n) {
            int r;
            int o = lv.getY() - n;
            int p = k + arg3.getFoliageRadius() + MathHelper.floor((float)o / (float)j * 3.5f);
            if (o > 0 && p == m && (n & 1) == 0) {
                int q = p + 1;
            } else {
                r = p;
            }
            this.generate(arg, random, arg2, new BlockPos(lv.getX(), n, lv.getZ()), r, set, 0, arg3.isGiantTrunk());
            m = p;
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return random.nextInt(this.heightRange + 1) + this.crownHeight;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        if (i + k >= 7) {
            return true;
        }
        return i * i + k * k > l * l;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("height_rand"), dynamicOps.createInt(this.heightRange));
        builder.put(dynamicOps.createString("crown_height"), dynamicOps.createInt(this.crownHeight));
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

