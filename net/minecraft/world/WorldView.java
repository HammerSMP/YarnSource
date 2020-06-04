/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;

public interface WorldView
extends BlockRenderView,
CollisionView,
BiomeAccess.Storage {
    @Nullable
    public Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean isChunkLoaded(int var1, int var2);

    public int getTopY(Heightmap.Type var1, int var2, int var3);

    public int getAmbientDarkness();

    public BiomeAccess getBiomeAccess();

    default public Biome getBiome(BlockPos arg) {
        return this.getBiomeAccess().getBiome(arg);
    }

    default public Stream<BlockState> method_29556(Box arg) {
        int n;
        int i = MathHelper.floor(arg.minX);
        int j = MathHelper.floor(arg.maxX);
        int k = MathHelper.floor(arg.minY);
        int l = MathHelper.floor(arg.maxY);
        int m = MathHelper.floor(arg.minZ);
        if (this.isRegionLoaded(i, k, m, j, l, n = MathHelper.floor(arg.maxZ))) {
            return this.method_29546(arg);
        }
        return Stream.empty();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    default public int getColor(BlockPos arg, ColorResolver colorResolver) {
        return colorResolver.getColor(this.getBiome(arg), arg.getX(), arg.getZ());
    }

    @Override
    default public Biome getBiomeForNoiseGen(int i, int j, int k) {
        Chunk lv = this.getChunk(i >> 2, k >> 2, ChunkStatus.BIOMES, false);
        if (lv != null && lv.getBiomeArray() != null) {
            return lv.getBiomeArray().getBiomeForNoiseGen(i, j, k);
        }
        return this.getGeneratorStoredBiome(i, j, k);
    }

    public Biome getGeneratorStoredBiome(int var1, int var2, int var3);

    public boolean isClient();

    @Deprecated
    public int getSeaLevel();

    public DimensionType getDimension();

    default public BlockPos getTopPosition(Heightmap.Type arg, BlockPos arg2) {
        return new BlockPos(arg2.getX(), this.getTopY(arg, arg2.getX(), arg2.getZ()), arg2.getZ());
    }

    default public boolean isAir(BlockPos arg) {
        return this.getBlockState(arg).isAir();
    }

    default public boolean isSkyVisibleAllowingSea(BlockPos arg) {
        if (arg.getY() >= this.getSeaLevel()) {
            return this.isSkyVisible(arg);
        }
        BlockPos lv = new BlockPos(arg.getX(), this.getSeaLevel(), arg.getZ());
        if (!this.isSkyVisible(lv)) {
            return false;
        }
        lv = lv.down();
        while (lv.getY() > arg.getY()) {
            BlockState lv2 = this.getBlockState(lv);
            if (lv2.getOpacity(this, lv) > 0 && !lv2.getMaterial().isLiquid()) {
                return false;
            }
            lv = lv.down();
        }
        return true;
    }

    @Deprecated
    default public float getBrightness(BlockPos arg) {
        return this.getDimension().method_28516(this.getLightLevel(arg));
    }

    default public int getStrongRedstonePower(BlockPos arg, Direction arg2) {
        return this.getBlockState(arg).getStrongRedstonePower(this, arg, arg2);
    }

    default public Chunk getChunk(BlockPos arg) {
        return this.getChunk(arg.getX() >> 4, arg.getZ() >> 4);
    }

    default public Chunk getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, true);
    }

    default public Chunk getChunk(int i, int j, ChunkStatus arg) {
        return this.getChunk(i, j, arg, true);
    }

    @Override
    @Nullable
    default public BlockView getExistingChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY, false);
    }

    default public boolean isWater(BlockPos arg) {
        return this.getFluidState(arg).matches(FluidTags.WATER);
    }

    default public boolean containsFluid(Box arg) {
        int i = MathHelper.floor(arg.minX);
        int j = MathHelper.ceil(arg.maxX);
        int k = MathHelper.floor(arg.minY);
        int l = MathHelper.ceil(arg.maxY);
        int m = MathHelper.floor(arg.minZ);
        int n = MathHelper.ceil(arg.maxZ);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    BlockState lv2 = this.getBlockState(lv.set(o, p, q));
                    if (lv2.getFluidState().isEmpty()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    default public int getLightLevel(BlockPos arg) {
        return this.getLightLevel(arg, this.getAmbientDarkness());
    }

    default public int getLightLevel(BlockPos arg, int i) {
        if (arg.getX() < -30000000 || arg.getZ() < -30000000 || arg.getX() >= 30000000 || arg.getZ() >= 30000000) {
            return 15;
        }
        return this.getBaseLightLevel(arg, i);
    }

    @Deprecated
    default public boolean isChunkLoaded(BlockPos arg) {
        return this.isChunkLoaded(arg.getX() >> 4, arg.getZ() >> 4);
    }

    @Deprecated
    default public boolean isRegionLoaded(BlockPos arg, BlockPos arg2) {
        return this.isRegionLoaded(arg.getX(), arg.getY(), arg.getZ(), arg2.getX(), arg2.getY(), arg2.getZ());
    }

    @Deprecated
    default public boolean isRegionLoaded(int i, int j, int k, int l, int m, int n) {
        if (m < 0 || j >= 256) {
            return false;
        }
        k >>= 4;
        n >>= 4;
        for (int o = i >>= 4; o <= (l >>= 4); ++o) {
            for (int p = k; p <= n; ++p) {
                if (this.isChunkLoaded(o, p)) continue;
                return false;
            }
        }
        return true;
    }
}

