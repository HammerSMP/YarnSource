/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceConfig;

public class MultiNoiseBiomeSourceConfig
implements BiomeSourceConfig {
    private final long seed;
    private ImmutableList<Integer> temperatureOctaves = (ImmutableList)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private ImmutableList<Integer> humidityOctaves = (ImmutableList)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private ImmutableList<Integer> hillinessOctaves = (ImmutableList)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private ImmutableList<Integer> styleOctaves = (ImmutableList)IntStream.rangeClosed(-7, -6).boxed().collect(ImmutableList.toImmutableList());
    private boolean field_24117;
    private List<Pair<Biome.MixedNoisePoint, Biome>> field_24118 = ImmutableList.of();

    public MultiNoiseBiomeSourceConfig(long l) {
        this.seed = l;
    }

    public MultiNoiseBiomeSourceConfig withBiomes(List<Biome> list) {
        return this.method_27350((List)list.stream().flatMap(arg -> arg.streamNoises().map(arg2 -> Pair.of((Object)arg2, (Object)arg))).collect(ImmutableList.toImmutableList()));
    }

    public MultiNoiseBiomeSourceConfig method_27350(List<Pair<Biome.MixedNoisePoint, Biome>> list) {
        this.field_24118 = list;
        return this;
    }

    public List<Pair<Biome.MixedNoisePoint, Biome>> method_27347() {
        return this.field_24118;
    }

    public long getSeed() {
        return this.seed;
    }

    public ImmutableList<Integer> getTemperatureOctaves() {
        return this.temperatureOctaves;
    }

    public ImmutableList<Integer> getHumidityOctaves() {
        return this.humidityOctaves;
    }

    public ImmutableList<Integer> getHillinessOctaves() {
        return this.hillinessOctaves;
    }

    public ImmutableList<Integer> getStyleOctaves() {
        return this.styleOctaves;
    }

    public boolean method_27351() {
        return this.field_24117;
    }
}

