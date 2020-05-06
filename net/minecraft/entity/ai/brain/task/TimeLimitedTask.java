/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class TimeLimitedTask<E extends LivingEntity>
extends Task<E> {
    private boolean needsTimeReset;
    private boolean delegateRunning;
    private final IntRange timeRange;
    private final Task<? super E> delegate;
    private int timeLeft;

    public TimeLimitedTask(Task<? super E> arg, IntRange arg2) {
        this(arg, false, arg2);
    }

    public TimeLimitedTask(Task<? super E> arg, boolean bl, IntRange arg2) {
        super(arg.requiredMemoryStates);
        this.delegate = arg;
        this.needsTimeReset = !bl;
        this.timeRange = arg2;
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, E arg2) {
        if (!this.delegate.shouldRun(arg, arg2)) {
            return false;
        }
        if (this.needsTimeReset) {
            this.resetTimeLeft(arg);
            this.needsTimeReset = false;
        }
        if (this.timeLeft > 0) {
            --this.timeLeft;
        }
        return !this.delegateRunning && this.timeLeft == 0;
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        this.delegate.run(arg, arg2, l);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, E arg2, long l) {
        return this.delegate.shouldKeepRunning(arg, arg2, l);
    }

    @Override
    protected void keepRunning(ServerWorld arg, E arg2, long l) {
        this.delegate.keepRunning(arg, arg2, l);
        this.delegateRunning = this.delegate.getStatus() == Task.Status.RUNNING;
    }

    @Override
    protected void finishRunning(ServerWorld arg, E arg2, long l) {
        this.resetTimeLeft(arg);
        this.delegate.finishRunning(arg, arg2, l);
    }

    private void resetTimeLeft(ServerWorld arg) {
        this.timeLeft = this.timeRange.choose(arg.random);
    }

    @Override
    protected boolean isTimeLimitExceeded(long l) {
        return false;
    }

    @Override
    public String toString() {
        return "RunSometimes: " + this.delegate;
    }
}

