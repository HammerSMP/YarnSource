/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

public final class Quaternion {
    public static final Quaternion IDENTITY = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public Quaternion(Vector3f arg, float f, boolean bl) {
        if (bl) {
            f *= (float)Math.PI / 180;
        }
        float g = Quaternion.sin(f / 2.0f);
        this.x = arg.getX() * g;
        this.y = arg.getY() * g;
        this.z = arg.getZ() * g;
        this.w = Quaternion.cos(f / 2.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion(float f, float g, float h, boolean bl) {
        if (bl) {
            f *= (float)Math.PI / 180;
            g *= (float)Math.PI / 180;
            h *= (float)Math.PI / 180;
        }
        float i = Quaternion.sin(0.5f * f);
        float j = Quaternion.cos(0.5f * f);
        float k = Quaternion.sin(0.5f * g);
        float l = Quaternion.cos(0.5f * g);
        float m = Quaternion.sin(0.5f * h);
        float n = Quaternion.cos(0.5f * h);
        this.x = i * l * n + j * k * m;
        this.y = j * k * n - i * l * m;
        this.z = i * k * n + j * l * m;
        this.w = j * l * n - i * k * m;
    }

    public Quaternion(Quaternion arg) {
        this.x = arg.x;
        this.y = arg.y;
        this.z = arg.z;
        this.w = arg.w;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Quaternion lv = (Quaternion)object;
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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternion[").append(this.getW()).append(" + ");
        stringBuilder.append(this.getX()).append("i + ");
        stringBuilder.append(this.getY()).append("j + ");
        stringBuilder.append(this.getZ()).append("k]");
        return stringBuilder.toString();
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

    public void hamiltonProduct(Quaternion arg) {
        float f = this.getX();
        float g = this.getY();
        float h = this.getZ();
        float i = this.getW();
        float j = arg.getX();
        float k = arg.getY();
        float l = arg.getZ();
        float m = arg.getW();
        this.x = i * j + f * m + g * l - h * k;
        this.y = i * k - f * l + g * m + h * j;
        this.z = i * l + f * k - g * j + h * m;
        this.w = i * m - f * j - g * k - h * l;
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    private static float cos(float f) {
        return (float)Math.cos(f);
    }

    private static float sin(float f) {
        return (float)Math.sin(f);
    }

    @Environment(value=EnvType.CLIENT)
    public void normalize() {
        float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
        if (f > 1.0E-6f) {
            float g = MathHelper.fastInverseSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            this.w *= g;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 0.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion copy() {
        return new Quaternion(this);
    }
}

