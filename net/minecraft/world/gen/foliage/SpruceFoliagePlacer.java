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

public class SpruceFoliagePlacer
extends FoliagePlacer {
    private final int trunkHeight;
    private final int randomTrunkHeight;

    public SpruceFoliagePlacer(int i, int j, int k, int l, int m, int n) {
        super(i, j, k, l, FoliagePlacerType.SPRUCE_FOLIAGE_PLACER);
        this.trunkHeight = m;
        this.randomTrunkHeight = n;
    }

    public <T> SpruceFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0), dynamic.get("trunk_height").asInt(0), dynamic.get("trunk_height_random").asInt(0));
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        BlockPos lv = arg3.getCenter();
        int m = random.nextInt(2);
        int n = 1;
        int o = 0;
        for (int p = l; p >= -j; --p) {
            this.generate(arg, random, arg2, lv, m, set, p, arg3.isGiantTrunk());
            if (m >= n) {
                m = o;
                o = 1;
                n = Math.min(n + 1, k + arg3.getFoliageRadius());
                continue;
            }
            ++m;
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return i - this.trunkHeight - random.nextInt(this.randomTrunkHeight + 1);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        return i == l && k == l && l > 0;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("trunk_height"), dynamicOps.createInt(this.trunkHeight)).put(dynamicOps.createString("trunk_height_random"), dynamicOps.createInt(this.randomTrunkHeight));
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

