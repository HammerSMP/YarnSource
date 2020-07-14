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

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector3f vector) {
        this(vector.getX(), vector.getY(), vector.getZ(), 1.0f);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vector4f lv = (Vector4f)o;
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

    public void multiplyComponentwise(Vector3f vector) {
        this.x *= vector.getX();
        this.y *= vector.getY();
        this.z *= vector.getZ();
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float dotProduct(Vector4f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
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

    public void transform(Matrix4f matrix) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = this.w;
        this.x = matrix.a00 * f + matrix.a01 * g + matrix.a02 * h + matrix.a03 * i;
        this.y = matrix.a10 * f + matrix.a11 * g + matrix.a12 * h + matrix.a13 * i;
        this.z = matrix.a20 * f + matrix.a21 * g + matrix.a22 * h + matrix.a23 * i;
        this.w = matrix.a30 * f + matrix.a31 * g + matrix.a32 * h + matrix.a33 * i;
    }

    public void rotate(Quaternion rotation) {
        Quaternion lv = new Quaternion(rotation);
        lv.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion lv2 = new Quaternion(rotation);
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

