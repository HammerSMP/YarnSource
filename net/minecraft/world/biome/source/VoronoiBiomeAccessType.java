/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.SeedMixer;

public enum VoronoiBiomeAccessType implements BiomeAccessType
{
    INSTANCE;


    @Override
    public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
        int m = x - 2;
        int n = y - 2;
        int o = z - 2;
        int p = m >> 2;
        int q = n >> 2;
        int r = o >> 2;
        double d = (double)(m & 3) / 4.0;
        double e = (double)(n & 3) / 4.0;
        double f = (double)(o & 3) / 4.0;
        double[] ds = new double[8];
        for (int s = 0; s < 8; ++s) {
            boolean bl = (s & 4) == 0;
            boolean bl2 = (s & 2) == 0;
            boolean bl3 = (s & 1) == 0;
            int t = bl ? p : p + 1;
            int u = bl2 ? q : q + 1;
            int v = bl3 ? r : r + 1;
            double g = bl ? d : d - 1.0;
            double h = bl2 ? e : e - 1.0;
            double w = bl3 ? f : f - 1.0;
            ds[s] = VoronoiBiomeAccessType.calcSquaredDistance(seed, t, u, v, g, h, w);
        }
        int x2 = 0;
        double y2 = ds[0];
        for (int z2 = 1; z2 < 8; ++z2) {
            if (!(y2 > ds[z2])) continue;
            x2 = z2;
            y2 = ds[z2];
        }
        int aa = (x2 & 4) == 0 ? p : p + 1;
        int ab = (x2 & 2) == 0 ? q : q + 1;
        int ac = (x2 & 1) == 0 ? r : r + 1;
        return storage.getBiomeForNoiseGen(aa, ab, ac);
    }

    private static double calcSquaredDistance(long seed, int x, int y, int z, double xFraction, double yFraction, double zFraction) {
        long m = seed;
        m = SeedMixer.mixSeed(m, x);
        m = SeedMixer.mixSeed(m, y);
        m = SeedMixer.mixSeed(m, z);
        m = SeedMixer.mixSeed(m, x);
        m = SeedMixer.mixSeed(m, y);
        m = SeedMixer.mixSeed(m, z);
        double g = VoronoiBiomeAccessType.distribute(m);
        m = SeedMixer.mixSeed(m, seed);
        double h = VoronoiBiomeAccessType.distribute(m);
        m = SeedMixer.mixSeed(m, seed);
        double n = VoronoiBiomeAccessType.distribute(m);
        return VoronoiBiomeAccessType.square(zFraction + n) + VoronoiBiomeAccessType.square(yFraction + h) + VoronoiBiomeAccessType.square(xFraction + g);
    }

    private static double distribute(long seed) {
        double d = (double)((int)Math.floorMod(seed >> 24, 1024L)) / 1024.0;
        return (d - 0.5) * 0.9;
    }

    private static double square(double d) {
        return d * d;
    }
}

