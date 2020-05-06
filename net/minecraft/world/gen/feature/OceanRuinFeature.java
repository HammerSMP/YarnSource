/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.OceanRuinGenerator;
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
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanRuinFeature
extends AbstractTempleFeature<OceanRuinFeatureConfig> {
    public OceanRuinFeature(Function<Dynamic<?>, ? extends OceanRuinFeatureConfig> function) {
        super(function);
    }

    @Override
    public String getName() {
        return "Ocean_Ruin";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    protected int getSpacing(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getOceanRuinSpacing();
    }

    @Override
    protected int getSeparation(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getOceanRuinSeparation();
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 14357621;
    }

    public static enum BiomeType {
        WARM("warm"),
        COLD("cold");

        private static final Map<String, BiomeType> nameMap;
        private final String name;

        private BiomeType(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static BiomeType byName(String string) {
            return nameMap.get(string);
        }

        static {
            nameMap = Arrays.stream(BiomeType.values()).collect(Collectors.toMap(BiomeType::getName, arg -> arg));
        }
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            OceanRuinFeatureConfig lv = arg.getStructureConfig(arg3, Feature.OCEAN_RUIN);
            int k = i * 16;
            int l = j * 16;
            BlockPos lv2 = new BlockPos(k, 90, l);
            BlockRotation lv3 = BlockRotation.random(this.random);
            OceanRuinGenerator.addPieces(arg2, lv2, lv3, this.children, this.random, lv);
            this.setBoundingBoxFromChildren();
        }
    }
}

