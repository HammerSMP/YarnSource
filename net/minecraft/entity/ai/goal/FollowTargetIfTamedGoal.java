/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class FollowTargetIfTamedGoal<T extends LivingEntity>
extends FollowTargetGoal<T> {
    private final TameableEntity tameable;

    public FollowTargetIfTamedGoal(TameableEntity arg, Class<T> arg2, boolean bl, @Nullable Predicate<LivingEntity> predicate) {
        super(arg, arg2, 10, bl, false, predicate);
        this.tameable = arg;
    }

    @Override
    public boolean canStart() {
        return !this.tameable.isTamed() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.targetPredicate != null) {
            return this.targetPredicate.test(this.mob, this.targetEntity);
        }
        return super.shouldContinue();
    }
}

