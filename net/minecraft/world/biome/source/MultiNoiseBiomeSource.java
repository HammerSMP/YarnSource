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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource
extends BiomeSource {
    public static final MapCodec<MultiNoiseBiomeSource> field_24718 = RecordCodecBuilder.mapCodec(instance2 -> instance2.group((App)Codec.LONG.fieldOf("seed").forGetter(arg -> arg.field_24720), (App)RecordCodecBuilder.create(instance -> instance.group((App)Biome.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), (App)Registry.BIOME.fieldOf("biome").forGetter(Pair::getSecond)).apply((Applicative)instance, Pair::of)).listOf().fieldOf("biomes").forGetter(arg -> arg.biomePoints)).apply((Applicative)instance2, MultiNoiseBiomeSource::new));
    public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(class_5305.field_24722, field_24718).xmap(either -> (MultiNoiseBiomeSource)either.map(pair -> ((class_5305)pair.getFirst()).method_28469((Long)pair.getSecond()), Function.identity()), arg -> arg.field_24721.map(arg2 -> Either.left((Object)Pair.of((Object)arg2, (Object)arg.field_24720))).orElseGet(() -> Either.right((Object)arg))).codec();
    private final DoublePerlinNoiseSampler temperatureNoise;
    private final DoublePerlinNoiseSampler humidityNoise;
    private final DoublePerlinNoiseSampler altitudeNoise;
    private final DoublePerlinNoiseSampler weirdnessNoise;
    private final List<Pair<Biome.MixedNoisePoint, Biome>> biomePoints;
    private final boolean threeDimensionalSampling;
    private final long field_24720;
    private final Optional<class_5305> field_24721;

    private MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Biome>> list) {
        this(l, list, Optional.empty());
    }

    public MultiNoiseBiomeSource(long l, List<Pair<Biome.MixedNoisePoint, Biome>> list, Optional<class_5305> optional) {
        super(list.stream().map(Pair::getSecond).collect(Collectors.toList()));
        this.field_24720 = l;
        this.field_24721 = optional;
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

    private static MultiNoiseBiomeSource method_28467(long l) {
        ImmutableList immutableList = ImmutableList.of((Object)Biomes.NETHER_WASTES, (Object)Biomes.SOUL_SAND_VALLEY, (Object)Biomes.CRIMSON_FOREST, (Object)Biomes.WARPED_FOREST, (Object)Biomes.BASALT_DELTAS);
        return new MultiNoiseBiomeSource(l, (List)immutableList.stream().flatMap(arg -> arg.streamNoises().map(arg2 -> Pair.of((Object)arg2, (Object)arg))).collect(ImmutableList.toImmutableList()), Optional.of(class_5305.field_24723));
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return CODEC;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource withSeed(long l) {
        return new MultiNoiseBiomeSource(l, this.biomePoints, this.field_24721);
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        int l = this.threeDimensionalSampling ? j : 0;
        Biome.MixedNoisePoint lv = new Biome.MixedNoisePoint((float)this.temperatureNoise.sample(i, l, k), (float)this.humidityNoise.sample(i, l, k), (float)this.altitudeNoise.sample(i, l, k), (float)this.weirdnessNoise.sample(i, l, k), 0.0f);
        return this.biomePoints.stream().min(Comparator.comparing(pair -> Float.valueOf(((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(lv)))).map(Pair::getSecond).orElse(Biomes.THE_VOID);
    }

    public boolean method_28462(long l) {
        return this.field_24720 == l && Objects.equals(this.field_24721, Optional.of(class_5305.field_24723));
    }

    public static class class_5305 {
        private static final Map<Identifier, class_5305> field_24724 = Maps.newHashMap();
        public static final MapCodec<Pair<class_5305, Long>> field_24722 = Codec.mapPair((MapCodec)Identifier.CODEC.flatXmap(arg -> Optional.ofNullable(field_24724.get(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + arg))), arg -> DataResult.success((Object)arg.field_24725)).fieldOf("preset"), (MapCodec)Codec.LONG.fieldOf("seed")).stable();
        public static final class_5305 field_24723 = new class_5305(new Identifier("nether"), l -> MultiNoiseBiomeSource.method_28465(l));
        private final Identifier field_24725;
        private final LongFunction<MultiNoiseBiomeSource> field_24726;

        public class_5305(Identifier arg, LongFunction<MultiNoiseBiomeSource> longFunction) {
            this.field_24725 = arg;
            this.field_24726 = longFunction;
            field_24724.put(arg, this);
        }

        public MultiNoiseBiomeSource method_28469(long l) {
            return this.field_24726.apply(l);
        }
    }
}

