/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class GoToEntityTargetGoal
extends Goal {
    private final MobEntityWithAi mob;
    private LivingEntity target;
    private double x;
    private double y;
    private double z;
    private final double speed;
    private final float maxDistance;

    public GoToEntityTargetGoal(MobEntityWithAi arg, double d, float f) {
        this.mob = arg;
        this.speed = d;
        this.maxDistance = f;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        }
        if (this.target.squaredDistanceTo(this.mob) > (double)(this.maxDistance * this.maxDistance)) {
            return false;
        }
        Vec3d lv = TargetFinder.findTargetTowards(this.mob, 16, 7, this.target.getPos());
        if (lv == null) {
            return false;
        }
        this.x = lv.x;
        this.y = lv.y;
        this.z = lv.z;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle() && this.target.isAlive() && this.target.squaredDistanceTo(this.mob) < (double)(this.maxDistance * this.maxDistance);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
    }
}

