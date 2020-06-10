/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class BastionRemnantFeature
extends StructureFeature<BastionRemnantFeatureConfig> {
    public BastionRemnantFeature(Codec<BastionRemnantFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, BastionRemnantFeatureConfig arg6) {
        return arg3.nextInt(5) >= 2;
    }

    @Override
    public StructureFeature.StructureStartFactory<BastionRemnantFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends VillageStructureStart<BastionRemnantFeatureConfig> {
        public Start(StructureFeature<BastionRemnantFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3, BastionRemnantFeatureConfig arg4) {
            BlockPos lv = new BlockPos(i * 16, 33, j * 16);
            BastionRemnantGenerator.addPieces(arg, arg2, lv, this.children, this.random, arg4);
            this.setBoundingBoxFromChildren();
        }
    }
}

