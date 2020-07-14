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
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_5455;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanMonumentFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList((Object[])new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)});

    public OceanMonumentFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean isUniformDistribution() {
        return false;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, DefaultFeatureConfig arg6) {
        Set<Biome> set = arg2.getBiomesInArea(i * 16 + 9, arg.getSeaLevel(), j * 16 + 9, 16);
        for (Biome lv : set) {
            if (lv.hasStructureFeature(this)) continue;
            return false;
        }
        Set<Biome> set2 = arg2.getBiomesInArea(i * 16 + 9, arg.getSeaLevel(), j * 16 + 9, 29);
        for (Biome lv2 : set2) {
            if (lv2.getCategory() == Biome.Category.OCEAN || lv2.getCategory() == Biome.Category.RIVER) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        private boolean field_13717;

        public Start(StructureFeature<DefaultFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, DefaultFeatureConfig arg5) {
            this.method_16588(i, j);
        }

        private void method_16588(int chunkX, int chunkZ) {
            int k = chunkX * 16 - 29;
            int l = chunkZ * 16 - 29;
            Direction lv = Direction.Type.HORIZONTAL.random(this.random);
            this.children.add(new OceanMonumentGenerator.Base(this.random, k, l, lv));
            this.setBoundingBoxFromChildren();
            this.field_13717 = true;
        }

        @Override
        public void generateStructure(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5) {
            if (!this.field_13717) {
                this.children.clear();
                this.method_16588(this.getChunkX(), this.getChunkZ());
            }
            super.generateStructure(arg, arg2, arg3, random, arg4, arg5);
        }
    }
}

