/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public final class DimensionOptions {
    public static final MapCodec<DimensionOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DimensionType.field_24756.fieldOf("type").forGetter(DimensionOptions::getDimensionTypeSupplier), (App)ChunkGenerator.field_24746.fieldOf("generator").forGetter(DimensionOptions::getChunkGenerator)).apply((Applicative)instance, instance.stable(DimensionOptions::new)));
    public static final RegistryKey<DimensionOptions> OVERWORLD = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("overworld"));
    public static final RegistryKey<DimensionOptions> NETHER = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("the_nether"));
    public static final RegistryKey<DimensionOptions> END = RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier("the_end"));
    private static final LinkedHashSet<RegistryKey<DimensionOptions>> BASE_DIMENSIONS = Sets.newLinkedHashSet((Iterable)ImmutableList.of(OVERWORLD, NETHER, END));
    private final Supplier<DimensionType> dimensionTypeSupplier;
    private final ChunkGenerator chunkGenerator;

    public DimensionOptions(Supplier<DimensionType> supplier, ChunkGenerator arg) {
        this.dimensionTypeSupplier = supplier;
        this.chunkGenerator = arg;
    }

    public Supplier<DimensionType> getDimensionTypeSupplier() {
        return this.dimensionTypeSupplier;
    }

    public DimensionType getDimensionType() {
        return this.dimensionTypeSupplier.get();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    public static SimpleRegistry<DimensionOptions> method_29569(SimpleRegistry<DimensionOptions> arg) {
        SimpleRegistry<DimensionOptions> lv = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
        for (RegistryKey registryKey : BASE_DIMENSIONS) {
            DimensionOptions lv3 = arg.get(registryKey);
            if (lv3 == null) continue;
            lv.add(registryKey, lv3);
            if (!arg.isLoaded(registryKey)) continue;
            lv.markLoaded(registryKey);
        }
        for (Map.Entry entry : arg.getEntries()) {
            RegistryKey lv4 = (RegistryKey)entry.getKey();
            if (BASE_DIMENSIONS.contains(lv4)) continue;
            lv.add(lv4, entry.getValue());
            if (!arg.isLoaded(lv4)) continue;
            lv.markLoaded(lv4);
        }
        return lv;
    }

    public static boolean method_29567(long l, SimpleRegistry<DimensionOptions> arg) {
        ArrayList list = Lists.newArrayList(arg.getEntries());
        if (list.size() != BASE_DIMENSIONS.size()) {
            return false;
        }
        Map.Entry entry = (Map.Entry)list.get(0);
        Map.Entry entry2 = (Map.Entry)list.get(1);
        Map.Entry entry3 = (Map.Entry)list.get(2);
        if (entry.getKey() != OVERWORLD || entry2.getKey() != NETHER || entry3.getKey() != END) {
            return false;
        }
        if (((DimensionOptions)entry.getValue()).getDimensionType() != DimensionType.OVERWORLD && ((DimensionOptions)entry.getValue()).getDimensionType() != DimensionType.field_25611) {
            return false;
        }
        if (((DimensionOptions)entry2.getValue()).getDimensionType() != DimensionType.THE_NETHER) {
            return false;
        }
        if (((DimensionOptions)entry3.getValue()).getDimensionType() != DimensionType.THE_END) {
            return false;
        }
        if (!(((DimensionOptions)entry2.getValue()).getChunkGenerator() instanceof SurfaceChunkGenerator) || !(((DimensionOptions)entry3.getValue()).getChunkGenerator() instanceof SurfaceChunkGenerator)) {
            return false;
        }
        SurfaceChunkGenerator lv = (SurfaceChunkGenerator)((DimensionOptions)entry2.getValue()).getChunkGenerator();
        SurfaceChunkGenerator lv2 = (SurfaceChunkGenerator)((DimensionOptions)entry3.getValue()).getChunkGenerator();
        if (!lv.method_28548(l, ChunkGeneratorType.Preset.NETHER)) {
            return false;
        }
        if (!lv2.method_28548(l, ChunkGeneratorType.Preset.END)) {
            return false;
        }
        if (!(lv.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return false;
        }
        MultiNoiseBiomeSource lv3 = (MultiNoiseBiomeSource)lv.getBiomeSource();
        if (!lv3.method_28462(l)) {
            return false;
        }
        if (!(lv2.getBiomeSource() instanceof TheEndBiomeSource)) {
            return false;
        }
        TheEndBiomeSource lv4 = (TheEndBiomeSource)lv2.getBiomeSource();
        return lv4.method_28479(l);
    }
}

