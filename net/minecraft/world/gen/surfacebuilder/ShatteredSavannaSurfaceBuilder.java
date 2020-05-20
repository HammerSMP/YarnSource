/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class ShatteredSavannaSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public ShatteredSavannaSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        if (d > 1.75) {
            SurfaceBuilder.DEFAULT.generate(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, SurfaceBuilder.STONE_CONFIG);
        } else if (d > -0.5) {
            SurfaceBuilder.DEFAULT.generate(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, SurfaceBuilder.COARSE_DIRT_CONFIG);
        } else {
            SurfaceBuilder.DEFAULT.generate(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, SurfaceBuilder.GRASS_CONFIG);
        }
    }
}

