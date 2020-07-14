/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Either
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem
implements ChunkHolder.LevelUpdateListener,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<MessageListener<?>, LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>> queues;
    private final Set<MessageListener<?>> actors;
    private final TaskExecutor<TaskQueue.PrioritizedTask> sorter;

    public ChunkTaskPrioritySystem(List<MessageListener<?>> actors, Executor executor, int maxQueues) {
        this.queues = actors.stream().collect(Collectors.toMap(Function.identity(), arg -> new LevelPrioritizedQueue(arg.getName() + "_queue", maxQueues)));
        this.actors = Sets.newHashSet(actors);
        this.sorter = new TaskExecutor<TaskQueue.PrioritizedTask>(new TaskQueue.Prioritized(4), executor, "sorter");
    }

    public static Task<Runnable> createMessage(Runnable runnable, long pos, IntSupplier lastLevelUpdatedToProvider) {
        return new Task<Runnable>(arg -> () -> {
            runnable.run();
            arg.send(Unit.INSTANCE);
        }, pos, lastLevelUpdatedToProvider);
    }

    public static Task<Runnable> createMessage(ChunkHolder holder, Runnable runnable) {
        return ChunkTaskPrioritySystem.createMessage(runnable, holder.getPos().toLong(), holder::getCompletedLevel);
    }

    public static SorterMessage createSorterMessage(Runnable runnable, long pos, boolean bl) {
        return new SorterMessage(runnable, pos, bl);
    }

    public <T> MessageListener<Task<T>> createExecutor(MessageListener<T> executor, boolean bl) {
        return (MessageListener)this.sorter.ask(arg2 -> new TaskQueue.PrioritizedTask(0, () -> {
            this.getQueue(executor);
            arg2.send(MessageListener.create("chunk priority sorter around " + executor.getName(), arg2 -> this.execute(executor, ((Task)arg2).function, ((Task)arg2).pos, ((Task)arg2).lastLevelUpdatedToProvider, bl)));
        })).join();
    }

    public MessageListener<SorterMessage> createSorterExecutor(MessageListener<Runnable> executor) {
        return (MessageListener)this.sorter.ask(arg2 -> new TaskQueue.PrioritizedTask(0, () -> arg2.send(MessageListener.create("chunk priority sorter around " + executor.getName(), arg2 -> this.sort(executor, ((SorterMessage)arg2).pos, ((SorterMessage)arg2).runnable, ((SorterMessage)arg2).field_17451))))).join();
    }

    @Override
    public void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter) {
        this.sorter.send(new TaskQueue.PrioritizedTask(0, () -> {
            int j = levelGetter.getAsInt();
            this.queues.values().forEach(arg2 -> arg2.updateLevel(j, pos, targetLevel));
            levelSetter.accept(targetLevel);
        }));
    }

    private <T> void sort(MessageListener<T> arg, long l, Runnable runnable, boolean bl) {
        this.sorter.send(new TaskQueue.PrioritizedTask(1, () -> {
            LevelPrioritizedQueue lv = this.getQueue(arg);
            lv.clearPosition(l, bl);
            if (this.actors.remove(arg)) {
                this.method_17630(lv, arg);
            }
            runnable.run();
        }));
    }

    private <T> void execute(MessageListener<T> actor, Function<MessageListener<Unit>, T> function, long l, IntSupplier lastLevelUpdatedToProvider, boolean bl) {
        this.sorter.send(new TaskQueue.PrioritizedTask(2, () -> {
            LevelPrioritizedQueue lv = this.getQueue(actor);
            int i = lastLevelUpdatedToProvider.getAsInt();
            lv.add(Optional.of(function), l, i);
            if (bl) {
                lv.add(Optional.empty(), l, i);
            }
            if (this.actors.remove(actor)) {
                this.method_17630(lv, actor);
            }
        }));
    }

    private <T> void method_17630(LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> arg, MessageListener<T> actor) {
        this.sorter.send(new TaskQueue.PrioritizedTask(3, () -> {
            Stream<Either<Either, Runnable>> stream = arg.poll();
            if (stream == null) {
                this.actors.add(actor);
            } else {
                Util.combine(stream.map(either -> (CompletableFuture)either.map(actor::ask, runnable -> {
                    runnable.run();
                    return CompletableFuture.completedFuture(Unit.INSTANCE);
                })).collect(Collectors.toList())).thenAccept(list -> this.method_17630(arg, actor));
            }
        }));
    }

    private <T> LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> getQueue(MessageListener<T> actor) {
        LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> lv = this.queues.get(actor);
        if (lv == null) {
            throw Util.throwOrPause(new IllegalArgumentException("No queue for: " + actor));
        }
        return lv;
    }

    @VisibleForTesting
    public String getDebugString() {
        return this.queues.entrySet().stream().map(entry -> ((MessageListener)entry.getKey()).getName() + "=[" + ((LevelPrioritizedQueue)entry.getValue()).method_21679().stream().map(long_ -> long_ + ":" + new ChunkPos((long)long_)).collect(Collectors.joining(",")) + "]").collect(Collectors.joining(",")) + ", s=" + this.actors.size();
    }

    @Override
    public void close() {
        this.queues.keySet().forEach(MessageListener::close);
    }

    public static final class SorterMessage {
        private final Runnable runnable;
        private final long pos;
        private final boolean field_17451;

        private SorterMessage(Runnable runnable, long pos, boolean bl) {
            this.runnable = runnable;
            this.pos = pos;
            this.field_17451 = bl;
        }
    }

    public static final class Task<T> {
        private final Function<MessageListener<Unit>, T> function;
        private final long pos;
        private final IntSupplier lastLevelUpdatedToProvider;

        private Task(Function<MessageListener<Unit>, T> function, long pos, IntSupplier lastLevelUpdatedToProvider) {
            this.function = function;
            this.pos = pos;
            this.lastLevelUpdatedToProvider = lastLevelUpdatedToProvider;
        }
    }
}

