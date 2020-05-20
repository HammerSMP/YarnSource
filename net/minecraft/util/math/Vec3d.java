/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;

public class Vec3d
implements Position {
    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
    public final double x;
    public final double y;
    public final double z;

    @Environment(value=EnvType.CLIENT)
    public static Vec3d unpackRgb(int i) {
        double d = (double)(i >> 16 & 0xFF) / 255.0;
        double e = (double)(i >> 8 & 0xFF) / 255.0;
        double f = (double)(i & 0xFF) / 255.0;
        return new Vec3d(d, e, f);
    }

    public static Vec3d ofCenter(Vec3i arg) {
        return new Vec3d((double)arg.getX() + 0.5, (double)arg.getY() + 0.5, (double)arg.getZ() + 0.5);
    }

    public static Vec3d of(Vec3i arg) {
        return new Vec3d(arg.getX(), arg.getY(), arg.getZ());
    }

    public static Vec3d ofBottomCenter(Vec3i arg) {
        return new Vec3d((double)arg.getX() + 0.5, arg.getY(), (double)arg.getZ() + 0.5);
    }

    public static Vec3d ofCenter(Vec3i arg, double d) {
        return new Vec3d((double)arg.getX() + 0.5, (double)arg.getY() + d, (double)arg.getZ() + 0.5);
    }

    public Vec3d(double d, double e, double f) {
        this.x = d;
        this.y = e;
        this.z = f;
    }

    public Vec3d(Vector3f arg) {
        this(arg.getX(), arg.getY(), arg.getZ());
    }

    public Vec3d reverseSubtract(Vec3d arg) {
        return new Vec3d(arg.x - this.x, arg.y - this.y, arg.z - this.z);
    }

    public Vec3d normalize() {
        double d = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (d < 1.0E-4) {
            return ZERO;
        }
        return new Vec3d(this.x / d, this.y / d, this.z / d);
    }

    public double dotProduct(Vec3d arg) {
        return this.x * arg.x + this.y * arg.y + this.z * arg.z;
    }

    public Vec3d crossProduct(Vec3d arg) {
        return new Vec3d(this.y * arg.z - this.z * arg.y, this.z * arg.x - this.x * arg.z, this.x * arg.y - this.y * arg.x);
    }

    public Vec3d subtract(Vec3d arg) {
        return this.subtract(arg.x, arg.y, arg.z);
    }

    public Vec3d subtract(double d, double e, double f) {
        return this.add(-d, -e, -f);
    }

    public Vec3d add(Vec3d arg) {
        return this.add(arg.x, arg.y, arg.z);
    }

    public Vec3d add(double d, double e, double f) {
        return new Vec3d(this.x + d, this.y + e, this.z + f);
    }

    public boolean isInRange(Position arg, double d) {
        return this.squaredDistanceTo(arg.getX(), arg.getY(), arg.getZ()) < d * d;
    }

    public double distanceTo(Vec3d arg) {
        double d = arg.x - this.x;
        double e = arg.y - this.y;
        double f = arg.z - this.z;
        return MathHelper.sqrt(d * d + e * e + f * f);
    }

    public double squaredDistanceTo(Vec3d arg) {
        double d = arg.x - this.x;
        double e = arg.y - this.y;
        double f = arg.z - this.z;
        return d * d + e * e + f * f;
    }

    public double squaredDistanceTo(double d, double e, double f) {
        double g = d - this.x;
        double h = e - this.y;
        double i = f - this.z;
        return g * g + h * h + i * i;
    }

    public Vec3d multiply(double d) {
        return this.multiply(d, d, d);
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d negate() {
        return this.multiply(-1.0);
    }

    public Vec3d multiply(Vec3d arg) {
        return this.multiply(arg.x, arg.y, arg.z);
    }

    public Vec3d multiply(double d, double e, double f) {
        return new Vec3d(this.x * d, this.y * e, this.z * f);
    }

    public double length() {
        return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vec3d)) {
            return false;
        }
        Vec3d lv = (Vec3d)object;
        if (Double.compare(lv.x, this.x) != 0) {
            return false;
        }
        if (Double.compare(lv.y, this.y) != 0) {
            return false;
        }
        return Double.compare(lv.z, this.z) == 0;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.x);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.y);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.z);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3d rotateX(float f) {
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        double d = this.x;
        double e = this.y * (double)g + this.z * (double)h;
        double i = this.z * (double)g - this.y * (double)h;
        return new Vec3d(d, e, i);
    }

    public Vec3d rotateY(float f) {
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        double d = this.x * (double)g + this.z * (double)h;
        double e = this.y;
        double i = this.z * (double)g - this.x * (double)h;
        return new Vec3d(d, e, i);
    }

    @Environment(value=EnvType.CLIENT)
    public static Vec3d fromPolar(Vec2f arg) {
        return Vec3d.fromPolar(arg.x, arg.y);
    }

    @Environment(value=EnvType.CLIENT)
    public static Vec3d fromPolar(float f, float g) {
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        return new Vec3d(i * j, k, h * j);
    }

    public Vec3d floorAlongAxes(EnumSet<Direction.Axis> enumSet) {
        double d = enumSet.contains(Direction.Axis.X) ? (double)MathHelper.floor(this.x) : this.x;
        double e = enumSet.contains(Direction.Axis.Y) ? (double)MathHelper.floor(this.y) : this.y;
        double f = enumSet.contains(Direction.Axis.Z) ? (double)MathHelper.floor(this.z) : this.z;
        return new Vec3d(d, e, f);
    }

    public double getComponentAlongAxis(Direction.Axis arg) {
        return arg.choose(this.x, this.y, this.z);
    }

    @Override
    public final double getX() {
        return this.x;
    }

    @Override
    public final double getY() {
        return this.y;
    }

    @Override
    public final double getZ() {
        return this.z;
    }
}

