/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.chunk.ChunkStatus;

@Environment(value=EnvType.CLIENT)
public class QueueingWorldGenerationProgressListener
implements WorldGenerationProgressListener {
    private final WorldGenerationProgressListener progressListener;
    private final TaskExecutor<Runnable> queue;

    public QueueingWorldGenerationProgressListener(WorldGenerationProgressListener arg, Executor executor) {
        this.progressListener = arg;
        this.queue = TaskExecutor.create(executor, "progressListener");
    }

    @Override
    public void start(ChunkPos arg) {
        this.queue.send(() -> this.progressListener.start(arg));
    }

    @Override
    public void setChunkStatus(ChunkPos arg, @Nullable ChunkStatus arg2) {
        this.queue.send(() -> this.progressListener.setChunkStatus(arg, arg2));
    }

    @Override
    public void stop() {
        this.queue.send(this.progressListener::stop);
    }
}

