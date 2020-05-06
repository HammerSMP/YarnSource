/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class EmptyChunk
extends WorldChunk {
    private static final Biome[] BIOMES = Util.make(new Biome[BiomeArray.DEFAULT_LENGTH], args -> Arrays.fill(args, Biomes.PLAINS));

    public EmptyChunk(World arg, ChunkPos arg2) {
        super(arg, arg2, new BiomeArray(BIOMES));
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        return Blocks.VOID_AIR.getDefaultState();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos arg, BlockState arg2, boolean bl) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return Fluids.EMPTY.getDefaultState();
    }

    @Override
    @Nullable
    public LightingProvider getLightingProvider() {
        return null;
    }

    @Override
    public int getLuminance(BlockPos arg) {
        return 0;
    }

    @Override
    public void addEntity(Entity arg) {
    }

    @Override
    public void remove(Entity arg) {
    }

    @Override
    public void remove(Entity arg, int i) {
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg, WorldChunk.CreationType arg2) {
        return null;
    }

    @Override
    public void addBlockEntity(BlockEntity arg) {
    }

    @Override
    public void setBlockEntity(BlockPos arg, BlockEntity arg2) {
    }

    @Override
    public void removeBlockEntity(BlockPos arg) {
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void getEntities(@Nullable Entity arg, Box arg2, List<Entity> list, Predicate<? super Entity> predicate) {
    }

    @Override
    public <T extends Entity> void getEntities(Class<? extends T> arg, Box arg2, List<T> list, Predicate<? super T> predicate) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean method_12228(int i, int j) {
        return true;
    }

    @Override
    public ChunkHolder.LevelType getLevelType() {
        return ChunkHolder.LevelType.BORDER;
    }
}

