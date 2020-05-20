/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ShipwreckFeature
extends StructureFeature<ShipwreckFeatureConfig> {
    public ShipwreckFeature(Codec<ShipwreckFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<ShipwreckFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends StructureStart<ShipwreckFeatureConfig> {
        public Start(StructureFeature<ShipwreckFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3, ShipwreckFeatureConfig arg4) {
            BlockRotation lv = BlockRotation.random(this.random);
            BlockPos lv2 = new BlockPos(i * 16, 90, j * 16);
            ShipwreckGenerator.addParts(arg2, lv2, lv, this.children, this.random, arg4);
            this.setBoundingBoxFromChildren();
        }
    }
}

