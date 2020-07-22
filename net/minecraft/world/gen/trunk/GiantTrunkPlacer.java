/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class GiantTrunkPlacer
extends TrunkPlacer {
    public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> GiantTrunkPlacer.method_28904(instance).apply((Applicative)instance, GiantTrunkPlacer::new));

    public GiantTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.GIANT_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        BlockPos lv = pos.down();
        GiantTrunkPlacer.method_27400(world, lv);
        GiantTrunkPlacer.method_27400(world, lv.east());
        GiantTrunkPlacer.method_27400(world, lv.south());
        GiantTrunkPlacer.method_27400(world, lv.south().east());
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int j = 0; j < trunkHeight; ++j) {
            GiantTrunkPlacer.method_27399(world, random, lv2, set, arg3, arg4, pos, 0, j, 0);
            if (j >= trunkHeight - 1) continue;
            GiantTrunkPlacer.method_27399(world, random, lv2, set, arg3, arg4, pos, 1, j, 0);
            GiantTrunkPlacer.method_27399(world, random, lv2, set, arg3, arg4, pos, 1, j, 1);
            GiantTrunkPlacer.method_27399(world, random, lv2, set, arg3, arg4, pos, 0, j, 1);
        }
        return ImmutableList.of((Object)new FoliagePlacer.TreeNode(pos.up(trunkHeight), 0, true));
    }

    private static void method_27399(ModifiableTestableWorld arg, Random random, BlockPos.Mutable arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4, BlockPos arg5, int i, int j, int k) {
        arg2.set(arg5, i, j, k);
        GiantTrunkPlacer.method_27401(arg, random, arg2, set, arg3, arg4);
    }
}

