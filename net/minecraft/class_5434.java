/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.class_5455;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class class_5434
extends StructureFeature<StructurePoolFeatureConfig> {
    private final int field_25835;
    private final boolean field_25836;
    private final boolean field_25837;

    public class_5434(Codec<StructurePoolFeatureConfig> codec, int i, boolean bl, boolean bl2) {
        super(codec);
        this.field_25835 = i;
        this.field_25836 = bl;
        this.field_25837 = bl2;
    }

    @Override
    public StructureFeature.StructureStartFactory<StructurePoolFeatureConfig> getStructureStartFactory() {
        return (arg, i, j, arg2, k, l) -> new class_5435(this, i, j, arg2, k, l);
    }

    public static class class_5435
    extends MarginedStructureStart<StructurePoolFeatureConfig> {
        private final class_5434 field_25838;

        public class_5435(class_5434 arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
            this.field_25838 = arg;
        }

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, StructurePoolFeatureConfig arg5) {
            BlockPos lv = new BlockPos(i * 16, this.field_25838.field_25835, j * 16);
            TemplatePools.method_30599();
            StructurePoolBasedGenerator.method_30419(arg, arg5, PoolStructurePiece::new, arg2, arg3, lv, this.children, this.random, this.field_25838.field_25836, this.field_25838.field_25837);
            this.setBoundingBoxFromChildren();
        }
    }
}

