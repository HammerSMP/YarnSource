/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.server.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class SimpleTickScheduler<T>
implements TickScheduler<T> {
    private final List<Tick<T>> scheduledTicks;
    private final Function<T, Identifier> identifierProvider;

    public SimpleTickScheduler(Function<T, Identifier> identifierProvider, List<ScheduledTick<T>> scheduledTicks, long startTime) {
        this(identifierProvider, scheduledTicks.stream().map(arg -> new Tick(arg.getObject(), arg.pos, (int)(arg.time - startTime), arg.priority)).collect(Collectors.toList()));
    }

    private SimpleTickScheduler(Function<T, Identifier> identifierProvider, List<Tick<T>> scheduledTicks) {
        this.scheduledTicks = scheduledTicks;
        this.identifierProvider = identifierProvider;
    }

    @Override
    public boolean isScheduled(BlockPos pos, T object) {
        return false;
    }

    @Override
    public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
        this.scheduledTicks.add(new Tick(object, pos, delay, priority));
    }

    @Override
    public boolean isTicking(BlockPos pos, T object) {
        return false;
    }

    public ListTag toNbt() {
        ListTag lv = new ListTag();
        for (Tick<T> lv2 : this.scheduledTicks) {
            CompoundTag lv3 = new CompoundTag();
            lv3.putString("i", this.identifierProvider.apply(((Tick)lv2).object).toString());
            lv3.putInt("x", lv2.pos.getX());
            lv3.putInt("y", lv2.pos.getY());
            lv3.putInt("z", lv2.pos.getZ());
            lv3.putInt("t", lv2.delay);
            lv3.putInt("p", lv2.priority.getIndex());
            lv.add(lv3);
        }
        return lv;
    }

    public static <T> SimpleTickScheduler<T> fromNbt(ListTag ticks, Function<T, Identifier> function, Function<Identifier, T> function2) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < ticks.size(); ++i) {
            CompoundTag lv = ticks.getCompound(i);
            T object = function2.apply(new Identifier(lv.getString("i")));
            if (object == null) continue;
            BlockPos lv2 = new BlockPos(lv.getInt("x"), lv.getInt("y"), lv.getInt("z"));
            list.add(new Tick(object, lv2, lv.getInt("t"), TickPriority.byIndex(lv.getInt("p"))));
        }
        return new SimpleTickScheduler<T>(function, list);
    }

    public void scheduleTo(TickScheduler<T> scheduler) {
        this.scheduledTicks.forEach(arg2 -> scheduler.schedule(arg2.pos, ((Tick)arg2).object, arg2.delay, arg2.priority));
    }

    static class Tick<T> {
        private final T object;
        public final BlockPos pos;
        public final int delay;
        public final TickPriority priority;

        private Tick(T object, BlockPos pos, int delay, TickPriority priority) {
            this.object = object;
            this.pos = pos;
            this.delay = delay;
            this.priority = priority;
        }

        public String toString() {
            return this.object + ": " + this.pos + ", " + this.delay + ", " + (Object)((Object)this.priority);
        }
    }
}

