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

public class DarkOakFoliagePlacer
extends FoliagePlacer {
    public DarkOakFoliagePlacer(int i, int j, int k, int l) {
        super(i, j, k, l, FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER);
    }

    public <T> DarkOakFoliagePlacer(Dynamic<T> dynamic) {
        this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0), dynamic.get("offset").asInt(0), dynamic.get("offset_random").asInt(0));
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l) {
        BlockPos lv = arg3.getCenter().up(l);
        boolean bl = arg3.isGiantTrunk();
        if (bl) {
            this.generate(arg, random, arg2, lv, k + 2, set, -1, bl);
            this.generate(arg, random, arg2, lv, k + 3, set, 0, bl);
            this.generate(arg, random, arg2, lv, k + 2, set, 1, bl);
            if (random.nextBoolean()) {
                this.generate(arg, random, arg2, lv, k, set, 2, bl);
            }
        } else {
            this.generate(arg, random, arg2, lv, k + 2, set, -1, bl);
            this.generate(arg, random, arg2, lv, k + 1, set, 0, bl);
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return 4;
    }

    @Override
    protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
        if (!(j != 0 || !bl || i != -l && i < l || k != -l && k < l)) {
            return true;
        }
        return super.method_27387(random, i, j, k, l, bl);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        if (j == -1 && !bl) {
            return i == l && k == l;
        }
        if (j == 1) {
            return i + k > l * 2 - 2;
        }
        return false;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

