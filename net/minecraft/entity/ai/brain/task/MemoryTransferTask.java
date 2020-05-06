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
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class MemoryTransferTask<E extends MobEntity, T>
extends Task<E> {
    private final Predicate<E> runPredicate;
    private final MemoryModuleType<? extends T> sourceType;
    private final MemoryModuleType<T> targetType;
    private final IntRange duration;

    public MemoryTransferTask(Predicate<E> predicate, MemoryModuleType<? extends T> arg, MemoryModuleType<T> arg2, IntRange arg3) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(arg, (Object)((Object)MemoryModuleState.VALUE_PRESENT), arg2, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.runPredicate = predicate;
        this.sourceType = arg;
        this.targetType = arg2;
        this.duration = arg3;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return this.runPredicate.test(arg2);
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        Brain<?> lv = ((LivingEntity)arg2).getBrain();
        lv.remember(this.targetType, lv.getOptionalMemory(this.sourceType).get(), this.duration.choose(arg.random));
    }
}

