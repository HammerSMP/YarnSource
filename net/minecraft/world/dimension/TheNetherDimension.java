/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceConfig;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheNetherDimension
extends Dimension {
    public TheNetherDimension(World arg, DimensionType arg2) {
        super(arg, arg2, 0.1f);
        this.waterVaporizes = true;
        this.isNether = true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d modifyFogColor(Vec3d arg, float f) {
        return arg;
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        CavesChunkGeneratorConfig lv = ChunkGeneratorType.CAVES.createConfig();
        lv.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
        lv.setDefaultFluid(Blocks.LAVA.getDefaultState());
        MultiNoiseBiomeSourceConfig lv2 = BiomeSourceType.MULTI_NOISE.getConfig(this.world.getSeed()).withBiomes((List<Biome>)ImmutableList.of((Object)Biomes.NETHER_WASTES, (Object)Biomes.SOUL_SAND_VALLEY, (Object)Biomes.CRIMSON_FOREST, (Object)Biomes.WARPED_FOREST, (Object)Biomes.BASALT_DELTAS));
        return ChunkGeneratorType.CAVES.create(this.world, BiomeSourceType.MULTI_NOISE.applyConfig(lv2), lv);
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos arg, boolean bl) {
        return null;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0.5f;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isFogThick(int i, int j) {
        return true;
    }

    @Override
    public WorldBorder createWorldBorder() {
        return new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0;
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0;
            }
        };
    }

    @Override
    public DimensionType getType() {
        return DimensionType.THE_NETHER;
    }
}

