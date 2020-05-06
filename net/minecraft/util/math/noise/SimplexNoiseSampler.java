/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class SimplexNoiseSampler {
    protected static final int[][] gradients = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    private static final double sqrt3 = Math.sqrt(3.0);
    private static final double SKEW_FACTOR_2D = 0.5 * (sqrt3 - 1.0);
    private static final double UNSKEW_FACTOR_2D = (3.0 - sqrt3) / 6.0;
    private final int[] permutations = new int[512];
    public final double originX;
    public final double originY;
    public final double originZ;

    public SimplexNoiseSampler(Random random) {
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;
        for (int i = 0; i < 256; ++i) {
            this.permutations[i] = i;
        }
        for (int j = 0; j < 256; ++j) {
            int k = random.nextInt(256 - j);
            int l = this.permutations[j];
            this.permutations[j] = this.permutations[k + j];
            this.permutations[k + j] = l;
        }
    }

    private int getGradient(int i) {
        return this.permutations[i & 0xFF];
    }

    protected static double dot(int[] is, double d, double e, double f) {
        return (double)is[0] * d + (double)is[1] * e + (double)is[2] * f;
    }

    private double grad(int i, double d, double e, double f, double g) {
        double k;
        double h = g - d * d - e * e - f * f;
        if (h < 0.0) {
            double j = 0.0;
        } else {
            h *= h;
            k = h * h * SimplexNoiseSampler.dot(gradients[i], d, e, f);
        }
        return k;
    }

    public double sample(double d, double e) {
        int q;
        int p;
        double k;
        double m;
        int j;
        double g;
        double f = (d + e) * SKEW_FACTOR_2D;
        int i = MathHelper.floor(d + f);
        double h = (double)i - (g = (double)(i + (j = MathHelper.floor(e + f))) * UNSKEW_FACTOR_2D);
        double l = d - h;
        if (l > (m = e - (k = (double)j - g))) {
            boolean n = true;
            boolean o = false;
        } else {
            p = 0;
            q = 1;
        }
        double r = l - (double)p + UNSKEW_FACTOR_2D;
        double s = m - (double)q + UNSKEW_FACTOR_2D;
        double t = l - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
        double u = m - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
        int v = i & 0xFF;
        int w = j & 0xFF;
        int x = this.getGradient(v + this.getGradient(w)) % 12;
        int y = this.getGradient(v + p + this.getGradient(w + q)) % 12;
        int z = this.getGradient(v + 1 + this.getGradient(w + 1)) % 12;
        double aa = this.grad(x, l, m, 0.0, 0.5);
        double ab = this.grad(y, r, s, 0.0, 0.5);
        double ac = this.grad(z, t, u, 0.0, 0.5);
        return 70.0 * (aa + ab + ac);
    }

    public double method_22416(double d, double e, double f) {
        int bc;
        int bb;
        int ba;
        int az;
        int ay;
        int ax;
        double g = 0.3333333333333333;
        double h = (d + e + f) * 0.3333333333333333;
        int i = MathHelper.floor(d + h);
        int j = MathHelper.floor(e + h);
        int k = MathHelper.floor(f + h);
        double l = 0.16666666666666666;
        double m = (double)(i + j + k) * 0.16666666666666666;
        double n = (double)i - m;
        double o = (double)j - m;
        double p = (double)k - m;
        double q = d - n;
        double r = e - o;
        double s = f - p;
        if (q >= r) {
            if (r >= s) {
                boolean t = true;
                boolean u = false;
                boolean v = false;
                boolean w = true;
                boolean x = true;
                boolean y = false;
            } else if (q >= s) {
                boolean z = true;
                boolean aa = false;
                boolean ab = false;
                boolean ac = true;
                boolean ad = false;
                boolean ae = true;
            } else {
                boolean af = false;
                boolean ag = false;
                boolean ah = true;
                boolean ai = true;
                boolean aj = false;
                boolean ak = true;
            }
        } else if (r < s) {
            boolean al = false;
            boolean am = false;
            boolean an = true;
            boolean ao = false;
            boolean ap = true;
            boolean aq = true;
        } else if (q < s) {
            boolean ar = false;
            boolean as = true;
            boolean at = false;
            boolean au = false;
            boolean av = true;
            boolean aw = true;
        } else {
            ax = 0;
            ay = 1;
            az = 0;
            ba = 1;
            bb = 1;
            bc = 0;
        }
        double bd = q - (double)ax + 0.16666666666666666;
        double be = r - (double)ay + 0.16666666666666666;
        double bf = s - (double)az + 0.16666666666666666;
        double bg = q - (double)ba + 0.3333333333333333;
        double bh = r - (double)bb + 0.3333333333333333;
        double bi = s - (double)bc + 0.3333333333333333;
        double bj = q - 1.0 + 0.5;
        double bk = r - 1.0 + 0.5;
        double bl = s - 1.0 + 0.5;
        int bm = i & 0xFF;
        int bn = j & 0xFF;
        int bo = k & 0xFF;
        int bp = this.getGradient(bm + this.getGradient(bn + this.getGradient(bo))) % 12;
        int bq = this.getGradient(bm + ax + this.getGradient(bn + ay + this.getGradient(bo + az))) % 12;
        int br = this.getGradient(bm + ba + this.getGradient(bn + bb + this.getGradient(bo + bc))) % 12;
        int bs = this.getGradient(bm + 1 + this.getGradient(bn + 1 + this.getGradient(bo + 1))) % 12;
        double bt = this.grad(bp, q, r, s, 0.6);
        double bu = this.grad(bq, bd, be, bf, 0.6);
        double bv = this.grad(br, bg, bh, bi, 0.6);
        double bw = this.grad(bs, bj, bk, bl, 0.6);
        return 32.0 * (bt + bu + bv + bw);
    }
}

