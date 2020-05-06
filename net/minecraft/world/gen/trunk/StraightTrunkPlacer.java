/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class StraightTrunkPlacer
extends TrunkPlacer {
    public StraightTrunkPlacer(int i, int j, int k) {
        super(i, j, k, TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
    }

    public <T> StraightTrunkPlacer(Dynamic<T> dynamic) {
        this(dynamic.get("base_height").asInt(0), dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0));
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld arg, Random random, int i, BlockPos arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        StraightTrunkPlacer.method_27400(arg, arg2.down());
        for (int j = 0; j < i; ++j) {
            StraightTrunkPlacer.method_27402(arg, random, arg2.up(j), set, arg3, arg4);
        }
        return ImmutableList.of((Object)new FoliagePlacer.TreeNode(arg2.up(i), 0, false));
    }
}

