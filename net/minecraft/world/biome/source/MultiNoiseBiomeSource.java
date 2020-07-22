/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    public static final MapCodec<MultiNoiseBiomeSource> field_24718 = RecordCodecBuilder.mapCodec(instance2 -> instance2.group((App)Codec.LONG.fieldOf("seed").forGetter(arg -> arg.seed), (App)RecordCodecBuilder.create(instance -> instance.group((App)Biome.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)Biome.field_24677.fieldOf("biome").forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).listOf().fieldOf("biomes").forGetter(arg -> arg.biomePoints)).apply((Applicative)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(Preset.CODEC, field_24718).xmap(either -> (MultiNoiseBiomeSource)either.map(pair -> ((Preset)pair.getFirst()).getBiomeSource((Long)pair.getSecond()), Function.identity()), arg -> arg.field_24721.map(arg2 -> Either.left((Object)Pair.of((Object)arg2, (Object)arg.seed))).orElseGet(() -> Either.right((Object)arg))).codec();
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
    private final boolean threeDimensionalSampling;
    private final long seed;
    private final Optional<Preset> field_24721;

    private MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints) {
        this(seed, biomePoints, Optional.empty());
    }

    public MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> list, Optional<Preset> optional) {
        super(list.stream().map(Pair::getSecond).map(Supplier::get).collect(Collectors.toList()));
        this.seed = seed;
        this.field_24721 = optional;
        IntStream intStream = IntStream.rangeClosed(-7, -6);
        IntStream intStream2 = IntStream.rangeClosed(-7, -6);
        IntStream intStream3 = IntStream.rangeClosed(-7, -6);
        IntStream intStream4 = IntStream.rangeClosed(-7, -6);
        this.temperatureNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed), intStream);
        this.humidityNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 1L), intStream2);
        this.altitudeNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 2L), intStream3);
        this.weirdnessNoise = new DoublePerlinNoiseSampler(new ChunkRandom(seed + 3L), intStream4);
        this.biomePoints = list;
        this.threeDimensionalSampling = false;
    }

    private static MultiNoiseBiomeSource method_28467(long l) {
        return new MultiNoiseBiomeSource(l, (List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>>)ImmutableList.of((Object)Pair.of((Object)new Biome.MixedNoisePoint(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> Biomes.NETHER_WASTES), (Object)Pair.of((Object)new Biome.MixedNoisePoint(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> Biomes.SOUL_SAND_VALLEY), (Object)Pair.of((Object)new Biome.MixedNoisePoint(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> Biomes.CRIMSON_FOREST), (Object)Pair.of((Object)new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> Biomes.WARPED_FOREST), (Object)Pair.of((Object)new Biome.MixedNoisePoint(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> Biomes.BASALT_DELTAS)), Optional.of(Preset.NETHER));
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new MultiNoiseBiomeSource(seed, this.biomePoints, this.field_24721);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int l = this.threeDimensionalSampling ? biomeY : 0;
        Biome.MixedNoisePoint lv = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(biomeX, l, biomeZ), (float)this.humidityNoise.sample(biomeX, l, biomeZ), (float)this.altitudeNoise.sample(biomeX, l, biomeZ), (float)this.weirdnessNoise.sample(biomeX, l, biomeZ), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(lv)))).map(Pair::getSecond).map(Supplier::get).orElse(Biomes.THE_VOID);
    }

    public boolean method_28462(long l) {
        return this.seed == l && Objects.equals(this.field_24721, Optional.of(Preset.NETHER));
    }

    public static class Preset {
        private static final Map<Identifier, Preset> field_24724 = Maps.newHashMap();
        public static final MapCodec<Pair<Preset, Long>> CODEC = Codec.mapPair((MapCodec)Identifier.CODEC.flatXmap(arg -> Optional.ofNullable(field_24724.get(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + arg))), arg -> DataResult.success((Object)arg.id)).fieldOf("preset"), (MapCodec)Codec.LONG.fieldOf("seed")).stable();
        public static final Preset NETHER = new Preset(new Identifier("nether"), seed -> MultiNoiseBiomeSource.method_28465(seed));
        private final Identifier id;
        private final LongFunction<MultiNoiseBiomeSource> biomeSourceFunction;

        public Preset(Identifier id, LongFunction<MultiNoiseBiomeSource> longFunction) {
            this.id = id;
            this.biomeSourceFunction = longFunction;
            field_24724.put(id, this);
        }

        public MultiNoiseBiomeSource getBiomeSource(long seed) {
            return this.biomeSourceFunction.apply(seed);
        }
    }
}

