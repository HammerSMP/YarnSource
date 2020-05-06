/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;

public final class Matrix4f {
    protected float a00;
    protected float a01;
    protected float a02;
    protected float a03;
    protected float a10;
    protected float a11;
    protected float a12;
    protected float a13;
    protected float a20;
    protected float a21;
    protected float a22;
    protected float a23;
    protected float a30;
    protected float a31;
    protected float a32;
    protected float a33;

    public Matrix4f() {
    }

    public Matrix4f(Matrix4f arg) {
        this.a00 = arg.a00;
        this.a01 = arg.a01;
        this.a02 = arg.a02;
        this.a03 = arg.a03;
        this.a10 = arg.a10;
        this.a11 = arg.a11;
        this.a12 = arg.a12;
        this.a13 = arg.a13;
        this.a20 = arg.a20;
        this.a21 = arg.a21;
        this.a22 = arg.a22;
        this.a23 = arg.a23;
        this.a30 = arg.a30;
        this.a31 = arg.a31;
        this.a32 = arg.a32;
        this.a33 = arg.a33;
    }

    public Matrix4f(Quaternion arg) {
        float f = arg.getX();
        float g = arg.getY();
        float h = arg.getZ();
        float i = arg.getW();
        float j = 2.0f * f * f;
        float k = 2.0f * g * g;
        float l = 2.0f * h * h;
        this.a00 = 1.0f - k - l;
        this.a11 = 1.0f - l - j;
        this.a22 = 1.0f - j - k;
        this.a33 = 1.0f;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        this.a10 = 2.0f * (m + r);
        this.a01 = 2.0f * (m - r);
        this.a20 = 2.0f * (o - q);
        this.a02 = 2.0f * (o + q);
        this.a21 = 2.0f * (n + p);
        this.a12 = 2.0f * (n - p);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Matrix4f lv = (Matrix4f)object;
        return Float.compare(lv.a00, this.a00) == 0 && Float.compare(lv.a01, this.a01) == 0 && Float.compare(lv.a02, this.a02) == 0 && Float.compare(lv.a03, this.a03) == 0 && Float.compare(lv.a10, this.a10) == 0 && Float.compare(lv.a11, this.a11) == 0 && Float.compare(lv.a12, this.a12) == 0 && Float.compare(lv.a13, this.a13) == 0 && Float.compare(lv.a20, this.a20) == 0 && Float.compare(lv.a21, this.a21) == 0 && Float.compare(lv.a22, this.a22) == 0 && Float.compare(lv.a23, this.a23) == 0 && Float.compare(lv.a30, this.a30) == 0 && Float.compare(lv.a31, this.a31) == 0 && Float.compare(lv.a32, this.a32) == 0 && Float.compare(lv.a33, this.a33) == 0;
    }

