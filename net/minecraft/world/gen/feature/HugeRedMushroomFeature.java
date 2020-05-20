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
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public class HugeRedMushroomFeature
extends HugeMushroomFeature {
    public HugeRedMushroomFeature(Codec<HugeMushroomFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected void generateCap(WorldAccess arg, Random random, BlockPos arg2, int i, BlockPos.Mutable arg3, HugeMushroomFeatureConfig arg4) {
        for (int j = i - 3; j <= i; ++j) {
            int k = j < i ? arg4.capSize : arg4.capSize - 1;
            int l = arg4.capSize - 2;
            for (int m = -k; m <= k; ++m) {
                for (int n = -k; n <= k; ++n) {
                    boolean bl6;
                    boolean bl = m == -k;
                    boolean bl2 = m == k;
                    boolean bl3 = n == -k;
                    boolean bl4 = n == k;
                    boolean bl5 = bl || bl2;
                    boolean bl7 = bl6 = bl3 || bl4;
                    if (j < i && bl5 == bl6) continue;
                    arg3.set(arg2, m, j, n);
                    if (arg.getBlockState(arg3).isOpaqueFullCube(arg, arg3)) continue;
                    this.setBlockState(arg, arg3, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)arg4.capProvider.getBlockState(random, arg2).with(MushroomBlock.UP, j >= i - 1)).with(MushroomBlock.WEST, m < -l)).with(MushroomBlock.EAST, m > l)).with(MushroomBlock.NORTH, n < -l)).with(MushroomBlock.SOUTH, n > l));
                }
            }
        }
    }

    @Override
    protected int getCapSize(int i, int j, int k, int l) {
        int m = 0;
        if (l < j && l >= j - 3) {
            m = k;
        } else if (l == j) {
            m = k;
        }
        return m;
    }
}

