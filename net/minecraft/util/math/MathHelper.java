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

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Environment(value=EnvType.CLIENT)
    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double clampedLerp(double start, double end, double delta) {
        if (delta < 0.0) {
            return start;
        }
        if (delta > 1.0) {
            return end;
        }
        return MathHelper.lerp(delta, start, end);
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

    public static int nextInt(Random random, int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }

    public static float nextFloat(Random random, float min, float max) {
        if (min >= max) {
            return min;
        }
        return random.nextFloat() * (max - min) + min;
    }

    public static double nextDouble(Random random, double min, double max) {
        if (min >= max) {
            return min;
        }
        return random.nextDouble() * (max - min) + min;
    }

    public static double average(long[] array) {
        long l = 0L;
        for (long m : array) {
            l += m;
        }
        return (double)l / (double)array.length;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean approximatelyEquals(float a, float b) {
        return Math.abs(b - a) < 1.0E-5f;
    }

    public static boolean approximatelyEquals(double a, double b) {
        return Math.abs(b - a) < (double)1.0E-5f;
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

    public static float subtractAngles(float start, float end) {
        return MathHelper.wrapDegrees(end - start);
    }

    public static float angleBetween(float first, float second) {
        return MathHelper.abs(MathHelper.subtractAngles(first, second));
    }

    public static float stepAngleTowards(float from, float to, float step) {
        float i = MathHelper.subtractAngles(from, to);
        float j = MathHelper.clamp(i, -step, step);
        return to - j;
    }

    public static float stepTowards(float from, float to, float step) {
        step = MathHelper.abs(step);
        if (from < to) {
            return MathHelper.clamp(from + step, from, to);
        }
        return MathHelper.clamp(from - step, to, from);
    }

    public static float stepUnwrappedAngleTowards(float from, float to, float step) {
        float i = MathHelper.subtractAngles(from, to);
        return MathHelper.stepTowards(from, from + i, step);
    }

    @Environment(value=EnvType.CLIENT)
    public static int parseInt(String string, int fallback) {
        return NumberUtils.toInt((String)string, (int)fallback);
    }

    public static int smallestEncompassingPowerOfTwo(int value) {
        int j = value - 1;
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

    public static int roundUpToMultiple(int value, int divisor) {
        int k;
        if (divisor == 0) {
            return 0;
        }
        if (value == 0) {
            return divisor;
        }
        if (value < 0) {
            divisor *= -1;
        }
        if ((k = value % divisor) == 0) {
            return value;
        }
        return value + divisor - k;
    }

    @Environment(value=EnvType.CLIENT)
    public static int packRgb(float r, float g, float b) {
        return MathHelper.packRgb(MathHelper.floor(r * 255.0f), MathHelper.floor(g * 255.0f), MathHelper.floor(b * 255.0f));
    }

    @Environment(value=EnvType.CLIENT)
    public static int packRgb(int r, int g, int b) {
        int l = r;
        l = (l << 8) + g;
        l = (l << 8) + b;
        return l;
    }

    public static float fractionalPart(float value) {
        return value - (float)MathHelper.floor(value);
    }

    public static double fractionalPart(double value) {
        return value - (double)MathHelper.lfloor(value);
    }

    public static long hashCode(Vec3i vec) {
        return MathHelper.hashCode(vec.getX(), vec.getY(), vec.getZ());
    }

    public static long hashCode(int x, int y, int z) {
        long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
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

    public static double getLerpProgress(double value, double start, double end) {
        return (value - start) / (end - start);
    }

    public static double atan2(double y, double x) {
        boolean bl3;
        boolean bl2;
        boolean bl;
        double f = x * x + y * y;
        if (Double.isNaN(f)) {
            return Double.NaN;
        }
        boolean bl4 = bl = y < 0.0;
        if (bl) {
            y = -y;
        }
        boolean bl5 = bl2 = x < 0.0;
        if (bl2) {
            x = -x;
        }
        boolean bl6 = bl3 = y > x;
        if (bl3) {
            double g = x;
            x = y;
            y = g;
        }
        double h = MathHelper.fastInverseSqrt(f);
        double i = SMALLEST_FRACTION_FREE_DOUBLE + (y *= h);
        int j = (int)Double.doubleToRawLongBits(i);
        double k = ARCSINE_TABLE[j];
        double l = COSINE_TABLE[j];
        double m = i - SMALLEST_FRACTION_FREE_DOUBLE;
        double n = y * l - (x *= h) * m;
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
    public static float fastInverseSqrt(float x) {
        float g = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 1597463007 - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= 1.5f - g * x * x;
        return x;
    }

    public static double fastInverseSqrt(double x) {
        double e = 0.5 * x;
        long l = Double.doubleToRawLongBits(x);
        l = 6910469410427058090L - (l >> 1);
        x = Double.longBitsToDouble(l);
        x *= 1.5 - e * x * x;
        return x;
    }

    @Environment(value=EnvType.CLIENT)
    public static float fastInverseCbrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 1419967116 - i / 3;
        float g = Float.intBitsToFloat(i);
        g = 0.6666667f * g + 1.0f / (3.0f * g * g * x);
        g = 0.6666667f * g + 1.0f / (3.0f * g * g * x);
        return g;
    }

    /*
     * WARNING - void declaration
     */
    public static int hsvToRgb(float hue, float saturation, float value) {
        void ah;
        void ag;
        void af;
        int i = (int)(hue * 6.0f) % 6;
        float j = hue * 6.0f - (float)i;
        float k = value * (1.0f - saturation);
        float l = value * (1.0f - j * saturation);
        float m = value * (1.0f - (1.0f - j) * saturation);
        switch (i) {
            case 0: {
                float n = value;
                float o = m;
                float p = k;
                break;
            }
            case 1: {
                float q = l;
                float r = value;
                float s = k;
                break;
            }
            case 2: {
                float t = k;
                float u = value;
                float v = m;
                break;
            }
            case 3: {
                float w = k;
                float x = l;
                float y = value;
                break;
            }
            case 4: {
                float z = m;
                float aa = k;
                float ab = value;
                break;
            }
            case 5: {
                float ac = value;
                float ad = k;
                float ae = l;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
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

    public static int binarySearch(int start, int end, IntPredicate leftPredicate) {
        int k = end - start;
        while (k > 0) {
            int l = k / 2;
            int m = start + l;
            if (leftPredicate.test(m)) {
                k = l;
                continue;
            }
            start = m + 1;
            k -= l + 1;
        }
        return start;
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static double lerp2(double deltaX, double deltaY, double val00, double val10, double val01, double val11) {
        return MathHelper.lerp(deltaY, MathHelper.lerp(deltaX, val00, val10), MathHelper.lerp(deltaX, val01, val11));
    }

    public static double lerp3(double deltaX, double deltaY, double deltaZ, double val000, double val100, double val010, double val110, double val001, double val101, double val011, double val111) {
        return MathHelper.lerp(deltaZ, MathHelper.lerp2(deltaX, deltaY, val000, val100, val010, val110), MathHelper.lerp2(deltaX, deltaY, val001, val101, val011, val111));
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
    public static float lerpAngleDegrees(float delta, float start, float end) {
        return start + delta * MathHelper.wrapDegrees(end - start);
    }

    @Deprecated
    public static float lerpAngle(float start, float end, float delta) {
        float i;
        for (i = end - start; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return start + delta * i;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static float fwrapDegrees(double degrees) {
        while (degrees >= 180.0) {
            degrees -= 360.0;
        }
        while (degrees < -180.0) {
            degrees += 360.0;
        }
        return (float)degrees;
    }

    @Environment(value=EnvType.CLIENT)
    public static float method_24504(float f, float g) {
        return (Math.abs(f % g - g * 0.5f) - g * 0.25f) / (g * 0.25f);
    }

    public static float square(float n) {
        return n * n;
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

