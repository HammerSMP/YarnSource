/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ShipwreckFeature
extends AbstractTempleFeature<ShipwreckFeatureConfig> {
    public ShipwreckFeature(Function<Dynamic<?>, ? extends ShipwreckFeatureConfig> function) {
        super(function);
    }

    @Override
    public String getName() {
        return "Shipwreck";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 165745295;
    }

    @Override
    protected int getSpacing(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getShipwreckSpacing();
    }

    @Override
    protected int getSeparation(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getShipwreckSeparation();
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            ShipwreckFeatureConfig lv = arg.getStructureConfig(arg3, Feature.SHIPWRECK);
            BlockRotation lv2 = BlockRotation.random(this.random);
            BlockPos lv3 = new BlockPos(i * 16, 90, j * 16);
            ShipwreckGenerator.addParts(arg2, lv3, lv2, this.children, this.random, lv);
            this.setBoundingBoxFromChildren();
        }
    }
}

