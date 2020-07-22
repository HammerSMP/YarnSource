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
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class ForestRockFeature
extends Feature<SingleStateFeatureConfig> {
    public ForestRockFeature(Codec<SingleStateFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, SingleStateFeatureConfig arg4) {
        Block lv;
        while (arg3.getY() > 3 && (arg.isAir(arg3.down()) || !ForestRockFeature.isSoil(lv = arg.getBlockState(arg3.down()).getBlock()) && !ForestRockFeature.isStone(lv))) {
            arg3 = arg3.down();
        }
        if (arg3.getY() <= 3) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            int j = random.nextInt(2);
            int k = random.nextInt(2);
            int l = random.nextInt(2);
            float f = (float)(j + k + l) * 0.333f + 0.5f;
            for (BlockPos lv2 : BlockPos.iterate(arg3.add(-j, -k, -l), arg3.add(j, k, l))) {
                if (!(lv2.getSquaredDistance(arg3) <= (double)(f * f))) continue;
                arg.setBlockState(lv2, arg4.state, 4);
            }
            arg3 = arg3.add(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }
        return true;
    }
}

