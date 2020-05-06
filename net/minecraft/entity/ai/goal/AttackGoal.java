/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public class AttackGoal
extends Goal {
    private final BlockView world;
    private final MobEntity mob;
    private LivingEntity target;
    private int cooldown;

    public AttackGoal(MobEntity arg) {
        this.mob = arg;
        this.world = arg.world;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity lv = this.mob.getTarget();
        if (lv == null) {
            return false;
        }
        this.target = lv;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (!this.target.isAlive()) {
            return false;
        }
        if (this.mob.squaredDistanceTo(this.target) > 225.0) {
            return false;
        }
        return !this.mob.getNavigation().isIdle() || this.canStart();
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.target, 30.0f, 30.0f);
        double d = this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f);
        double e = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
        double f = 0.8;
        if (e > d && e < 16.0) {
            f = 1.33;
        } else if (e < 225.0) {
            f = 0.6;
        }
        this.mob.getNavigation().startMovingTo(this.target, f);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        if (e > d) {
            return;
        }
        if (this.cooldown > 0) {
            return;
        }
        this.cooldown = 20;
        this.mob.tryAttack(this.target);
    }
}

