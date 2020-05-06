/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionBasedStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorageIoWorker
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Thread thread;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Queue<Runnable> tasks = Queues.newConcurrentLinkedQueue();
    private final RegionBasedStorage storage;
    private final Map<ChunkPos, Result> results = Maps.newLinkedHashMap();
    private boolean active = true;
    private CompletableFuture<Void> future = new CompletableFuture();

    StorageIoWorker(RegionBasedStorage arg, String string) {
        this.storage = arg;
        this.thread = new Thread(this::work);
        this.thread.setName(string + " IO worker");
        this.thread.start();
    }

    public CompletableFuture<Void> setResult(ChunkPos arg, CompoundTag arg2) {
        return this.run(completableFuture -> () -> {
            Result lv = this.results.computeIfAbsent(arg, arg -> new Result());
            lv.nbt = arg2;
            lv.future.whenComplete((arg, throwable) -> {
                if (throwable != null) {
                    completableFuture.completeExceptionally((Throwable)throwable);
                } else {
                    completableFuture.complete(null);
                }
            });
        });
    }

    @Nullable
    public CompoundTag getNbt(ChunkPos arg) throws IOException {
        CompletableFuture completableFuture2 = this.run(completableFuture -> () -> {
            Result lv = this.results.get(arg);
            if (lv != null) {
                completableFuture.complete(lv.nbt);
            } else {
                try {
                    CompoundTag lv2 = this.storage.getTagAt(arg);
                    completableFuture.complete(lv2);
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to read chunk {}", (Object)arg, (Object)exception);
                    completableFuture.completeExceptionally(exception);
                }
            }
        });
        try {
            return (CompoundTag)completableFuture2.join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    private CompletableFuture<Void> shutdown() {
        return this.run(completableFuture -> () -> {
            this.active = false;
            this.future = completableFuture;
        });
    }

    public CompletableFuture<Void> completeAll() {
        return this.run(completableFuture -> () -> {
            CompletableFuture<Void> completableFuture2 = CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(arg -> ((Result)arg).future).toArray(CompletableFuture[]::new));
            completableFuture2.whenComplete((object, throwable) -> {
                try {
                    this.storage.method_26982();
                    completableFuture.complete(null);
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to synchronized chunks", (Throwable)exception);
                    completableFuture.completeExceptionally(exception);
                }
            });
        });
    }

    private <T> CompletableFuture<T> run(Function<CompletableFuture<T>, Runnable> function) {
        CompletableFuture completableFuture = new CompletableFuture();
        this.tasks.add(function.apply(completableFuture));
        LockSupport.unpark(this.thread);
        return completableFuture;
    }

    private void park() {
        LockSupport.park("waiting for tasks");
    }

    private void work() {
        try {
            while (this.active) {
                boolean bl = this.runTask();
                boolean bl2 = this.writeResult();
                if (bl || bl2) continue;
                this.park();
            }
            this.runTask();
            this.writeAll();
        }
        finally {
            this.finish();
        }
    }

    private boolean writeResult() {
        Iterator<Map.Entry<ChunkPos, Result>> iterator = this.results.entrySet().iterator();
        if (!iterator.hasNext()) {
            return false;
        }
        Map.Entry<ChunkPos, Result> entry = iterator.next();
        iterator.remove();
        this.write(entry.getKey(), entry.getValue());
        return true;
    }

    private void writeAll() {
        this.results.forEach((arg_0, arg_1) -> this.write(arg_0, arg_1));
        this.results.clear();
    }

    private void write(ChunkPos arg, Result arg2) {
        try {
            this.storage.write(arg, arg2.nbt);
            arg2.future.complete(null);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to store chunk {}", (Object)arg, (Object)exception);
            arg2.future.completeExceptionally(exception);
        }
    }

    private void finish() {
        try {
            this.storage.close();
            this.future.complete(null);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to close storage", (Throwable)exception);
            this.future.completeExceptionally(exception);
        }
    }

    private boolean runTask() {
        Runnable runnable;
        boolean bl = false;
        while ((runnable = this.tasks.poll()) != null) {
            bl = true;
            runnable.run();
        }
        return bl;
    }

    @Override
    public void close() throws IOException {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        try {
            this.shutdown().join();
        }
        catch (CompletionException completionException) {
            if (completionException.getCause() instanceof IOException) {
                throw (IOException)completionException.getCause();
            }
            throw completionException;
        }
    }

    static class Result {
        private CompoundTag nbt;
        private final CompletableFuture<Void> future = new CompletableFuture();

        private Result() {
        }
    }
}

