/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface WorldGenerationProgressListener {
    public void start(ChunkPos var1);

    public void setChunkStatus(ChunkPos var1, @Nullable ChunkStatus var2);

    public void stop();
}

