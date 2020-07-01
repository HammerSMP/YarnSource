/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class LargeTreeSaplingGenerator
extends SaplingGenerator {
    @Override
    public boolean generate(ServerWorld arg, ChunkGenerator arg2, BlockPos arg3, BlockState arg4, Random random) {
        for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (!LargeTreeSaplingGenerator.canGenerateLargeTree(arg4, arg, arg3, i, j)) continue;
                return this.generateLargeTree(arg, arg2, arg3, arg4, random, i, j);
            }
        }
        return super.generate(arg, arg2, arg3, arg4, random);
    }

    @Nullable
    protected abstract ConfiguredFeature<TreeFeatureConfig, ?> createLargeTreeFeature(Random var1);

    public boolean generateLargeTree(ServerWorld arg, ChunkGenerator arg2, BlockPos arg3, BlockState arg4, Random random, int i, int j) {
        ConfiguredFeature<TreeFeatureConfig, ?> lv = this.createLargeTreeFeature(random);
        if (lv == null) {
            return false;
        }
        ((TreeFeatureConfig)lv.config).ignoreFluidCheck();
        BlockState lv2 = Blocks.AIR.getDefaultState();
        arg.setBlockState(arg3.add(i, 0, j), lv2, 4);
        arg.setBlockState(arg3.add(i + 1, 0, j), lv2, 4);
        arg.setBlockState(arg3.add(i, 0, j + 1), lv2, 4);
        arg.setBlockState(arg3.add(i + 1, 0, j + 1), lv2, 4);
        if (lv.generate(arg, arg2, random, arg3.add(i, 0, j))) {
            return true;
        }
        arg.setBlockState(arg3.add(i, 0, j), arg4, 4);
        arg.setBlockState(arg3.add(i + 1, 0, j), arg4, 4);
        arg.setBlockState(arg3.add(i, 0, j + 1), arg4, 4);
        arg.setBlockState(arg3.add(i + 1, 0, j + 1), arg4, 4);
        return false;
    }

    public static boolean canGenerateLargeTree(BlockState arg, BlockView arg2, BlockPos arg3, int i, int j) {
        Block lv = arg.getBlock();
        return lv == arg2.getBlockState(arg3.add(i, 0, j)).getBlock() && lv == arg2.getBlockState(arg3.add(i + 1, 0, j)).getBlock() && lv == arg2.getBlockState(arg3.add(i, 0, j + 1)).getBlock() && lv == arg2.getBlockState(arg3.add(i + 1, 0, j + 1)).getBlock();
    }
}

