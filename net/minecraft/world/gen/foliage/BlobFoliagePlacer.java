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

public class BlobFoliagePlacer
extends FoliagePlacer {
    protected final int height;

    protected BlobFoliagePlacer(int i, int j, int k, int l, int m, FoliagePlacerType<?> arg) {
        super(i, j, k, l, arg);
        this.height = m;
    }

    public BlobFoliagePlacer(int i, int j, int k, int l, int m) {
        this(i, j, k, l, m, FoliagePlacerType.BLOB_FOLIAGE_PLACER);
    }

    public <T> BlobFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0), dynamic.get("height").asInt(0));
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        for (int m = l; m >= l - j; --m) {
            int n = Math.max(k + arg3.getFoliageRadius() - 1 - m / 2, 0);
            this.generate(arg, random, arg2, arg3.getCenter(), n, set, m, arg3.isGiantTrunk());
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && (random.nextInt(2) == 0 || j == 0);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("height"), dynamicOps.createInt(this.height));
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

