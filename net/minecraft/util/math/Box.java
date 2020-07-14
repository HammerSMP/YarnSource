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

    public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public Box(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public Box(BlockPos pos1, BlockPos pos2) {
        this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public Box(Vec3d pos1, Vec3d pos2) {
        this(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

    public static Box from(BlockBox mutable) {
        return new Box(mutable.minX, mutable.minY, mutable.minZ, mutable.maxX + 1, mutable.maxY + 1, mutable.maxZ + 1);
    }

    public static Box method_29968(Vec3d arg) {
        return new Box(arg.x, arg.y, arg.z, arg.x + 1.0, arg.y + 1.0, arg.z + 1.0);
    }

    public double getMin(Direction.Axis axis) {
        return axis.choose(this.minX, this.minY, this.minZ);
    }

    public double getMax(Direction.Axis axis) {
        return axis.choose(this.maxX, this.maxY, this.maxZ);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Box)) {
            return false;
        }
        Box lv = (Box)o;
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

    public Box shrink(double x, double y, double z) {
        double g = this.minX;
        double h = this.minY;
        double i = this.minZ;
        double j = this.maxX;
        double k = this.maxY;
        double l = this.maxZ;
        if (x < 0.0) {
            g -= x;
        } else if (x > 0.0) {
            j -= x;
        }
        if (y < 0.0) {
            h -= y;
        } else if (y > 0.0) {
            k -= y;
        }
        if (z < 0.0) {
            i -= z;
        } else if (z > 0.0) {
            l -= z;
        }
        return new Box(g, h, i, j, k, l);
    }

    public Box stretch(Vec3d scale) {
        return this.stretch(scale.x, scale.y, scale.z);
    }

    public Box stretch(double x, double y, double z) {
        double g = this.minX;
        double h = this.minY;
        double i = this.minZ;
        double j = this.maxX;
        double k = this.maxY;
        double l = this.maxZ;
        if (x < 0.0) {
            g += x;
        } else if (x > 0.0) {
            j += x;
        }
        if (y < 0.0) {
            h += y;
        } else if (y > 0.0) {
            k += y;
        }
        if (z < 0.0) {
            i += z;
        } else if (z > 0.0) {
            l += z;
        }
        return new Box(g, h, i, j, k, l);
    }

    public Box expand(double x, double y, double z) {
        double g = this.minX - x;
        double h = this.minY - y;
        double i = this.minZ - z;
        double j = this.maxX + x;
        double k = this.maxY + y;
        double l = this.maxZ + z;
        return new Box(g, h, i, j, k, l);
    }

    public Box expand(double value) {
        return this.expand(value, value, value);
    }

    public Box intersection(Box box) {
        double d = Math.max(this.minX, box.minX);
        double e = Math.max(this.minY, box.minY);
        double f = Math.max(this.minZ, box.minZ);
        double g = Math.min(this.maxX, box.maxX);
        double h = Math.min(this.maxY, box.maxY);
        double i = Math.min(this.maxZ, box.maxZ);
        return new Box(d, e, f, g, h, i);
    }

    public Box union(Box box) {
        double d = Math.min(this.minX, box.minX);
        double e = Math.min(this.minY, box.minY);
        double f = Math.min(this.minZ, box.minZ);
        double g = Math.max(this.maxX, box.maxX);
        double h = Math.max(this.maxY, box.maxY);
        double i = Math.max(this.maxZ, box.maxZ);
        return new Box(d, e, f, g, h, i);
    }

    public Box offset(double x, double y, double z) {
        return new Box(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public Box offset(BlockPos blockPos) {
        return new Box(this.minX + (double)blockPos.getX(), this.minY + (double)blockPos.getY(), this.minZ + (double)blockPos.getZ(), this.maxX + (double)blockPos.getX(), this.maxY + (double)blockPos.getY(), this.maxZ + (double)blockPos.getZ());
    }

    public Box offset(Vec3d vec3d) {
        return this.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean intersects(Box box) {
        return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean intersects(Vec3d from, Vec3d to) {
        return this.intersects(Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z), Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z));
    }

    public boolean contains(Vec3d vec) {
        return this.contains(vec.x, vec.y, vec.z);
    }

    public boolean contains(double x, double y, double z) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
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

    public Box contract(double value) {
        return this.expand(-value);
    }

    public Optional<Vec3d> rayTrace(Vec3d min, Vec3d max) {
        double[] ds = new double[]{1.0};
        double d = max.x - min.x;
        double e = max.y - min.y;
        double f = max.z - min.z;
        Direction lv = Box.traceCollisionSide(this, min, ds, null, d, e, f);
        if (lv == null) {
            return Optional.empty();
        }
        double g = ds[0];
        return Optional.of(min.add(g * d, g * e, g * f));
    }

    @Nullable
    public static BlockHitResult rayTrace(Iterable<Box> boxes, Vec3d from, Vec3d to, BlockPos pos) {
        double[] ds = new double[]{1.0};
        Direction lv = null;
        double d = to.x - from.x;
        double e = to.y - from.y;
        double f = to.z - from.z;
        for (Box lv2 : boxes) {
            lv = Box.traceCollisionSide(lv2.offset(pos), from, ds, lv, d, e, f);
        }
        if (lv == null) {
            return null;
        }
        double g = ds[0];
        return new BlockHitResult(from.add(g * d, g * e, g * f), lv, pos, false);
    }

    @Nullable
    private static Direction traceCollisionSide(Box box, Vec3d intersectingVector, double[] traceDistanceResult, @Nullable Direction approachDirection, double xDelta, double yDelta, double zDelta) {
        if (xDelta > 1.0E-7) {
            approachDirection = Box.traceCollisionSide(traceDistanceResult, approachDirection, xDelta, yDelta, zDelta, box.minX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.WEST, intersectingVector.x, intersectingVector.y, intersectingVector.z);
        } else if (xDelta < -1.0E-7) {
            approachDirection = Box.traceCollisionSide(traceDistanceResult, approachDirection, xDelta, yDelta, zDelta, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.EAST, intersectingVector.x, intersectingVector.y, intersectingVector.z);
        }
        if (yDelta > 1.0E-7) {
            approachDirection = Box.traceCollisionSide(traceDistanceResult, approachDirection, yDelta, zDelta, xDelta, box.minY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.DOWN, intersectingVector.y, intersectingVector.z, intersectingVector.x);
        } else if (yDelta < -1.0E-7) {
            approachDirection = Box.traceCollisionSide(traceDistanceResult, approachDirection, yDelta, zDelta, xDelta, box.maxY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.UP, intersectingVector.y, intersectingVector.z, intersectingVector.x);
        }
        if (zDelta > 1.0E-7) {
            approachDirection = Box.traceCollisionSide(traceDistanceResult, approachDirection, zDelta, xDelta, yDelta, box.minZ, box.minX, box.maxX, box.minY, box.maxY, Direction.NORTH, intersectingVector.z, intersectingVector.x, intersectingVector.y);
        } else if (zDelta < -1.0E-7) {
            approachDirection = Box.traceCollisionSide(traceDistanceResult, approachDirection, zDelta, xDelta, yDelta, box.maxZ, box.minX, box.maxX, box.minY, box.maxY, Direction.SOUTH, intersectingVector.z, intersectingVector.x, intersectingVector.y);
        }
        return approachDirection;
    }

    @Nullable
    private static Direction traceCollisionSide(double[] traceDistanceResult, @Nullable Direction approachDirection, double xDelta, double yDelta, double zDelta, double begin, double minX, double maxX, double minZ, double maxZ, Direction resultDirection, double startX, double startY, double startZ) {
        double o = (begin - startX) / xDelta;
        double p = startY + o * yDelta;
        double q = startZ + o * zDelta;
        if (0.0 < o && o < traceDistanceResult[0] && minX - 1.0E-7 < p && p < maxX + 1.0E-7 && minZ - 1.0E-7 < q && q < maxZ + 1.0E-7) {
            traceDistanceResult[0] = o;
            return resultDirection;
        }
        return approachDirection;
    }

    public String toString() {
        return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
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

