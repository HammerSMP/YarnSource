/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.math.DoubleMath
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.shape;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.OffsetDoubleList;
import net.minecraft.util.shape.SlicedVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShapes;

public abstract class VoxelShape {
    protected final VoxelSet voxels;
    @Nullable
    private VoxelShape[] shapeCache;

    VoxelShape(VoxelSet arg) {
        this.voxels = arg;
    }

    public double getMin(Direction.Axis arg) {
        int i = this.voxels.getMin(arg);
        if (i >= this.voxels.getSize(arg)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.getPointPosition(arg, i);
    }

    public double getMax(Direction.Axis arg) {
        int i = this.voxels.getMax(arg);
        if (i <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getPointPosition(arg, i);
    }

    public Box getBoundingBox() {
        if (this.isEmpty()) {
            throw Util.throwOrPause(new UnsupportedOperationException("No bounds for empty shape."));
        }
        return new Box(this.getMin(Direction.Axis.X), this.getMin(Direction.Axis.Y), this.getMin(Direction.Axis.Z), this.getMax(Direction.Axis.X), this.getMax(Direction.Axis.Y), this.getMax(Direction.Axis.Z));
    }

    protected double getPointPosition(Direction.Axis arg, int i) {
        return this.getPointPositions(arg).getDouble(i);
    }

    protected abstract DoubleList getPointPositions(Direction.Axis var1);

    public boolean isEmpty() {
        return this.voxels.isEmpty();
    }

    public VoxelShape offset(double d, double e, double f) {
        if (this.isEmpty()) {
            return VoxelShapes.empty();
        }
        return new ArrayVoxelShape(this.voxels, (DoubleList)new OffsetDoubleList(this.getPointPositions(Direction.Axis.X), d), (DoubleList)new OffsetDoubleList(this.getPointPositions(Direction.Axis.Y), e), (DoubleList)new OffsetDoubleList(this.getPointPositions(Direction.Axis.Z), f));
    }

    public VoxelShape simplify() {
        VoxelShape[] lvs = new VoxelShape[]{VoxelShapes.empty()};
        this.forEachBox((d, e, f, g, h, i) -> {
            args[0] = VoxelShapes.combine(lvs[0], VoxelShapes.cuboid(d, e, f, g, h, i), BooleanBiFunction.OR);
        });
        return lvs[0];
    }

    @Environment(value=EnvType.CLIENT)
    public void forEachEdge(VoxelShapes.BoxConsumer arg) {
        this.voxels.forEachEdge((i, j, k, l, m, n) -> arg.consume(this.getPointPosition(Direction.Axis.X, i), this.getPointPosition(Direction.Axis.Y, j), this.getPointPosition(Direction.Axis.Z, k), this.getPointPosition(Direction.Axis.X, l), this.getPointPosition(Direction.Axis.Y, m), this.getPointPosition(Direction.Axis.Z, n)), true);
    }

    public void forEachBox(VoxelShapes.BoxConsumer arg) {
        DoubleList doubleList = this.getPointPositions(Direction.Axis.X);
        DoubleList doubleList2 = this.getPointPositions(Direction.Axis.Y);
        DoubleList doubleList3 = this.getPointPositions(Direction.Axis.Z);
        this.voxels.forEachBox((i, j, k, l, m, n) -> arg.consume(doubleList.getDouble(i), doubleList2.getDouble(j), doubleList3.getDouble(k), doubleList.getDouble(l), doubleList2.getDouble(m), doubleList3.getDouble(n)), true);
    }

    public List<Box> getBoundingBoxes() {
        ArrayList list = Lists.newArrayList();
        this.forEachBox((d, e, f, g, h, i) -> list.add(new Box(d, e, f, g, h, i)));
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public double getEndingCoord(Direction.Axis arg, double d, double e) {
        int j;
        Direction.Axis lv = AxisCycleDirection.FORWARD.cycle(arg);
        Direction.Axis lv2 = AxisCycleDirection.BACKWARD.cycle(arg);
        int i = this.getCoordIndex(lv, d);
        int k = this.voxels.getEndingAxisCoord(arg, i, j = this.getCoordIndex(lv2, e));
        if (k <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getPointPosition(arg, k);
    }

    protected int getCoordIndex(Direction.Axis arg, double d) {
        return MathHelper.binarySearch(0, this.voxels.getSize(arg) + 1, i -> {
            if (i < 0) {
                return false;
            }
            if (i > this.voxels.getSize(arg)) {
                return true;
            }
            return d < this.getPointPosition(arg, i);
        }) - 1;
    }

    protected boolean contains(double d, double e, double f) {
        return this.voxels.inBoundsAndContains(this.getCoordIndex(Direction.Axis.X, d), this.getCoordIndex(Direction.Axis.Y, e), this.getCoordIndex(Direction.Axis.Z, f));
    }

    @Nullable
    public BlockHitResult rayTrace(Vec3d arg, Vec3d arg2, BlockPos arg3) {
        if (this.isEmpty()) {
            return null;
        }
        Vec3d lv = arg2.subtract(arg);
        if (lv.lengthSquared() < 1.0E-7) {
            return null;
        }
        Vec3d lv2 = arg.add(lv.multiply(0.001));
        if (this.contains(lv2.x - (double)arg3.getX(), lv2.y - (double)arg3.getY(), lv2.z - (double)arg3.getZ())) {
            return new BlockHitResult(lv2, Direction.getFacing(lv.x, lv.y, lv.z).getOpposite(), arg3, true);
        }
        return Box.rayTrace(this.getBoundingBoxes(), arg, arg2, arg3);
    }

    public VoxelShape getFace(Direction arg) {
        VoxelShape lv2;
        if (this.isEmpty() || this == VoxelShapes.fullCube()) {
            return this;
        }
        if (this.shapeCache != null) {
            VoxelShape lv = this.shapeCache[arg.ordinal()];
            if (lv != null) {
                return lv;
            }
        } else {
            this.shapeCache = new VoxelShape[6];
        }
        this.shapeCache[arg.ordinal()] = lv2 = this.getUncachedFace(arg);
        return lv2;
    }

    private VoxelShape getUncachedFace(Direction arg) {
        Direction.Axis lv = arg.getAxis();
        Direction.AxisDirection lv2 = arg.getDirection();
        DoubleList doubleList = this.getPointPositions(lv);
        if (doubleList.size() == 2 && DoubleMath.fuzzyEquals((double)doubleList.getDouble(0), (double)0.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)doubleList.getDouble(1), (double)1.0, (double)1.0E-7)) {
            return this;
        }
        int i = this.getCoordIndex(lv, lv2 == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
        return new SlicedVoxelShape(this, lv, i);
    }

    public double calculateMaxDistance(Direction.Axis arg, Box arg2, double d) {
        return this.calculateMaxDistance(AxisCycleDirection.between(arg, Direction.Axis.X), arg2, d);
    }

    protected double calculateMaxDistance(AxisCycleDirection arg, Box arg2, double d) {
        block11: {
            int n;
            int l;
            double f;
            Direction.Axis lv2;
            AxisCycleDirection lv;
            block10: {
                if (this.isEmpty()) {
                    return d;
                }
                if (Math.abs(d) < 1.0E-7) {
                    return 0.0;
                }
                lv = arg.opposite();
                lv2 = lv.cycle(Direction.Axis.X);
                Direction.Axis lv3 = lv.cycle(Direction.Axis.Y);
                Direction.Axis lv4 = lv.cycle(Direction.Axis.Z);
                double e = arg2.getMax(lv2);
                f = arg2.getMin(lv2);
                int i = this.getCoordIndex(lv2, f + 1.0E-7);
                int j = this.getCoordIndex(lv2, e - 1.0E-7);
                int k = Math.max(0, this.getCoordIndex(lv3, arg2.getMin(lv3) + 1.0E-7));
                l = Math.min(this.voxels.getSize(lv3), this.getCoordIndex(lv3, arg2.getMax(lv3) - 1.0E-7) + 1);
                int m = Math.max(0, this.getCoordIndex(lv4, arg2.getMin(lv4) + 1.0E-7));
                n = Math.min(this.voxels.getSize(lv4), this.getCoordIndex(lv4, arg2.getMax(lv4) - 1.0E-7) + 1);
                int o = this.voxels.getSize(lv2);
                if (!(d > 0.0)) break block10;
                for (int p = j + 1; p < o; ++p) {
                    for (int q = k; q < l; ++q) {
                        for (int r = m; r < n; ++r) {
                            if (!this.voxels.inBoundsAndContains(lv, p, q, r)) continue;
                            double g = this.getPointPosition(lv2, p) - e;
                            if (g >= -1.0E-7) {
                                d = Math.min(d, g);
                            }
                            return d;
                        }
                    }
                }
                break block11;
            }
            if (!(d < 0.0)) break block11;
            for (int s = i - 1; s >= 0; --s) {
                for (int t = k; t < l; ++t) {
                    for (int u = m; u < n; ++u) {
                        if (!this.voxels.inBoundsAndContains(lv, s, t, u)) continue;
                        double h = this.getPointPosition(lv2, s + 1) - f;
                        if (h <= 1.0E-7) {
                            d = Math.max(d, h);
                        }
                        return d;
                    }
                }
            }
        }
        return d;
    }

    public String toString() {
        return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
    }
}

