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

    public ServerLightingProvider(ChunkProvider chunkProvider, ThreadedAnvilChunkStorage chunkStorage, boolean hasBlockLight, TaskExecutor<Runnable> processor, MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> executor) {
        super(chunkProvider, true, hasBlockLight);
        this.chunkStorage = chunkStorage;
        this.executor = executor;
        this.processor = processor;
    }

    @Override
    public void close() {
    }

    @Override
    public int doLightUpdates(int maxUpdateCount, boolean doSkylight, boolean skipEdgeLightPropagation) {
        throw Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void addLightSource(BlockPos pos, int level) {
        throw Util.throwOrPause(new UnsupportedOperationException("Ran authomatically on a different thread!"));
    }

    @Override
    public void checkBlock(BlockPos pos) {
        BlockPos lv = pos.toImmutable();
        this.enqueue(pos.getX() >> 4, pos.getZ() >> 4, Stage.POST_UPDATE, Util.debugRunnable(() -> super.checkBlock(lv), () -> "checkBlock " + lv));
    }

    protected void updateChunkStatus(ChunkPos pos) {
        this.enqueue(pos.x, pos.z, () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> {
            super.setRetainData(pos, false);
            super.setColumnEnabled(pos, false);
            for (int i = -1; i < 17; ++i) {
                super.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, i), null, true);
                super.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, i), null, true);
            }
            for (int j = 0; j < 16; ++j) {
                super.setSectionStatus(ChunkSectionPos.from(pos, j), true);
            }
        }, () -> "updateChunkStatus " + pos + " " + true));
    }

    @Override
    public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
        this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setSectionStatus(pos, notReady), () -> "updateSectionStatus " + pos + " " + notReady));
    }

    @Override
    public void setColumnEnabled(ChunkPos pos, boolean lightEnabled) {
        this.enqueue(pos.x, pos.z, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setColumnEnabled(pos, lightEnabled), () -> "enableLight " + pos + " " + lightEnabled));
    }

    @Override
    public void enqueueSectionData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles, boolean bl) {
        this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.enqueueSectionData(lightType, pos, nibbles, bl), () -> "queueData " + pos));
    }

    private void enqueue(int x, int z, Stage stage, Runnable task) {
        this.enqueue(x, z, this.chunkStorage.getCompletedLevelSupplier(ChunkPos.toLong(x, z)), stage, task);
    }

    private void enqueue(int x, int z, IntSupplier completedLevelSupplier, Stage stage, Runnable task) {
        this.executor.send(ChunkTaskPrioritySystem.createMessage(() -> {
            this.pendingTasks.add((Object)Pair.of((Object)((Object)stage), (Object)task));
            if (this.pendingTasks.size() >= this.taskBatchSize) {
                this.runTasks();
            }
        }, ChunkPos.toLong(x, z), completedLevelSupplier));
    }

    @Override
    public void setRetainData(ChunkPos pos, boolean retainData) {
        this.enqueue(pos.x, pos.z, () -> 0, Stage.PRE_UPDATE, Util.debugRunnable(() -> super.setRetainData(pos, retainData), () -> "retainData " + pos));
    }

    public CompletableFuture<Chunk> light(Chunk chunk, boolean excludeBlocks) {
        ChunkPos lv = chunk.getPos();
        chunk.setLightOn(false);
        this.enqueue(lv.x, lv.z, Stage.PRE_UPDATE, Util.debugRunnable(() -> {
            ChunkSection[] lvs = chunk.getSectionArray();
            for (int i = 0; i < 16; ++i) {
                ChunkSection lv = lvs[i];
                if (ChunkSection.isEmpty(lv)) continue;
                super.setSectionStatus(ChunkSectionPos.from(lv, i), false);
            }
            super.setColumnEnabled(lv, true);
            if (!excludeBlocks) {
                chunk.getLightSourcesStream().forEach(arg2 -> super.addLightSource((BlockPos)arg2, chunk.getLuminance((BlockPos)arg2)));
            }
            this.chunkStorage.releaseLightTicket(lv);
        }, () -> "lightChunk " + lv + " " + excludeBlocks));
        return CompletableFuture.supplyAsync(() -> {
            chunk.setLightOn(true);
            super.setRetainData(lv, false);
            return chunk;
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

    public void setTaskBatchSize(int taskBatchSize) {
        this.taskBatchSize = taskBatchSize;
    }

    static enum Stage {
        PRE_UPDATE,
        POST_UPDATE;

    }
}

