/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_5455;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;

public class BuriedTreasureFeature
extends StructureFeature<ProbabilityConfig> {
    public BuriedTreasureFeature(Codec<ProbabilityConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, ProbabilityConfig arg6) {
        arg3.setRegionSeed(l, i, j, 10387320);
        return arg3.nextFloat() < arg6.probability;
    }

    @Override
    public StructureFeature.StructureStartFactory<ProbabilityConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends StructureStart<ProbabilityConfig> {
        public Start(StructureFeature<ProbabilityConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, ProbabilityConfig arg5) {
            int k = i * 16;
            int l = j * 16;
            BlockPos lv = new BlockPos(k + 9, 90, l + 9);
            this.children.add(new BuriedTreasureGenerator.Piece(lv));
            this.setBoundingBoxFromChildren();
        }

        @Override
        public BlockPos getPos() {
            return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
        }
    }
}

