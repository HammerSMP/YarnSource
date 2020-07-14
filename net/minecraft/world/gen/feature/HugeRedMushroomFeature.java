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
    protected void generateCap(WorldAccess world, Random random, BlockPos start, int y, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
        for (int j = y - 3; j <= y; ++j) {
            int k = j < y ? config.capSize : config.capSize - 1;
            int l = config.capSize - 2;
            for (int m = -k; m <= k; ++m) {
                for (int n = -k; n <= k; ++n) {
                    boolean bl6;
                    boolean bl = m == -k;
                    boolean bl2 = m == k;
                    boolean bl3 = n == -k;
                    boolean bl4 = n == k;
                    boolean bl5 = bl || bl2;
                    boolean bl7 = bl6 = bl3 || bl4;
                    if (j < y && bl5 == bl6) continue;
                    mutable.set(start, m, j, n);
                    if (world.getBlockState(mutable).isOpaqueFullCube(world, mutable)) continue;
                    this.setBlockState(world, mutable, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)config.capProvider.getBlockState(random, start).with(MushroomBlock.UP, j >= y - 1)).with(MushroomBlock.WEST, m < -l)).with(MushroomBlock.EAST, m > l)).with(MushroomBlock.NORTH, n < -l)).with(MushroomBlock.SOUTH, n > l));
                }
            }
        }
    }

    @Override
    protected int getCapSize(int i, int j, int capSize, int y) {
        int m = 0;
        if (y < j && y >= j - 3) {
            m = capSize;
        } else if (y == j) {
            m = capSize;
        }
        return m;
    }
}

