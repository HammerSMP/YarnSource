/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class AbstractTempleFeature<C extends FeatureConfig>
extends StructureFeature<C> {
    public AbstractTempleFeature(Function<Dynamic<?>, ? extends C> function) {
        super(function);
    }

    @Override
    protected int getSpacing(ChunkGeneratorConfig arg) {
        return arg.getTempleSpacing();
    }

    @Override
    protected int getSeparation(ChunkGeneratorConfig arg) {
        return arg.getTempleSeparation();
    }

    @Override
    protected abstract int getSeedModifier(ChunkGeneratorConfig var1);
}

