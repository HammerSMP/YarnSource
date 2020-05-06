/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;

public class StopPanickingTask
extends Task<VillagerEntity> {
    public StopPanickingTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        boolean bl;
        boolean bl2 = bl = PanicTask.wasHurt(arg2) || PanicTask.isHostileNearby(arg2) || StopPanickingTask.wasHurtByNearbyEntity(arg2);
        if (!bl) {
            arg2.getBrain().forget(MemoryModuleType.HURT_BY);
            arg2.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
            arg2.getBrain().refreshActivities(arg.getTimeOfDay(), arg.getTime());
        }
    }

    private static boolean wasHurtByNearbyEntity(VillagerEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.HURT_BY_ENTITY).filter(arg2 -> arg2.squaredDistanceTo(arg) <= 36.0).isPresent();
    }
}

