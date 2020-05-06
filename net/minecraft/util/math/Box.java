/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Box {
    public final double x1;
    public final double y1;
    public final double z1;
    public final double x2;
    public final double y2;
    public final double z2;

    public Box(double d, double e, double f, double g, double h, double i) {
        this.x1 = Math.min(d, g);
        this.y1 = Math.min(e, h);
        this.z1 = Math.min(f, i);
        this.x2 = Math.max(d, g);
        this.y2 = Math.max(e, h);
        this.z2 = Math.max(f, i);
    }

    public Box(BlockPos arg) {
        this(arg.getX(), arg.getY(), arg.getZ(), arg.getX() + 1, arg.getY() + 1, arg.getZ() + 1);
    }

    public Box(BlockPos arg, BlockPos arg2) {
        this(arg.getX(), arg.getY(), arg.getZ(), arg2.getX(), arg2.getY(), arg2.getZ());
    }

    public Box(Vec3d arg, Vec3d arg2) {
        this(arg.x, arg.y, arg.z, arg2.x, arg2.y, arg2.z);
    }

    public static Box from(BlockBox arg) {
        return new Box(arg.minX, arg.minY, arg.minZ, arg.maxX + 1, arg.maxY + 1, arg.maxZ + 1);
    }

    public double getMin(Direction.Axis arg) {
        return arg.choose(this.x1, this.y1, this.z1);
    }

    public double getMax(Direction.Axis arg) {
        return arg.choose(this.x2, this.y2, this.z2);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Box)) {
            return false;
        }
        Box lv = (Box)object;
        if (Double.compare(lv.x1, this.x1) != 0) {
            return false;
        }
        if (Double.compare(lv.y1, this.y1) != 0) {
            return false;
        }
        if (Double.compare(lv.z1, this.z1) != 0) {
            return false;
        }
        if (Double.compare(lv.x2, this.x2) != 0) {
            return false;
        }
        if (Double.compare(lv.y2, this.y2) != 0) {
            return false;
        }
        return Double.compare(lv.z2, this.z2) == 0;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.x1);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.y1);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.z1);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.x2);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.y2);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.z2);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public Box shrink(double d, double e, double f) {
        double g = this.x1;
        double h = this.y1;
        double i = this.z1;
        double j = this.x2;
        double k = this.y2;
        double l = this.z2;
        if (d < 0.0) {
            g -= d;
        } else if (d > 0.0) {
            j -= d;
        }
        if (e < 0.0) {
            h -= e;
        } else if (e > 0.0) {
            k -= e;
        }
        if (f < 0.0) {
            i -= f;
        } else if (f > 0.0) {
            l -= f;
        }
        return new Box(g, h, i, j, k, l);
    }

    public Box stretch(Vec3d arg) {
        return this.stretch(arg.x, arg.y, arg.z);
    }

    public Box stretch(double d, double e, double f) {
        double g = this.x1;
        double h = this.y1;
        double i = this.z1;
        double j = this.x2;
        double k = this.y2;
        double l = this.z2;
        if (d < 0.0) {
            g += d;
        } else if (d > 0.0) {
            j += d;
        }
        if (e < 0.0) {
            h += e;
        } else if (e > 0.0) {
            k += e;
        }
        if (f < 0.0) {
            i += f;
        } else if (f > 0.0) {
            l += f;
        }
        return new Box(g, h, i, j, k, l);
    }

    public Box expand(double d, double e, double f) {
        double g = this.x1 - d;
        double h = this.y1 - e;
        double i = this.z1 - f;
        double j = this.x2 + d;
        double k = this.y2 + e;
        double l = this.z2 + f;
        return new Box(g, h, i, j, k, l);
    }

    public Box expand(double d) {
        return this.expand(d, d, d);
    }

    public Box intersection(Box arg) {
        double d = Math.max(this.x1, arg.x1);
        double e = Math.max(this.y1, arg.y1);
        double f = Math.max(this.z1, arg.z1);
        double g = Math.min(this.x2, arg.x2);
        double h = Math.min(this.y2, arg.y2);
        double i = Math.min(this.z2, arg.z2);
        return new Box(d, e, f, g, h, i);
    }

    public Box union(Box arg) {
        double d = Math.min(this.x1, arg.x1);
        double e = Math.min(this.y1, arg.y1);
        double f = Math.min(this.z1, arg.z1);
        double g = Math.max(this.x2, arg.x2);
        double h = Math.max(this.y2, arg.y2);
        double i = Math.max(this.z2, arg.z2);
        return new Box(d, e, f, g, h, i);
    }

    public Box offset(double d, double e, double f) {
        return new Box(this.x1 + d, this.y1 + e, this.z1 + f, this.x2 + d, this.y2 + e, this.z2 + f);
    }

    public Box offset(BlockPos arg) {
        return new Box(this.x1 + (double)arg.getX(), this.y1 + (double)arg.getY(), this.z1 + (double)arg.getZ(), this.x2 + (double)arg.getX(), this.y2 + (double)arg.getY(), this.z2 + (double)arg.getZ());
    }

    public Box offset(Vec3d arg) {
        return this.offset(arg.x, arg.y, arg.z);
    }

    public boolean intersects(Box arg) {
        return this.intersects(arg.x1, arg.y1, arg.z1, arg.x2, arg.y2, arg.z2);
    }

    public boolean intersects(double d, double e, double f, double g, double h, double i) {
        return this.x1 < g && this.x2 > d && this.y1 < h && this.y2 > e && this.z1 < i && this.z2 > f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean intersects(Vec3d arg, Vec3d arg2) {
        return this.intersects(Math.min(arg.x, arg2.x), Math.min(arg.y, arg2.y), Math.min(arg.z, arg2.z), Math.max(arg.x, arg2.x), Math.max(arg.y, arg2.y), Math.max(arg.z, arg2.z));
    }

    public boolean contains(Vec3d arg) {
        return this.contains(arg.x, arg.y, arg.z);
    }

    public boolean contains(double d, double e, double f) {
        return d >= this.x1 && d < this.x2 && e >= this.y1 && e < this.y2 && f >= this.z1 && f < this.z2;
    }

    public double getAverageSideLength() {
        double d = this.getXLength();
        double e = this.getYLength();
        double f = this.getZLength();
        return (d + e + f) / 3.0;
    }

    public double getXLength() {
        return this.x2 - this.x1;
    }

    public double getYLength() {
        return this.y2 - this.y1;
    }

    public double getZLength() {
        return this.z2 - this.z1;
    }

    public Box contract(double d) {
        return this.expand(-d);
    }

    public Optional<Vec3d> rayTrace(Vec3d arg, Vec3d arg2) {
        double[] ds = new double[]{1.0};
        double d = arg2.x - arg.x;
        double e = arg2.y - arg.y;
        double f = arg2.z - arg.z;
        Direction lv = Box.traceCollisionSide(this, arg, ds, null, d, e, f);
        if (lv == null) {
            return Optional.empty();
        }
        double g = ds[0];
        return Optional.of(arg.add(g * d, g * e, g * f));
    }

    @Nullable
    public static BlockHitResult rayTrace(Iterable<Box> iterable, Vec3d arg, Vec3d arg2, BlockPos arg3) {
        double[] ds = new double[]{1.0};
        Direction lv = null;
        double d = arg2.x - arg.x;
        double e = arg2.y - arg.y;
        double f = arg2.z - arg.z;
        for (Box lv2 : iterable) {
            lv = Box.traceCollisionSide(lv2.offset(arg3), arg, ds, lv, d, e, f);
        }
        if (lv == null) {
            return null;
        }
        double g = ds[0];
        return new BlockHitResult(arg.add(g * d, g * e, g * f), lv, arg3, false);
    }

    @Nullable
    private static Direction traceCollisionSide(Box arg, Vec3d arg2, double[] ds, @Nullable Direction arg3, double d, double e, double f) {
        if (d > 1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, d, e, f, arg.x1, arg.y1, arg.y2, arg.z1, arg.z2, Direction.WEST, arg2.x, arg2.y, arg2.z);
        } else if (d < -1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, d, e, f, arg.x2, arg.y1, arg.y2, arg.z1, arg.z2, Direction.EAST, arg2.x, arg2.y, arg2.z);
        }
        if (e > 1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, e, f, d, arg.y1, arg.z1, arg.z2, arg.x1, arg.x2, Direction.DOWN, arg2.y, arg2.z, arg2.x);
        } else if (e < -1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, e, f, d, arg.y2, arg.z1, arg.z2, arg.x1, arg.x2, Direction.UP, arg2.y, arg2.z, arg2.x);
        }
        if (f > 1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, f, d, e, arg.z1, arg.x1, arg.x2, arg.y1, arg.y2, Direction.NORTH, arg2.z, arg2.x, arg2.y);
        } else if (f < -1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, f, d, e, arg.z2, arg.x1, arg.x2, arg.y1, arg.y2, Direction.SOUTH, arg2.z, arg2.x, arg2.y);
        }
        return arg3;
    }

    @Nullable
    private static Direction traceCollisionSide(double[] ds, @Nullable Direction arg, double d, double e, double f, double g, double h, double i, double j, double k, Direction arg2, double l, double m, double n) {
        double o = (g - l) / d;
        double p = m + o * e;
        double q = n + o * f;
        if (0.0 < o && o < ds[0] && h - 1.0E-7 < p && p < i + 1.0E-7 && j - 1.0E-7 < q && q < k + 1.0E-7) {
            ds[0] = o;
            return arg2;
        }
        return arg;
    }

    public String toString() {
        return "box[" + this.x1 + ", " + this.y1 + ", " + this.z1 + "] -> [" + this.x2 + ", " + this.y2 + ", " + this.z2 + "]";
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isValid() {
        return Double.isNaN(this.x1) || Double.isNaN(this.y1) || Double.isNaN(this.z1) || Double.isNaN(this.x2) || Double.isNaN(this.y2) || Double.isNaN(this.z2);
    }

    public Vec3d getCenter() {
        return new Vec3d(MathHelper.lerp(0.5, this.x1, this.x2), MathHelper.lerp(0.5, this.y1, this.y2), MathHelper.lerp(0.5, this.z1, this.z2));
    }
}

