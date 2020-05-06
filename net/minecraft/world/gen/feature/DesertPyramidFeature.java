/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class DesertPyramidFeature
extends AbstractTempleFeature<DefaultFeatureConfig> {
    public DesertPyramidFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public String getName() {
        return "Desert_Pyramid";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 14357617;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
        }

        @Override
        public void init(ChunkGenerator<?> arg, StructureManager arg2, int i, int j, Biome arg3) {
            DesertTempleGenerator lv = new DesertTempleGenerator(this.random, i * 16, j * 16);
            this.children.add(lv);
            this.setBoundingBoxFromChildren();
        }
    }
}

