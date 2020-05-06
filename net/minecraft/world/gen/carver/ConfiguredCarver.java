/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;

public class ConfiguredCarver<WC extends CarverConfig> {
    public final Carver<WC> carver;
    public final WC config;

    public ConfiguredCarver(Carver<WC> arg, WC arg2) {
        this.carver = arg;
        this.config = arg2;
    }

    public boolean shouldCarve(Random random, int i, int j) {
        return this.carver.shouldCarve(random, i, j, this.config);
    }

    public boolean carve(Chunk arg, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
        return this.carver.carve(arg, function, random, i, j, k, l, m, bitSet, this.config);
    }
}

