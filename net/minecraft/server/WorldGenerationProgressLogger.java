/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import javax.annotation.Nullable;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenerationProgressLogger
implements WorldGenerationProgressListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int totalCount;
    private int generatedCount;
    private long startTime;
    private long nextMessageTime = Long.MAX_VALUE;

    public WorldGenerationProgressLogger(int i) {
        int j = i * 2 + 1;
        this.totalCount = j * j;
    }

    @Override
    public void start(ChunkPos arg) {
        this.startTime = this.nextMessageTime = Util.getMeasuringTimeMs();
    }

    @Override
    public void setChunkStatus(ChunkPos arg, @Nullable ChunkStatus arg2) {
        if (arg2 == ChunkStatus.FULL) {
            ++this.generatedCount;
        }
        int i = this.getProgressPercentage();
        if (Util.getMeasuringTimeMs() > this.nextMessageTime) {
            this.nextMessageTime += 500L;
            LOGGER.info(new TranslatableText("menu.preparingSpawn", MathHelper.clamp(i, 0, 100)).getString());
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Time elapsed: {} ms", (Object)(Util.getMeasuringTimeMs() - this.startTime));
        this.nextMessageTime = Long.MAX_VALUE;
    }

    public int getProgressPercentage() {
        return MathHelper.floor((float)this.generatedCount * 100.0f / (float)this.totalCount);
    }
}

