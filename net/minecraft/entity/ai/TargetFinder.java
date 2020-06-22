/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetFinder {
    @Nullable
    public static Vec3d findTarget(MobEntityWithAi arg, int i, int j) {
        return TargetFinder.findTarget(arg, i, j, 0, null, true, 1.5707963705062866, arg::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi arg, int i, int j, int k, @Nullable Vec3d arg2, double d) {
        return TargetFinder.findTarget(arg, i, j, k, arg2, true, d, arg::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi arg, int i, int j) {
        return TargetFinder.findGroundTarget(arg, i, j, arg::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi arg, int i, int j, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return TargetFinder.findTarget(arg, i, j, 0, null, false, 0.0, toDoubleFunction, true, 0, 0, true);
    }

    @Nullable
    public static Vec3d findAirTarget(MobEntityWithAi arg, int i, int j, Vec3d arg2, float f, int k, int l) {
        return TargetFinder.findTarget(arg, i, j, 0, arg2, false, f, arg::getPathfindingFavor, true, k, l, true);
    }

    @Nullable
    public static Vec3d method_27929(MobEntityWithAi arg, int i, int j, Vec3d arg2) {
        Vec3d lv = arg2.subtract(arg.getX(), arg.getY(), arg.getZ());
        return TargetFinder.findTarget(arg, i, j, 0, lv, false, 1.5707963705062866, arg::getPathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi arg, int i, int j, Vec3d arg2) {
        Vec3d lv = arg2.subtract(arg.getX(), arg.getY(), arg.getZ());
        return TargetFinder.findTarget(arg, i, j, 0, lv, true, 1.5707963705062866, arg::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi arg, int i, int j, Vec3d arg2, double d) {
        Vec3d lv = arg2.subtract(arg.getX(), arg.getY(), arg.getZ());
        return TargetFinder.findTarget(arg, i, j, 0, lv, true, d, arg::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetTowards(MobEntityWithAi arg, int i, int j, int k, Vec3d arg2, double d) {
        Vec3d lv = arg2.subtract(arg.getX(), arg.getY(), arg.getZ());
        return TargetFinder.findTarget(arg, i, j, k, lv, false, d, arg::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findTargetAwayFrom(MobEntityWithAi arg, int i, int j, Vec3d arg2) {
        Vec3d lv = arg.getPos().subtract(arg2);
        return TargetFinder.findTarget(arg, i, j, 0, lv, true, 1.5707963705062866, arg::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetAwayFrom(MobEntityWithAi arg, int i, int j, Vec3d arg2) {
        Vec3d lv = arg.getPos().subtract(arg2);
        return TargetFinder.findTarget(arg, i, j, 0, lv, false, 1.5707963705062866, arg::getPathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi arg, int i, int j, int k, @Nullable Vec3d arg22, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction, boolean bl2, int l, int m, boolean bl3) {
        boolean bl5;
        EntityNavigation lv = arg.getNavigation();
        Random random = arg.getRandom();
        if (arg.hasPositionTarget()) {
            boolean bl4 = arg.getPositionTarget().isWithinDistance(arg.getPos(), (double)(arg.getPositionTargetRange() + (float)i) + 1.0);
        } else {
            bl5 = false;
        }
        boolean bl6 = false;
        double e = Double.NEGATIVE_INFINITY;
        BlockPos lv2 = arg.getBlockPos();
        for (int n = 0; n < 10; ++n) {
            double f;
            PathNodeType lv6;
            BlockPos lv5;
            BlockPos lv3 = TargetFinder.getRandomOffset(random, i, j, k, arg22, d);
            if (lv3 == null) continue;
            int o = lv3.getX();
            int p = lv3.getY();
            int q = lv3.getZ();
            if (arg.hasPositionTarget() && i > 1) {
                BlockPos lv4 = arg.getPositionTarget();
                o = arg.getX() > (double)lv4.getX() ? (o -= random.nextInt(i / 2)) : (o += random.nextInt(i / 2));
                q = arg.getZ() > (double)lv4.getZ() ? (q -= random.nextInt(i / 2)) : (q += random.nextInt(i / 2));
            }
            if ((lv5 = new BlockPos((double)o + arg.getX(), (double)p + arg.getY(), (double)q + arg.getZ())).getY() < 0 || lv5.getY() > arg.world.getHeight() || bl5 && !arg.isInWalkTargetRange(lv5) || bl3 && !lv.isValidPosition(lv5)) continue;
            if (bl2) {
                lv5 = TargetFinder.findValidPositionAbove(lv5, random.nextInt(l + 1) + m, arg.world.getHeight(), arg2 -> arg.world.getBlockState((BlockPos)arg2).getMaterial().isSolid());
            }
            if (!bl && arg.world.getFluidState(lv5).isIn(FluidTags.WATER) || arg.getPathfindingPenalty(lv6 = LandPathNodeMaker.getLandNodeType(arg.world, lv5.mutableCopy())) != 0.0f || !((f = toDoubleFunction.applyAsDouble(lv5)) > e)) continue;
            e = f;
            lv2 = lv5;
            bl6 = true;
        }
        if (bl6) {
            return Vec3d.ofBottomCenter(lv2);
        }
        return null;
    }

    @Nullable
    private static BlockPos getRandomOffset(Random random, int i, int j, int k, @Nullable Vec3d arg, double d) {
        if (arg == null || d >= Math.PI) {
            int l = random.nextInt(2 * i + 1) - i;
            int m = random.nextInt(2 * j + 1) - j + k;
            int n = random.nextInt(2 * i + 1) - i;
            return new BlockPos(l, m, n);
        }
        double e = MathHelper.atan2(arg.z, arg.x) - 1.5707963705062866;
        double f = e + (double)(2.0f * random.nextFloat() - 1.0f) * d;
        double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
        double h = -g * Math.sin(f);
        double o = g * Math.cos(f);
        if (Math.abs(h) > (double)i || Math.abs(o) > (double)i) {
            return null;
        }
        int p = random.nextInt(2 * j + 1) - j + k;
        return new BlockPos(h, (double)p, o);
    }

    static BlockPos findValidPositionAbove(BlockPos arg, int i, int j, Predicate<BlockPos> predicate) {
        if (i < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + i + ", expected >= 0");
        }
        if (predicate.test(arg)) {
            BlockPos lv3;
            BlockPos lv = arg.up();
            while (lv.getY() < j && predicate.test(lv)) {
                lv = lv.up();
            }
            BlockPos lv2 = lv;
            while (lv2.getY() < j && lv2.getY() - lv.getY() < i && !predicate.test(lv3 = lv2.up())) {
                lv2 = lv3;
            }
            return lv2;
        }
        return arg;
    }
}

