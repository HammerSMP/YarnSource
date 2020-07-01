/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;

public class FillLayerFeature
extends Feature<FillLayerFeatureConfig> {
    public FillLayerFeature(Codec<FillLayerFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, FillLayerFeatureConfig arg4) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = arg3.getX() + i;
                int l = arg3.getZ() + j;
                int m = arg4.height;
                lv.set(k, m, l);
                if (!arg.getBlockState(lv).isAir()) continue;
                arg.setBlockState(lv, arg4.state, 2);
            }
        }
        return true;
    }
}

