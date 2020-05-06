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

    public ForgetTask(Predicate<E> predicate, MemoryModuleType<?> arg) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(arg, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.condition = predicate;
        this.memory = arg;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return this.condition.test(arg2);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        ((LivingEntity)arg2).getBrain().forget(this.memory);
    }
}