    public int hashCode() {
        int i = this.a00 != 0.0f ? Float.floatToIntBits(this.a00) : 0;
        i = 31 * i + (this.a01 != 0.0f ? Float.floatToIntBits(this.a01) : 0);
        i = 31 * i + (this.a02 != 0.0f ? Float.floatToIntBits(this.a02) : 0);
        i = 31 * i + (this.a03 != 0.0f ? Float.floatToIntBits(this.a03) : 0);
        i = 31 * i + (this.a10 != 0.0f ? Float.floatToIntBits(this.a10) : 0);
        i = 31 * i + (this.a11 != 0.0f ? Float.floatToIntBits(this.a11) : 0);
        i = 31 * i + (this.a12 != 0.0f ? Float.floatToIntBits(this.a12) : 0);
        i = 31 * i + (this.a13 != 0.0f ? Float.floatToIntBits(this.a13) : 0);
        i = 31 * i + (this.a20 != 0.0f ? Float.floatToIntBits(this.a20) : 0);
        i = 31 * i + (this.a21 != 0.0f ? Float.floatToIntBits(this.a21) : 0);
        i = 31 * i + (this.a22 != 0.0f ? Float.floatToIntBits(this.a22) : 0);
        i = 31 * i + (this.a23 != 0.0f ? Float.floatToIntBits(this.a23) : 0);
        i = 31 * i + (this.a30 != 0.0f ? Float.floatToIntBits(this.a30) : 0);
        i = 31 * i + (this.a31 != 0.0f ? Float.floatToIntBits(this.a31) : 0);
        i = 31 * i + (this.a32 != 0.0f ? Float.floatToIntBits(this.a32) : 0);
        i = 31 * i + (this.a33 != 0.0f ? Float.floatToIntBits(this.a33) : 0);
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    private static int pack(int i, int j) {
        return j * 4 + i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Matrix4f:\n");
        stringBuilder.append(this.a00);
        stringBuilder.append(" ");
        stringBuilder.append(this.a01);
        stringBuilder.append(" ");
        stringBuilder.append(this.a02);
        stringBuilder.append(" ");
        stringBuilder.append(this.a03);
        stringBuilder.append("\n");
        stringBuilder.append(this.a10);
        stringBuilder.append(" ");
        stringBuilder.append(this.a11);
        stringBuilder.append(" ");
        stringBuilder.append(this.a12);
        stringBuilder.append(" ");
        stringBuilder.append(this.a13);
        stringBuilder.append("\n");
        stringBuilder.append(this.a20);
        stringBuilder.append(" ");
        stringBuilder.append(this.a21);
        stringBuilder.append(" ");
        stringBuilder.append(this.a22);
        stringBuilder.append(" ");
        stringBuilder.append(this.a23);
        stringBuilder.append("\n");
        stringBuilder.append(this.a30);
        stringBuilder.append(" ");
        stringBuilder.append(this.a31);
        stringBuilder.append(" ");
        stringBuilder.append(this.a32);
        stringBuilder.append(" ");
        stringBuilder.append(this.a33);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    @Environment(value=EnvType.CLIENT)
    public void writeToBuffer(FloatBuffer floatBuffer) {
        floatBuffer.put(Matrix4f.pack(0, 0), this.a00);
        floatBuffer.put(Matrix4f.pack(0, 1), this.a01);
        floatBuffer.put(Matrix4f.pack(0, 2), this.a02);
        floatBuffer.put(Matrix4f.pack(0, 3), this.a03);
        floatBuffer.put(Matrix4f.pack(1, 0), this.a10);
        floatBuffer.put(Matrix4f.pack(1, 1), this.a11);
        floatBuffer.put(Matrix4f.pack(1, 2), this.a12);
        floatBuffer.put(Matrix4f.pack(1, 3), this.a13);
        floatBuffer.put(Matrix4f.pack(2, 0), this.a20);
        floatBuffer.put(Matrix4f.pack(2, 1), this.a21);
        floatBuffer.put(Matrix4f.pack(2, 2), this.a22);
        floatBuffer.put(Matrix4f.pack(2, 3), this.a23);
        floatBuffer.put(Matrix4f.pack(3, 0), this.a30);
        floatBuffer.put(Matrix4f.pack(3, 1), this.a31);
        floatBuffer.put(Matrix4f.pack(3, 2), this.a32);
        floatBuffer.put(Matrix4f.pack(3, 3), this.a33);
    }

    @Environment(value=EnvType.CLIENT)
    public void loadIdentity() {
        this.a00 = 1.0f;
        this.a01 = 0.0f;
        this.a02 = 0.0f;
        this.a03 = 0.0f;
        this.a10 = 0.0f;
        this.a11 = 1.0f;
        this.a12 = 0.0f;
        this.a13 = 0.0f;
        this.a20 = 0.0f;
        this.a21 = 0.0f;
        this.a22 = 1.0f;
        this.a23 = 0.0f;
        this.a30 = 0.0f;
        this.a31 = 0.0f;
        this.a32 = 0.0f;
        this.a33 = 1.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float determinantAndAdjugate() {
        float f = this.a00 * this.a11 - this.a01 * this.a10;
        float g = this.a00 * this.a12 - this.a02 * this.a10;
        float h = this.a00 * this.a13 - this.a03 * this.a10;
        float i = this.a01 * this.a12 - this.a02 * this.a11;
        float j = this.a01 * this.a13 - this.a03 * this.a11;
        float k = this.a02 * this.a13 - this.a03 * this.a12;
        float l = this.a20 * this.a31 - this.a21 * this.a30;
        float m = this.a20 * this.a32 - this.a22 * this.a30;
        float n = this.a20 * this.a33 - this.a23 * this.a30;
        float o = this.a21 * this.a32 - this.a22 * this.a31;
        float p = this.a21 * this.a33 - this.a23 * this.a31;
        float q = this.a22 * this.a33 - this.a23 * this.a32;
        float r = this.a11 * q - this.a12 * p + this.a13 * o;
        float s = -this.a10 * q + this.a12 * n - this.a13 * m;
        float t = this.a10 * p - this.a11 * n + this.a13 * l;
        float u = -this.a10 * o + this.a11 * m - this.a12 * l;
        float v = -this.a01 * q + this.a02 * p - this.a03 * o;
        float w = this.a00 * q - this.a02 * n + this.a03 * m;
        float x = -this.a00 * p + this.a01 * n - this.a03 * l;
        float y = this.a00 * o - this.a01 * m + this.a02 * l;
        float z = this.a31 * k - this.a32 * j + this.a33 * i;
        float aa = -this.a30 * k + this.a32 * h - this.a33 * g;
        float ab = this.a30 * j - this.a31 * h + this.a33 * f;
        float ac = -this.a30 * i + this.a31 * g - this.a32 * f;
        float ad = -this.a21 * k + this.a22 * j - this.a23 * i;
        float ae = this.a20 * k - this.a22 * h + this.a23 * g;
        float af = -this.a20 * j + this.a21 * h - this.a23 * f;
        float ag = this.a20 * i - this.a21 * g + this.a22 * f;
        this.a00 = r;
        this.a10 = s;
        this.a20 = t;
        this.a30 = u;
        this.a01 = v;
        this.a11 = w;
        this.a21 = x;
        this.a31 = y;
        this.a02 = z;
        this.a12 = aa;
        this.a22 = ab;
        this.a32 = ac;
        this.a03 = ad;
        this.a13 = ae;
        this.a23 = af;
        this.a33 = ag;
        return f * q - g * p + h * o + i * n - j * m + k * l;
    }

    @Environment(value=EnvType.CLIENT)
    public void transpose() {
        float f = this.a10;
        this.a10 = this.a01;
        this.a01 = f;
        f = this.a20;
        this.a20 = this.a02;
        this.a02 = f;
        f = this.a21;
        this.a21 = this.a12;
        this.a12 = f;
        f = this.a30;
        this.a30 = this.a03;
        this.a03 = f;
        f = this.a31;
        this.a31 = this.a13;
        this.a13 = f;
        f = this.a32;
        this.a32 = this.a23;
        this.a23 = f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean invert() {
        float f = this.determinantAndAdjugate();
        if (Math.abs(f) > 1.0E-6f) {
            this.multiply(f);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public void multiply(Matrix4f arg) {
        float f = this.a00 * arg.a00 + this.a01 * arg.a10 + this.a02 * arg.a20 + this.a03 * arg.a30;
        float g = this.a00 * arg.a01 + this.a01 * arg.a11 + this.a02 * arg.a21 + this.a03 * arg.a31;
        float h = this.a00 * arg.a02 + this.a01 * arg.a12 + this.a02 * arg.a22 + this.a03 * arg.a32;
        float i = this.a00 * arg.a03 + this.a01 * arg.a13 + this.a02 * arg.a23 + this.a03 * arg.a33;
        float j = this.a10 * arg.a00 + this.a11 * arg.a10 + this.a12 * arg.a20 + this.a13 * arg.a30;
        float k = this.a10 * arg.a01 + this.a11 * arg.a11 + this.a12 * arg.a21 + this.a13 * arg.a31;
        float l = this.a10 * arg.a02 + this.a11 * arg.a12 + this.a12 * arg.a22 + this.a13 * arg.a32;
        float m = this.a10 * arg.a03 + this.a11 * arg.a13 + this.a12 * arg.a23 + this.a13 * arg.a33;
        float n = this.a20 * arg.a00 + this.a21 * arg.a10 + this.a22 * arg.a20 + this.a23 * arg.a30;
        float o = this.a20 * arg.a01 + this.a21 * arg.a11 + this.a22 * arg.a21 + this.a23 * arg.a31;
        float p = this.a20 * arg.a02 + this.a21 * arg.a12 + this.a22 * arg.a22 + this.a23 * arg.a32;
        float q = this.a20 * arg.a03 + this.a21 * arg.a13 + this.a22 * arg.a23 + this.a23 * arg.a33;
        float r = this.a30 * arg.a00 + this.a31 * arg.a10 + this.a32 * arg.a20 + this.a33 * arg.a30;
        float s = this.a30 * arg.a01 + this.a31 * arg.a11 + this.a32 * arg.a21 + this.a33 * arg.a31;
        float t = this.a30 * arg.a02 + this.a31 * arg.a12 + this.a32 * arg.a22 + this.a33 * arg.a32;
        float u = this.a30 * arg.a03 + this.a31 * arg.a13 + this.a32 * arg.a23 + this.a33 * arg.a33;
        this.a00 = f;
        this.a01 = g;
        this.a02 = h;
        this.a03 = i;
        this.a10 = j;
        this.a11 = k;
        this.a12 = l;
        this.a13 = m;
        this.a20 = n;
        this.a21 = o;
        this.a22 = p;
        this.a23 = q;
        this.a30 = r;
        this.a31 = s;
        this.a32 = t;
        this.a33 = u;
    }

    @Environment(value=EnvType.CLIENT)
    public void multiply(Quaternion arg) {
        this.multiply(new Matrix4f(arg));
    }

    @Environment(value=EnvType.CLIENT)
    public void multiply(float f) {
        this.a00 *= f;
        this.a01 *= f;
        this.a02 *= f;
        this.a03 *= f;
        this.a10 *= f;
        this.a11 *= f;
        this.a12 *= f;
        this.a13 *= f;
        this.a20 *= f;
        this.a21 *= f;
        this.a22 *= f;
        this.a23 *= f;
        this.a30 *= f;
        this.a31 *= f;
        this.a32 *= f;
        this.a33 *= f;
    }

    @Environment(value=EnvType.CLIENT)
    public static Matrix4f viewboxMatrix(double d, float f, float g, float h) {
        float i = (float)(1.0 / Math.tan(d * 0.01745329238474369 / 2.0));
        Matrix4f lv = new Matrix4f();
        lv.a00 = i / f;
        lv.a11 = i;
        lv.a22 = (h + g) / (g - h);
        lv.a32 = -1.0f;
        lv.a23 = 2.0f * h * g / (g - h);
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static Matrix4f projectionMatrix(float f, float g, float h, float i) {
        Matrix4f lv = new Matrix4f();
        lv.a00 = 2.0f / f;
        lv.a11 = 2.0f / g;
        float j = i - h;
        lv.a22 = -2.0f / j;
        lv.a33 = 1.0f;
        lv.a03 = -1.0f;
        lv.a13 = -1.0f;
        lv.a23 = -(i + h) / j;
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public void addToLastColumn(Vector3f arg) {
        this.a03 += arg.getX();
        this.a13 += arg.getY();
        this.a23 += arg.getZ();
    }

    @Environment(value=EnvType.CLIENT)
    public Matrix4f copy() {
        return new Matrix4f(this);
    }

    @Environment(value=EnvType.CLIENT)
    public static Matrix4f scale(float f, float g, float h) {
        Matrix4f lv = new Matrix4f();
        lv.a00 = f;
        lv.a11 = g;
        lv.a22 = h;
        lv.a33 = 1.0f;
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static Matrix4f translate(float f, float g, float h) {
        Matrix4f lv = new Matrix4f();
        lv.a00 = 1.0f;
        lv.a11 = 1.0f;
        lv.a22 = 1.0f;
        lv.a33 = 1.0f;
        lv.a03 = f;
        lv.a13 = g;
        lv.a23 = h;
        return lv;
    }
}

