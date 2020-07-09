/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_5455;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class MineshaftFeature
extends StructureFeature<MineshaftFeatureConfig> {
    public MineshaftFeature(Codec<MineshaftFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, MineshaftFeatureConfig arg6) {
        arg3.setCarverSeed(l, i, j);
        double d = arg6.probability;
        return arg3.nextDouble() < d;
    }

    @Override
    public StructureFeature.StructureStartFactory<MineshaftFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends StructureStart<MineshaftFeatureConfig> {
        public Start(StructureFeature<MineshaftFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, MineshaftFeatureConfig arg5) {
            MineshaftGenerator.MineshaftRoom lv = new MineshaftGenerator.MineshaftRoom(0, this.random, (i << 4) + 2, (j << 4) + 2, arg5.type);
            this.children.add(lv);
            lv.placeJigsaw(lv, this.children, this.random);
            this.setBoundingBoxFromChildren();
            if (arg5.type == Type.MESA) {
                int k = -5;
                int l = arg2.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
                this.boundingBox.offset(0, l, 0);
                for (StructurePiece lv2 : this.children) {
                    lv2.translate(0, l, 0);
                }
            } else {
                this.method_14978(arg2.getSeaLevel(), this.random, 10);
            }
        }
    }

    public static enum Type implements StringIdentifiable
    {
        NORMAL("normal"),
        MESA("mesa");

        public static final Codec<Type> field_24839;
        private static final Map<String, Type> nameMap;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        private static Type byName(String string) {
            return nameMap.get(string);
        }

        public static Type byIndex(int i) {
            if (i < 0 || i >= Type.values().length) {
                return NORMAL;
            }
            return Type.values()[i];
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24839 = StringIdentifiable.createCodec(Type::values, Type::byName);
            nameMap = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, arg -> arg));
        }
    }
}

