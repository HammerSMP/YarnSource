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
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return !((Entity)arg2).hasVehicle();
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        if (this.isRideTargetClose(arg2)) {
            ((Entity)arg2).startRiding(this.getRideTarget(arg2));
        } else {
            LookTargetUtil.walkTowards(arg2, this.getRideTarget(arg2), this.field_23132, 1);
        }
    }

    private boolean isRideTargetClose(E arg) {
        return this.getRideTarget(arg).isInRange((Entity)arg, 1.0);
    }

    private Entity getRideTarget(E arg) {
        return ((LivingEntity)arg).getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}

