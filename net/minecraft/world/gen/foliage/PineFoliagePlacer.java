/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.foliage;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class PineFoliagePlacer
extends FoliagePlacer {
    private final int height;
    private final int randomHeight;

    public PineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
        super(i, j, k, l, FoliagePlacerType.PINE_FOLIAGE_PLACER);
        this.height = m;
        this.randomHeight = n;
    }

    public <T> PineFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0), dynamic.get("height").asInt(0), dynamic.get("height_random").asInt(0));
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        int m = 0;
        for (int n = l; n >= l - j; --n) {
            this.generate(arg, random, arg2, arg3.getCenter(), m, set, n, arg3.isGiantTrunk());
            if (m >= 1 && n == l - j + 1) {
                --m;
                continue;
            }
            if (m >= k + arg3.getFoliageRadius()) continue;
            ++m;
        }
    }

    @Override
    public int getRadius(Random random, int i) {
        return super.getRadius(random, i) + random.nextInt(i + 1);
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return this.height + random.nextInt(this.randomHeight + 1);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && l > 0;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("height"), dynamicOps.createInt(this.height)).put(dynamicOps.createString("height_random"), dynamicOps.createInt(this.randomHeight));
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

