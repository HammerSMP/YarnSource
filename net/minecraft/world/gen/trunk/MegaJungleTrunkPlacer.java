/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class MegaJungleTrunkPlacer
extends GiantTrunkPlacer {
    public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> MegaJungleTrunkPlacer.method_28904(instance).apply((Applicative)instance, MegaJungleTrunkPlacer::new));

    public MegaJungleTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        ArrayList list = Lists.newArrayList();
        list.addAll(super.generate(world, random, trunkHeight, pos, set, arg3, arg4));
        for (int j = trunkHeight - 2 - random.nextInt(4); j > trunkHeight / 2; j -= 2 + random.nextInt(4)) {
            float f = random.nextFloat() * ((float)Math.PI * 2);
            int k = 0;
            int l = 0;
            for (int m = 0; m < 5; ++m) {
                k = (int)(1.5f + MathHelper.cos(f) * (float)m);
                l = (int)(1.5f + MathHelper.sin(f) * (float)m);
                BlockPos lv = pos.add(k, j - 3 + m / 2, l);
                MegaJungleTrunkPlacer.method_27402(world, random, lv, set, arg3, arg4);
            }
            list.add(new FoliagePlacer.TreeNode(pos.add(k, j, l), -2, false));
        }
        return list;
    }
}

