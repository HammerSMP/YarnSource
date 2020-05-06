/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ForestRockFeature
extends Feature<BoulderFeatureConfig> {
    public ForestRockFeature(Function<Dynamic<?>, ? extends BoulderFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, BoulderFeatureConfig arg5) {
        Block lv;
        while (arg4.getY() > 3 && (arg.isAir(arg4.down()) || !ForestRockFeature.isDirt(lv = arg.getBlockState(arg4.down()).getBlock()) && !ForestRockFeature.isStone(lv))) {
            arg4 = arg4.down();
        }
        if (arg4.getY() <= 3) {
            return false;
        }
        int i = arg5.startRadius;
        for (int j = 0; i >= 0 && j < 3; ++j) {
            int k = i + random.nextInt(2);
            int l = i + random.nextInt(2);
            int m = i + random.nextInt(2);
            float f = (float)(k + l + m) * 0.333f + 0.5f;
            for (BlockPos lv2 : BlockPos.iterate(arg4.add(-k, -l, -m), arg4.add(k, l, m))) {
                if (!(lv2.getSquaredDistance(arg4) <= (double)(f * f))) continue;
                arg.setBlockState(lv2, arg5.state, 4);
            }
            arg4 = arg4.add(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
        }
        return true;
    }
}

