/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class StartRidingTask<E extends LivingEntity>
extends Task<E> {
    private final float field_23132;

    public StartRidingTask(float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.RIDE_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.field_23132 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return !((Entity)entity).hasVehicle();
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        if (this.isRideTargetClose(entity)) {
            ((Entity)entity).startRiding(this.getRideTarget(entity));
        } else {
            LookTargetUtil.walkTowards(entity, this.getRideTarget(entity), this.field_23132, 1);
        }
    }

    private boolean isRideTargetClose(E entity) {
        return this.getRideTarget(entity).isInRange((Entity)entity, 1.0);
    }

    private Entity getRideTarget(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}

