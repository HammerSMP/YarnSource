/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class StrongholdFeature
extends StructureFeature<DefaultFeatureConfig> {
    public StrongholdFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, DefaultFeatureConfig arg6) {
        return arg.isStrongholdStartingChunk(new ChunkPos(i, j));
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        private final long field_24559;

        public Start(StructureFeature<DefaultFeatureConfig> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
            this.field_24559 = l;
        }

        @Override
        public void init(DynamicRegistryManager arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, DefaultFeatureConfig arg5) {
            StrongholdGenerator.Start lv;
            int k = 0;
            do {
                this.children.clear();
                this.boundingBox = BlockBox.empty();
                this.random.setCarverSeed(this.field_24559 + (long)k++, i, j);
                StrongholdGenerator.init();
                lv = new StrongholdGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
                this.children.add(lv);
                lv.placeJigsaw(lv, this.children, this.random);
                List<StructurePiece> list = lv.field_15282;
                while (!list.isEmpty()) {
                    int l = this.random.nextInt(list.size());
                    StructurePiece lv2 = list.remove(l);
                    lv2.placeJigsaw(lv, this.children, this.random);
                }
                this.setBoundingBoxFromChildren();
                this.method_14978(arg2.getSeaLevel(), this.random, 10);
            } while (this.children.isEmpty() || lv.field_15283 == null);
        }
    }
}

