/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public abstract class ChunkManager
implements ChunkProvider,
AutoCloseable {
    @Nullable
    public WorldChunk getWorldChunk(int i, int j, boolean bl) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL, bl);
    }

    @Nullable
    public WorldChunk getWorldChunk(int i, int j) {
        return this.getWorldChunk(i, j, false);
    }

    @Override
    @Nullable
    public BlockView getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY, false);
    }

    public boolean isChunkLoaded(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Environment(value=EnvType.CLIENT)
    public abstract void tick(BooleanSupplier var1);

    public abstract String getDebugString();

    @Override
    public void close() throws IOException {
    }

    public abstract LightingProvider getLightingProvider();

    public void setMobSpawnOptions(boolean bl, boolean bl2) {
    }

    public void setChunkForced(ChunkPos arg, boolean bl) {
    }

    public boolean shouldTickEntity(Entity arg) {
        return true;
    }

    public boolean shouldTickChunk(ChunkPos arg) {
        return true;
    }

    public boolean shouldTickBlock(BlockPos arg) {
        return true;
    }
}

