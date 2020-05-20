/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class GoToWalkTargetGoal
extends Goal {
    private final MobEntityWithAi mob;
    private double x;
    private double y;
    private double z;
    private final double speed;

    public GoToWalkTargetGoal(MobEntityWithAi arg, double d) {
        this.mob = arg;
        this.speed = d;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.mob.isInWalkTargetRange()) {
            return false;
        }
        Vec3d lv = TargetFinder.findTargetTowards(this.mob, 16, 7, Vec3d.ofBottomCenter(this.mob.getPositionTarget()));
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
        return !this.mob.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
    }
}

