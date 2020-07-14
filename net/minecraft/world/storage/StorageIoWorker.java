/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import net.minecraft.world.storage.RegionBasedStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorageIoWorker
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final TaskExecutor<TaskQueue.PrioritizedTask> field_24468;
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();

    protected StorageIoWorker(File file, boolean bl, String string) {
        this.storage = new RegionBasedStorage(file, bl);
        this.field_24468 = new TaskExecutor<TaskQueue.PrioritizedTask>(new TaskQueue.Prioritized(Priority.values().length), Util.method_27958(), "IOWorker-" + string);
    }

    public CompletableFuture<Void> setResult(ChunkPos pos, CompoundTag nbt) {
        return this.run(() -> {
            Result lv = this.results.computeIfAbsent(pos, arg2 -> new Result(nbt));
            lv.nbt = nbt;
            return Either.left((Object)lv.future);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public CompoundTag getNbt(ChunkPos pos) throws IOException {
        CompletableFuture completableFuture = this.run(() -> {
            Result lv = this.results.get(pos);
            if (lv != null) {
                return Either.left((Object)lv.nbt);
            }
            try {
                CompoundTag lv2 = this.storage.getTagAt(pos);
                return Either.left((Object)lv2);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to read chunk {}", (Object)pos, (Object)exception);
                return Either.right((Object)exception);
            }
        });
        try {
            return (CompoundTag)completableFuture.join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    public CompletableFuture<Void> completeAll() {
        CompletionStage completableFuture = this.run(() -> Either.left(CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(arg -> ((Result)arg).future).toArray(CompletableFuture[]::new)))).thenCompose(Function.identity());
        return ((CompletableFuture)completableFuture).thenCompose(void_ -> this.run(() -> {
            try {
                this.storage.method_26982();
                return Either.left(null);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to synchronized chunks", (Throwable)exception);
                return Either.right((Object)exception);
            }
        }));
    }

    private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> supplier) {
        return this.field_24468.method_27918(arg -> new TaskQueue.PrioritizedTask(Priority.HIGH.ordinal(), () -> this.method_27939(arg, (Supplier)supplier)));
    }

    private void writeResult() {
        Iterator<Map.Entry<ChunkPos, Result>> iterator = this.results.entrySet().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        Map.Entry<ChunkPos, Result> entry = iterator.next();
        iterator.remove();
        this.write(entry.getKey(), entry.getValue());
        this.method_27945();
    }

    private void method_27945() {
        this.field_24468.send(new TaskQueue.PrioritizedTask(Priority.LOW.ordinal(), this::writeResult));
    }

    private void write(ChunkPos pos, Result arg2) {
        try {
            this.storage.write(pos, arg2.nbt);
            arg2.future.complete(null);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to store chunk {}", (Object)pos, (Object)exception);
            arg2.future.completeExceptionally(exception);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        CompletableFuture completableFuture = this.field_24468.ask(arg -> new TaskQueue.PrioritizedTask(Priority.HIGH.ordinal(), () -> arg.send(Unit.INSTANCE)));
        try {
            completableFuture.join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
        this.field_24468.close();
        this.results.forEach((arg_0, arg_1) -> this.write(arg_0, arg_1));
        this.results.clear();
        try {
            this.storage.close();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
        }
    }

    private /* synthetic */ void method_27939(MessageListener arg, Supplier supplier) {
        if (!this.closed.get()) {
            arg.send(supplier.get());
        }
        this.method_27945();
    }

    static class Result {
        private CompoundTag nbt;
        private final CompletableFuture<Void> future = new CompletableFuture();

        public Result(CompoundTag arg) {
            this.nbt = arg;
        }
    }

    static enum Priority {
        HIGH,
        LOW;

    }
}

