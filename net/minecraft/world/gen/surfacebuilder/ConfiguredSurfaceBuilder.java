/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig> {
    public static final MapCodec<ConfiguredSurfaceBuilder<?>> field_25878 = Registry.SURFACE_BUILDER.dispatchMap("name", arg -> arg.surfaceBuilder, SurfaceBuilder::method_29003);
    public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> field_25015 = RegistryElementCodec.of(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, field_25878);
    public final SurfaceBuilder<SC> surfaceBuilder;
    public final SC config;

    public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC arg2) {
        this.surfaceBuilder = surfaceBuilder;
        this.config = arg2;
    }

    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState defaultBlock, BlockState defaultFluid, int l, long seed) {
        this.surfaceBuilder.generate(random, chunk, biome, i, j, k, d, defaultBlock, defaultFluid, l, seed, this.config);
    }

    public void initSeed(long seed) {
        this.surfaceBuilder.initSeed(seed);
    }

    public SC getConfig() {
        return this.config;
    }
}

