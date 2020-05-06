/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.FluidTags;

public class SwimGoal
extends Goal {
    private final MobEntity mob;

    public SwimGoal(MobEntity arg) {
        this.mob = arg;
        this.setControls(EnumSet.of(Goal.Control.JUMP));
        arg.getNavigation().setCanSwim(true);
    }

    @Override
    public boolean canStart() {
        double d = (double)this.mob.getStandingEyeHeight() < 0.4 ? 0.2 : 0.4;
        return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > d || this.mob.isInLava();
    }

    @Override
    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8f) {
            this.mob.getJumpControl().setActive();
        }
    }
}

