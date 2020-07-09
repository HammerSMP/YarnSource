/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_5455;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class EndCityFeature
extends StructureFeature<DefaultFeatureConfig> {
    public EndCityFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, DefaultFeatureConfig arg6) {
        return EndCityFeature.getGenerationHeight(i, j, arg) >= 60;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    private static int getGenerationHeight(int i, int j, ChunkGenerator arg) {
        Random random = new Random(i + j * 10387313);
        BlockRotation lv = BlockRotation.random(random);
        int k = 5;
        int l = 5;
        if (lv == BlockRotation.CLOCKWISE_90) {
            k = -5;
        } else if (lv == BlockRotation.CLOCKWISE_180) {
            k = -5;
            l = -5;
        } else if (lv == BlockRotation.COUNTERCLOCKWISE_90) {
            l = -5;
        }
        int m = (i << 4) + 7;
        int n = (j << 4) + 7;
        int o = arg.getHeightInGround(m, n, Heightmap.Type.WORLD_SURFACE_WG);
        int p = arg.getHeightInGround(m, n + l, Heightmap.Type.WORLD_SURFACE_WG);
        int q = arg.getHeightInGround(m + k, n, Heightmap.Type.WORLD_SURFACE_WG);
        int r = arg.getHeightInGround(m + k, n + l, Heightmap.Type.WORLD_SURFACE_WG);
        return Math.min(Math.min(o, p), Math.min(q, r));
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        public Start(StructureFeature<DefaultFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, DefaultFeatureConfig arg5) {
            BlockRotation lv = BlockRotation.random(this.random);
            int k = EndCityFeature.getGenerationHeight(i, j, arg2);
            if (k < 60) {
                return;
            }
            BlockPos lv2 = new BlockPos(i * 16 + 8, k, j * 16 + 8);
            EndCityGenerator.addPieces(arg3, lv2, lv, this.children, this.random);
            this.setBoundingBoxFromChildren();
        }
    }
}

