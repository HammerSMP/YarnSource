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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class DefaultFlowerFeature
extends FlowerFeature<RandomPatchFeatureConfig> {
    public DefaultFlowerFeature(Codec<RandomPatchFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean isPosValid(WorldAccess arg, BlockPos arg2, RandomPatchFeatureConfig arg3) {
        return !arg3.blacklist.contains(arg.getBlockState(arg2));
    }

    @Override
    public int getFlowerAmount(RandomPatchFeatureConfig arg) {
        return arg.tries;
    }

    @Override
    public BlockPos getPos(Random random, BlockPos arg, RandomPatchFeatureConfig arg2) {
        return arg.add(random.nextInt(arg2.spreadX) - random.nextInt(arg2.spreadX), random.nextInt(arg2.spreadY) - random.nextInt(arg2.spreadY), random.nextInt(arg2.spreadZ) - random.nextInt(arg2.spreadZ));
    }

    @Override
    public BlockState getFlowerState(Random random, BlockPos arg, RandomPatchFeatureConfig arg2) {
        return arg2.stateProvider.getBlockState(random, arg);
    }
}

