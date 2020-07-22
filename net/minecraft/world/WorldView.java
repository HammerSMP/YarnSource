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

    default public Biome getBiome(BlockPos pos) {
        return this.getBiomeAccess().getBiome(pos);
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
    default public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return colorResolver.getColor(this.getBiome(pos), pos.getX(), pos.getZ());
    }

    @Override
    default public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        Chunk lv = this.getChunk(biomeX >> 2, biomeZ >> 2, ChunkStatus.BIOMES, false);
        if (lv != null && lv.getBiomeArray() != null) {
            return lv.getBiomeArray().getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
        }
        return this.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    public Biome getGeneratorStoredBiome(int var1, int var2, int var3);

    public boolean isClient();

    @Deprecated
    public int getSeaLevel();

    public DimensionType getDimension();

    default public BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
        return new BlockPos(pos.getX(), this.getTopY(heightmap, pos.getX(), pos.getZ()), pos.getZ());
    }

    default public boolean isAir(BlockPos pos) {
        return this.getBlockState(pos).isAir();
    }

    default public boolean isSkyVisibleAllowingSea(BlockPos pos) {
        if (pos.getY() >= this.getSeaLevel()) {
            return this.isSkyVisible(pos);
        }
        BlockPos lv = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
        if (!this.isSkyVisible(lv)) {
            return false;
        }
        lv = lv.down();
        while (lv.getY() > pos.getY()) {
            BlockState lv2 = this.getBlockState(lv);
            if (lv2.getOpacity(this, lv) > 0 && !lv2.getMaterial().isLiquid()) {
                return false;
            }
            lv = lv.down();
        }
        return true;
    }

    @Deprecated
    default public float getBrightness(BlockPos pos) {
        return this.getDimension().method_28516(this.getLightLevel(pos));
    }

    default public int getStrongRedstonePower(BlockPos pos, Direction direction) {
        return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
    }

    default public Chunk getChunk(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    default public Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
    }

    default public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status) {
        return this.getChunk(chunkX, chunkZ, status, true);
    }

    @Override
    @Nullable
    default public BlockView getExistingChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
    }

    default public boolean isWater(BlockPos pos) {
        return this.getFluidState(pos).isIn(FluidTags.WATER);
    }

    default public boolean containsFluid(Box box) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
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

    default public int getLightLevel(BlockPos pos) {
        return this.getLightLevel(pos, this.getAmbientDarkness());
    }

    default public int getLightLevel(BlockPos pos, int ambientDarkness) {
        if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) {
            return 15;
        }
        return this.getBaseLightLevel(pos, ambientDarkness);
    }

    @Deprecated
    default public boolean isChunkLoaded(BlockPos pos) {
        return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Deprecated
    default public boolean isRegionLoaded(BlockPos min, BlockPos max) {
        return this.isRegionLoaded(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    @Deprecated
    default public boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (maxY < 0 || minY >= 256) {
            return false;
        }
        minZ >>= 4;
        maxZ >>= 4;
        for (int o = minX >>= 4; o <= (maxX >>= 4); ++o) {
            for (int p = minZ; p <= maxZ; ++p) {
                if (this.isChunkLoaded(o, p)) continue;
                return false;
            }
        }
        return true;
    }
}

