/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class DummyClientTickScheduler<T>
implements TickScheduler<T> {
    private static final DummyClientTickScheduler<Object> INSTANCE = new DummyClientTickScheduler();

    public static <T> DummyClientTickScheduler<T> get() {
        return INSTANCE;
    }

    @Override
    public boolean isScheduled(BlockPos arg, T object) {
        return false;
    }

    @Override
    public void schedule(BlockPos arg, T object, int i) {
    }

    @Override
    public void schedule(BlockPos arg, T object, int i, TickPriority arg2) {
    }

    @Override
    public boolean isTicking(BlockPos arg, T object) {
        return false;
    }
}

