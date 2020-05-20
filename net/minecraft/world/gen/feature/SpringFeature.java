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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

public class SpringFeature
extends Feature<SpringFeatureConfig> {
    public SpringFeature(Codec<SpringFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, SpringFeatureConfig arg5) {
        if (!arg5.validBlocks.contains(arg.getBlockState(arg4.up()).getBlock())) {
            return false;
        }
        if (arg5.requiresBlockBelow && !arg5.validBlocks.contains(arg.getBlockState(arg4.down()).getBlock())) {
            return false;
        }
        BlockState lv = arg.getBlockState(arg4);
        if (!lv.isAir() && !arg5.validBlocks.contains(lv.getBlock())) {
            return false;
        }
        int i = 0;
        int j = 0;
        if (arg5.validBlocks.contains(arg.getBlockState(arg4.west()).getBlock())) {
            ++j;
        }
        if (arg5.validBlocks.contains(arg.getBlockState(arg4.east()).getBlock())) {
            ++j;
        }
        if (arg5.validBlocks.contains(arg.getBlockState(arg4.north()).getBlock())) {
            ++j;
        }
        if (arg5.validBlocks.contains(arg.getBlockState(arg4.south()).getBlock())) {
            ++j;
        }
        if (arg5.validBlocks.contains(arg.getBlockState(arg4.down()).getBlock())) {
            ++j;
        }
        int k = 0;
        if (arg.isAir(arg4.west())) {
            ++k;
        }
        if (arg.isAir(arg4.east())) {
            ++k;
        }
        if (arg.isAir(arg4.north())) {
            ++k;
        }
        if (arg.isAir(arg4.south())) {
            ++k;
        }
        if (arg.isAir(arg4.down())) {
            ++k;
        }
        if (j == arg5.rockCount && k == arg5.holeCount) {
            arg.setBlockState(arg4, arg5.state.getBlockState(), 2);
            arg.getFluidTickScheduler().schedule(arg4, arg5.state.getFluid(), 0);
            ++i;
        }
        return i > 0;
    }
}

