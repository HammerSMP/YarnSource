/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLightingProvider
extends LightingProvider
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TaskExecutor<Runnable> processor;
    private final ObjectList<Pair<Stage, Runnable>> pendingTasks = new ObjectArrayList();
    private final ThreadedAnvilChunkStorage chunkStorage;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> executor;
    private volatile int taskBatchSize = 5;
    private final AtomicBoolean ticking = new AtomicBoolean();

    public ServerLightingProvider(ChunkProvider arg, ThreadedAnvilChunkStorage arg2, boolean bl, TaskExecutor<Runnable> arg3, MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> arg4) {
        super(arg, true, bl);
        this.chunkStorage = arg2;
        this.executor = arg4;
        this.processor = arg3;
    }

    @Override
    public void close() {
    }

    @Override
    public int doLightUpdates(int i, boolean bl, boolean bl2) {
        throw Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void addLightSource(BlockPos arg, int i) {
        throw Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void checkBlock(BlockPos arg) {
        BlockPos lv = arg.toImmutable();
        this.enqueue(arg.getX() >> 4, arg.getZ() >> 4, Stage.POST_UPDATE, Util.debugRunnable(() -> super.checkBlock(lv), () -> "checkBlock " + lv));
    }

    protected void updateChunkStatus(ChunkPos arg) {
        this.enqueue(arg.x, arg.z, () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> {
            super.setRetainData(arg, false);
            super.setLightEnabled(arg, false);
            for (int i = -1; i < 17; ++i) {
                super.queueData(LightType.BLOCK, ChunkSectionPos.from(arg, i), null, true);
                super.queueData(LightType.SKY, ChunkSectionPos.from(arg, i), null, true);
            }
            for (int j = 0; j < 16; ++j) {
                super.updateSectionStatus(ChunkSectionPos.from(arg, j), true);
            }
        }, () -> "updateChunkStatus " + arg + " " + true));
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos arg, boolean bl) {
        this.enqueue(arg.getSectionX(), arg.getSectionZ(), () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.updateSectionStatus(arg, bl), () -> "updateSectionStatus " + arg + " " + bl));
    }

    @Override
    public void setLightEnabled(ChunkPos arg, boolean bl) {
        this.enqueue(arg.x, arg.z, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setLightEnabled(arg, bl), () -> "enableLight " + arg + " " + bl));
    }

    @Override
    public void queueData(LightType arg, ChunkSectionPos arg2, @Nullable ChunkNibbleArray arg3, boolean bl) {
        this.enqueue(arg2.getSectionX(), arg2.getSectionZ(), () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.queueData(arg, arg2, arg3, bl), () -> "queueData " + arg2));
    }

    private void enqueue(int i, int j, Stage arg, Runnable runnable) {
        this.enqueue(i, j, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(i, j)), arg, runnable);
    }

    private void enqueue(int i, int j, IntSupplier intSupplier, Stage arg, Runnable runnable) {
        this.executor.send(ChunkTaskPrioritySystem.createMessage(() -> {
            this.pendingTasks.add((Object)Pair.of((Object)((Object)arg), (Object)runnable));
            if (this.pendingTasks.size() >= this.taskBatchSize) {
                this.runTasks();
            }
        }, ChunkPos.toLong(i, j), intSupplier));
    }

    @Override
    public void setRetainData(ChunkPos arg, boolean bl) {
        this.enqueue(arg.x, arg.z, () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setRetainData(arg, bl), () -> "retainData " + arg));
    }

    public CompletableFuture<Chunk> light(Chunk arg, boolean bl) {
        ChunkPos lv = arg.getPos();
        arg.setLightOn(false);
        this.enqueue(lv.x, lv.z, Stage.PRE_UPDATE, Util.debugRunnable(() -> {
            ChunkSection[] lvs = arg.getSectionArray();
            for (int i = 0; i < 16; ++i) {
                ChunkSection lv = lvs[i];
                if (ChunkSection.isEmpty(lv)) continue;
                super.updateSectionStatus(ChunkSectionPos.from(lv, i), false);
            }
            super.setLightEnabled(lv, true);
            if (!bl) {
                arg.getLightSourcesStream().forEach(arg2 -> super.addLightSource((BlockPos)arg2, arg.getLuminance((BlockPos)arg2)));
            }
            this.chunkStorage.releaseLightTicket(lv);
        }, () -> "lightChunk " + lv + " " + bl));
        return CompletableFuture.supplyAsync(() -> {
            arg.setLightOn(true);
            super.setRetainData(lv, false);
            return arg;
        }, runnable -> this.enqueue(arg.x, arg.z, Stage.POST_UPDATE, runnable));
    }

    public void tick() {
        if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.ticking.compareAndSet(false, true)) {
            this.processor.send(() -> {
                this.runTasks();
                this.ticking.set(false);
            });
        }
    }

    private void runTasks() {
        int j;
        int i = Math.min(this.pendingTasks.size(), this.taskBatchSize);
        ObjectListIterator objectListIterator = this.pendingTasks.iterator();
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            Pair pair = (Pair)objectListIterator.next();
            if (pair.getFirst() != Stage.PRE_UPDATE) continue;
            ((Runnable)pair.getSecond()).run();
        }
        objectListIterator.back(j);
        super.doLightUpdates(Integer.MAX_VALUE, true, true);
        for (j = 0; objectListIterator.hasNext() && j < i; ++j) {
            Pair pair2 = (Pair)objectListIterator.next();
            if (pair2.getFirst() == Stage.POST_UPDATE) {
                ((Runnable)pair2.getSecond()).run();
            }
            objectListIterator.remove();
        }
    }

    public void setTaskBatchSize(int i) {
        this.taskBatchSize = i;
    }

    static enum Stage {
        PRE_UPDATE,
        POST_UPDATE;

    }
}

