/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ForestRockFeatureConfig;

public class ForestRockFeature
extends Feature<ForestRockFeatureConfig> {
    public ForestRockFeature(Codec<ForestRockFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, ForestRockFeatureConfig arg4) {
        Block lv;
        while (arg3.getY() > 3 && (arg.isAir(arg3.down()) || !ForestRockFeature.isSoil(lv = arg.getBlockState(arg3.down()).getBlock()) && !ForestRockFeature.isStone(lv))) {
            arg3 = arg3.down();
        }
        if (arg3.getY() <= 3) {
            return false;
        }
        int i = arg4.startRadius;
        for (int j = 0; i >= 0 && j < 3; ++j) {
            int k = i + random.nextInt(2);
            int l = i + random.nextInt(2);
            int m = i + random.nextInt(2);
            float f = (float)(k + l + m) * 0.333f + 0.5f;
            for (BlockPos lv2 : BlockPos.iterate(arg3.add(-k, -l, -m), arg3.add(k, l, m))) {
                if (!(lv2.getSquaredDistance(arg3) <= (double)(f * f))) continue;
                arg.setBlockState(lv2, arg4.state, 4);
            }
            arg3 = arg3.add(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
        }
        return true;
    }
}

