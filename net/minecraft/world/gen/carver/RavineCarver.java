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

public class RavineCarver
extends Carver<ProbabilityConfig> {
    private final float[] heightToHorizontalStretchFactor = new float[1024];

    public RavineCarver(Codec<ProbabilityConfig> codec) {
        super(codec, 256);
    }

    @Override
    public boolean shouldCarve(Random random, int i, int j, ProbabilityConfig arg) {
        return random.nextFloat() <= arg.probability;
    }

    @Override
    public boolean carve(Chunk arg, Function<BlockPos, Biome> function, Random random, int i, int j, int k, int l, int m, BitSet bitSet, ProbabilityConfig arg2) {
        int n = (this.getBranchFactor() * 2 - 1) * 16;
        double d = j * 16 + random.nextInt(16);
        double e = random.nextInt(random.nextInt(40) + 8) + 20;
        double f = k * 16 + random.nextInt(16);
        float g = random.nextFloat() * ((float)Math.PI * 2);
        float h = (random.nextFloat() - 0.5f) * 2.0f / 8.0f;
        double o = 3.0;
        float p = (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
        int q = n - random.nextInt(n / 4);
        boolean r = false;
        this.carveRavine(arg, function, random.nextLong(), i, l, m, d, e, f, p, g, h, 0, q, 3.0, bitSet);
        return true;
    }

    private void carveRavine(Chunk arg, Function<BlockPos, Biome> function, long l, int i, int j, int k, double d, double e, double f, float g, float h, float m, int n, int o, double p, BitSet bitSet) {
        Random random = new Random(l);
        float q = 1.0f;
        for (int r = 0; r < 256; ++r) {
            if (r == 0 || random.nextInt(3) == 0) {
                q = 1.0f + random.nextFloat() * random.nextFloat();
            }
            this.heightToHorizontalStretchFactor[r] = q * q;
        }
        float s = 0.0f;
        float t = 0.0f;
        for (int u = n; u < o; ++u) {
            double v = 1.5 + (double)(MathHelper.sin((float)u * (float)Math.PI / (float)o) * g);
            double w = v * p;
            v *= (double)random.nextFloat() * 0.25 + 0.75;
            w *= (double)random.nextFloat() * 0.25 + 0.75;
            float x = MathHelper.cos(m);
            float y = MathHelper.sin(m);
            d += (double)(MathHelper.cos(h) * x);
            e += (double)y;
            f += (double)(MathHelper.sin(h) * x);
            m *= 0.7f;
            m += t * 0.05f;
            h += s * 0.05f;
            t *= 0.8f;
            s *= 0.5f;
            t += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (random.nextInt(4) == 0) continue;
            if (!this.canCarveBranch(j, k, d, f, u, o, g)) {
                return;
            }
            this.carveRegion(arg, function, l, i, j, k, d, e, f, v, w, bitSet);
        }
    }

    @Override
    protected boolean isPositionExcluded(double d, double e, double f, int i) {
        return (d * d + f * f) * (double)this.heightToHorizontalStretchFactor[i - 1] + e * e / 6.0 >= 1.0;
    }
}

