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
    public static ChunkRendererRegion create(World arg, BlockPos arg2, BlockPos arg3, int i) {
        int j = arg2.getX() - i >> 4;
        int k = arg2.getZ() - i >> 4;
        int l = arg3.getX() + i >> 4;
        int m = arg3.getZ() + i >> 4;
        WorldChunk[][] lvs = new WorldChunk[l - j + 1][m - k + 1];
        for (int n = j; n <= l; ++n) {
            for (int o = k; o <= m; ++o) {
                lvs[n - j][o - k] = arg.getChunk(n, o);
            }
        }
        if (ChunkRendererRegion.method_30000(arg2, arg3, j, k, lvs)) {
            return null;
        }
        boolean p = true;
        BlockPos lv = arg2.add(-1, -1, -1);
        BlockPos lv2 = arg3.add(1, 1, 1);
        return new ChunkRendererRegion(arg, j, k, lvs, lv, lv2);
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

    public ChunkRendererRegion(World arg, int i, int j, WorldChunk[][] args, BlockPos arg2, BlockPos arg3) {
        this.world = arg;
        this.chunkXOffset = i;
        this.chunkZOffset = j;
        this.chunks = args;
        this.offset = arg2;
        this.xSize = arg3.getX() - arg2.getX() + 1;
        this.ySize = arg3.getY() - arg2.getY() + 1;
        this.zSize = arg3.getZ() - arg2.getZ() + 1;
        this.blockStates = new BlockState[this.xSize * this.ySize * this.zSize];
        this.fluidStates = new FluidState[this.xSize * this.ySize * this.zSize];
        for (BlockPos lv : BlockPos.iterate(arg2, arg3)) {
            int k = (lv.getX() >> 4) - i;
            int l = (lv.getZ() >> 4) - j;
            WorldChunk lv2 = args[k][l];
            int m = this.getIndex(lv);
            this.blockStates[m] = lv2.getBlockState(lv);
            this.fluidStates[m] = lv2.getFluidState(lv);
        }
    }

    protected final int getIndex(BlockPos arg) {
        return this.getIndex(arg.getX(), arg.getY(), arg.getZ());
    }

    protected int getIndex(int i, int j, int k) {
        int l = i - this.offset.getX();
        int m = j - this.offset.getY();
        int n = k - this.offset.getZ();
        return n * this.xSize * this.ySize + m * this.xSize + l;
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        return this.blockStates[this.getIndex(arg)];
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return this.fluidStates[this.getIndex(arg)];
    }

    @Override
    public float getBrightness(Direction arg, boolean bl) {
        return this.world.getBrightness(arg, bl);
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        return this.getBlockEntity(arg, WorldChunk.CreationType.IMMEDIATE);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg, WorldChunk.CreationType arg2) {
        int i = (arg.getX() >> 4) - this.chunkXOffset;
        int j = (arg.getZ() >> 4) - this.chunkZOffset;
        return this.chunks[i][j].getBlockEntity(arg, arg2);
    }

    @Override
    public int getColor(BlockPos arg, ColorResolver colorResolver) {
        return this.world.getColor(arg, colorResolver);
    }
}

