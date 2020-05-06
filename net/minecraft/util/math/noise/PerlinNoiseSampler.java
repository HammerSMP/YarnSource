/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.SimplexNoiseSampler;

public final class PerlinNoiseSampler {
    private final byte[] permutations;
    public final double originX;
    public final double originY;
    public final double originZ;

    public PerlinNoiseSampler(Random random) {
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;
        this.permutations = new byte[256];
        for (int i = 0; i < 256; ++i) {
            this.permutations[i] = (byte)i;
        }
        for (int j = 0; j < 256; ++j) {
            int k = random.nextInt(256 - j);
            byte b = this.permutations[j];
            this.permutations[j] = this.permutations[j + k];
            this.permutations[j + k] = b;
        }
    }

    public double sample(double d, double e, double f, double g, double h) {
        double w;
        double i = d + this.originX;
        double j = e + this.originY;
        double k = f + this.originZ;
        int l = MathHelper.floor(i);
        int m = MathHelper.floor(j);
        int n = MathHelper.floor(k);
        double o = i - (double)l;
        double p = j - (double)m;
        double q = k - (double)n;
        double r = MathHelper.perlinFade(o);
        double s = MathHelper.perlinFade(p);
        double t = MathHelper.perlinFade(q);
        if (g != 0.0) {
            double u = Math.min(h, p);
            double v = (double)MathHelper.floor(u / g) * g;
        } else {
            w = 0.0;
        }
        return this.sample(l, m, n, o, p - w, q, r, s, t);
    }

    private static double grad(int i, double d, double e, double f) {
        int j = i & 0xF;
        return SimplexNoiseSampler.dot(SimplexNoiseSampler.gradients[j], d, e, f);
    }

    private int getGradient(int i) {
        return this.permutations[i & 0xFF] & 0xFF;
    }

    public double sample(int i, int j, int k, double d, double e, double f, double g, double h, double l) {
        int m = this.getGradient(i) + j;
        int n = this.getGradient(m) + k;
        int o = this.getGradient(m + 1) + k;
        int p = this.getGradient(i + 1) + j;
        int q = this.getGradient(p) + k;
        int r = this.getGradient(p + 1) + k;
        double s = PerlinNoiseSampler.grad(this.getGradient(n), d, e, f);
        double t = PerlinNoiseSampler.grad(this.getGradient(q), d - 1.0, e, f);
        double u = PerlinNoiseSampler.grad(this.getGradient(o), d, e - 1.0, f);
        double v = PerlinNoiseSampler.grad(this.getGradient(r), d - 1.0, e - 1.0, f);
        double w = PerlinNoiseSampler.grad(this.getGradient(n + 1), d, e, f - 1.0);
        double x = PerlinNoiseSampler.grad(this.getGradient(q + 1), d - 1.0, e, f - 1.0);
        double y = PerlinNoiseSampler.grad(this.getGradient(o + 1), d, e - 1.0, f - 1.0);
        double z = PerlinNoiseSampler.grad(this.getGradient(r + 1), d - 1.0, e - 1.0, f - 1.0);
        return MathHelper.lerp3(g, h, l, s, t, u, v, w, x, y, z);
    }
}

