/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ConfiguredStructureFeature<FC extends FeatureConfig, F extends StructureFeature<FC>> {
    public static final Codec<ConfiguredStructureFeature<?, ?>> field_24834 = Registry.STRUCTURE_FEATURE.dispatch("name", arg -> arg.field_24835, StructureFeature::method_28665);
    public final F field_24835;
    public final FC field_24836;

    public ConfiguredStructureFeature(F arg, FC arg2) {
        this.field_24835 = arg;
        this.field_24836 = arg2;
    }

    public StructureStart<?> method_28622(ChunkGenerator arg, BiomeSource arg2, StructureManager arg3, long l, ChunkPos arg4, Biome arg5, int i, StructureConfig arg6) {
        return ((StructureFeature)this.field_24835).method_28657(arg, arg2, arg3, l, arg4, arg5, i, new ChunkRandom(), arg6, this.field_24836);
    }
}

