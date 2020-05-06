/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class MoveThroughVillageGoal
extends Goal {
    protected final MobEntityWithAi mob;
    private final double speed;
    private Path targetPath;
    private BlockPos target;
    private final boolean requiresNighttime;
    private final List<BlockPos> visitedTargets = Lists.newArrayList();
    private final int distance;
    private final BooleanSupplier doorPassingThroughGetter;

    public MoveThroughVillageGoal(MobEntityWithAi arg, double d, boolean bl, int i, BooleanSupplier booleanSupplier) {
        this.mob = arg;
        this.speed = d;
        this.requiresNighttime = bl;
        this.distance = i;
        this.doorPassingThroughGetter = booleanSupplier;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        if (!(arg.getNavigation() instanceof MobNavigation)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean canStart() {
        this.forgetOldTarget();
        if (this.requiresNighttime && this.mob.world.isDay()) {
            return false;
        }
        ServerWorld lv = (ServerWorld)this.mob.world;
        BlockPos lv2 = this.mob.getBlockPos();
        if (!lv.isNearOccupiedPointOfInterest(lv2, 6)) {
            return false;
        }
        Vec3d lv3 = TargetFinder.findGroundTarget(this.mob, 15, 7, arg3 -> {
            if (!lv.isNearOccupiedPointOfInterest((BlockPos)arg3)) {
                return Double.NEGATIVE_INFINITY;
            }
            Optional<BlockPos> optional = lv.getPointOfInterestStorage().getPosition(PointOfInterestType.ALWAYS_TRUE, this::shouldVisit, (BlockPos)arg3, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
            if (!optional.isPresent()) {
                return Double.NEGATIVE_INFINITY;
            }
            return -optional.get().getSquaredDistance(lv2);
        });
        if (lv3 == null) {
            return false;
        }
        Optional<BlockPos> optional = lv.getPointOfInterestStorage().getPosition(PointOfInterestType.ALWAYS_TRUE, this::shouldVisit, new BlockPos(lv3), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
        if (!optional.isPresent()) {
            return false;
        }
        this.target = optional.get().toImmutable();
        MobNavigation lv4 = (MobNavigation)this.mob.getNavigation();
        boolean bl = lv4.canEnterOpenDoors();
        lv4.setCanPathThroughDoors(this.doorPassingThroughGetter.getAsBoolean());
        this.targetPath = lv4.findPathTo(this.target, 0);
        lv4.setCanPathThroughDoors(bl);
        if (this.targetPath == null) {
            Vec3d lv5 = TargetFinder.findTargetTowards(this.mob, 10, 7, Vec3d.method_24955(this.target));
            if (lv5 == null) {
                return false;
            }
            lv4.setCanPathThroughDoors(this.doorPassingThroughGetter.getAsBoolean());
            this.targetPath = this.mob.getNavigation().findPathTo(lv5.x, lv5.y, lv5.z, 0);
            lv4.setCanPathThroughDoors(bl);
            if (this.targetPath == null) {
                return false;
            }
        }
        for (int i = 0; i < this.targetPath.getLength(); ++i) {
            PathNode lv6 = this.targetPath.getNode(i);
            BlockPos lv7 = new BlockPos(lv6.x, lv6.y + 1, lv6.z);
            if (!DoorBlock.isWoodenDoor(this.mob.world, lv7)) continue;
            this.targetPath = this.mob.getNavigation().findPathTo(lv6.x, lv6.y, lv6.z, 0);
            break;
        }
        return this.targetPath != null;
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getNavigation().isIdle()) {
            return false;
        }
        return !this.target.isWithinDistance(this.mob.getPos(), (double)(this.mob.getWidth() + (float)this.distance));
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingAlong(this.targetPath, this.speed);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isIdle() || this.target.isWithinDistance(this.mob.getPos(), (double)this.distance)) {
            this.visitedTargets.add(this.target);
        }
    }

    private boolean shouldVisit(BlockPos arg) {
        for (BlockPos lv : this.visitedTargets) {
            if (!Objects.equals(arg, lv)) continue;
            return false;
        }
        return true;
    }

    private void forgetOldTarget() {
        if (this.visitedTargets.size() > 15) {
            this.visitedTargets.remove(0);
        }
    }
}

