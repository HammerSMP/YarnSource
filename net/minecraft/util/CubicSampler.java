/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util;

import javax.annotation.Nonnull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CubicSampler {
    private static final double[] DENSITY_CURVE = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

    @Nonnull
    @Environment(value=EnvType.CLIENT)
    public static Vec3d sampleColor(Vec3d arg, RgbFetcher arg2) {
        int i = MathHelper.floor(arg.getX());
        int j = MathHelper.floor(arg.getY());
        int k = MathHelper.floor(arg.getZ());
        double d = arg.getX() - (double)i;
        double e = arg.getY() - (double)j;
        double f = arg.getZ() - (double)k;
        double g = 0.0;
        Vec3d lv = Vec3d.ZERO;
        for (int l = 0; l < 6; ++l) {
            double h = MathHelper.lerp(d, DENSITY_CURVE[l + 1], DENSITY_CURVE[l]);
            int m = i - 2 + l;
            for (int n = 0; n < 6; ++n) {
                double o = MathHelper.lerp(e, DENSITY_CURVE[n + 1], DENSITY_CURVE[n]);
                int p = j - 2 + n;
                for (int q = 0; q < 6; ++q) {
                    double r = MathHelper.lerp(f, DENSITY_CURVE[q + 1], DENSITY_CURVE[q]);
                    int s = k - 2 + q;
                    double t = h * o * r;
                    g += t;
                    lv = lv.add(arg2.fetch(m, p, s).multiply(t));
                }
            }
        }
        lv = lv.multiply(1.0 / g);
        return lv;
    }

    public static interface RgbFetcher {
        public Vec3d fetch(int var1, int var2, int var3);
    }
}

