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
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class NetherForestVegetationFeature
extends Feature<BlockPileFeatureConfig> {
    public NetherForestVegetationFeature(Codec<BlockPileFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, BlockPileFeatureConfig arg4) {
        return NetherForestVegetationFeature.method_26264(arg, random, arg3, arg4, 8, 4);
    }

    public static boolean method_26264(WorldAccess arg, Random random, BlockPos arg2, BlockPileFeatureConfig arg3, int i, int j) {
        Block lv = arg.getBlockState(arg2.down()).getBlock();
        if (!lv.isIn(BlockTags.NYLIUM)) {
            return false;
        }
        int k = arg2.getY();
        if (k < 1 || k + 1 >= 256) {
            return false;
        }
        int l = 0;
        for (int m = 0; m < i * i; ++m) {
            BlockPos lv2 = arg2.add(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
            BlockState lv3 = arg3.stateProvider.getBlockState(random, lv2);
            if (!arg.isAir(lv2) || lv2.getY() <= 0 || !lv3.canPlaceAt(arg, lv2)) continue;
            arg.setBlockState(lv2, lv3, 2);
            ++l;
        }
        return l > 0;
    }
}

