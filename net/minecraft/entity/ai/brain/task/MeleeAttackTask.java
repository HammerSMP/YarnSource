/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class MeleeAttackTask
extends Task<MobEntity> {
    private final int interval;

    public MeleeAttackTask(int interval) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ATTACK_COOLING_DOWN, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.interval = interval;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, MobEntity arg2) {
        LivingEntity lv = this.method_25944(arg2);
        return !this.method_25942(arg2) && LookTargetUtil.isVisibleInMemory(arg2, lv) && LookTargetUtil.method_25941(arg2, lv);
    }

    private boolean method_25942(MobEntity arg) {
        return arg.isHolding(arg2 -> arg2 instanceof RangedWeaponItem && arg.canUseRangedWeapon((RangedWeaponItem)arg2));
    }

    @Override
    protected void run(ServerWorld arg, MobEntity arg2, long l) {
        LivingEntity lv = this.method_25944(arg2);
        LookTargetUtil.lookAt(arg2, lv);
        arg2.swingHand(Hand.MAIN_HAND);
        arg2.tryAttack(lv);
        arg2.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
    }

    private LivingEntity method_25944(MobEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

