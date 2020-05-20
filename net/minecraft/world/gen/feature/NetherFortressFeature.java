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
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class NetherFortressFeature
extends StructureFeature<DefaultFeatureConfig> {
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.newArrayList((Object[])new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)});

    public NetherFortressFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, DefaultFeatureConfig arg6) {
        return arg3.nextInt(6) < 2;
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
        public Start(StructureFeature<DefaultFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3, DefaultFeatureConfig arg4) {
            NetherFortressGenerator.Start lv = new NetherFortressGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
            this.children.add(lv);
            lv.placeJigsaw(lv, this.children, this.random);
            List<StructurePiece> list = lv.field_14505;
            while (!list.isEmpty()) {
                int k = this.random.nextInt(list.size());
                StructurePiece lv2 = list.remove(k);
                lv2.placeJigsaw(lv, this.children, this.random);
            }
            this.setBoundingBoxFromChildren();
            this.method_14976(this.random, 48, 70);
        }
    }
}

