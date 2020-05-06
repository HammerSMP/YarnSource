/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.biome.source;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceConfig;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler hillinessNoise;
    private final DoublePerlinNoiseSampler styleNoise;
    private final List<Pair<Biome.MixedNoisePoint, Biome>> field_24115;
    private final boolean field_24116;

    public MultiNoiseBiomeSource(MultiNoiseBiomeSourceConfig arg) {
        super(arg.method_27347().stream().map(Pair::getSecond).collect(Collectors.toSet()));
        long l = arg.getSeed();
        this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l), (List<Integer>)arg.getTemperatureOctaves());
        this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 1L), (List<Integer>)arg.getHumidityOctaves());
        this.hillinessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 2L), (List<Integer>)arg.getHillinessOctaves());
        this.styleNoise = new DoublePerlinNoiseSampler(new ChunkRandom(l + 3L), (List<Integer>)arg.getStyleOctaves());
        this.field_24115 = arg.method_27347();
        this.field_24116 = arg.method_27351();
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        int l = this.field_24116 ? j : 0;
        Biome.MixedNoisePoint lv = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(i, l, k), (float)this.humidityNoise.sample(i, l, k), (float)this.hillinessNoise.sample(i, l, k), (float)this.styleNoise.sample(i, l, k), 0.0f);
        return this.field_24115.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(lv)))).map(Pair::getSecond).orElse(Biomes.THE_VOID);
    }
}

