/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;

public interface BlockView {
    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLuminance(BlockPos pos) {
        return this.getBlockState(pos).getLuminance();
    }

    default public int getMaxLightLevel() {
        return 15;
    }

    default public int getHeight() {
        return 256;
    }

    default public Stream<BlockState> method_29546(Box arg) {
        return BlockPos.method_29715(arg).map(this::getBlockState);
    }

    default public BlockHitResult rayTrace(RayTraceContext context) {
        return BlockView.rayTrace(context, (arg, arg2) -> {
            BlockState lv = this.getBlockState((BlockPos)arg2);
            FluidState lv2 = this.getFluidState((BlockPos)arg2);
            Vec3d lv3 = arg.getStart();
            Vec3d lv4 = arg.getEnd();
            VoxelShape lv5 = arg.getBlockShape(lv, this, (BlockPos)arg2);
            BlockHitResult lv6 = this.rayTraceBlock(lv3, lv4, (BlockPos)arg2, lv5, lv);
            VoxelShape lv7 = arg.getFluidShape(lv2, this, (BlockPos)arg2);
            BlockHitResult lv8 = lv7.rayTrace(lv3, lv4, (BlockPos)arg2);
            double d = lv6 == null ? Double.MAX_VALUE : arg.getStart().squaredDistanceTo(lv6.getPos());
            double e = lv8 == null ? Double.MAX_VALUE : arg.getStart().squaredDistanceTo(lv8.getPos());
            return d <= e ? lv6 : lv8;
        }, arg -> {
            Vec3d lv = arg.getStart().subtract(arg.getEnd());
            return BlockHitResult.createMissed(arg.getEnd(), Direction.getFacing(lv.x, lv.y, lv.z), new BlockPos(arg.getEnd()));
        });
    }

    @Nullable
    default public BlockHitResult rayTraceBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state) {
        BlockHitResult lv2;
        BlockHitResult lv = shape.rayTrace(start, end, pos);
        if (lv != null && (lv2 = state.getRayTraceShape(this, pos).rayTrace(start, end, pos)) != null && lv2.getPos().subtract(start).lengthSquared() < lv.getPos().subtract(start).lengthSquared()) {
            return lv.withSide(lv2.getSide());
        }
        return lv;
    }

    default public double getDismountHeight(VoxelShape blockCollisionShape, Supplier<VoxelShape> belowBlockCollisionShapeGetter) {
        if (!blockCollisionShape.isEmpty()) {
            return blockCollisionShape.getMax(Direction.Axis.Y);
        }
        double d = belowBlockCollisionShapeGetter.get().getMax(Direction.Axis.Y);
        if (d >= 1.0) {
            return d - 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    default public double getDismountHeight(BlockPos pos) {
        return this.getDismountHeight(this.getBlockState(pos).getCollisionShape(this, pos), () -> {
            BlockPos lv = pos.down();
            return this.getBlockState(lv).getCollisionShape(this, lv);
        });
    }

    public static <T> T rayTrace(RayTraceContext arg, BiFunction<RayTraceContext, BlockPos, T> context, Function<RayTraceContext, T> blockRayTracer) {
        int l;
        int k;
        Vec3d lv2;
        Vec3d lv = arg.getStart();
        if (lv.equals(lv2 = arg.getEnd())) {
            return blockRayTracer.apply(arg);
        }
        double d = MathHelper.lerp(-1.0E-7, lv2.x, lv.x);
        double e = MathHelper.lerp(-1.0E-7, lv2.y, lv.y);
        double f = MathHelper.lerp(-1.0E-7, lv2.z, lv.z);
        double g = MathHelper.lerp(-1.0E-7, lv.x, lv2.x);
        double h = MathHelper.lerp(-1.0E-7, lv.y, lv2.y);
        double i = MathHelper.lerp(-1.0E-7, lv.z, lv2.z);
        int j = MathHelper.floor(g);
        BlockPos.Mutable lv3 = new BlockPos.Mutable(j, k = MathHelper.floor(h), l = MathHelper.floor(i));
        T object = context.apply(arg, lv3);
        if (object != null) {
            return object;
        }
        double m = d - g;
        double n = e - h;
        double o = f - i;
        int p = MathHelper.sign(m);
        int q = MathHelper.sign(n);
        int r = MathHelper.sign(o);
        double s = p == 0 ? Double.MAX_VALUE : (double)p / m;
        double t = q == 0 ? Double.MAX_VALUE : (double)q / n;
        double u = r == 0 ? Double.MAX_VALUE : (double)r / o;
        double v = s * (p > 0 ? 1.0 - MathHelper.fractionalPart(g) : MathHelper.fractionalPart(g));
        double w = t * (q > 0 ? 1.0 - MathHelper.fractionalPart(h) : MathHelper.fractionalPart(h));
        double x = u * (r > 0 ? 1.0 - MathHelper.fractionalPart(i) : MathHelper.fractionalPart(i));
        while (v <= 1.0 || w <= 1.0 || x <= 1.0) {
            T object2;
            if (v < w) {
                if (v < x) {
                    j += p;
                    v += s;
                } else {
                    l += r;
                    x += u;
                }
            } else if (w < x) {
                k += q;
                w += t;
            } else {
                l += r;
                x += u;
            }
            if ((object2 = context.apply(arg, lv3.set(j, k, l))) == null) continue;
            return object2;
        }
        return blockRayTracer.apply(arg);
    }
}

