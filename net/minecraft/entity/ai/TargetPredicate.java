/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class TargetPredicate {
    public static final TargetPredicate DEFAULT = new TargetPredicate();
    private double baseMaxDistance = -1.0;
    private boolean includeInvulnerable;
    private boolean includeTeammates;
    private boolean includeHidden;
    private boolean ignoreEntityTargetRules;
    private boolean useDistanceScalingFactor = true;
    private Predicate<LivingEntity> predicate;

    public TargetPredicate setBaseMaxDistance(double d) {
        this.baseMaxDistance = d;
        return this;
    }

    public TargetPredicate includeInvulnerable() {
        this.includeInvulnerable = true;
        return this;
    }

    public TargetPredicate includeTeammates() {
        this.includeTeammates = true;
        return this;
    }

    public TargetPredicate includeHidden() {
        this.includeHidden = true;
        return this;
    }

    public TargetPredicate ignoreEntityTargetRules() {
        this.ignoreEntityTargetRules = true;
        return this;
    }

    public TargetPredicate ignoreDistanceScalingFactor() {
        this.useDistanceScalingFactor = false;
        return this;
    }

    public TargetPredicate setPredicate(@Nullable Predicate<LivingEntity> predicate) {
        this.predicate = predicate;
        return this;
    }

    public boolean test(@Nullable LivingEntity arg, LivingEntity arg2) {
        if (arg == arg2) {
            return false;
        }
        if (arg2.isSpectator()) {
            return false;
        }
        if (!arg2.isAlive()) {
            return false;
        }
        if (!this.includeInvulnerable && arg2.isInvulnerable()) {
            return false;
        }
        if (this.predicate != null && !this.predicate.test(arg2)) {
            return false;
        }
        if (arg != null) {
            if (!this.ignoreEntityTargetRules) {
                if (!arg.canTarget(arg2)) {
                    return false;
                }
                if (!arg.canTarget(arg2.getType())) {
                    return false;
                }
            }
            if (!this.includeTeammates && arg.isTeammate(arg2)) {
                return false;
            }
            if (this.baseMaxDistance > 0.0) {
                double d = this.useDistanceScalingFactor ? arg2.getAttackDistanceScalingFactor(arg) : 1.0;
                double e = this.baseMaxDistance * d;
                double f = arg.squaredDistanceTo(arg2.getX(), arg2.getY(), arg2.getZ());
                if (f > e * e) {
                    return false;
                }
            }
            if (!this.includeHidden && arg instanceof MobEntity && !((MobEntity)arg).getVisibilityCache().canSee(arg2)) {
                return false;
            }
        }
        return true;
    }
}

