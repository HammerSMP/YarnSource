/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.class_5423;
import net.minecraft.class_5424;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.chunk.ChunkManager;

public interface WorldAccess
extends class_5423,
class_5424 {
    @Override
    default public long method_30271() {
        return this.getLevelProperties().getTimeOfDay();
    }

    public TickScheduler<Block> getBlockTickScheduler();

    public TickScheduler<Fluid> getFluidTickScheduler();

    public WorldProperties getLevelProperties();

    public LocalDifficulty getLocalDifficulty(BlockPos var1);

    default public Difficulty getDifficulty() {
        return this.getLevelProperties().getDifficulty();
    }

    public ChunkManager getChunkManager();

    @Override
    default public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.getChunkManager().isChunkLoaded(chunkX, chunkZ);
    }

    public Random getRandom();

    default public void updateNeighbors(BlockPos pos, Block block) {
    }

    public void playSound(@Nullable PlayerEntity var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void addParticle(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void syncWorldEvent(@Nullable PlayerEntity var1, int var2, BlockPos var3, int var4);

    default public int getDimensionHeight() {
        return this.getDimension().getLogicalHeight();
    }

    default public void syncWorldEvent(int eventId, BlockPos pos, int data) {
        this.syncWorldEvent(null, eventId, pos, data);
    }
}

