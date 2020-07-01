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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

public class SpringFeature
extends Feature<SpringFeatureConfig> {
    public SpringFeature(Codec<SpringFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, SpringFeatureConfig arg4) {
        if (!arg4.validBlocks.contains(arg.getBlockState(arg3.up()).getBlock())) {
            return false;
        }
        if (arg4.requiresBlockBelow && !arg4.validBlocks.contains(arg.getBlockState(arg3.down()).getBlock())) {
            return false;
        }
        BlockState lv = arg.getBlockState(arg3);
        if (!lv.isAir() && !arg4.validBlocks.contains(lv.getBlock())) {
            return false;
        }
        int i = 0;
        int j = 0;
        if (arg4.validBlocks.contains(arg.getBlockState(arg3.west()).getBlock())) {
            ++j;
        }
        if (arg4.validBlocks.contains(arg.getBlockState(arg3.east()).getBlock())) {
            ++j;
        }
        if (arg4.validBlocks.contains(arg.getBlockState(arg3.north()).getBlock())) {
            ++j;
        }
        if (arg4.validBlocks.contains(arg.getBlockState(arg3.south()).getBlock())) {
            ++j;
        }
        if (arg4.validBlocks.contains(arg.getBlockState(arg3.down()).getBlock())) {
            ++j;
        }
        int k = 0;
        if (arg.isAir(arg3.west())) {
            ++k;
        }
        if (arg.isAir(arg3.east())) {
            ++k;
        }
        if (arg.isAir(arg3.north())) {
            ++k;
        }
        if (arg.isAir(arg3.south())) {
            ++k;
        }
        if (arg.isAir(arg3.down())) {
            ++k;
        }
        if (j == arg4.rockCount && k == arg4.holeCount) {
            arg.setBlockState(arg3, arg4.state.getBlockState(), 2);
            arg.getFluidTickScheduler().schedule(arg3, arg4.state.getFluid(), 0);
            ++i;
        }
        return i > 0;
    }
}

