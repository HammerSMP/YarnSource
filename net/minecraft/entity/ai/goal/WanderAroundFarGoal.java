/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

public class WanderAroundFarGoal
extends WanderAroundGoal {
    protected final float probability;

    public WanderAroundFarGoal(PathAwareEntity arg, double d) {
        this(arg, d, 0.001f);
    }

    public WanderAroundFarGoal(PathAwareEntity arg, double d, float f) {
        super(arg, d);
        this.probability = f;
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        if (this.mob.isInsideWaterOrBubbleColumn()) {
            Vec3d lv = TargetFinder.findGroundTarget(this.mob, 15, 7);
            return lv == null ? super.getWanderTarget() : lv;
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return TargetFinder.findGroundTarget(this.mob, 10, 7);
        }
        return super.getWanderTarget();
    }
}

