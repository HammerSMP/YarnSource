/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.surfacebuilder;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
    public final SurfaceBuilder<SC> surfaceBuilder;
    public final SC config;

    public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> arg, SC arg2) {
        this.surfaceBuilder = arg;
        this.config = arg2;
    }

    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m) {
        this.surfaceBuilder.generate(random, arg, arg2, i, j, k, d, arg3, arg4, l, m, this.config);
    }

    public void initSeed(long l) {
        this.surfaceBuilder.initSeed(l);
    }

    public SC getConfig() {
        return this.config;
    }
}

