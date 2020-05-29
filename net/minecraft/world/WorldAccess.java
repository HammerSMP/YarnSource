/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EntityView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.DimensionType;

public interface WorldAccess
extends EntityView,
WorldView,
ModifiableTestableWorld {
    default public float getMoonSize() {
        return DimensionType.field_24752[this.getDimension().method_28531(this.getLevelProperties().getTimeOfDay())];
    }

    default public float getSkyAngle(float f) {
        return this.getDimension().method_28528(this.getLevelProperties().getTimeOfDay());
    }

    @Environment(value=EnvType.CLIENT)
    default public int getMoonPhase() {
        return this.getDimension().method_28531(this.getLevelProperties().getTimeOfDay());
    }

    public TickScheduler<Block> getBlockTickScheduler();

    public TickScheduler<Fluid> getFluidTickScheduler();

    public World getWorld();

    public WorldProperties getLevelProperties();

    public LocalDifficulty getLocalDifficulty(BlockPos var1);

    default public Difficulty getDifficulty() {
        return this.getLevelProperties().getDifficulty();
    }

    public ChunkManager getChunkManager();

    @Override
    default public boolean isChunkLoaded(int i, int j) {
        return this.getChunkManager().isChunkLoaded(i, j);
    }

    public Random getRandom();

    default public void updateNeighbors(BlockPos arg, Block arg2) {
    }

    public void playSound(@Nullable PlayerEntity var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void addParticle(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void syncWorldEvent(@Nullable PlayerEntity var1, int var2, BlockPos var3, int var4);

    default public int getDimensionHeight() {
        return this.getDimension().hasCeiling() ? 128 : 256;
    }

    default public void syncWorldEvent(int i, BlockPos arg, int j) {
        this.syncWorldEvent(null, i, arg, j);
    }

    @Override
    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return EntityView.super.getEntityCollisions(arg, arg2, predicate);
    }

    @Override
    default public boolean intersectsEntities(@Nullable Entity arg, VoxelShape arg2) {
        return EntityView.super.intersectsEntities(arg, arg2);
    }

    @Override
    default public BlockPos getTopPosition(Heightmap.Type arg, BlockPos arg2) {
        return WorldView.super.getTopPosition(arg, arg2);
    }
}

