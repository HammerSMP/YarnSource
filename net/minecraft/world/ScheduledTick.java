/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public class ScheduledTick<T> {
    private static long idCounter;
    private final T object;
    public final BlockPos pos;
    public final long time;
    public final TickPriority priority;
    private final long id = idCounter++;

    public ScheduledTick(BlockPos arg, T object) {
        this(arg, object, 0L, TickPriority.NORMAL);
    }

    public ScheduledTick(BlockPos arg, T object, long l, TickPriority arg2) {
        this.pos = arg.toImmutable();
        this.object = object;
        this.time = l;
        this.priority = arg2;
    }

    public boolean equals(Object object) {
        if (object instanceof ScheduledTick) {
            ScheduledTick lv = (ScheduledTick)object;
            return this.pos.equals(lv.pos) && this.object == lv.object;
        }
        return false;
    }

    public int hashCode() {
        return this.pos.hashCode();
    }

    public static <T> Comparator<ScheduledTick<T>> getComparator() {
        return Comparator.comparingLong(arg -> arg.time).thenComparing(arg -> arg.priority).thenComparingLong(arg -> arg.id);
    }

    public String toString() {
        return this.object + ": " + this.pos + ", " + this.time + ", " + (Object)((Object)this.priority) + ", " + this.id;
    }

    public T getObject() {
        return this.object;
    }
}

