/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints;
    private final boolean threeDimensionalSampling;

    public static MultiNoiseBiomeSource fromBiomes(long l, List<Biome> list) {
        return new MultiNoiseBiomeSource(l, (List)list.stream().flatMap(arg -> arg.streamNoises().map(arg2 -> Pair.of((Object)arg2, (Object)arg))).collect(ImmutableList.toImmutableList()));
    }

    public MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Biome>> list) {
        super(list.stream().map(Pair::getSecond).collect(Collectors.toSet()));
        IntStream intStream = IntStream.rangeClosed(-7, -6);
        IntStream intStream2 = IntStream.rangeClosed(-7, -6);
        IntStream intStream3 = IntStream.rangeClosed(-7, -6);
        IntStream intStream4 = IntStream.rangeClosed(-7, -6);
        this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l), intStream);
        this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 1L), intStream2);
        this.altitudeNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 2L), intStream3);
        this.weirdnessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 3L), intStream4);
        this.biomePoints = list;
        this.threeDimensionalSampling = false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource create(long l) {
        return new MultiNoiseBiomeSource(l, this.biomePoints);
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        int l = this.threeDimensionalSampling ? j : 0;
        Biome.MixedNoisePoint lv = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(i, l, k), (float)this.humidityNoise.sample(i, l, k), (float)this.altitudeNoise.sample(i, l, k), (float)this.weirdnessNoise.sample(i, l, k), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(lv)))).map(Pair::getSecond).orElse(Biomes.THE_VOID);
    }
}

