/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.math.NumberUtils
 */
package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
    public static final float SQUARE_ROOT_OF_TWO = MathHelper.sqrt(2.0f);
    private static final float[] SINE_TABLE = Util.make(new float[65536], fs -> {
        for (int i = 0; i < ((float[])fs).length; ++i) {
            fs[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
        }
    });
    private static final Random RANDOM = new Random();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double SMALLEST_FRACTION_FREE_DOUBLE = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ARCSINE_TABLE = new double[257];
    private static final double[] COSINE_TABLE = new double[257];

    public static float sin(float f) {
        return SINE_TABLE[(int)(f * 10430.378f) & 0xFFFF];
    }

    public static float cos(float f) {
        return SINE_TABLE[(int)(f * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float f) {
        return (float)Math.sqrt(f);
    }

    public static float sqrt(double d) {
        return (float)Math.sqrt(d);
    }

    public static int floor(float f) {
        int i = (int)f;
        return f < (float)i ? i - 1 : i;
    }

    @Environment(value=EnvType.CLIENT)
    public static int fastFloor(double d) {
        return (int)(d + 1024.0) - 1024;
    }

    public static int floor(double d) {
        int i = (int)d;
        return d < (double)i ? i - 1 : i;
    }

    public static long lfloor(double d) {
        long l = (long)d;
        return d < (double)l ? l - 1L : l;
    }

    public static float abs(float f) {
        return Math.abs(f);
    }

    public static int abs(int i) {
        return Math.abs(i);
    }

    public static int ceil(float f) {
        int i = (int)f;
        return f > (float)i ? i + 1 : i;
    }

    public static int ceil(double d) {
        int i = (int)d;
        return d > (double)i ? i + 1 : i;
    }

    public static int clamp(int i, int j, int k) {
        if (i < j) {
            return j;
        }
        if (i > k) {
            return k;
        }
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public static long clamp(long l, long m, long n) {
        if (l < m) {
            return m;
        }
        if (l > n) {
            return n;
        }
        return l;
    }

    public static float clamp(float f, float g, float h) {
        if (f < g) {
            return g;
        }
        if (f > h) {
            return h;
        }
        return f;
    }

    public static double clamp(double d, double e, double f) {
        if (d < e) {
            return e;
        }
        if (d > f) {
            return f;
        }
        return d;
    }

    public static double clampedLerp(double d, double e, double f) {
        if (f < 0.0) {
            return d;
        }
        if (f > 1.0) {
            return e;
        }
        return MathHelper.lerp(f, d, e);
    }

    public static double absMax(double d, double e) {
        if (d < 0.0) {
            d = -d;
        }
        if (e < 0.0) {
            e = -e;
        }
        return d > e ? d : e;
    }

    public static int floorDiv(int i, int j) {
        return Math.floorDiv(i, j);
    }

    public static int nextInt(Random random, int i, int j) {
        if (i >= j) {
            return i;
        }
        return random.nextInt(j - i + 1) + i;
    }

    public static float nextFloat(Random random, float f, float g) {
        if (f >= g) {
            return f;
        }
        return random.nextFloat() * (g - f) + f;
    }

    public static double nextDouble(Random random, double d, double e) {
        if (d >= e) {
            return d;
        }
        return random.nextDouble() * (e - d) + d;
    }

    public static double average(long[] ls) {
        long l = 0L;
        for (long m : ls) {
            l += m;
        }
        return (double)l / (double)ls.length;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean approximatelyEquals(float f, float g) {
        return Math.abs(g - f) < 1.0E-5f;
    }

    public static boolean approximatelyEquals(double d, double e) {
        return Math.abs(e - d) < (double)1.0E-5f;
    }

    public static int floorMod(int i, int j) {
        return Math.floorMod(i, j);
    }

    @Environment(value=EnvType.CLIENT)
    public static float floorMod(float f, float g) {
        return (f % g + g) % g;
    }

    @Environment(value=EnvType.CLIENT)
    public static double floorMod(double d, double e) {
        return (d % e + e) % e;
    }

    @Environment(value=EnvType.CLIENT)
    public static int wrapDegrees(int i) {
        int j = i % 360;
        if (j >= 180) {
            j -= 360;
        }
        if (j < -180) {
            j += 360;
        }
        return j;
    }

    public static float wrapDegrees(float f) {
        float g = f % 360.0f;
        if (g >= 180.0f) {
            g -= 360.0f;
        }
        if (g < -180.0f) {
            g += 360.0f;
        }
        return g;
    }

    public static double wrapDegrees(double d) {
        double e = d % 360.0;
        if (e >= 180.0) {
            e -= 360.0;
        }
        if (e < -180.0) {
            e += 360.0;
        }
        return e;
    }

    public static float subtractAngles(float f, float g) {
        return MathHelper.wrapDegrees(g - f);
    }

    public static float angleBetween(float f, float g) {
        return MathHelper.abs(MathHelper.subtractAngles(f, g));
    }

    public static float capRotation(float f, float g, float h) {
        float i = MathHelper.subtractAngles(f, g);
        float j = MathHelper.clamp(i, -h, h);
        return g - j;
    }

    public static float method_15348(float f, float g, float h) {
        h = MathHelper.abs(h);
        if (f < g) {
            return MathHelper.clamp(f + h, f, g);
        }
        return MathHelper.clamp(f - h, g, f);
    }

    public static float method_15388(float f, float g, float h) {
        float i = MathHelper.subtractAngles(f, g);
        return MathHelper.method_15348(f, f + i, h);
    }

    @Environment(value=EnvType.CLIENT)
    public static int parseInt(String string, int i) {
        return NumberUtils.toInt((String)string, (int)i);
    }

    @Environment(value=EnvType.CLIENT)
    public static int parseInt(String string, int i, int j) {
        return Math.max(j, MathHelper.parseInt(string, i));
    }

    public static int smallestEncompassingPowerOfTwo(int i) {
        int j = i - 1;
        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    public static boolean isPowerOfTwo(int i) {
        return i != 0 && (i & i - 1) == 0;
    }

    public static int log2DeBruijn(int i) {
        i = MathHelper.isPowerOfTwo(i) ? i : MathHelper.smallestEncompassingPowerOfTwo(i);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)i * 125613361L >> 27) & 0x1F];
    }

    public static int log2(int i) {
        return MathHelper.log2DeBruijn(i) - (MathHelper.isPowerOfTwo(i) ? 0 : 1);
    }

    @Environment(value=EnvType.CLIENT)
    public static int packRgb(float f, float g, float h) {
        return MathHelper.packRgb(MathHelper.floor(f * 255.0f), MathHelper.floor(g * 255.0f), MathHelper.floor(h * 255.0f));
    }

    @Environment(value=EnvType.CLIENT)
    public static int packRgb(int i, int j, int k) {
        int l = i;
        l = (l << 8) + j;
        l = (l << 8) + k;
        return l;
    }

    public static float fractionalPart(float f) {
        return f - (float)MathHelper.floor(f);
    }

    public static double fractionalPart(double d) {
        return d - (double)MathHelper.lfloor(d);
    }

    public static long hashCode(Vec3i arg) {
        return MathHelper.hashCode(arg.getX(), arg.getY(), arg.getZ());
    }

    public static long hashCode(int i, int j, int k) {
        long l = (long)(i * 3129871) ^ (long)k * 116129781L ^ (long)j;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static UUID randomUuid(Random random) {
        long l = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long m = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(l, m);
    }

    public static UUID randomUuid() {
        return MathHelper.randomUuid(RANDOM);
    }

    public static double getLerpProgress(double d, double e, double f) {
        return (d - e) / (f - e);
    }

    public static double atan2(double d, double e) {
        boolean bl3;
        boolean bl2;
        boolean bl;
        double f = e * e + d * d;
        if (Double.isNaN(f)) {
            return Double.NaN;
        }
        boolean bl4 = bl = d < 0.0;
        if (bl) {
            d = -d;
        }
        boolean bl5 = bl2 = e < 0.0;
        if (bl2) {
            e = -e;
        }
        boolean bl6 = bl3 = d > e;
        if (bl3) {
            double g = e;
            e = d;
            d = g;
        }
        double h = MathHelper.fastInverseSqrt(f);
        double i = SMALLEST_FRACTION_FREE_DOUBLE + (d *= h);
        int j = (int)Double.doubleToRawLongBits(i);
        double k = ARCSINE_TABLE[j];
        double l = COSINE_TABLE[j];
        double m = i - SMALLEST_FRACTION_FREE_DOUBLE;
        double n = d * l - (e *= h) * m;
        double o = (6.0 + n * n) * n * 0.16666666666666666;
        double p = k + o;
        if (bl3) {
            p = 1.5707963267948966 - p;
        }
        if (bl2) {
            p = Math.PI - p;
        }
        if (bl) {
            p = -p;
        }
        return p;
    }

    @Environment(value=EnvType.CLIENT)
    public static float fastInverseSqrt(float f) {
        float g = 0.5f * f;
        int i = Float.floatToIntBits(f);
        i = 1597463007 - (i >> 1);
        f = Float.intBitsToFloat(i);
        f *= 1.5f - g * f * f;
        return f;
    }

    public static double fastInverseSqrt(double d) {
        double e = 0.5 * d;
        long l = Double.doubleToRawLongBits(d);
        l = 6910469410427058090L - (l >> 1);
        d = Double.longBitsToDouble(l);
        d *= 1.5 - e * d * d;
        return d;
    }

    @Environment(value=EnvType.CLIENT)
    public static float fastInverseCbrt(float f) {
        int i = Float.floatToIntBits(f);
        i = 1419967116 - i / 3;
        float g = Float.intBitsToFloat(i);
        g = 0.6666667f * g + 1.0f / (3.0f * g * g * f);
        g = 0.6666667f * g + 1.0f / (3.0f * g * g * f);
        return g;
    }

    /*
     * WARNING - void declaration
     */
    public static int hsvToRgb(float f, float g, float h) {
        void ah;
        void ag;
        void af;
        int i = (int)(f * 6.0f) % 6;
        float j = f * 6.0f - (float)i;
        float k = h * (1.0f - g);
        float l = h * (1.0f - j * g);
        float m = h * (1.0f - (1.0f - j) * g);
        switch (i) {
            case 0: {
                float n = h;
                float o = m;
                float p = k;
                break;
            }
            case 1: {
                float q = l;
                float r = h;
                float s = k;
                break;
            }
            case 2: {
                float t = k;
                float u = h;
                float v = m;
                break;
            }
            case 3: {
                float w = k;
                float x = l;
                float y = h;
                break;
            }
            case 4: {
                float z = m;
                float aa = k;
                float ab = h;
                break;
            }
            case 5: {
                float ac = h;
                float ad = k;
                float ae = l;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f + ", " + g + ", " + h);
            }
        }
        int ai = MathHelper.clamp((int)(af * 255.0f), 0, 255);
        int aj = MathHelper.clamp((int)(ag * 255.0f), 0, 255);
        int ak = MathHelper.clamp((int)(ah * 255.0f), 0, 255);
        return ai << 16 | aj << 8 | ak;
    }

    public static int idealHash(int i) {
        i ^= i >>> 16;
        i *= -2048144789;
        i ^= i >>> 13;
        i *= -1028477387;
        i ^= i >>> 16;
        return i;
    }

    public static int binarySearch(int i, int j, IntPredicate intPredicate) {
        int k = j - i;
        while (k > 0) {
            int l = k / 2;
            int m = i + l;
            if (intPredicate.test(m)) {
                k = l;
                continue;
            }
            i = m + 1;
            k -= l + 1;
        }
        return i;
    }

    public static float lerp(float f, float g, float h) {
        return g + f * (h - g);
    }

    public static double lerp(double d, double e, double f) {
        return e + d * (f - e);
    }

    public static double lerp2(double d, double e, double f, double g, double h, double i) {
        return MathHelper.lerp(e, MathHelper.lerp(d, f, g), MathHelper.lerp(d, h, i));
    }

    public static double lerp3(double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n) {
        return MathHelper.lerp(f, MathHelper.lerp2(d, e, g, h, i, j), MathHelper.lerp2(d, e, k, l, m, n));
    }

    public static double perlinFade(double d) {
        return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
    }

    public static int sign(double d) {
        if (d == 0.0) {
            return 0;
        }
        return d > 0.0 ? 1 : -1;
    }

    @Environment(value=EnvType.CLIENT)
    public static float lerpAngleDegrees(float f, float g, float h) {
        return g + f * MathHelper.wrapDegrees(h - g);
    }

    @Deprecated
    public static float lerpAngle(float f, float g, float h) {
        float i;
        for (i = g - f; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return f + h * i;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static float fwrapDegrees(double d) {
        while (d >= 180.0) {
            d -= 360.0;
        }
        while (d < -180.0) {
            d += 360.0;
        }
        return (float)d;
    }

    @Environment(value=EnvType.CLIENT)
    public static float method_24504(float f, float g) {
        return (Math.abs(f % g - g * 0.5f) - g * 0.25f) / (g * 0.25f);
    }

    public static float square(float f) {
        return f * f;
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d = (double)i / 256.0;
            double e = Math.asin(d);
            MathHelper.COSINE_TABLE[i] = Math.cos(e);
            MathHelper.ARCSINE_TABLE[i] = e;
        }
    }
}

