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
import net.minecraft.class_5434;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class PillagerOutpostFeature
extends class_5434 {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList((Object[])new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1)});

    public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, 0, true, true);
    }

    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return MONSTER_SPAWNS;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, StructurePoolFeatureConfig arg6) {
        int k = i >> 4;
        int m = j >> 4;
        arg3.setSeed((long)(k ^ m << 4) ^ l);
        arg3.nextInt();
        if (arg3.nextInt(5) != 0) {
            return false;
        }
        for (int n = i - 10; n <= i + 10; ++n) {
            for (int o = j - 10; o <= j + 10; ++o) {
                ChunkPos lv = StructureFeature.VILLAGE.getStartChunk(arg.getConfig().getForType(StructureFeature.VILLAGE), l, arg3, n, o);
                if (n != lv.x || o != lv.z) continue;
                return false;
            }
        }
        return true;
    }
}

