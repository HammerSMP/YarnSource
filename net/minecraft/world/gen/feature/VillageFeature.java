/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class VillageFeature
extends StructureFeature<StructurePoolFeatureConfig> {
    public VillageFeature(Function<Dynamic<?>, ? extends StructurePoolFeatureConfig> function) {
        super(function);
    }

    @Override
    protected int getSpacing(ChunkGeneratorConfig arg) {
        return arg.getVillageSpacing();
    }

    @Override
    protected int getSeparation(ChunkGeneratorConfig arg) {
        return arg.getVillageSeparation();
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 10387312;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Village";
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
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3) {
            StructurePoolFeatureConfig lv = arg.getStructureConfig(arg3, Feature.VILLAGE);
            BlockPos lv2 = new BlockPos(i * 16, 0, j * 16);
            VillageGenerator.addPieces(arg, arg2, lv2, this.children, this.random, lv);
            this.setBoundingBoxFromChildren();
        }
    }
}

