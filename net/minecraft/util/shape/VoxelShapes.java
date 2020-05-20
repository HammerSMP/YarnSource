/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.math.DoubleMath
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.shape;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.DisjointPairList;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.FractionalPairList;
import net.minecraft.util.shape.IdentityPairList;
import net.minecraft.util.shape.PairList;
import net.minecraft.util.shape.SimplePairList;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.SlicedVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldView;

public final class VoxelShapes {
    private static final VoxelShape FULL_CUBE = Util.make(() -> {
        BitSetVoxelSet lv = new BitSetVoxelSet(1, 1, 1);
        ((VoxelSet)lv).set(0, 0, 0, true, true);
        return new SimpleVoxelShape(lv);
    });
    public static final VoxelShape UNBOUNDED = VoxelShapes.cuboid(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final VoxelShape EMPTY = new ArrayVoxelShape((VoxelSet)new BitSetVoxelSet(0, 0, 0), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}));

    public static VoxelShape empty() {
        return EMPTY;
    }

    public static VoxelShape fullCube() {
        return FULL_CUBE;
    }

    public static VoxelShape cuboid(double d, double e, double f, double g, double h, double i) {
        return VoxelShapes.cuboid(new Box(d, e, f, g, h, i));
    }

    public static VoxelShape cuboid(Box arg) {
        int i = VoxelShapes.findRequiredBitResolution(arg.minX, arg.maxX);
        int j = VoxelShapes.findRequiredBitResolution(arg.minY, arg.maxY);
        int k = VoxelShapes.findRequiredBitResolution(arg.minZ, arg.maxZ);
        if (i < 0 || j < 0 || k < 0) {
            return new ArrayVoxelShape(VoxelShapes.FULL_CUBE.voxels, new double[]{arg.minX, arg.maxX}, new double[]{arg.minY, arg.maxY}, new double[]{arg.minZ, arg.maxZ});
        }
        if (i == 0 && j == 0 && k == 0) {
            return arg.contains(0.5, 0.5, 0.5) ? VoxelShapes.fullCube() : VoxelShapes.empty();
        }
        int l = 1 << i;
        int m = 1 << j;
        int n = 1 << k;
        int o = (int)Math.round(arg.minX * (double)l);
        int p = (int)Math.round(arg.maxX * (double)l);
        int q = (int)Math.round(arg.minY * (double)m);
        int r = (int)Math.round(arg.maxY * (double)m);
        int s = (int)Math.round(arg.minZ * (double)n);
        int t = (int)Math.round(arg.maxZ * (double)n);
        BitSetVoxelSet lv = new BitSetVoxelSet(l, m, n, o, q, s, p, r, t);
        for (long u = (long)o; u < (long)p; ++u) {
            for (long v = (long)q; v < (long)r; ++v) {
                for (long w = (long)s; w < (long)t; ++w) {
                    lv.set((int)u, (int)v, (int)w, false, true);
                }
            }
        }
        return new SimpleVoxelShape(lv);
    }

    private static int findRequiredBitResolution(double d, double e) {
        if (d < -1.0E-7 || e > 1.0000001) {
            return -1;
        }
        for (int i = 0; i <= 3; ++i) {
            boolean bl2;
            double f = d * (double)(1 << i);
            double g = e * (double)(1 << i);
            boolean bl = Math.abs(f - Math.floor(f)) < 1.0E-7;
            boolean bl3 = bl2 = Math.abs(g - Math.floor(g)) < 1.0E-7;
            if (!bl || !bl2) continue;
            return i;
        }
        return -1;
    }

    protected static long lcm(int i, int j) {
        return (long)i * (long)(j / IntMath.gcd((int)i, (int)j));
    }

    public static VoxelShape union(VoxelShape arg, VoxelShape arg2) {
        return VoxelShapes.combineAndSimplify(arg, arg2, BooleanBiFunction.OR);
    }

    public static VoxelShape union(VoxelShape arg, VoxelShape ... args) {
        return Arrays.stream(args).reduce(arg, VoxelShapes::union);
    }

    public static VoxelShape combineAndSimplify(VoxelShape arg, VoxelShape arg2, BooleanBiFunction arg3) {
        return VoxelShapes.combine(arg, arg2, arg3).simplify();
    }

    public static VoxelShape combine(VoxelShape arg, VoxelShape arg2, BooleanBiFunction arg3) {
        if (arg3.apply(false, false)) {
            throw Util.throwOrPause(new IllegalArgumentException());
        }
        if (arg == arg2) {
            return arg3.apply(true, true) ? arg : VoxelShapes.empty();
        }
        boolean bl = arg3.apply(true, false);
        boolean bl2 = arg3.apply(false, true);
        if (arg.isEmpty()) {
            return bl2 ? arg2 : VoxelShapes.empty();
        }
        if (arg2.isEmpty()) {
            return bl ? arg : VoxelShapes.empty();
        }
        PairList lv = VoxelShapes.createListPair(1, arg.getPointPositions(Direction.Axis.X), arg2.getPointPositions(Direction.Axis.X), bl, bl2);
        PairList lv2 = VoxelShapes.createListPair(lv.getPairs().size() - 1, arg.getPointPositions(Direction.Axis.Y), arg2.getPointPositions(Direction.Axis.Y), bl, bl2);
        PairList lv3 = VoxelShapes.createListPair((lv.getPairs().size() - 1) * (lv2.getPairs().size() - 1), arg.getPointPositions(Direction.Axis.Z), arg2.getPointPositions(Direction.Axis.Z), bl, bl2);
        BitSetVoxelSet lv4 = BitSetVoxelSet.combine(arg.voxels, arg2.voxels, lv, lv2, lv3, arg3);
        if (lv instanceof FractionalPairList && lv2 instanceof FractionalPairList && lv3 instanceof FractionalPairList) {
            return new SimpleVoxelShape(lv4);
        }
        return new ArrayVoxelShape((VoxelSet)lv4, lv.getPairs(), lv2.getPairs(), lv3.getPairs());
    }

    public static boolean matchesAnywhere(VoxelShape arg, VoxelShape arg2, BooleanBiFunction arg3) {
        if (arg3.apply(false, false)) {
            throw Util.throwOrPause(new IllegalArgumentException());
        }
        if (arg == arg2) {
            return arg3.apply(true, true);
        }
        if (arg.isEmpty()) {
            return arg3.apply(false, !arg2.isEmpty());
        }
        if (arg2.isEmpty()) {
            return arg3.apply(!arg.isEmpty(), false);
        }
        boolean bl = arg3.apply(true, false);
        boolean bl2 = arg3.apply(false, true);
        for (Direction.Axis lv : AxisCycleDirection.AXES) {
            if (arg.getMaximum(lv) < arg2.getMinimum(lv) - 1.0E-7) {
                return bl || bl2;
            }
            if (!(arg2.getMaximum(lv) < arg.getMinimum(lv) - 1.0E-7)) continue;
            return bl || bl2;
        }
        PairList lv2 = VoxelShapes.createListPair(1, arg.getPointPositions(Direction.Axis.X), arg2.getPointPositions(Direction.Axis.X), bl, bl2);
        PairList lv3 = VoxelShapes.createListPair(lv2.getPairs().size() - 1, arg.getPointPositions(Direction.Axis.Y), arg2.getPointPositions(Direction.Axis.Y), bl, bl2);
        PairList lv4 = VoxelShapes.createListPair((lv2.getPairs().size() - 1) * (lv3.getPairs().size() - 1), arg.getPointPositions(Direction.Axis.Z), arg2.getPointPositions(Direction.Axis.Z), bl, bl2);
        return VoxelShapes.matchesAnywhere(lv2, lv3, lv4, arg.voxels, arg2.voxels, arg3);
    }

    private static boolean matchesAnywhere(PairList arg, PairList arg2, PairList arg3, VoxelSet arg4, VoxelSet arg5, BooleanBiFunction arg6) {
        return !arg.forEachPair((i, j, k2) -> arg2.forEachPair((k, l, m2) -> arg3.forEachPair((m, n, o) -> !arg6.apply(arg4.inBoundsAndContains(i, k, m), arg5.inBoundsAndContains(j, l, n)))));
    }

    public static double calculateMaxOffset(Direction.Axis arg, Box arg2, Stream<VoxelShape> stream, double d) {
        Iterator iterator = stream.iterator();
        while (iterator.hasNext()) {
            if (Math.abs(d) < 1.0E-7) {
                return 0.0;
            }
            d = ((VoxelShape)iterator.next()).calculateMaxDistance(arg, arg2, d);
        }
        return d;
    }

    public static double calculatePushVelocity(Direction.Axis arg, Box arg2, WorldView arg3, double d, ShapeContext arg4, Stream<VoxelShape> stream) {
        return VoxelShapes.calculatePushVelocity(arg2, arg3, d, arg4, AxisCycleDirection.between(arg, Direction.Axis.Z), stream);
    }

    private static double calculatePushVelocity(Box arg, WorldView arg2, double d, ShapeContext arg32, AxisCycleDirection arg4, Stream<VoxelShape> stream) {
        if (arg.getXLength() < 1.0E-6 || arg.getYLength() < 1.0E-6 || arg.getZLength() < 1.0E-6) {
            return d;
        }
        if (Math.abs(d) < 1.0E-7) {
            return 0.0;
        }
        AxisCycleDirection lv = arg4.opposite();
        Direction.Axis lv2 = lv.cycle(Direction.Axis.X);
        Direction.Axis lv3 = lv.cycle(Direction.Axis.Y);
        Direction.Axis lv4 = lv.cycle(Direction.Axis.Z);
        BlockPos.Mutable lv5 = new BlockPos.Mutable();
        int i = MathHelper.floor(arg.getMin(lv2) - 1.0E-7) - 1;
        int j = MathHelper.floor(arg.getMax(lv2) + 1.0E-7) + 1;
        int k = MathHelper.floor(arg.getMin(lv3) - 1.0E-7) - 1;
        int l = MathHelper.floor(arg.getMax(lv3) + 1.0E-7) + 1;
        double e = arg.getMin(lv4) - 1.0E-7;
        double f = arg.getMax(lv4) + 1.0E-7;
        boolean bl = d > 0.0;
        int m = bl ? MathHelper.floor(arg.getMax(lv4) - 1.0E-7) - 1 : MathHelper.floor(arg.getMin(lv4) + 1.0E-7) + 1;
        int n = VoxelShapes.clamp(d, e, f);
        int o = bl ? 1 : -1;
        int p = m;
        while (bl ? p <= n : p >= n) {
            for (int q = i; q <= j; ++q) {
                for (int r = k; r <= l; ++r) {
                    int s = 0;
                    if (q == i || q == j) {
                        ++s;
                    }
                    if (r == k || r == l) {
                        ++s;
                    }
                    if (p == m || p == n) {
                        ++s;
                    }
                    if (s >= 3) continue;
                    lv5.set(lv, q, r, p);
                    BlockState lv6 = arg2.getBlockState(lv5);
                    if (s == 1 && !lv6.exceedsCube() || s == 2 && !lv6.isOf(Blocks.MOVING_PISTON)) continue;
                    d = lv6.getCollisionShape(arg2, lv5, arg32).calculateMaxDistance(lv4, arg.offset(-lv5.getX(), -lv5.getY(), -lv5.getZ()), d);
                    if (Math.abs(d) < 1.0E-7) {
                        return 0.0;
                    }
                    n = VoxelShapes.clamp(d, e, f);
                }
            }
            p += o;
        }
        double[] ds = new double[]{d};
        stream.forEach(arg3 -> {
            ds[0] = arg3.calculateMaxDistance(lv4, arg, ds[0]);
        });
        return ds[0];
    }

    private static int clamp(double d, double e, double f) {
        return d > 0.0 ? MathHelper.floor(f + d) + 1 : MathHelper.floor(e + d) - 1;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean isSideCovered(VoxelShape arg, VoxelShape arg2, Direction arg3) {
        if (arg == VoxelShapes.fullCube() && arg2 == VoxelShapes.fullCube()) {
            return true;
        }
        if (arg2.isEmpty()) {
            return false;
        }
        Direction.Axis lv = arg3.getAxis();
        Direction.AxisDirection lv2 = arg3.getDirection();
        VoxelShape lv3 = lv2 == Direction.AxisDirection.POSITIVE ? arg : arg2;
        VoxelShape lv4 = lv2 == Direction.AxisDirection.POSITIVE ? arg2 : arg;
        BooleanBiFunction lv5 = lv2 == Direction.AxisDirection.POSITIVE ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
        return DoubleMath.fuzzyEquals((double)lv3.getMaximum(lv), (double)1.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)lv4.getMinimum(lv), (double)0.0, (double)1.0E-7) && !VoxelShapes.matchesAnywhere(new SlicedVoxelShape(lv3, lv, lv3.voxels.getSize(lv) - 1), new SlicedVoxelShape(lv4, lv, 0), lv5);
    }

    public static VoxelShape extrudeFace(VoxelShape arg, Direction arg2) {
        int j;
        boolean bl2;
        if (arg == VoxelShapes.fullCube()) {
            return VoxelShapes.fullCube();
        }
        Direction.Axis lv = arg2.getAxis();
        if (arg2.getDirection() == Direction.AxisDirection.POSITIVE) {
            boolean bl = DoubleMath.fuzzyEquals((double)arg.getMaximum(lv), (double)1.0, (double)1.0E-7);
            int i = arg.voxels.getSize(lv) - 1;
        } else {
            bl2 = DoubleMath.fuzzyEquals((double)arg.getMinimum(lv), (double)0.0, (double)1.0E-7);
            j = 0;
        }
        if (!bl2) {
            return VoxelShapes.empty();
        }
        return new SlicedVoxelShape(arg, lv, j);
    }

    public static boolean adjacentSidesCoverSquare(VoxelShape arg, VoxelShape arg2, Direction arg3) {
        VoxelShape lv4;
        if (arg == VoxelShapes.fullCube() || arg2 == VoxelShapes.fullCube()) {
            return true;
        }
        Direction.Axis lv = arg3.getAxis();
        Direction.AxisDirection lv2 = arg3.getDirection();
        VoxelShape lv3 = lv2 == Direction.AxisDirection.POSITIVE ? arg : arg2;
        VoxelShape voxelShape = lv4 = lv2 == Direction.AxisDirection.POSITIVE ? arg2 : arg;
        if (!DoubleMath.fuzzyEquals((double)lv3.getMaximum(lv), (double)1.0, (double)1.0E-7)) {
            lv3 = VoxelShapes.empty();
        }
        if (!DoubleMath.fuzzyEquals((double)lv4.getMinimum(lv), (double)0.0, (double)1.0E-7)) {
            lv4 = VoxelShapes.empty();
        }
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), VoxelShapes.combine(new SlicedVoxelShape(lv3, lv, lv3.voxels.getSize(lv) - 1), new SlicedVoxelShape(lv4, lv, 0), BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }

    public static boolean unionCoversFullCube(VoxelShape arg, VoxelShape arg2) {
        if (arg == VoxelShapes.fullCube() || arg2 == VoxelShapes.fullCube()) {
            return true;
        }
        if (arg.isEmpty() && arg2.isEmpty()) {
            return false;
        }
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), VoxelShapes.combine(arg, arg2, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }

    @VisibleForTesting
    protected static PairList createListPair(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
        long l;
        int j = doubleList.size() - 1;
        int k = doubleList2.size() - 1;
        if (doubleList instanceof FractionalDoubleList && doubleList2 instanceof FractionalDoubleList && (long)i * (l = VoxelShapes.lcm(j, k)) <= 256L) {
            return new FractionalPairList(j, k);
        }
        if (doubleList.getDouble(j) < doubleList2.getDouble(0) - 1.0E-7) {
            return new DisjointPairList(doubleList, doubleList2, false);
        }
        if (doubleList2.getDouble(k) < doubleList.getDouble(0) - 1.0E-7) {
            return new DisjointPairList(doubleList2, doubleList, true);
        }
        if (j == k && Objects.equals((Object)doubleList, (Object)doubleList2)) {
            if (doubleList instanceof IdentityPairList) {
                return (PairList)doubleList;
            }
            if (doubleList2 instanceof IdentityPairList) {
                return (PairList)doubleList2;
            }
            return new IdentityPairList(doubleList);
        }
        return new SimplePairList(doubleList, doubleList2, bl, bl2);
    }

    public static interface BoxConsumer {
        public void consume(double var1, double var3, double var5, double var7, double var9, double var11);
    }
}

