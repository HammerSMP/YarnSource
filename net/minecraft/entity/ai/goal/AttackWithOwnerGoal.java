/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class AttackWithOwnerGoal
extends TrackTargetGoal {
    private final TameableEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public AttackWithOwnerGoal(TameableEntity arg) {
        super(arg, false);
        this.tameable = arg;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!this.tameable.isTamed() || this.tameable.isSitting()) {
            return false;
        }
        LivingEntity lv = this.tameable.getOwner();
        if (lv == null) {
            return false;
        }
        this.attacking = lv.getAttacking();
        int i = lv.getLastAttackTime();
        return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, lv);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity lv = this.tameable.getOwner();
        if (lv != null) {
            this.lastAttackTime = lv.getLastAttackTime();
        }
        super.start();
    }
}

