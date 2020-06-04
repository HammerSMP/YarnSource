/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class DefeatTargetTask
extends Task<LivingEntity> {
    private final int duration;
    private final BiPredicate<LivingEntity, LivingEntity> field_25157;

    public DefeatTargetTask(int i, BiPredicate<LivingEntity, LivingEntity> biPredicate) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.CELEBRATE_LOCATION, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.DANCING, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.duration = i;
        this.field_25157 = biPredicate;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        return this.getAttackTarget(arg2).method_29504();
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        LivingEntity lv = this.getAttackTarget(arg2);
        if (this.field_25157.test(arg2, lv)) {
            arg2.getBrain().remember(MemoryModuleType.DANCING, true, this.duration);
        }
        arg2.getBrain().remember(MemoryModuleType.CELEBRATE_LOCATION, lv.getBlockPos(), this.duration);
        if (lv.getType() != EntityType.PLAYER || arg.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            arg2.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            arg2.getBrain().forget(MemoryModuleType.ANGRY_AT);
        }
    }

    private LivingEntity getAttackTarget(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

