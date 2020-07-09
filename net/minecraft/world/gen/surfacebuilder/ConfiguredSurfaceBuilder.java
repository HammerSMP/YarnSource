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

