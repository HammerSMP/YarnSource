/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.math;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class Vector3f {
    public static Vector3f NEGATIVE_X = new Vector3f(-1.0f, 0.0f, 0.0f);
    public static Vector3f POSITIVE_X = new Vector3f(1.0f, 0.0f, 0.0f);
    public static Vector3f NEGATIVE_Y = new Vector3f(0.0f, -1.0f, 0.0f);
    public static Vector3f POSITIVE_Y = new Vector3f(0.0f, 1.0f, 0.0f);
    public static Vector3f NEGATIVE_Z = new Vector3f(0.0f, 0.0f, -1.0f);
    public static Vector3f POSITIVE_Z = new Vector3f(0.0f, 0.0f, 1.0f);
    private float x;
    private float y;
    private float z;

    public Vector3f() {
    }

    public Vector3f(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public Vector3f(Vec3d arg) {
        this((float)arg.x, (float)arg.y, (float)arg.z);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector3f lv = (Vector3f)object;
        if (Float.compare(lv.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(lv.y, this.y) != 0) {
            return false;
        }
        return Float.compare(lv.z, this.z) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return i;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
    }

    @Environment(value=EnvType.CLIENT)
    public void multiplyComponentwise(float f, float g, float h) {
        this.x *= f;
        this.y *= g;
        this.z *= h;
    }

    @Environment(value=EnvType.CLIENT)
    public void clamp(float f, float g) {
        this.x = MathHelper.clamp(this.x, f, g);
        this.y = MathHelper.clamp(this.y, f, g);
        this.z = MathHelper.clamp(this.z, f, g);
    }

    public void set(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    @Environment(value=EnvType.CLIENT)
    public void add(float f, float g, float h) {
        this.x += f;
        this.y += g;
        this.z += h;
    }

    @Environment(value=EnvType.CLIENT)
    public void add(Vector3f arg) {
        this.x += arg.x;
        this.y += arg.y;
        this.z += arg.z;
    }

    @Environment(value=EnvType.CLIENT)
    public void subtract(Vector3f arg) {
        this.x -= arg.x;
        this.y -= arg.y;
        this.z -= arg.z;
    }

    @Environment(value=EnvType.CLIENT)
    public float dot(Vector3f arg) {
        return this.x * arg.x + this.y * arg.y + this.z * arg.z;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if ((double)f < 1.0E-5) {
            return false;
        }
        float g = MathHelper.fastInverseSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void cross(Vector3f arg) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = arg.getX();
        float j = arg.getY();
        float k = arg.getZ();
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
    }

    @Environment(value=EnvType.CLIENT)
    public void transform(Matrix3f arg) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        this.x = arg.a00 * f + arg.a01 * g + arg.a02 * h;
        this.y = arg.a10 * f + arg.a11 * g + arg.a12 * h;
        this.z = arg.a20 * f + arg.a21 * g + arg.a22 * h;
    }

    public void rotate(Quaternion arg) {
        Quaternion lv = new Quaternion(arg);
        lv.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion lv2 = new Quaternion(arg);
        lv2.conjugate();
        lv.hamiltonProduct(lv2);
        this.set(lv.getX(), lv.getY(), lv.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    public void lerp(Vector3f arg, float f) {
        float g = 1.0f - f;
        this.x = this.x * g + arg.x * f;
        this.y = this.y * g + arg.y * f;
        this.z = this.z * g + arg.z * f;
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion getRadialQuaternion(float f) {
        return new Quaternion(this, f, false);
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion getDegreesQuaternion(float f) {
        return new Quaternion(this, f, true);
    }

    @Environment(value=EnvType.CLIENT)
    public Vector3f copy() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Environment(value=EnvType.CLIENT)
    public void modify(Float2FloatFunction float2FloatFunction) {
        this.x = float2FloatFunction.get(this.x);
        this.y = float2FloatFunction.get(this.y);
        this.z = float2FloatFunction.get(this.z);
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }
}

