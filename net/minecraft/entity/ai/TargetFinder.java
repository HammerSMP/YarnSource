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
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetFinder {
    @Nullable
    public static Vec3d findTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, null, true, 1.5707963705062866, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, preferredAngle, true, maxAngleDifference, mob::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findGroundTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance) {
        return TargetFinder.findGroundTarget(mob, maxHorizontalDistance, maxVerticalDistance, mob::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, ToDoubleFunction<BlockPos> pathfindingFavor) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, null, false, 0.0, pathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    public static Vec3d findAirTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d preferredAngle, float maxAngleDifference, int distanceAboveGroundRange, int minDistanceAboveGround) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, preferredAngle, false, maxAngleDifference, mob::getPathfindingFavor, true, distanceAboveGroundRange, minDistanceAboveGround, true);
    }

    @Nullable
    public static Vec3d method_27929(PathAwareEntity arg, int i, int j, Vec3d arg2) {
        Vec3d lv = arg2.subtract(arg.getX(), arg.getY(), arg.getZ());
        return TargetFinder.findTarget(arg, i, j, 0, lv, false, 1.5707963705062866, arg::getPathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
        Vec3d lv = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, lv, true, 1.5707963705062866, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos, double maxAngleDifference) {
        Vec3d lv = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, lv, true, maxAngleDifference, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetTowards(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, Vec3d pos, double maxAngleDifference) {
        Vec3d lv = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, lv, false, maxAngleDifference, mob::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findTargetAwayFrom(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
        Vec3d lv = mob.getPos().subtract(pos);
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, lv, true, 1.5707963705062866, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetAwayFrom(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
        Vec3d lv = mob.getPos().subtract(pos);
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, lv, false, 1.5707963705062866, mob::getPathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    private static Vec3d findTarget(PathAwareEntity mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, boolean notInWater, double maxAngleDifference, ToDoubleFunction<BlockPos> favorProvider, boolean aboveGround, int distanceAboveGroundRange, int minDistanceAboveGround, boolean validPositionsOnly) {
        boolean bl5;
        EntityNavigation lv = mob.getNavigation();
        Random random = mob.getRandom();
        if (mob.hasPositionTarget()) {
            boolean bl4 = mob.getPositionTarget().isWithinDistance(mob.getPos(), (double)(mob.getPositionTargetRange() + (float)maxHorizontalDistance) + 1.0);
        } else {
            bl5 = false;
        }
        boolean bl6 = false;
        double e = Double.NEGATIVE_INFINITY;
        BlockPos lv2 = mob.getBlockPos();
        for (int n = 0; n < 10; ++n) {
            double f;
            PathNodeType lv6;
            BlockPos lv5;
            BlockPos lv3 = TargetFinder.getRandomOffset(random, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, preferredAngle, maxAngleDifference);
            if (lv3 == null) continue;
            int o = lv3.getX();
            int p = lv3.getY();
            int q = lv3.getZ();
            if (mob.hasPositionTarget() && maxHorizontalDistance > 1) {
                BlockPos lv4 = mob.getPositionTarget();
                o = mob.getX() > (double)lv4.getX() ? (o -= random.nextInt(maxHorizontalDistance / 2)) : (o += random.nextInt(maxHorizontalDistance / 2));
                q = mob.getZ() > (double)lv4.getZ() ? (q -= random.nextInt(maxHorizontalDistance / 2)) : (q += random.nextInt(maxHorizontalDistance / 2));
            }
            if ((lv5 = new BlockPos((double)o + mob.getX(), (double)p + mob.getY(), (double)q + mob.getZ())).getY() < 0 || lv5.getY() > mob.world.getHeight() || bl5 && !mob.isInWalkTargetRange(lv5) || validPositionsOnly && !lv.isValidPosition(lv5)) continue;
            if (aboveGround) {
                lv5 = TargetFinder.findValidPositionAbove(lv5, random.nextInt(distanceAboveGroundRange + 1) + minDistanceAboveGround, mob.world.getHeight(), arg2 -> arg.world.getBlockState((BlockPos)arg2).getMaterial().isSolid());
            }
            if (!notInWater && mob.world.getFluidState(lv5).isIn(FluidTags.WATER) || mob.getPathfindingPenalty(lv6 = LandPathNodeMaker.getLandNodeType(mob.world, lv5.mutableCopy())) != 0.0f || !((f = favorProvider.applyAsDouble(lv5)) > e)) continue;
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
    private static BlockPos getRandomOffset(Random random, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference) {
        if (preferredAngle == null || maxAngleDifference >= Math.PI) {
            int l = random.nextInt(2 * maxHorizontalDistance + 1) - maxHorizontalDistance;
            int m = random.nextInt(2 * maxVerticalDistance + 1) - maxVerticalDistance + preferredYDifference;
            int n = random.nextInt(2 * maxHorizontalDistance + 1) - maxHorizontalDistance;
            return new BlockPos(l, m, n);
        }
        double e = MathHelper.atan2(preferredAngle.z, preferredAngle.x) - 1.5707963705062866;
        double f = e + (double)(2.0f * random.nextFloat() - 1.0f) * maxAngleDifference;
        double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)maxHorizontalDistance;
        double h = -g * Math.sin(f);
        double o = g * Math.cos(f);
        if (Math.abs(h) > (double)maxHorizontalDistance || Math.abs(o) > (double)maxHorizontalDistance) {
            return null;
        }
        int p = random.nextInt(2 * maxVerticalDistance + 1) - maxVerticalDistance + preferredYDifference;
        return new BlockPos(h, (double)p, o);
    }

    static BlockPos findValidPositionAbove(BlockPos pos, int minDistanceAboveIllegal, int maxOffset, Predicate<BlockPos> illegalPredicate) {
        if (minDistanceAboveIllegal < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + minDistanceAboveIllegal + ", expected >= 0");
        }
        if (illegalPredicate.test(pos)) {
            BlockPos lv3;
            BlockPos lv = pos.up();
            while (lv.getY() < maxOffset && illegalPredicate.test(lv)) {
                lv = lv.up();
            }
            BlockPos lv2 = lv;
            while (lv2.getY() < maxOffset && lv2.getY() - lv.getY() < minDistanceAboveIllegal && !illegalPredicate.test(lv3 = lv2.up())) {
                lv2 = lv3;
            }
            return lv2;
        }
        return pos;
    }
}

