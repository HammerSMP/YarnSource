/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanRuinFeature
extends StructureFeature<OceanRuinFeatureConfig> {
    public OceanRuinFeature(Codec<OceanRuinFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<OceanRuinFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static enum BiomeType implements StringIdentifiable
    {
        WARM("warm"),
        COLD("cold");

        public static final Codec<BiomeType> field_24990;
        private static final Map<String, BiomeType> nameMap;
        private final String name;

        private BiomeType(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static BiomeType byName(String string) {
            return nameMap.get(string);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24990 = StringIdentifiable.method_28140(BiomeType::values, BiomeType::byName);
            nameMap = Arrays.stream(BiomeType.values()).collect(Collectors.toMap(BiomeType::getName, arg -> arg));
        }
    }

    public static class Start
    extends StructureStart<OceanRuinFeatureConfig> {
        public Start(StructureFeature<OceanRuinFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3, OceanRuinFeatureConfig arg4) {
            int k = i * 16;
            int l = j * 16;
            BlockPos lv = new BlockPos(k, 90, l);
            BlockRotation lv2 = BlockRotation.random(this.random);
            OceanRuinGenerator.addPieces(arg2, lv, lv2, this.children, this.random, arg4);
            this.setBoundingBoxFromChildren();
        }
    }
}

