/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;

public class MeleeAttackGoal
extends Goal {
    protected final MobEntityWithAi mob;
    protected int ticksUntilAttack;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private int updateCountdownTicks;
    private double targetX;
    private double targetY;
    private double targetZ;
    protected final int attackIntervalTicks = 20;
    private long lastUpdateTime;

    public MeleeAttackGoal(MobEntityWithAi arg, double d, boolean bl) {
        this.mob = arg;
        this.speed = d;
        this.pauseWhenMobIdle = bl;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        long l = this.mob.world.getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        }
        this.lastUpdateTime = l;
        LivingEntity lv = this.mob.getTarget();
        if (lv == null) {
            return false;
        }
        if (!lv.isAlive()) {
            return false;
        }
        this.path = this.mob.getNavigation().findPathTo(lv, 0);
        if (this.path != null) {
            return true;
        }
        return this.getSquaredMaxAttackDistance(lv) >= this.mob.squaredDistanceTo(lv.getX(), lv.getY(), lv.getZ());
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity lv = this.mob.getTarget();
        if (lv == null) {
            return false;
        }
        if (!lv.isAlive()) {
            return false;
        }
        if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        }
        if (!this.mob.isInWalkTargetRange(lv.getBlockPos())) {
            return false;
        }
        return !(lv instanceof PlayerEntity) || !lv.isSpectator() && !((PlayerEntity)lv).isCreative();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
    }

    @Override
    public void stop() {
        LivingEntity lv = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(lv)) {
            this.mob.setTarget(null);
        }
        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity lv = this.mob.getTarget();
        this.mob.getLookControl().lookAt(lv, 30.0f, 30.0f);
        double d = this.mob.squaredDistanceTo(lv.getX(), lv.getY(), lv.getZ());
        --this.updateCountdownTicks;
        if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(lv)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || lv.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f)) {
            this.targetX = lv.getX();
            this.targetY = lv.getY();
            this.targetZ = lv.getZ();
            this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
            if (d > 1024.0) {
                this.updateCountdownTicks += 10;
            } else if (d > 256.0) {
                this.updateCountdownTicks += 5;
            }
            if (!this.mob.getNavigation().startMovingTo(lv, this.speed)) {
                this.updateCountdownTicks += 15;
            }
        }
        this.ticksUntilAttack = Math.max(this.ticksUntilAttack - 1, 0);
        this.attack(lv, d);
    }

    protected void attack(LivingEntity arg, double d) {
        double e = this.getSquaredMaxAttackDistance(arg);
        if (d <= e && this.ticksUntilAttack <= 0) {
            this.ticksUntilAttack = 20;
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(arg);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity arg) {
        return this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + arg.getWidth();
    }
}

