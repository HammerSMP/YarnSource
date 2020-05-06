/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f() {
    }

    public Vector4f(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public Vector4f(Vector3f arg) {
        this(arg.getX(), arg.getY(), arg.getZ(), 1.0f);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector4f lv = (Vector4f)object;
        if (Float.compare(lv.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(lv.y, this.y) != 0) {
            return false;
        }
        if (Float.compare(lv.z, this.z) != 0) {
            return false;
        }
        return Float.compare(lv.w, this.w) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
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

    public float getW() {
        return this.w;
    }

    public void multiplyComponentwise(Vector3f arg) {
        this.x *= arg.getX();
        this.y *= arg.getY();
        this.z *= arg.getZ();
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public float dotProduct(Vector4f arg) {
        return this.x * arg.x + this.y * arg.y + this.z * arg.z + this.w * arg.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if ((double)f < 1.0E-5) {
            return false;
        }
        float g = MathHelper.fastInverseSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        this.w *= g;
        return true;
    }

    public void transform(Matrix4f arg) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = this.w;
        this.x = arg.a00 * f + arg.a01 * g + arg.a02 * h + arg.a03 * i;
        this.y = arg.a10 * f + arg.a11 * g + arg.a12 * h + arg.a13 * i;
        this.z = arg.a20 * f + arg.a21 * g + arg.a22 * h + arg.a23 * i;
        this.w = arg.a30 * f + arg.a31 * g + arg.a32 * h + arg.a33 * i;
    }

    public void rotate(Quaternion arg) {
        Quaternion lv = new Quaternion(arg);
        lv.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion lv2 = new Quaternion(arg);
        lv2.conjugate();
        lv.hamiltonProduct(lv2);
        this.set(lv.getX(), lv.getY(), lv.getZ(), this.getW());
    }

    public void normalizeProjectiveCoordinates() {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0f;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }
}

