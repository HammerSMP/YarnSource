/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

public class BowAttackGoal<T extends HostileEntity>
extends Goal {
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;

    public BowAttackGoal(T arg, double d, int i, float f) {
        this.actor = arg;
        this.speed = d;
        this.attackInterval = i;
        this.squaredRange = f * f;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public void setAttackInterval(int i) {
        this.attackInterval = i;
    }

    @Override
    public boolean canStart() {
        if (((MobEntity)this.actor).getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return ((LivingEntity)this.actor).isHolding(Items.BOW);
    }

    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !((MobEntity)this.actor).getNavigation().isIdle()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        ((MobEntity)this.actor).setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        ((MobEntity)this.actor).setAttacking(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        ((LivingEntity)this.actor).clearActiveItem();
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity lv = ((MobEntity)this.actor).getTarget();
        if (lv == null) {
            return;
        }
        double d = ((Entity)this.actor).squaredDistanceTo(lv.getX(), lv.getY(), lv.getZ());
        boolean bl = ((MobEntity)this.actor).getVisibilityCache().canSee(lv);
        boolean bl3 = bl2 = this.targetSeeingTicker > 0;
        if (bl != bl2) {
            this.targetSeeingTicker = 0;
        }
        this.targetSeeingTicker = bl ? ++this.targetSeeingTicker : --this.targetSeeingTicker;
        if (d > (double)this.squaredRange || this.targetSeeingTicker < 20) {
            ((MobEntity)this.actor).getNavigation().startMovingTo(lv, this.speed);
            this.combatTicks = -1;
        } else {
            ((MobEntity)this.actor).getNavigation().stop();
            ++this.combatTicks;
        }
        if (this.combatTicks >= 20) {
            if ((double)((LivingEntity)this.actor).getRandom().nextFloat() < 0.3) {
                boolean bl4 = this.movingToLeft = !this.movingToLeft;
            }
            if ((double)((LivingEntity)this.actor).getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }
            this.combatTicks = 0;
        }
        if (this.combatTicks > -1) {
            if (d > (double)(this.squaredRange * 0.75f)) {
                this.backward = false;
            } else if (d < (double)(this.squaredRange * 0.25f)) {
                this.backward = true;
            }
            ((MobEntity)this.actor).getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
            ((MobEntity)this.actor).lookAtEntity(lv, 30.0f, 30.0f);
        } else {
            ((MobEntity)this.actor).getLookControl().lookAt(lv, 30.0f, 30.0f);
        }
        if (((LivingEntity)this.actor).isUsingItem()) {
            int i;
            if (!bl && this.targetSeeingTicker < -60) {
                ((LivingEntity)this.actor).clearActiveItem();
            } else if (bl && (i = ((LivingEntity)this.actor).getItemUseTime()) >= 20) {
                ((LivingEntity)this.actor).clearActiveItem();
                ((RangedAttackMob)this.actor).attack(lv, BowItem.getPullProgress(i));
                this.cooldown = this.attackInterval;
            }
        } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            ((LivingEntity)this.actor).setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
        }
    }
}

