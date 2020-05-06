/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class CrossbowAttackTask<E extends MobEntity, T extends LivingEntity>
extends Task<E> {
    private int chargingCooldown;
    private CrossbowState state = CrossbowState.UNCHARGED;

    public CrossbowAttackTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 1200);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        LivingEntity lv = CrossbowAttackTask.getAttackTarget(arg2);
        return ((LivingEntity)arg2).isHolding(Items.CROSSBOW) && LookTargetUtil.isVisibleInMemory(arg2, lv) && LookTargetUtil.method_25940(arg2, lv, 0);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, E arg2, long l) {
        return ((LivingEntity)arg2).getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.shouldRun(arg, arg2);
    }

    @Override
    protected void keepRunning(ServerWorld arg, E arg2, long l) {
        LivingEntity lv = CrossbowAttackTask.getAttackTarget(arg2);
        this.setLookTarget((MobEntity)arg2, lv);
        this.tickState(arg2, lv);
    }

    @Override
    protected void finishRunning(ServerWorld arg, E arg2, long l) {
        if (((LivingEntity)arg2).isUsingItem()) {
            ((LivingEntity)arg2).clearActiveItem();
        }
        if (((LivingEntity)arg2).isHolding(Items.CROSSBOW)) {
            ((CrossbowUser)arg2).setCharging(false);
            CrossbowItem.setCharged(((LivingEntity)arg2).getActiveItem(), false);
        }
    }

    private void tickState(E arg, LivingEntity arg2) {
        if (this.state == CrossbowState.UNCHARGED) {
            ((LivingEntity)arg).setCurrentHand(ProjectileUtil.getHandPossiblyHolding(arg, Items.CROSSBOW));
            this.state = CrossbowState.CHARGING;
            ((CrossbowUser)arg).setCharging(true);
        } else if (this.state == CrossbowState.CHARGING) {
            ItemStack lv;
            int i;
            if (!((LivingEntity)arg).isUsingItem()) {
                this.state = CrossbowState.UNCHARGED;
            }
            if ((i = ((LivingEntity)arg).getItemUseTime()) >= CrossbowItem.getPullTime(lv = ((LivingEntity)arg).getActiveItem())) {
                ((LivingEntity)arg).stopUsingItem();
                this.state = CrossbowState.CHARGED;
                this.chargingCooldown = 20 + ((LivingEntity)arg).getRandom().nextInt(20);
                ((CrossbowUser)arg).setCharging(false);
            }
        } else if (this.state == CrossbowState.CHARGED) {
            --this.chargingCooldown;
            if (this.chargingCooldown == 0) {
                this.state = CrossbowState.READY_TO_ATTACK;
            }
        } else if (this.state == CrossbowState.READY_TO_ATTACK) {
            ((RangedAttackMob)arg).attack(arg2, 1.0f);
            ItemStack lv2 = ((LivingEntity)arg).getStackInHand(ProjectileUtil.getHandPossiblyHolding(arg, Items.CROSSBOW));
            CrossbowItem.setCharged(lv2, false);
            this.state = CrossbowState.UNCHARGED;
        }
    }

    private void setLookTarget(MobEntity arg, LivingEntity arg2) {
        arg.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(arg2, true));
    }

    private static LivingEntity getAttackTarget(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (E)((MobEntity)arg2), l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (E)((MobEntity)arg2), l);
    }

    static enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;

    }
}

