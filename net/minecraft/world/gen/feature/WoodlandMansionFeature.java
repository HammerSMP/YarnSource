/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.class_5455;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WoodlandMansionFeature
extends StructureFeature<DefaultFeatureConfig> {
    public WoodlandMansionFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, DefaultFeatureConfig arg6) {
        Set<Biome> set = arg2.getBiomesInArea(i * 16 + 9, arg.getSeaLevel(), j * 16 + 9, 32);
        for (Biome lv : set) {
            if (lv.hasStructureFeature(this)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        public Start(StructureFeature<DefaultFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, DefaultFeatureConfig arg5) {
            BlockRotation lv = BlockRotation.random(this.random);
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
            int o = arg2.getHeightInGround(m, n, Heightmap.Type.WORLD_SURFACE_WG);
            int p = arg2.getHeightInGround(m, n + l, Heightmap.Type.WORLD_SURFACE_WG);
            int q = arg2.getHeightInGround(m + k, n, Heightmap.Type.WORLD_SURFACE_WG);
            int r = arg2.getHeightInGround(m + k, n + l, Heightmap.Type.WORLD_SURFACE_WG);
            int s = Math.min(Math.min(o, p), Math.min(q, r));
            if (s < 60) {
                return;
            }
            BlockPos lv2 = new BlockPos(i * 16 + 8, s + 1, j * 16 + 8);
            LinkedList list = Lists.newLinkedList();
            WoodlandMansionGenerator.addPieces(arg3, lv2, lv, list, this.random);
            this.children.addAll(list);
            this.setBoundingBoxFromChildren();
        }

        @Override
        public void generateStructure(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5) {
            super.generateStructure(arg, arg2, arg3, random, arg4, arg5);
            int i = this.boundingBox.minY;
            for (int j = arg4.minX; j <= arg4.maxX; ++j) {
                for (int k = arg4.minZ; k <= arg4.maxZ; ++k) {
                    BlockPos lv3;
                    BlockPos lv = new BlockPos(j, i, k);
                    if (arg.isAir(lv) || !this.boundingBox.contains(lv)) continue;
                    boolean bl = false;
                    for (StructurePiece lv2 : this.children) {
                        if (!lv2.getBoundingBox().contains(lv)) continue;
                        bl = true;
                        break;
                    }
                    if (!bl) continue;
                    for (int l = i - 1; l > 1 && (arg.isAir(lv3 = new BlockPos(j, l, k)) || arg.getBlockState(lv3).getMaterial().isLiquid()); --l) {
                        arg.setBlockState(lv3, Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}

