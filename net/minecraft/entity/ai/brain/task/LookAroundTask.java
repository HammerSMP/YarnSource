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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask
extends Task<MobEntity> {
    public LookAroundTask(int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), i, j);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, MobEntity arg22, long l) {
        return arg22.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET).filter(arg2 -> arg2.isSeenBy(arg22)).isPresent();
    }

    @Override
    protected void finishRunning(ServerWorld arg, MobEntity arg2, long l) {
        arg2.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld arg, MobEntity arg22, long l) {
        arg22.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET).ifPresent(arg2 -> arg22.getLookControl().lookAt(arg2.getPos()));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (MobEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (MobEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (MobEntity)arg2, l);
    }
}

