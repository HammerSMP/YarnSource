/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanMonumentFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList((Object[])new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)});

    public OceanMonumentFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected int getSpacing(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getOceanMonumentSpacing();
    }

    @Override
    protected int getSeparation(DimensionType arg, ChunkGeneratorConfig arg2) {
        return arg2.getOceanMonumentSeparation();
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
        Set<Biome> set = arg2.getBiomeSource().getBiomesInArea(i * 16 + 9, arg2.getSeaLevel(), j * 16 + 9, 16);
        for (Biome lv : set) {
            if (arg2.hasStructure(lv, this)) continue;
            return false;
        }
        Set<Biome> set2 = arg2.getBiomeSource().getBiomesInArea(i * 16 + 9, arg2.getSeaLevel(), j * 16 + 9, 29);
        for (Biome lv2 : set2) {
            if (lv2.getCategory() == Biome.Category.OCEAN || lv2.getCategory() == Biome.Category.RIVER) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Monument";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    public static class Start
    extends StructureStart {
        private boolean field_13717;

        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            this.method_16588(i, j);
        }

        private void method_16588(int i, int j) {
            int k = i * 16 - 29;
            int l = j * 16 - 29;
            Direction lv = Direction.Type.HORIZONTAL.random(this.random);
            this.children.add(new OceanMonumentGenerator.Base(this.random, k, l, lv));
            this.setBoundingBoxFromChildren();
            this.field_13717 = true;
        }

        @Override
        public void generateStructure(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5) {
            if (!this.field_13717) {
                this.children.clear();
                this.method_16588(this.getChunkX(), this.getChunkZ());
            }
            super.generateStructure(arg, arg2, arg3, random, arg4, arg5);
        }
    }
}

