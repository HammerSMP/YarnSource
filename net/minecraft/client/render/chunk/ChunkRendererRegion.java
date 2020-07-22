/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

@Environment(value=EnvType.CLIENT)
public class ChunkRendererRegion
implements BlockRenderView {
    protected final int chunkXOffset;
    protected final int chunkZOffset;
    protected final BlockPos offset;
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;
    protected final WorldChunk[][] chunks;
    protected final BlockState[] blockStates;
    protected final FluidState[] fluidStates;
    protected final World world;

    @Nullable
    public static ChunkRendererRegion create(World world, BlockPos startPos, BlockPos endPos, int chunkRadius) {
        int j = startPos.getX() - chunkRadius >> 4;
        int k = startPos.getZ() - chunkRadius >> 4;
        int l = endPos.getX() + chunkRadius >> 4;
        int m = endPos.getZ() + chunkRadius >> 4;
        WorldChunk[][] lvs = new WorldChunk[l - j + 1][m - k + 1];
        for (int n = j; n <= l; ++n) {
            for (int o = k; o <= m; ++o) {
                lvs[n - j][o - k] = world.getChunk(n, o);
            }
        }
        if (ChunkRendererRegion.method_30000(startPos, endPos, j, k, lvs)) {
            return null;
        }
        boolean p = true;
        BlockPos lv = startPos.add(-1, -1, -1);
        BlockPos lv2 = endPos.add(1, 1, 1);
        return new ChunkRendererRegion(world, j, k, lvs, lv, lv2);
    }

    public static boolean method_30000(BlockPos arg, BlockPos arg2, int i, int j, WorldChunk[][] args) {
        for (int k = arg.getX() >> 4; k <= arg2.getX() >> 4; ++k) {
            for (int l = arg.getZ() >> 4; l <= arg2.getZ() >> 4; ++l) {
                WorldChunk lv = args[k - i][l - j];
                if (lv.areSectionsEmptyBetween(arg.getY(), arg2.getY())) continue;
                return false;
            }
        }
        return true;
    }

    public ChunkRendererRegion(World world, int chunkX, int chunkZ, WorldChunk[][] chunks, BlockPos startPos, BlockPos endPos) {
        this.world = world;
        this.chunkXOffset = chunkX;
        this.chunkZOffset = chunkZ;
        this.chunks = chunks;
        this.offset = startPos;
        this.xSize = endPos.getX() - startPos.getX() + 1;
        this.ySize = endPos.getY() - startPos.getY() + 1;
        this.zSize = endPos.getZ() - startPos.getZ() + 1;
        this.blockStates = new BlockState[this.xSize * this.ySize * this.zSize];
        this.fluidStates = new FluidState[this.xSize * this.ySize * this.zSize];
        for (BlockPos lv : BlockPos.iterate(startPos, endPos)) {
            int k = (lv.getX() >> 4) - chunkX;
            int l = (lv.getZ() >> 4) - chunkZ;
            WorldChunk lv2 = chunks[k][l];
            int m = this.getIndex(lv);
            this.blockStates[m] = lv2.getBlockState(lv);
            this.fluidStates[m] = lv2.getFluidState(lv);
        }
    }

    protected final int getIndex(BlockPos pos) {
        return this.getIndex(pos.getX(), pos.getY(), pos.getZ());
    }

    protected int getIndex(int x, int y, int z) {
        int l = x - this.offset.getX();
        int m = y - this.offset.getY();
        int n = z - this.offset.getZ();
        return n * this.xSize * this.ySize + m * this.xSize + l;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.blockStates[this.getIndex(pos)];
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.fluidStates[this.getIndex(pos)];
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return this.world.getBrightness(direction, shaded);
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg, WorldChunk.CreationType arg2) {
        int i = (arg.getX() >> 4) - this.chunkXOffset;
        int j = (arg.getZ() >> 4) - this.chunkZOffset;
        return this.chunks[i][j].getBlockEntity(arg, arg2);
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return this.world.getColor(pos, colorResolver);
    }
}

