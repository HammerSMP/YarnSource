/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;

public class ConfiguredCarver<WC extends CarverConfig> {
    public static final MapCodec<ConfiguredCarver<?>> field_25832 = Registry.CARVER.dispatchMap("name", arg -> arg.carver, Carver::getCodec);
    public static final Codec<Supplier<ConfiguredCarver<?>>> field_24828 = RegistryElementCodec.of(Registry.CONFIGURED_CARVER_WORLDGEN, field_25832);
    private final Carver<WC> carver;
    private final WC config;

    public ConfiguredCarver(Carver<WC> arg, WC arg2) {
        this.carver = arg;
        this.config = arg2;
    }

    public WC method_30378() {
        return this.config;
    }

    public boolean shouldCarve(Random random, int i, int j) {
        return this.carver.shouldCarve(random, i, j, this.config);
    }

    public boolean carve(Chunk arg, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
        return this.carver.carve(arg, function, random, i, j, k, l, m, bitSet, this.config);
    }
}

