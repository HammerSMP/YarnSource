/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.class_5455;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.dynamic.RegistryElementCodec;
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
    public static final MapCodec<ConfiguredStructureFeature<?, ?>> field_25834 = Registry.STRUCTURE_FEATURE.dispatchMap("name", arg -> arg.feature, StructureFeature::getCodec);
    public static final Codec<Supplier<ConfiguredStructureFeature<?, ?>>> TYPE_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, field_25834);
    public final F feature;
    public final FC config;

    public ConfiguredStructureFeature(F arg, FC arg2) {
        this.feature = arg;
        this.config = arg2;
    }

    public StructureStart<?> tryPlaceStart(class_5455 arg, ChunkGenerator arg2, BiomeSource arg3, StructureManager arg4, long worldSeed, ChunkPos arg5, Biome arg6, int referenceCount, StructureConfig arg7) {
        return ((StructureFeature)this.feature).tryPlaceStart(arg, arg2, arg3, arg4, worldSeed, arg5, arg6, referenceCount, new ChunkRandom(), arg7, this.config);
    }
}

