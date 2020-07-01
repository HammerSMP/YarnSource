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
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;

public class SeaPickleFeature
extends Feature<SeaPickleFeatureConfig> {
    public SeaPickleFeature(Codec<SeaPickleFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, SeaPickleFeatureConfig arg4) {
        int i = 0;
        for (int j = 0; j < arg4.count; ++j) {
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int m = arg.getTopY(Heightmap.Type.OCEAN_FLOOR, arg3.getX() + k, arg3.getZ() + l);
            BlockPos lv = new BlockPos(arg3.getX() + k, m, arg3.getZ() + l);
            BlockState lv2 = (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (!arg.getBlockState(lv).isOf(Blocks.WATER) || !lv2.canPlaceAt(arg, lv)) continue;
            arg.setBlockState(lv, lv2, 2);
            ++i;
        }
        return i > 0;
    }
}

