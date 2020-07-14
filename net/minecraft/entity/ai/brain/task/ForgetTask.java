/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class ForgetTask<E extends LivingEntity>
extends Task<E> {
    private final Predicate<E> condition;
    private final MemoryModuleType<?> memory;

    public ForgetTask(Predicate<E> condition, MemoryModuleType<?> memory) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(memory, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.condition = condition;
        this.memory = memory;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.condition.test(entity);
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        ((LivingEntity)entity).getBrain().forget(this.memory);
    }
}

