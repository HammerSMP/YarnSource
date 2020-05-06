/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class ConditionalTask<E extends LivingEntity>
extends Task<E> {
    private final Predicate<E> condition;
    private final Task<? super E> delegate;
    private final boolean allowsContinuation;

    public ConditionalTask(Map<MemoryModuleType<?>, MemoryModuleState> map, Predicate<E> predicate, Task<? super E> arg, boolean bl) {
        super(ConditionalTask.merge(map, arg.requiredMemoryStates));
        this.condition = predicate;
        this.delegate = arg;
        this.allowsContinuation = bl;
    }

    private static Map<MemoryModuleType<?>, MemoryModuleState> merge(Map<MemoryModuleType<?>, MemoryModuleState> map, Map<MemoryModuleType<?>, MemoryModuleState> map2) {
        HashMap map3 = Maps.newHashMap();
        map3.putAll(map);
        map3.putAll(map2);
        return map3;
    }

    public ConditionalTask(Predicate<E> predicate, Task<? super E> arg) {
        this((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), (Predicate<? super E>)predicate, arg, false);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return this.condition.test(arg2) && this.delegate.shouldRun(arg, arg2);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, E arg2, long l) {
        return this.allowsContinuation && this.condition.test(arg2) && this.delegate.shouldKeepRunning(arg, arg2, l);
    }

    @Override
    protected boolean isTimeLimitExceeded(long l) {
        return false;
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        this.delegate.run(arg, arg2, l);
    }

    @Override
    protected void keepRunning(ServerWorld arg, E arg2, long l) {
        this.delegate.keepRunning(arg, arg2, l);
    }

    @Override
    protected void finishRunning(ServerWorld arg, E arg2, long l) {
        this.delegate.finishRunning(arg, arg2, l);
    }

    @Override
    public String toString() {
        return "RunIf: " + this.delegate;
    }
}

