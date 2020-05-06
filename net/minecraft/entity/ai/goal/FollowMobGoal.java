/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;

public class FollowMobGoal
extends Goal {
    private final MobEntity mob;
    private final Predicate<MobEntity> targetPredicate;
    private MobEntity target;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float minDistance;
    private float oldWaterPathFindingPenalty;
    private final float maxDistance;

    public FollowMobGoal(MobEntity arg, double d, float f, float g) {
        this.mob = arg;
        this.targetPredicate = arg2 -> arg2 != null && arg.getClass() != arg2.getClass();
        this.speed = d;
        this.navigation = arg.getNavigation();
        this.minDistance = f;
        this.maxDistance = g;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(arg.getNavigation() instanceof MobNavigation) && !(arg.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    @Override
    public boolean canStart() {
        List<MobEntity> list = this.mob.world.getEntities(MobEntity.class, this.mob.getBoundingBox().expand(this.maxDistance), this.targetPredicate);
        if (!list.isEmpty()) {
            for (MobEntity lv : list) {
                if (lv.isInvisible()) continue;
                this.target = lv;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && !this.navigation.isIdle() && this.mob.squaredDistanceTo(this.target) > (double)(this.minDistance * this.minDistance);
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathFindingPenalty = this.mob.getPathfindingPenalty(PathNodeType.WATER);
        this.mob.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.target = null;
        this.navigation.stop();
        this.mob.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathFindingPenalty);
    }

    @Override
    public void tick() {
        double f;
        double e;
        if (this.target == null || this.mob.isLeashed()) {
            return;
        }
        this.mob.getLookControl().lookAt(this.target, 10.0f, this.mob.getLookPitchSpeed());
        if (--this.updateCountdownTicks > 0) {
            return;
        }
        this.updateCountdownTicks = 10;
        double d = this.mob.getX() - this.target.getX();
        double g = d * d + (e = this.mob.getY() - this.target.getY()) * e + (f = this.mob.getZ() - this.target.getZ()) * f;
        if (g <= (double)(this.minDistance * this.minDistance)) {
            this.navigation.stop();
            LookControl lv = this.target.getLookControl();
            if (g <= (double)this.minDistance || lv.getLookX() == this.mob.getX() && lv.getLookY() == this.mob.getY() && lv.getLookZ() == this.mob.getZ()) {
                double h = this.target.getX() - this.mob.getX();
                double i = this.target.getZ() - this.mob.getZ();
                this.navigation.startMovingTo(this.mob.getX() - h, this.mob.getY(), this.mob.getZ() - i, this.speed);
            }
            return;
        }
        this.navigation.startMovingTo(this.target, this.speed);
    }
}

