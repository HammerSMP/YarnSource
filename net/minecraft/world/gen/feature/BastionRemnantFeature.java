/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class BastionRemnantFeature
extends StructureFeature<BastionRemnantFeatureConfig> {
    public BastionRemnantFeature(Function<Dynamic<?>, ? extends BastionRemnantFeatureConfig> function) {
        super(function);
    }

    @Override
    protected int getSpacing(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getNetherStructureSpacing();
    }

    @Override
    protected int getSeparation(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getNetherStructureSeparation();
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return arg.getNetherStructureSeedModifier();
    }

    @Override
    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator<?> arg2, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        return arg3.nextInt(6) >= 2;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Bastion_Remnant";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start
    extends VillageStructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            BastionRemnantFeatureConfig lv = arg.getStructureConfig(arg3, Feature.BASTION_REMNANT);
            BlockPos lv2 = new BlockPos(i * 16, 33, j * 16);
            BastionRemnantGenerator.addPieces(arg, arg2, lv2, this.children, this.random, lv);
            this.setBoundingBoxFromChildren();
        }
    }
}

