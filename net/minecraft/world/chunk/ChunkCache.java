/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.EmptyChunk;

public class ChunkCache
implements BlockView,
CollisionView {
    protected final int minX;
    protected final int minZ;
    protected final Chunk[][] chunks;
    protected boolean empty;
    protected final World world;

    public ChunkCache(World world, BlockPos minPos, BlockPos maxPos) {
        this.world = world;
        this.minX = minPos.getX() >> 4;
        this.minZ = minPos.getZ() >> 4;
        int i = maxPos.getX() >> 4;
        int j = maxPos.getZ() >> 4;
        this.chunks = new Chunk[i - this.minX + 1][j - this.minZ + 1];
        ChunkManager lv = world.getChunkManager();
        this.empty = true;
        for (int k = this.minX; k <= i; ++k) {
            for (int l = this.minZ; l <= j; ++l) {
                this.chunks[k - this.minX][l - this.minZ] = lv.getWorldChunk(k, l);
            }
        }
        for (int m = minPos.getX() >> 4; m <= maxPos.getX() >> 4; ++m) {
            for (int n = minPos.getZ() >> 4; n <= maxPos.getZ() >> 4; ++n) {
                Chunk lv2 = this.chunks[m - this.minX][n - this.minZ];
                if (lv2 == null || lv2.areSectionsEmptyBetween(minPos.getY(), maxPos.getY())) continue;
                this.empty = false;
                return;
            }
        }
    }

    private Chunk method_22354(BlockPos arg) {
        return this.method_22353(arg.getX() >> 4, arg.getZ() >> 4);
    }

    private Chunk method_22353(int i, int j) {
        int k = i - this.minX;
        int l = j - this.minZ;
        if (k < 0 || k >= this.chunks.length || l < 0 || l >= this.chunks[k].length) {
            return new EmptyChunk(this.world, new ChunkPos(i, j));
        }
        Chunk lv = this.chunks[k][l];
        return lv != null ? lv : new EmptyChunk(this.world, new ChunkPos(i, j));
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public BlockView getExistingChunk(int chunkX, int chunkZ) {
        return this.method_22353(chunkX, chunkZ);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        Chunk lv = this.method_22354(pos);
        return lv.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return Blocks.AIR.getDefaultState();
        }
        Chunk lv = this.method_22354(pos);
        return lv.getBlockState(pos);
    }

    @Override
    public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        return Stream.empty();
    }

    @Override
    public Stream<VoxelShape> getCollisions(@Nullable Entity arg, Box arg2, Predicate<Entity> predicate) {
        return this.getBlockCollisions(arg, arg2);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        Chunk lv = this.method_22354(pos);
        return lv.getFluidState(pos);
    }
}

