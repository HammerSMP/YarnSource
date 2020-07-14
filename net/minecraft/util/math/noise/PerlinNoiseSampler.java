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

    public double sample(double x, double y, double z, double g, double h) {
        double w;
        double i = x + this.originX;
        double j = y + this.originY;
        double k = z + this.originZ;
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

    private static double grad(int hash, double x, double y, double z) {
        int j = hash & 0xF;
        return SimplexNoiseSampler.dot(SimplexNoiseSampler.gradients[j], x, y, z);
    }

    private int getGradient(int hash) {
        return this.permutations[hash & 0xFF] & 0xFF;
    }

    public double sample(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalX, double fadeLocalY, double fadeLocalZ) {
        int m = this.getGradient(sectionX) + sectionY;
        int n = this.getGradient(m) + sectionZ;
        int o = this.getGradient(m + 1) + sectionZ;
        int p = this.getGradient(sectionX + 1) + sectionY;
        int q = this.getGradient(p) + sectionZ;
        int r = this.getGradient(p + 1) + sectionZ;
        double s = PerlinNoiseSampler.grad(this.getGradient(n), localX, localY, localZ);
        double t = PerlinNoiseSampler.grad(this.getGradient(q), localX - 1.0, localY, localZ);
        double u = PerlinNoiseSampler.grad(this.getGradient(o), localX, localY - 1.0, localZ);
        double v = PerlinNoiseSampler.grad(this.getGradient(r), localX - 1.0, localY - 1.0, localZ);
        double w = PerlinNoiseSampler.grad(this.getGradient(n + 1), localX, localY, localZ - 1.0);
        double x = PerlinNoiseSampler.grad(this.getGradient(q + 1), localX - 1.0, localY, localZ - 1.0);
        double y = PerlinNoiseSampler.grad(this.getGradient(o + 1), localX, localY - 1.0, localZ - 1.0);
        double z = PerlinNoiseSampler.grad(this.getGradient(r + 1), localX - 1.0, localY - 1.0, localZ - 1.0);
        return MathHelper.lerp3(fadeLocalX, fadeLocalY, fadeLocalZ, s, t, u, v, w, x, y, z);
    }
}

