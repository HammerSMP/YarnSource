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
import net.minecraft.block.BlockState;
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
        boolean bl = false;
        int i = arg4.radius.method_30321(random);
        for (int j = arg3.getX() - i; j <= arg3.getX() + i; ++j) {
            for (int k = arg3.getZ() - i; k <= arg3.getZ() + i; ++k) {
                int m;
                int l = j - arg3.getX();
                if (l * l + (m = k - arg3.getZ()) * m > i * i) continue;
                block2: for (int n = arg3.getY() - arg4.ySize; n <= arg3.getY() + arg4.ySize; ++n) {
                    BlockPos lv = new BlockPos(j, n, k);
                    Block lv2 = arg.getBlockState(lv).getBlock();
                    for (BlockState lv3 : arg4.targets) {
                        if (!lv3.isOf(lv2)) continue;
                        arg.setBlockState(lv, arg4.state, 2);
                        bl = true;
                        continue block2;
                    }
                }
            }
        }
        return bl;
    }
}

