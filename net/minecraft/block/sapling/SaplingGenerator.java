/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.sapling;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class SaplingGenerator {
    @Nullable
    protected abstract ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random var1, boolean var2);

    public boolean generate(ServerWorld arg, ChunkGenerator<?> arg2, BlockPos arg3, BlockState arg4, Random random) {
        ConfiguredFeature<TreeFeatureConfig, ?> lv = this.createTreeFeature(random, this.method_24282(arg, arg3));
        if (lv == null) {
            return false;
        }
        arg.setBlockState(arg3, Blocks.AIR.getDefaultState(), 4);
        ((TreeFeatureConfig)lv.config).ignoreFluidCheck();
        if (lv.generate(arg, arg.getStructureAccessor(), arg2, random, arg3)) {
            return true;
        }
        arg.setBlockState(arg3, arg4, 4);
        return false;
    }

    private boolean method_24282(IWorld arg, BlockPos arg2) {
        for (BlockPos lv : BlockPos.Mutable.iterate(arg2.down().north(2).west(2), arg2.up().south(2).east(2))) {
            if (!arg.getBlockState(lv).isIn(BlockTags.FLOWERS)) continue;
            return true;
        }
        return false;
    }
}

