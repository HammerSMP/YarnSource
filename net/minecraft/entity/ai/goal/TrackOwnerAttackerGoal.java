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

public class TrackOwnerAttackerGoal
extends TrackTargetGoal {
    private final TameableEntity tameable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public TrackOwnerAttackerGoal(TameableEntity arg) {
        super(arg, false);
        this.tameable = arg;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!this.tameable.isTamed() || this.tameable.method_24345()) {
            return false;
        }
        LivingEntity lv = this.tameable.getOwner();
        if (lv == null) {
            return false;
        }
        this.attacker = lv.getAttacker();
        int i = lv.getLastAttackedTime();
        return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacker, lv);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity lv = this.tameable.getOwner();
        if (lv != null) {
            this.lastAttackedTime = lv.getLastAttackedTime();
        }
        super.start();
    }
}

