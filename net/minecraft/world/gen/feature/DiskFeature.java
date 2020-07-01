/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DiskFeature
extends Feature<DiskFeatureConfig> {
    public DiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DiskFeatureConfig arg4) {
        if (!arg.getFluidState(arg3).isIn(FluidTags.WATER)) {
            return false;
        }
        int i = 0;
        int j = random.nextInt(arg4.radius - 2) + 2;
        for (int k = arg3.getX() - j; k <= arg3.getX() + j; ++k) {
            for (int l = arg3.getZ() - j; l <= arg3.getZ() + j; ++l) {
                int n;
                int m = k - arg3.getX();
                if (m * m + (n = l - arg3.getZ()) * n > j * j) continue;
                block2: for (int o = arg3.getY() - arg4.ySize; o <= arg3.getY() + arg4.ySize; ++o) {
                    BlockPos lv = new BlockPos(k, o, l);
                    BlockState lv2 = arg.getBlockState(lv);
                    for (BlockState lv3 : arg4.targets) {
                        if (!lv3.isOf(lv2.getBlock())) continue;
                        arg.setBlockState(lv, arg4.state, 2);
                        ++i;
                        continue block2;
                    }
                }
            }
        }
        return i > 0;
    }
}

