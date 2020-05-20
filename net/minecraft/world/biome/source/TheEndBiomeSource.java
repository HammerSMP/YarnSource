/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> field_24730 = Codec.LONG.fieldOf("seed").xmap(TheEndBiomeSource::new, arg -> arg.field_24731).stable().codec();
    private final SimplexNoiseSampler noise;
    private static final List<Biome> BIOMES = ImmutableList.of((Object)Biomes.THE_END, (Object)Biomes.END_HIGHLANDS, (Object)Biomes.END_MIDLANDS, (Object)Biomes.SMALL_END_ISLANDS, (Object)Biomes.END_BARRENS);
    private final long field_24731;

    public TheEndBiomeSource(long l) {
        super(BIOMES);
        this.field_24731 = l;
        ChunkRandom lv = new ChunkRandom(l);
        lv.consume(17292);
        this.noise = new SimplexNoiseSampler(lv);
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return field_24730;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BiomeSource create(long l) {
        return new TheEndBiomeSource(l);
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        int l = i >> 2;
        int m = k >> 2;
        if ((long)l * (long)l + (long)m * (long)m <= 4096L) {
            return Biomes.THE_END;
        }
        float f = TheEndBiomeSource.getNoiseAt(this.noise, l * 2 + 1, m * 2 + 1);
        if (f > 40.0f) {
            return Biomes.END_HIGHLANDS;
        }
        if (f >= 0.0f) {
            return Biomes.END_MIDLANDS;
        }
        if (f < -20.0f) {
            return Biomes.SMALL_END_ISLANDS;
        }
        return Biomes.END_BARRENS;
    }

    public boolean method_28479(long l) {
        return this.field_24731 == l;
    }

    public static float getNoiseAt(SimplexNoiseSampler arg, int i, int j) {
        int k = i / 2;
        int l = j / 2;
        int m = i % 2;
        int n = j % 2;
        float f = 100.0f - MathHelper.sqrt(i * i + j * j) * 8.0f;
        f = MathHelper.clamp(f, -100.0f, 80.0f);
        for (int o = -12; o <= 12; ++o) {
            for (int p = -12; p <= 12; ++p) {
                long q = k + o;
                long r = l + p;
                if (q * q + r * r <= 4096L || !(arg.sample(q, r) < (double)-0.9f)) continue;
                float g = (MathHelper.abs(q) * 3439.0f + MathHelper.abs(r) * 147.0f) % 13.0f + 9.0f;
                float h = m - o * 2;
                float s = n - p * 2;
                float t = 100.0f - MathHelper.sqrt(h * h + s * s) * g;
                t = MathHelper.clamp(t, -100.0f, 80.0f);
                f = Math.max(f, t);
            }
        }
        return f;
    }
}

