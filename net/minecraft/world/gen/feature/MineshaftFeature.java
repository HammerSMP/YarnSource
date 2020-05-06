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
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class MineshaftFeature
extends StructureFeature<MineshaftFeatureConfig> {
    public MineshaftFeature(Function<Dynamic<?>, ? extends MineshaftFeatureConfig> function) {
        super(function);
    }

    @Override
    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator<?> arg2, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        arg3.setCarverSeed(arg2.getSeed(), i, j);
        MineshaftFeatureConfig lv = arg2.getStructureConfig(arg4, this);
        double d = lv.probability;
        return arg3.nextDouble() < d;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Mineshaft";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            MineshaftFeatureConfig lv = arg.getStructureConfig(arg3, Feature.MINESHAFT);
            MineshaftGenerator.MineshaftRoom lv2 = new MineshaftGenerator.MineshaftRoom(0, this.random, (i << 4) + 2, (j << 4) + 2, lv.type);
            this.children.add(lv2);
            lv2.placeJigsaw(lv2, this.children, this.random);
            this.setBoundingBoxFromChildren();
            if (lv.type == Type.MESA) {
                int k = -5;
                int l = arg.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
                this.boundingBox.offset(0, l, 0);
                for (StructurePiece lv3 : this.children) {
                    lv3.translate(0, l, 0);
                }
            } else {
                this.method_14978(arg.getSeaLevel(), this.random, 10);
            }
        }
    }

    public static enum Type {
        NORMAL("normal"),
        MESA("mesa");

        private static final Map<String, Type> nameMap;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Type byName(String string) {
            return nameMap.get(string);
        }

        public static Type byIndex(int i) {
            if (i < 0 || i >= Type.values().length) {
                return NORMAL;
            }
            return Type.values()[i];
        }

        static {
            nameMap = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, arg -> arg));
        }
    }
}

