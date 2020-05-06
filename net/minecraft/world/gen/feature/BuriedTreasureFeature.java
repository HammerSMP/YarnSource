/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class BuriedTreasureFeature
extends StructureFeature<BuriedTreasureFeatureConfig> {
    public BuriedTreasureFeature(Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig> function) {
        super(function);
    }

    @Override
    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator<?> arg2, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        arg3.setRegionSeed(arg2.getSeed(), i, j, 10387320);
        BuriedTreasureFeatureConfig lv = arg2.getStructureConfig(arg4, this);
        return arg3.nextFloat() < lv.probability;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Buried_Treasure";
    }

    @Override
    public int getRadius() {
        return 1;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            int k = i * 16;
            int l = j * 16;
            BlockPos lv = new BlockPos(k + 9, 90, l + 9);
            this.children.add(new BuriedTreasureGenerator.Piece(lv));
            this.setBoundingBoxFromChildren();
        }

        @Override
        public BlockPos getPos() {
            return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
        }
    }
}

