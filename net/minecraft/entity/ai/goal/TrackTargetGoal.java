/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.math.MathHelper;

public abstract class TrackTargetGoal
extends Goal {
    protected final MobEntity mob;
    protected final boolean checkVisibility;
    private final boolean checkCanNavigate;
    private int canNavigateFlag;
    private int checkCanNavigateCooldown;
    private int timeWithoutVisibility;
    protected LivingEntity target;
    protected int maxTimeWithoutVisibility = 60;

    public TrackTargetGoal(MobEntity arg, boolean bl) {
        this(arg, bl, false);
    }

    public TrackTargetGoal(MobEntity arg, boolean bl, boolean bl2) {
        this.mob = arg;
        this.checkVisibility = bl;
        this.checkCanNavigate = bl2;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity lv = this.mob.getTarget();
        if (lv == null) {
            lv = this.target;
        }
        if (lv == null) {
            return false;
        }
        if (!lv.isAlive()) {
            return false;
        }
        AbstractTeam lv2 = this.mob.getScoreboardTeam();
        AbstractTeam lv3 = lv.getScoreboardTeam();
        if (lv2 != null && lv3 == lv2) {
            return false;
        }
        double d = this.getFollowRange();
        if (this.mob.squaredDistanceTo(lv) > d * d) {
            return false;
        }
        if (this.checkVisibility) {
            if (this.mob.getVisibilityCache().canSee(lv)) {
                this.timeWithoutVisibility = 0;
            } else if (++this.timeWithoutVisibility > this.maxTimeWithoutVisibility) {
                return false;
            }
        }
        if (lv instanceof PlayerEntity && ((PlayerEntity)lv).abilities.invulnerable) {
            return false;
        }
        this.mob.setTarget(lv);
        return true;
    }

    protected double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    @Override
    public void start() {
        this.canNavigateFlag = 0;
        this.checkCanNavigateCooldown = 0;
        this.timeWithoutVisibility = 0;
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.target = null;
    }

    protected boolean canTrack(@Nullable LivingEntity arg, TargetPredicate arg2) {
        if (arg == null) {
            return false;
        }
        if (!arg2.test(this.mob, arg)) {
            return false;
        }
        if (!this.mob.isInWalkTargetRange(arg.getBlockPos())) {
            return false;
        }
        if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) {
                this.canNavigateFlag = 0;
            }
            if (this.canNavigateFlag == 0) {
                int n = this.canNavigateFlag = this.canNavigateToEntity(arg) ? 1 : 2;
            }
            if (this.canNavigateFlag == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean canNavigateToEntity(LivingEntity arg) {
        int j;
        this.checkCanNavigateCooldown = 10 + this.mob.getRandom().nextInt(5);
        Path lv = this.mob.getNavigation().findPathTo(arg, 0);
        if (lv == null) {
            return false;
        }
        PathNode lv2 = lv.getEnd();
        if (lv2 == null) {
            return false;
        }
        int i = lv2.x - MathHelper.floor(arg.getX());
        return (double)(i * i + (j = lv2.z - MathHelper.floor(arg.getZ())) * j) <= 2.25;
    }

    public TrackTargetGoal setMaxTimeWithoutVisibility(int i) {
        this.maxTimeWithoutVisibility = i;
        return this;
    }
}

