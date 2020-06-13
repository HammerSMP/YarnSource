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
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public Box(double d, double e, double f, double g, double h, double i) {
        this.minX = Math.min(d, g);
        this.minY = Math.min(e, h);
        this.minZ = Math.min(f, i);
        this.maxX = Math.max(d, g);
        this.maxY = Math.max(e, h);
        this.maxZ = Math.max(f, i);
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

    public static Box method_29968(Vec3d arg) {
        return new Box(arg.x, arg.y, arg.z, arg.x + 1.0, arg.y + 1.0, arg.z + 1.0);
    }

    public double getMin(Direction.Axis arg) {
        return arg.choose(this.minX, this.minY, this.minZ);
    }

    public double getMax(Direction.Axis arg) {
        return arg.choose(this.maxX, this.maxY, this.maxZ);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Box)) {
            return false;
        }
        Box lv = (Box)object;
        if (Double.compare(lv.minX, this.minX) != 0) {
            return false;
        }
        if (Double.compare(lv.minY, this.minY) != 0) {
            return false;
        }
        if (Double.compare(lv.minZ, this.minZ) != 0) {
            return false;
        }
        if (Double.compare(lv.maxX, this.maxX) != 0) {
            return false;
        }
        if (Double.compare(lv.maxY, this.maxY) != 0) {
            return false;
        }
        return Double.compare(lv.maxZ, this.maxZ) == 0;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.minX);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.minY);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.minZ);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxX);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxY);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxZ);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public Box shrink(double d, double e, double f) {
        double g = this.minX;
        double h = this.minY;
        double i = this.minZ;
        double j = this.maxX;
        double k = this.maxY;
        double l = this.maxZ;
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
        double g = this.minX;
        double h = this.minY;
        double i = this.minZ;
        double j = this.maxX;
        double k = this.maxY;
        double l = this.maxZ;
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
        double g = this.minX - d;
        double h = this.minY - e;
        double i = this.minZ - f;
        double j = this.maxX + d;
        double k = this.maxY + e;
        double l = this.maxZ + f;
        return new Box(g, h, i, j, k, l);
    }

    public Box expand(double d) {
        return this.expand(d, d, d);
    }

    public Box intersection(Box arg) {
        double d = Math.max(this.minX, arg.minX);
        double e = Math.max(this.minY, arg.minY);
        double f = Math.max(this.minZ, arg.minZ);
        double g = Math.min(this.maxX, arg.maxX);
        double h = Math.min(this.maxY, arg.maxY);
        double i = Math.min(this.maxZ, arg.maxZ);
        return new Box(d, e, f, g, h, i);
    }

    public Box union(Box arg) {
        double d = Math.min(this.minX, arg.minX);
        double e = Math.min(this.minY, arg.minY);
        double f = Math.min(this.minZ, arg.minZ);
        double g = Math.max(this.maxX, arg.maxX);
        double h = Math.max(this.maxY, arg.maxY);
        double i = Math.max(this.maxZ, arg.maxZ);
        return new Box(d, e, f, g, h, i);
    }

    public Box offset(double d, double e, double f) {
        return new Box(this.minX + d, this.minY + e, this.minZ + f, this.maxX + d, this.maxY + e, this.maxZ + f);
    }

    public Box offset(BlockPos arg) {
        return new Box(this.minX + (double)arg.getX(), this.minY + (double)arg.getY(), this.minZ + (double)arg.getZ(), this.maxX + (double)arg.getX(), this.maxY + (double)arg.getY(), this.maxZ + (double)arg.getZ());
    }

    public Box offset(Vec3d arg) {
        return this.offset(arg.x, arg.y, arg.z);
    }

    public boolean intersects(Box arg) {
        return this.intersects(arg.minX, arg.minY, arg.minZ, arg.maxX, arg.maxY, arg.maxZ);
    }

    public boolean intersects(double d, double e, double f, double g, double h, double i) {
        return this.minX < g && this.maxX > d && this.minY < h && this.maxY > e && this.minZ < i && this.maxZ > f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean intersects(Vec3d arg, Vec3d arg2) {
        return this.intersects(Math.min(arg.x, arg2.x), Math.min(arg.y, arg2.y), Math.min(arg.z, arg2.z), Math.max(arg.x, arg2.x), Math.max(arg.y, arg2.y), Math.max(arg.z, arg2.z));
    }

    public boolean contains(Vec3d arg) {
        return this.contains(arg.x, arg.y, arg.z);
    }

    public boolean contains(double d, double e, double f) {
        return d >= this.minX && d < this.maxX && e >= this.minY && e < this.maxY && f >= this.minZ && f < this.maxZ;
    }

    public double getAverageSideLength() {
        double d = this.getXLength();
        double e = this.getYLength();
        double f = this.getZLength();
        return (d + e + f) / 3.0;
    }

    public double getXLength() {
        return this.maxX - this.minX;
    }

    public double getYLength() {
        return this.maxY - this.minY;
    }

    public double getZLength() {
        return this.maxZ - this.minZ;
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
            arg3 = Box.traceCollisionSide(ds, arg3, d, e, f, arg.minX, arg.minY, arg.maxY, arg.minZ, arg.maxZ, Direction.WEST, arg2.x, arg2.y, arg2.z);
        } else if (d < -1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, d, e, f, arg.maxX, arg.minY, arg.maxY, arg.minZ, arg.maxZ, Direction.EAST, arg2.x, arg2.y, arg2.z);
        }
        if (e > 1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, e, f, d, arg.minY, arg.minZ, arg.maxZ, arg.minX, arg.maxX, Direction.DOWN, arg2.y, arg2.z, arg2.x);
        } else if (e < -1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, e, f, d, arg.maxY, arg.minZ, arg.maxZ, arg.minX, arg.maxX, Direction.UP, arg2.y, arg2.z, arg2.x);
        }
        if (f > 1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, f, d, e, arg.minZ, arg.minX, arg.maxX, arg.minY, arg.maxY, Direction.NORTH, arg2.z, arg2.x, arg2.y);
        } else if (f < -1.0E-7) {
            arg3 = Box.traceCollisionSide(ds, arg3, f, d, e, arg.maxZ, arg.minX, arg.maxX, arg.minY, arg.maxY, Direction.SOUTH, arg2.z, arg2.x, arg2.y);
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
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isValid() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vec3d getCenter() {
        return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
    }

    public static Box method_30048(double d, double e, double f) {
        return new Box(-d / 2.0, -e / 2.0, -f / 2.0, d / 2.0, e / 2.0, f / 2.0);
    }
}

