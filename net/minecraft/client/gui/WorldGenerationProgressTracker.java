/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(value=EnvType.CLIENT)
public class WorldGenerationProgressTracker
implements WorldGenerationProgressListener {
    private final WorldGenerationProgressLogger progressLogger;
    private final Long2ObjectOpenHashMap<ChunkStatus> chunkStatuses;
    private ChunkPos spawnPos = new ChunkPos(0, 0);
    private final int centerSize;
    private final int radius;
    private final int size;
    private boolean isRunning;

    public WorldGenerationProgressTracker(int i) {
        this.progressLogger = new WorldGenerationProgressLogger(i);
        this.centerSize = i * 2 + 1;
        this.radius = i + ChunkStatus.getMaxTargetGenerationRadius();
        this.size = this.radius * 2 + 1;
        this.chunkStatuses = new Long2ObjectOpenHashMap();
    }

    @Override
    public void start(ChunkPos arg) {
        if (!this.isRunning) {
            return;
        }
        this.progressLogger.start(arg);
        this.spawnPos = arg;
    }

    @Override
    public void setChunkStatus(ChunkPos arg, @Nullable ChunkStatus arg2) {
        if (!this.isRunning) {
            return;
        }
        this.progressLogger.setChunkStatus(arg, arg2);
        if (arg2 == null) {
            this.chunkStatuses.remove(arg.toLong());
        } else {
            this.chunkStatuses.put(arg.toLong(), (Object)arg2);
        }
    }

    public void start() {
        this.isRunning = true;
        this.chunkStatuses.clear();
    }

    @Override
    public void stop() {
        this.isRunning = false;
        this.progressLogger.stop();
    }

    public int getCenterSize() {
        return this.centerSize;
    }

    public int getSize() {
        return this.size;
    }

    public int getProgressPercentage() {
        return this.progressLogger.getProgressPercentage();
    }

    @Nullable
    public ChunkStatus getChunkStatus(int i, int j) {
        return (ChunkStatus)this.chunkStatuses.get(ChunkPos.toLong(i + this.spawnPos.x - this.radius, j + this.spawnPos.z - this.radius));
    }
}

