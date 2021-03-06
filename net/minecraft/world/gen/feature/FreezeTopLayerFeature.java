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
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class FreezeTopLayerFeature
extends Feature<DefaultFeatureConfig> {
    public FreezeTopLayerFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DefaultFeatureConfig arg4) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = arg3.getX() + i;
                int l = arg3.getZ() + j;
                int m = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);
                lv.set(k, m, l);
                lv2.set(lv).move(Direction.DOWN, 1);
                Biome lv3 = arg.getBiome(lv);
                if (lv3.canSetIce(arg, lv2, false)) {
                    arg.setBlockState(lv2, Blocks.ICE.getDefaultState(), 2);
                }
                if (!lv3.canSetSnow(arg, lv)) continue;
                arg.setBlockState(lv, Blocks.SNOW.getDefaultState(), 2);
                BlockState lv4 = arg.getBlockState(lv2);
                if (!lv4.contains(SnowyBlock.SNOWY)) continue;
                arg.setBlockState(lv2, (BlockState)lv4.with(SnowyBlock.SNOWY, true), 2);
            }
        }
        return true;
    }
}

