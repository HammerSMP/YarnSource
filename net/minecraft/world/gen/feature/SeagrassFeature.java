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
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;

public class SeagrassFeature
extends Feature<SeagrassFeatureConfig> {
    public SeagrassFeature(Codec<SeagrassFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, SeagrassFeatureConfig arg5) {
        int i = 0;
        for (int j = 0; j < arg5.count; ++j) {
            BlockState lv2;
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int m = arg.getTopY(Heightmap.Type.OCEAN_FLOOR, arg4.getX() + k, arg4.getZ() + l);
            BlockPos lv = new BlockPos(arg4.getX() + k, m, arg4.getZ() + l);
            if (!arg.getBlockState(lv).isOf(Blocks.WATER)) continue;
            boolean bl = random.nextDouble() < arg5.tallSeagrassProbability;
            BlockState blockState = lv2 = bl ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
            if (!lv2.canPlaceAt(arg, lv)) continue;
            if (bl) {
                BlockState lv3 = (BlockState)lv2.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
                BlockPos lv4 = lv.up();
                if (arg.getBlockState(lv4).isOf(Blocks.WATER)) {
                    arg.setBlockState(lv, lv2, 2);
                    arg.setBlockState(lv4, lv3, 2);
                }
            } else {
                arg.setBlockState(lv, lv2, 2);
            }
            ++i;
        }
        return i > 0;
    }
}

