/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class FollowOwnerGoal
extends Goal {
    private final TameableEntity tameable;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    public FollowOwnerGoal(TameableEntity arg, double d, float f, float g, boolean bl) {
        this.tameable = arg;
        this.world = arg.world;
        this.speed = d;
        this.navigation = arg.getNavigation();
        this.minDistance = f;
        this.maxDistance = g;
        this.leavesAllowed = bl;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(arg.getNavigation() instanceof MobNavigation) && !(arg.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canStart() {
        LivingEntity lv = this.tameable.getOwner();
        if (lv == null) {
            return false;
        }
        if (lv.isSpectator()) {
            return false;
        }
        if (this.tameable.isSitting()) {
            return false;
        }
        if (this.tameable.squaredDistanceTo(lv) < (double)(this.minDistance * this.minDistance)) {
            return false;
        }
        this.owner = lv;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        }
        if (this.tameable.isSitting()) {
            return false;
        }
        return !(this.tameable.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        this.tameable.getLookControl().lookAt(this.owner, 10.0f, this.tameable.getLookPitchSpeed());
        if (--this.updateCountdownTicks > 0) {
            return;
        }
        this.updateCountdownTicks = 10;
        if (this.tameable.isLeashed() || this.tameable.hasVehicle()) {
            return;
        }
        if (this.tameable.squaredDistanceTo(this.owner) >= 144.0) {
            this.tryTeleport();
        } else {
            this.navigation.startMovingTo(this.owner, this.speed);
        }
    }

    private void tryTeleport() {
        BlockPos lv = this.owner.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(lv.getX() + j, lv.getY() + k, lv.getZ() + l);
            if (!bl) continue;
            return;
        }
    }

    private boolean tryTeleportTo(int i, int j, int k) {
        if (Math.abs((double)i - this.owner.getX()) < 2.0 && Math.abs((double)k - this.owner.getZ()) < 2.0) {
            return false;
        }
        if (!this.canTeleportTo(new BlockPos(i, j, k))) {
            return false;
        }
        this.tameable.refreshPositionAndAngles((float)i + 0.5f, j, (float)k + 0.5f, this.tameable.yaw, this.tameable.pitch);
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos arg) {
        PathNodeType lv = LandPathNodeMaker.getLandNodeType(this.world, arg.mutableCopy());
        if (lv != PathNodeType.WALKABLE) {
            return false;
        }
        BlockState lv2 = this.world.getBlockState(arg.down());
        if (!this.leavesAllowed && lv2.getBlock() instanceof LeavesBlock) {
            return false;
        }
        BlockPos lv3 = arg.subtract(this.tameable.getBlockPos());
        return this.world.doesNotCollide(this.tameable, this.tameable.getBoundingBox().offset(lv3));
    }

    private int getRandomInt(int i, int j) {
        return this.tameable.getRandom().nextInt(j - i + 1) + i;
    }
}

