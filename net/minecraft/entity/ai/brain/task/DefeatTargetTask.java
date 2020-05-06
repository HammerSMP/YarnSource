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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DefeatTargetTask
extends Task<LivingEntity> {
    private final int duration;

    public DefeatTargetTask(int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.CELEBRATE_LOCATION, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.duration = i;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, LivingEntity arg2) {
        return this.getAttackTarget(arg2).getHealth() <= 0.0f;
    }

    @Override
    protected void run(ServerWorld arg, LivingEntity arg2, long l) {
        BlockPos lv = this.getAttackTarget(arg2).getBlockPos();
        arg2.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        arg2.getBrain().forget(MemoryModuleType.ANGRY_AT);
        arg2.getBrain().remember(MemoryModuleType.CELEBRATE_LOCATION, lv, this.duration);
    }

    private LivingEntity getAttackTarget(LivingEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

