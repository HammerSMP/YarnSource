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

    VoxelShape(VoxelSet voxels) {
        this.voxels = voxels;
    }

    public double getMin(Direction.Axis axis) {
        int i = this.voxels.getMin(axis);
        if (i >= this.voxels.getSize(axis)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.getPointPosition(axis, i);
    }

    public double getMax(Direction.Axis axis) {
        int i = this.voxels.getMax(axis);
        if (i <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getPointPosition(axis, i);
    }

    public Box getBoundingBox() {
        if (this.isEmpty()) {
            throw Util.throwOrPause(new UnsupportedOperationException("No bounds for empty shape."));
        }
        return new Box(this.getMin(Direction.Axis.X), this.getMin(Direction.Axis.Y), this.getMin(Direction.Axis.Z), this.getMax(Direction.Axis.X), this.getMax(Direction.Axis.Y), this.getMax(Direction.Axis.Z));
    }

    protected double getPointPosition(Direction.Axis axis, int index) {
        return this.getPointPositions(axis).getDouble(index);
    }

    protected abstract DoubleList getPointPositions(Direction.Axis var1);

    public boolean isEmpty() {
        return this.voxels.isEmpty();
    }

    public VoxelShape offset(double x, double y, double z) {
        if (this.isEmpty()) {
            return VoxelShapes.empty();
        }
        return new ArrayVoxelShape(this.voxels, (DoubleList)new OffsetDoubleList(this.getPointPositions(Direction.Axis.X), x), (DoubleList)new OffsetDoubleList(this.getPointPositions(Direction.Axis.Y), y), (DoubleList)new OffsetDoubleList(this.getPointPositions(Direction.Axis.Z), z));
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
    public double getEndingCoord(Direction.Axis arg, double from, double to) {
        int j;
        Direction.Axis lv = AxisCycleDirection.FORWARD.cycle(arg);
        Direction.Axis lv2 = AxisCycleDirection.BACKWARD.cycle(arg);
        int i = this.getCoordIndex(lv, from);
        int k = this.voxels.getEndingAxisCoord(arg, i, j = this.getCoordIndex(lv2, to));
        if (k <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getPointPosition(arg, k);
    }

    protected int getCoordIndex(Direction.Axis arg, double coord) {
        return MathHelper.binarySearch(0, this.voxels.getSize(arg) + 1, i -> {
            if (i < 0) {
                return false;
            }
            if (i > this.voxels.getSize(arg)) {
                return true;
            }
            return coord < this.getPointPosition(arg, i);
        }) - 1;
    }

    protected boolean contains(double x, double y, double z) {
        return this.voxels.inBoundsAndContains(this.getCoordIndex(Direction.Axis.X, x), this.getCoordIndex(Direction.Axis.Y, y), this.getCoordIndex(Direction.Axis.Z, z));
    }

    @Nullable
    public BlockHitResult rayTrace(Vec3d start, Vec3d end, BlockPos pos) {
        if (this.isEmpty()) {
            return null;
        }
        Vec3d lv = end.subtract(start);
        if (lv.lengthSquared() < 1.0E-7) {
            return null;
        }
        Vec3d lv2 = start.add(lv.multiply(0.001));
        if (this.contains(lv2.x - (double)pos.getX(), lv2.y - (double)pos.getY(), lv2.z - (double)pos.getZ())) {
            return new BlockHitResult(lv2, Direction.getFacing(lv.x, lv.y, lv.z).getOpposite(), pos, true);
        }
        return Box.rayTrace(this.getBoundingBoxes(), start, end, pos);
    }

    public VoxelShape getFace(Direction facing) {
        VoxelShape lv2;
        if (this.isEmpty() || this == VoxelShapes.fullCube()) {
            return this;
        }
        if (this.shapeCache != null) {
            VoxelShape lv = this.shapeCache[facing.ordinal()];
            if (lv != null) {
                return lv;
            }
        } else {
            this.shapeCache = new VoxelShape[6];
        }
        this.shapeCache[facing.ordinal()] = lv2 = this.getUncachedFace(facing);
        return lv2;
    }

    private VoxelShape getUncachedFace(Direction facing) {
        Direction.Axis lv = facing.getAxis();
        Direction.AxisDirection lv2 = facing.getDirection();
        DoubleList doubleList = this.getPointPositions(lv);
        if (doubleList.size() == 2 && DoubleMath.fuzzyEquals((double)doubleList.getDouble(0), (double)0.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)doubleList.getDouble(1), (double)1.0, (double)1.0E-7)) {
            return this;
        }
        int i = this.getCoordIndex(lv, lv2 == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
        return new SlicedVoxelShape(this, lv, i);
    }

    public double calculateMaxDistance(Direction.Axis axis, Box box, double maxDist) {
        return this.calculateMaxDistance(AxisCycleDirection.between(axis, Direction.Axis.X), box, maxDist);
    }

    protected double calculateMaxDistance(AxisCycleDirection axisCycle, Box box, double maxDist) {
        block11: {
            int n;
            int l;
            double f;
            Direction.Axis lv2;
            AxisCycleDirection lv;
            block10: {
                if (this.isEmpty()) {
                    return maxDist;
                }
                if (Math.abs(maxDist) < 1.0E-7) {
                    return 0.0;
                }
                lv = axisCycle.opposite();
                lv2 = lv.cycle(Direction.Axis.X);
                Direction.Axis lv3 = lv.cycle(Direction.Axis.Y);
                Direction.Axis lv4 = lv.cycle(Direction.Axis.Z);
                double e = box.getMax(lv2);
                f = box.getMin(lv2);
                int i = this.getCoordIndex(lv2, f + 1.0E-7);
                int j = this.getCoordIndex(lv2, e - 1.0E-7);
                int k = Math.max(0, this.getCoordIndex(lv3, box.getMin(lv3) + 1.0E-7));
                l = Math.min(this.voxels.getSize(lv3), this.getCoordIndex(lv3, box.getMax(lv3) - 1.0E-7) + 1);
                int m = Math.max(0, this.getCoordIndex(lv4, box.getMin(lv4) + 1.0E-7));
                n = Math.min(this.voxels.getSize(lv4), this.getCoordIndex(lv4, box.getMax(lv4) - 1.0E-7) + 1);
                int o = this.voxels.getSize(lv2);
                if (!(maxDist > 0.0)) break block10;
                for (int p = j + 1; p < o; ++p) {
                    for (int q = k; q < l; ++q) {
                        for (int r = m; r < n; ++r) {
                            if (!this.voxels.inBoundsAndContains(lv, p, q, r)) continue;
                            double g = this.getPointPosition(lv2, p) - e;
                            if (g >= -1.0E-7) {
                                maxDist = Math.min(maxDist, g);
                            }
                            return maxDist;
                        }
                    }
                }
                break block11;
            }
            if (!(maxDist < 0.0)) break block11;
            for (int s = i - 1; s >= 0; --s) {
                for (int t = k; t < l; ++t) {
                    for (int u = m; u < n; ++u) {
                        if (!this.voxels.inBoundsAndContains(lv, s, t, u)) continue;
                        double h = this.getPointPosition(lv2, s + 1) - f;
                        if (h <= 1.0E-7) {
                            maxDist = Math.max(maxDist, h);
                        }
                        return maxDist;
                    }
                }
            }
        }
        return maxDist;
    }

    public String toString() {
        return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
    }
}

