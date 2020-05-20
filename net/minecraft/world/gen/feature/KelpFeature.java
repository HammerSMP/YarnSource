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
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class KelpFeature
extends Feature<DefaultFeatureConfig> {
    public KelpFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        int i = 0;
        int j = arg.getTopY(Heightmap.Type.OCEAN_FLOOR, arg4.getX(), arg4.getZ());
        BlockPos lv = new BlockPos(arg4.getX(), j, arg4.getZ());
        if (arg.getBlockState(lv).isOf(Blocks.WATER)) {
            BlockState lv2 = Blocks.KELP.getDefaultState();
            BlockState lv3 = Blocks.KELP_PLANT.getDefaultState();
            int k = 1 + random.nextInt(10);
            for (int l = 0; l <= k; ++l) {
                if (arg.getBlockState(lv).isOf(Blocks.WATER) && arg.getBlockState(lv.up()).isOf(Blocks.WATER) && lv3.canPlaceAt(arg, lv)) {
                    if (l == k) {
                        arg.setBlockState(lv, (BlockState)lv2.with(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                        ++i;
                    } else {
                        arg.setBlockState(lv, lv3, 2);
                    }
                } else if (l > 0) {
                    BlockPos lv4 = lv.down();
                    if (!lv2.canPlaceAt(arg, lv4) || arg.getBlockState(lv4.down()).isOf(Blocks.KELP)) break;
                    arg.setBlockState(lv4, (BlockState)lv2.with(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                    ++i;
                    break;
                }
                lv = lv.up();
            }
        }
        return i > 0;
    }
}

