/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

public class ServerTickScheduler<T>
implements TickScheduler<T> {
    protected final Predicate<T> invalidObjPredicate;
    private final Function<T, Identifier> idToName;
    private final Set<ScheduledTick<T>> scheduledTickActions = Sets.newHashSet();
    private final TreeSet<ScheduledTick<T>> scheduledTickActionsInOrder = Sets.newTreeSet(ScheduledTick.getComparator());
    private final ServerWorld world;
    private final Queue<ScheduledTick<T>> currentTickActions = Queues.newArrayDeque();
    private final List<ScheduledTick<T>> consumedTickActions = Lists.newArrayList();
    private final Consumer<ScheduledTick<T>> tickConsumer;

    public ServerTickScheduler(ServerWorld world, Predicate<T> invalidObjPredicate, Function<T, Identifier> idToName, Consumer<ScheduledTick<T>> consumer) {
        this.invalidObjPredicate = invalidObjPredicate;
        this.idToName = idToName;
        this.world = world;
        this.tickConsumer = consumer;
    }

    public void tick() {
        ScheduledTick<T> lv3;
        int i = this.scheduledTickActionsInOrder.size();
        if (i != this.scheduledTickActions.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (i > 65536) {
            i = 65536;
        }
        ServerChunkManager lv = this.world.getChunkManager();
        Iterator<ScheduledTick<T>> iterator = this.scheduledTickActionsInOrder.iterator();
        this.world.getProfiler().push("cleaning");
        while (i > 0 && iterator.hasNext()) {
            ScheduledTick<T> lv2 = iterator.next();
            if (lv2.time > this.world.getTime()) break;
            if (!lv.shouldTickBlock(lv2.pos)) continue;
            iterator.remove();
            this.scheduledTickActions.remove(lv2);
            this.currentTickActions.add(lv2);
            --i;
        }
        this.world.getProfiler().swap("ticking");
        while ((lv3 = this.currentTickActions.poll()) != null) {
            if (lv.shouldTickBlock(lv3.pos)) {
                try {
                    this.consumedTickActions.add(lv3);
                    this.tickConsumer.accept(lv3);
                    continue;
                }
                catch (Throwable throwable) {
                    CrashReport lv4 = CrashReport.create(throwable, "Exception while ticking");
                    CrashReportSection lv5 = lv4.addElement("Block being ticked");
                    CrashReportSection.addBlockInfo(lv5, lv3.pos, null);
                    throw new CrashException(lv4);
                }
            }
            this.schedule(lv3.pos, lv3.getObject(), 0);
        }
        this.world.getProfiler().pop();
        this.consumedTickActions.clear();
        this.currentTickActions.clear();
    }

    @Override
    public boolean isTicking(BlockPos pos, T object) {
        return this.currentTickActions.contains(new ScheduledTick<T>(pos, object));
    }

    public List<ScheduledTick<T>> getScheduledTicksInChunk(ChunkPos arg, boolean updateState, boolean getStaleTicks) {
        int i = (arg.x << 4) - 2;
        int j = i + 16 + 2;
        int k = (arg.z << 4) - 2;
        int l = k + 16 + 2;
        return this.getScheduledTicks(new BlockBox(i, 0, k, j, 256, l), updateState, getStaleTicks);
    }

    public List<ScheduledTick<T>> getScheduledTicks(BlockBox bounds, boolean updateState, boolean getStaleTicks) {
        List<ScheduledTick<T>> list = this.transferTicksInBounds(null, this.scheduledTickActionsInOrder, bounds, updateState);
        if (updateState && list != null) {
            this.scheduledTickActions.removeAll(list);
        }
        list = this.transferTicksInBounds(list, this.currentTickActions, bounds, updateState);
        if (!getStaleTicks) {
            list = this.transferTicksInBounds(list, this.consumedTickActions, bounds, updateState);
        }
        return list == null ? Collections.emptyList() : list;
    }

    @Nullable
    private List<ScheduledTick<T>> transferTicksInBounds(@Nullable List<ScheduledTick<T>> dst, Collection<ScheduledTick<T>> src, BlockBox bounds, boolean move) {
        Iterator<ScheduledTick<T>> iterator = src.iterator();
        while (iterator.hasNext()) {
            ScheduledTick<T> lv = iterator.next();
            BlockPos lv2 = lv.pos;
            if (lv2.getX() < bounds.minX || lv2.getX() >= bounds.maxX || lv2.getZ() < bounds.minZ || lv2.getZ() >= bounds.maxZ) continue;
            if (move) {
                iterator.remove();
            }
            if (dst == null) {
                dst = Lists.newArrayList();
            }
            dst.add(lv);
        }
        return dst;
    }

    public void copyScheduledTicks(BlockBox box, BlockPos offset) {
        List<ScheduledTick<T>> list = this.getScheduledTicks(box, false, false);
        for (ScheduledTick<T> lv : list) {
            if (!box.contains(lv.pos)) continue;
            BlockPos lv2 = lv.pos.add(offset);
            T object = lv.getObject();
            this.addScheduledTick(new ScheduledTick<T>(lv2, object, lv.time, lv.priority));
        }
    }

    public ListTag toTag(ChunkPos arg) {
        List<ScheduledTick<T>> list = this.getScheduledTicksInChunk(arg, false, true);
        return ServerTickScheduler.serializeScheduledTicks(this.idToName, list, this.world.getTime());
    }

    private static <T> ListTag serializeScheduledTicks(Function<T, Identifier> identifierProvider, Iterable<ScheduledTick<T>> scheduledTicks, long time) {
        ListTag lv = new ListTag();
        for (ScheduledTick<T> lv2 : scheduledTicks) {
            CompoundTag lv3 = new CompoundTag();
            lv3.putString("i", identifierProvider.apply(lv2.getObject()).toString());
            lv3.putInt("x", lv2.pos.getX());
            lv3.putInt("y", lv2.pos.getY());
            lv3.putInt("z", lv2.pos.getZ());
            lv3.putInt("t", (int)(lv2.time - time));
            lv3.putInt("p", lv2.priority.getIndex());
            lv.add(lv3);
        }
        return lv;
    }

    @Override
    public boolean isScheduled(BlockPos pos, T object) {
        return this.scheduledTickActions.contains(new ScheduledTick<T>(pos, object));
    }

    @Override
    public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
        if (!this.invalidObjPredicate.test(object)) {
            this.addScheduledTick(new ScheduledTick<T>(pos, object, (long)delay + this.world.getTime(), priority));
        }
    }

    private void addScheduledTick(ScheduledTick<T> arg) {
        if (!this.scheduledTickActions.contains(arg)) {
            this.scheduledTickActions.add(arg);
            this.scheduledTickActionsInOrder.add(arg);
        }
    }

    public int getTicks() {
        return this.scheduledTickActions.size();
    }
}

