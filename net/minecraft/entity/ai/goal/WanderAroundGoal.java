/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal
extends Goal {
    protected final PathAwareEntity mob;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;
    private boolean field_24463;

    public WanderAroundGoal(PathAwareEntity arg, double d) {
        this(arg, d, 120);
    }

    public WanderAroundGoal(PathAwareEntity arg, double d, int i) {
        this(arg, d, i, true);
    }

    public WanderAroundGoal(PathAwareEntity arg, double d, int i, boolean bl) {
        this.mob = arg;
        this.speed = d;
        this.chance = i;
        this.field_24463 = bl;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        Vec3d lv;
        if (this.mob.hasPassengers()) {
            return false;
        }
        if (!this.ignoringChance) {
            if (this.field_24463 && this.mob.getDespawnCounter() >= 100) {
                return false;
            }
            if (this.mob.getRandom().nextInt(this.chance) != 0) {
                return false;
            }
        }
        if ((lv = this.getWanderTarget()) == null) {
            return false;
        }
        this.targetX = lv.x;
        this.targetY = lv.y;
        this.targetZ = lv.z;
        this.ignoringChance = false;
        return true;
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return TargetFinder.findTarget(this.mob, 10, 7);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle() && !this.mob.hasPassengers();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void ignoreChanceOnce() {
        this.ignoringChance = true;
    }

    public void setChance(int i) {
        this.chance = i;
    }
}

