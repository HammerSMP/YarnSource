/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;

public class GoToEntityGoal
extends LookAtEntityGoal {
    public GoToEntityGoal(MobEntity arg, Class<? extends LivingEntity> class_, float f, float g) {
        super(arg, class_, f, g);
        this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.MOVE));
    }
}

