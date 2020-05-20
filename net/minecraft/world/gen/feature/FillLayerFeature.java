/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;

public class FillLayerFeature
extends Feature<FillLayerFeatureConfig> {
    public FillLayerFeature(Function<Dynamic<?>, ? extends FillLayerFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, FillLayerFeatureConfig arg5) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int k = arg4.getX() + i;
                int l = arg4.getZ() + j;
                int m = arg5.height;
                lv.set(k, m, l);
                if (!arg.getBlockState(lv).isAir()) continue;
                arg.setBlockState(lv, arg5.state, 2);
            }
        }
        return true;
    }
}

