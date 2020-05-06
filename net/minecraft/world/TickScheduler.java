/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public interface TickScheduler<T> {
    public boolean isScheduled(BlockPos var1, T var2);

    default public void schedule(BlockPos arg, T object, int i) {
        this.schedule(arg, object, i, TickPriority.NORMAL);
    }

    public void schedule(BlockPos var1, T var2, int var3, TickPriority var4);

    public boolean isTicking(BlockPos var1, T var2);
}

