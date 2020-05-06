/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class EndCityFeature
extends StructureFeature<DefaultFeatureConfig> {
    public EndCityFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected int getSpacing(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getEndCityDistance();
    }

    @Override
    protected int getSeparation(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getEndCitySeparation();
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 10387313;
    }

    @Override
    protected boolean method_27219() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator<?> arg2, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        return EndCityFeature.getGenerationHeight(i, j, arg2) >= 60;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "EndCity";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    private static int getGenerationHeight(int i, int j, ChunkGenerator<?> arg) {
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
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            BlockRotation lv = BlockRotation.random(this.random);
            int k = EndCityFeature.getGenerationHeight(i, j, arg);
            if (k < 60) {
                return;
            }
            BlockPos lv2 = new BlockPos(i * 16 + 8, k, j * 16 + 8);
            EndCityGenerator.addPieces(arg2, lv2, lv, this.children, this.random);
            this.setBoundingBoxFromChildren();
        }
    }
}

