/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Box;

public class RevengeGoal
extends TrackTargetGoal {
    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
    private boolean groupRevenge;
    private int lastAttackedTime;
    private final Class<?>[] noRevengeTypes;
    private Class<?>[] noHelpTypes;

    public RevengeGoal(MobEntityWithAi arg, Class<?> ... args) {
        super(arg, true);
        this.noRevengeTypes = args;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        int i = this.mob.getLastAttackedTime();
        LivingEntity lv = this.mob.getAttacker();
        if (i == this.lastAttackedTime || lv == null) {
            return false;
        }
        for (Class<?> lv2 : this.noRevengeTypes) {
            if (!lv2.isAssignableFrom(lv.getClass())) continue;
            return false;
        }
        return this.canTrack(lv, VALID_AVOIDABLES_PREDICATE);
    }

    public RevengeGoal setGroupRevenge(Class<?> ... args) {
        this.groupRevenge = true;
        this.noHelpTypes = args;
        return this;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getAttacker());
        this.target = this.mob.getTarget();
        this.lastAttackedTime = this.mob.getLastAttackedTime();
        this.maxTimeWithoutVisibility = 300;
        if (this.groupRevenge) {
            this.callSameTypeForRevenge();
        }
        super.start();
    }

    protected void callSameTypeForRevenge() {
        double d = this.getFollowRange();
        List<?> list = this.mob.world.getEntitiesIncludingUngeneratedChunks(this.mob.getClass(), new Box(this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getX() + 1.0, this.mob.getY() + 1.0, this.mob.getZ() + 1.0).expand(d, 10.0, d));
        for (MobEntity lv : list) {
            if (this.mob == lv || lv.getTarget() != null || this.mob instanceof TameableEntity && ((TameableEntity)this.mob).getOwner() != ((TameableEntity)lv).getOwner() || lv.isTeammate(this.mob.getAttacker())) continue;
            if (this.noHelpTypes != null) {
                boolean bl = false;
                for (Class<?> lv2 : this.noHelpTypes) {
                    if (lv.getClass() != lv2) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
            }
            this.setMobEntityTarget(lv, this.mob.getAttacker());
        }
    }

    protected void setMobEntityTarget(MobEntity arg, LivingEntity arg2) {
        arg.setTarget(arg2);
    }
}

