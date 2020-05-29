/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class FlatChunkGenerator
extends ChunkGenerator {
    public static final Codec<FlatChunkGenerator> field_24769 = FlatChunkGeneratorConfig.CODEC.fieldOf("settings").xmap(FlatChunkGenerator::new, FlatChunkGenerator::method_28545).codec();
    private final FlatChunkGeneratorConfig generatorConfig;

    public FlatChunkGenerator(FlatChunkGeneratorConfig arg) {
        super(new FixedBiomeSource(arg.method_28917()), new FixedBiomeSource(arg.getBiome()), arg.getConfig(), 0L);
        this.generatorConfig = arg;
    }

    @Override
    protected Codec<? extends ChunkGenerator> method_28506() {
        return field_24769;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator withSeed(long l) {
        return this;
    }

    public FlatChunkGeneratorConfig method_28545() {
        return this.generatorConfig;
    }

    @Override
    public void buildSurface(ChunkRegion arg, Chunk arg2) {
    }

    @Override
    public int getSpawnHeight() {
        BlockState[] lvs = this.generatorConfig.getLayerBlocks();
        for (int i = 0; i < lvs.length; ++i) {
            BlockState lv;
            BlockState blockState = lv = lvs[i] == null ? Blocks.AIR.getDefaultState() : lvs[i];
            if (Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(lv)) continue;
            return i - 1;
        }
        return lvs.length;
    }

    @Override
    public void populateNoise(WorldAccess arg, StructureAccessor arg2, Chunk arg3) {
        BlockState[] lvs = this.generatorConfig.getLayerBlocks();
        BlockPos.Mutable lv = new BlockPos.Mutable();
        Heightmap lv2 = arg3.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap lv3 = arg3.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        for (int i = 0; i < lvs.length; ++i) {
            BlockState lv4 = lvs[i];
            if (lv4 == null) continue;
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    arg3.setBlockState(lv.set(j, i, k), lv4, false);
                    lv2.trackUpdate(j, i, k, lv4);
                    lv3.trackUpdate(j, i, k, lv4);
                }
            }
        }
    }

    @Override
    public int getHeight(int i, int j, Heightmap.Type arg) {
        BlockState[] lvs = this.generatorConfig.getLayerBlocks();
        for (int k = lvs.length - 1; k >= 0; --k) {
            BlockState lv = lvs[k];
            if (lv == null || !arg.getBlockPredicate().test(lv)) continue;
            return k + 1;
        }
        return 0;
    }

    @Override
    public BlockView getColumnSample(int i, int j) {
        return new VerticalBlockSample((BlockState[])Arrays.stream(this.generatorConfig.getLayerBlocks()).map(arg -> arg == null ? Blocks.AIR.getDefaultState() : arg).toArray(BlockState[]::new));
    }
}

