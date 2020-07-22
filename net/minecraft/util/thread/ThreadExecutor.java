/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.thread.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadExecutor<R extends Runnable>
implements MessageListener<R>,
Executor {
    private final String name;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Queue<R> tasks = Queues.newConcurrentLinkedQueue();
    private int executionsInProgress;

    protected ThreadExecutor(String name) {
        this.name = name;
    }

    protected abstract R createTask(Runnable var1);

    protected abstract boolean canExecute(R var1);

    public boolean isOnThread() {
        return Thread.currentThread() == this.getThread();
    }

    protected abstract Thread getThread();

    protected boolean shouldExecuteAsync() {
        return !this.isOnThread();
    }

    public int getTaskCount() {
        return this.tasks.size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public <V> CompletableFuture<V> submit(Supplier<V> task) {
        if (this.shouldExecuteAsync()) {
            return CompletableFuture.supplyAsync(task, this);
        }
        return CompletableFuture.completedFuture(task.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable task) {
        if (this.shouldExecuteAsync()) {
            return this.submitAsync(task);
        }
        task.run();
        return CompletableFuture.completedFuture(null);
    }

    public void submitAndJoin(Runnable runnable) {
        if (!this.isOnThread()) {
            this.submitAsync(runnable).join();
        } else {
            runnable.run();
        }
    }

    @Override
    public void send(R runnable) {
        this.tasks.add(runnable);
        LockSupport.unpark(this.getThread());
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.shouldExecuteAsync()) {
            this.send(this.createTask(runnable));
        } else {
            runnable.run();
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected void cancelTasks() {
        this.tasks.clear();
    }

    protected void runTasks() {
        while (this.runTask()) {
        }
    }

    protected boolean runTask() {
        Runnable runnable = (Runnable)this.tasks.peek();
        if (runnable == null) {
            return false;
        }
        if (this.executionsInProgress == 0 && !this.canExecute(runnable)) {
            return false;
        }
        this.executeTask((Runnable)this.tasks.remove());
        return true;
    }

    public void runTasks(BooleanSupplier stopCondition) {
        ++this.executionsInProgress;
        try {
            while (!stopCondition.getAsBoolean()) {
                if (this.runTask()) continue;
                this.waitForTasks();
            }
        }
        finally {
            --this.executionsInProgress;
        }
    }

    protected void waitForTasks() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void executeTask(R task) {
        try {
            task.run();
        }
        catch (Exception exception) {
            LOGGER.fatal("Error executing task on {}", (Object)this.getName(), (Object)exception);
        }
    }

    @Override
    public /* synthetic */ void send(Object message) {
        this.send((R)((Runnable)message));
    }
}

