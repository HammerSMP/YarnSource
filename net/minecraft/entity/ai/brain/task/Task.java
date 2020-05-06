/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Task<E extends LivingEntity> {
    protected final Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryStates;
    private Status status = Status.STOPPED;
    private long endTime;
    private final int minRunTime;
    private final int maxRunTime;

    public Task(Map<MemoryModuleType<?>, MemoryModuleState> map) {
        this(map, 60);
    }

    public Task(Map<MemoryModuleType<?>, MemoryModuleState> map, int i) {
        this(map, i, i);
    }

    public Task(Map<MemoryModuleType<?>, MemoryModuleState> map, int i, int j) {
        this.minRunTime = i;
        this.maxRunTime = j;
        this.requiredMemoryStates = map;
    }

    public Status getStatus() {
        return this.status;
    }

    public final boolean tryStarting(ServerWorld arg, E arg2, long l) {
        if (this.hasRequiredMemoryState(arg2) && this.shouldRun(arg, arg2)) {
            this.status = Status.RUNNING;
            int i = this.minRunTime + arg.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
            this.endTime = l + (long)i;
            this.run(arg, arg2, l);
            return true;
        }
        return false;
    }

    protected void run(ServerWorld arg, E arg2, long l) {
    }

    public final void tick(ServerWorld arg, E arg2, long l) {
        if (!this.isTimeLimitExceeded(l) && this.shouldKeepRunning(arg, arg2, l)) {
            this.keepRunning(arg, arg2, l);
        } else {
            this.stop(arg, arg2, l);
        }
    }

    protected void keepRunning(ServerWorld arg, E arg2, long l) {
    }

    public final void stop(ServerWorld arg, E arg2, long l) {
        this.status = Status.STOPPED;
        this.finishRunning(arg, arg2, l);
    }

    protected void finishRunning(ServerWorld arg, E arg2, long l) {
    }

    protected boolean shouldKeepRunning(ServerWorld arg, E arg2, long l) {
        return false;
    }

    protected boolean isTimeLimitExceeded(long l) {
        return l > this.endTime;
    }

    protected boolean shouldRun(ServerWorld arg, E arg2) {
        return true;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private boolean hasRequiredMemoryState(E arg) {
        return this.requiredMemoryStates.entrySet().stream().allMatch(entry -> {
            MemoryModuleType lv = (MemoryModuleType)entry.getKey();
            MemoryModuleState lv2 = (MemoryModuleState)((Object)((Object)entry.getValue()));
            return arg.getBrain().isMemoryInState(lv, lv2);
        });
    }

    public static enum Status {
        STOPPED,
        RUNNING;

    }
}

