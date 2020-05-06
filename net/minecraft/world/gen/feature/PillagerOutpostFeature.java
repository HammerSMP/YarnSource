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
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public class PillagerOutpostFeature
extends AbstractTempleFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList((Object[])new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1)});

    public PillagerOutpostFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public String getName() {
        return "Pillager_Outpost";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    @Override
    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator<?> arg2, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        int k = i >> 4;
        int l = j >> 4;
        arg3.setSeed((long)(k ^ l << 4) ^ arg2.getSeed());
        arg3.nextInt();
        if (arg3.nextInt(5) != 0) {
            return false;
        }
        for (int m = i - 10; m <= i + 10; ++m) {
            for (int n = j - 10; n <= j + 10; ++n) {
                Biome lv = arg.getBiome(new BlockPos((m << 4) + 9, 0, (n << 4) + 9));
                if (!Feature.VILLAGE.method_27217(arg, arg2, arg3, m, n, lv)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 165745296;
    }

    public static class Start
    extends VillageStructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            BlockPos lv = new BlockPos(i * 16, 0, j * 16);
            PillagerOutpostGenerator.addPieces(arg, arg2, lv, this.children, this.random);
            this.setBoundingBoxFromChildren();
        }
    }
}

