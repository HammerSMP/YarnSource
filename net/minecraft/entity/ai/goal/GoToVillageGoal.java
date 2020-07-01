/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class GoToVillageGoal
extends Goal {
    private final PathAwareEntity mob;
    private final int searchRange;
    @Nullable
    private BlockPos targetPosition;

    public GoToVillageGoal(PathAwareEntity arg, int i) {
        this.mob = arg;
        this.searchRange = i;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.mob.hasPassengers()) {
            return false;
        }
        if (this.mob.world.isDay()) {
            return false;
        }
        if (this.mob.getRandom().nextInt(this.searchRange) != 0) {
            return false;
        }
        ServerWorld lv = (ServerWorld)this.mob.world;
        BlockPos lv2 = this.mob.getBlockPos();
        if (!lv.isNearOccupiedPointOfInterest(lv2, 6)) {
            return false;
        }
        Vec3d lv3 = TargetFinder.findGroundTarget(this.mob, 15, 7, arg2 -> -lv.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(arg2)));
        this.targetPosition = lv3 == null ? null : new BlockPos(lv3);
        return this.targetPosition != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.targetPosition != null && !this.mob.getNavigation().isIdle() && this.mob.getNavigation().getTargetPos().equals(this.targetPosition);
    }

    @Override
    public void tick() {
        if (this.targetPosition == null) {
            return;
        }
        EntityNavigation lv = this.mob.getNavigation();
        if (lv.isIdle() && !this.targetPosition.isWithinDistance(this.mob.getPos(), 10.0)) {
            Vec3d lv2 = Vec3d.ofBottomCenter(this.targetPosition);
            Vec3d lv3 = this.mob.getPos();
            Vec3d lv4 = lv3.subtract(lv2);
            lv2 = lv4.multiply(0.4).add(lv2);
            Vec3d lv5 = lv2.subtract(lv3).normalize().multiply(10.0).add(lv3);
            BlockPos lv6 = new BlockPos(lv5);
            if (!lv.startMovingTo((lv6 = this.mob.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv6)).getX(), lv6.getY(), lv6.getZ(), 1.0)) {
                this.findOtherWaypoint();
            }
        }
    }

    private void findOtherWaypoint() {
        Random random = this.mob.getRandom();
        BlockPos lv = this.mob.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.mob.getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        this.mob.getNavigation().startMovingTo(lv.getX(), lv.getY(), lv.getZ(), 1.0);
    }
}

