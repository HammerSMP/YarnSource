/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class MultiTickScheduler<T>
implements TickScheduler<T> {
    private final Function<BlockPos, TickScheduler<T>> mapper;

    public MultiTickScheduler(Function<BlockPos, TickScheduler<T>> function) {
        this.mapper = function;
    }

    @Override
    public boolean isScheduled(BlockPos arg, T object) {
        return this.mapper.apply(arg).isScheduled(arg, object);
    }

    @Override
    public void schedule(BlockPos arg, T object, int i, TickPriority arg2) {
        this.mapper.apply(arg).schedule(arg, object, i, arg2);
    }

    @Override
    public boolean isTicking(BlockPos arg, T object) {
        return false;
    }
}

