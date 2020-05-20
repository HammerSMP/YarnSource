/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

public class CaveCarver
extends Carver<ProbabilityConfig> {
    public CaveCarver(Codec<ProbabilityConfig> codec, int i) {
        super(codec, i);
    }

    @Override
    public boolean shouldCarve(Random random, int i, int j, ProbabilityConfig arg) {
        return random.nextFloat() <= arg.probability;
    }

    @Override
    public boolean carve(Chunk arg, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig arg2) {
        int n = (this.getBranchFactor() * 2 - 1) * 16;
        int o = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);
        for (int p = 0; p < o; ++p) {
            double d = j * 16 + random.nextInt(16);
            double e = this.getCaveY(random);
            double f = k * 16 + random.nextInt(16);
            int q = 1;
            if (random.nextInt(4) == 0) {
                double g = 0.5;
                float h = 1.0f + random.nextFloat() * 6.0f;
                this.carveCave(arg, function, random.nextLong(), i, l, m, d, e, f, h, 0.5, bitSet);
                q += random.nextInt(4);
            }
            for (int r = 0; r < q; ++r) {
                float s = random.nextFloat() * ((float)Math.PI * 2);
                float t = (random.nextFloat() - 0.5f) / 4.0f;
                float u = this.getTunnelSystemWidth(random);
                int v = n - random.nextInt(n / 4);
                boolean w = false;
                this.carveTunnels(arg, function, random.nextLong(), i, l, m, d, e, f, u, s, t, 0, v, this.getTunnelSystemHeightWidthRatio(), bitSet);
            }
        }
        return true;
    }

    protected int getMaxCaveCount() {
        return 15;
    }

    protected float getTunnelSystemWidth(Random random) {
        float f = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return f;
    }

    protected double getTunnelSystemHeightWidthRatio() {
        return 1.0;
    }

    protected int getCaveY(Random random) {
        return random.nextInt(random.nextInt(120) + 8);
    }

    protected void carveCave(Chunk arg, Function<BlockPos, Biome> function, long l, int i, int j, int k, double d, double e, double f, float g, double h, BitSet bitSet) {
        double m = 1.5 + (double)(MathHelper.sin(1.5707964f) * g);
        double n = m * h;
        this.carveRegion(arg, function, l, i, j, k, d + 1.0, e, f, m, n, bitSet);
    }

    protected void carveTunnels(Chunk arg, Function<BlockPos, Biome> function, long l, int i, int j, int k, double d, double e, double f, float g, float h, float m, int n, int o, double p, BitSet bitSet) {
        Random random = new Random(l);
        int q = random.nextInt(o / 2) + o / 4;
        boolean bl = random.nextInt(6) == 0;
        float r = 0.0f;
        float s = 0.0f;
        for (int t = n; t < o; ++t) {
            double u = 1.5 + (double)(MathHelper.sin((float)Math.PI * (float)t / (float)o) * g);
            double v = u * p;
            float w = MathHelper.cos(m);
            d += (double)(MathHelper.cos(h) * w);
            e += (double)MathHelper.sin(m);
            f += (double)(MathHelper.sin(h) * w);
            m *= bl ? 0.92f : 0.7f;
            m += s * 0.1f;
            h += r * 0.1f;
            s *= 0.9f;
            r *= 0.75f;
            s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (t == q && g > 1.0f) {
                this.carveTunnels(arg, function, random.nextLong(), i, j, k, d, e, f, random.nextFloat() * 0.5f + 0.5f, h - 1.5707964f, m / 3.0f, t, o, 1.0, bitSet);
                this.carveTunnels(arg, function, random.nextLong(), i, j, k, d, e, f, random.nextFloat() * 0.5f + 0.5f, h + 1.5707964f, m / 3.0f, t, o, 1.0, bitSet);
                return;
            }
            if (random.nextInt(4) == 0) continue;
            if (!this.canCarveBranch(j, k, d, f, t, o, g)) {
                return;
            }
            this.carveRegion(arg, function, l, i, j, k, d, e, f, u, v, bitSet);
        }
    }

    @Override
    protected boolean isPositionExcluded(double d, double e, double f, int i) {
        return e <= -0.7 || d * d + e * e + f * f >= 1.0;
    }
}

