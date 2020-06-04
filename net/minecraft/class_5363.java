/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
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

public final class class_5363 {
    public static final Codec<class_5363> field_25411 = RecordCodecBuilder.create(instance -> instance.group((App)DimensionType.field_24756.fieldOf("type").forGetter(class_5363::method_29566), (App)ChunkGenerator.field_24746.fieldOf("generator").forGetter(class_5363::method_29571)).apply((Applicative)instance, instance.stable(class_5363::new)));
    public static final RegistryKey<class_5363> field_25412 = RegistryKey.of(Registry.field_25490, new Identifier("overworld"));
    public static final RegistryKey<class_5363> field_25413 = RegistryKey.of(Registry.field_25490, new Identifier("the_nether"));
    public static final RegistryKey<class_5363> field_25414 = RegistryKey.of(Registry.field_25490, new Identifier("the_end"));
    private static final LinkedHashSet<RegistryKey<class_5363>> field_25415 = Sets.newLinkedHashSet((Iterable)ImmutableList.of(field_25412, field_25413, field_25414));
    private final Supplier<DimensionType> field_25416;
    private final ChunkGenerator field_25417;

    public class_5363(Supplier<DimensionType> supplier, ChunkGenerator arg) {
        this.field_25416 = supplier;
        this.field_25417 = arg;
    }

    public Supplier<DimensionType> method_29566() {
        return this.field_25416;
    }

    public DimensionType method_29570() {
        return this.field_25416.get();
    }

    public ChunkGenerator method_29571() {
        return this.field_25417;
    }

    public static SimpleRegistry<class_5363> method_29569(SimpleRegistry<class_5363> arg) {
        SimpleRegistry<class_5363> lv = new SimpleRegistry<class_5363>(Registry.field_25490, Lifecycle.experimental());
        for (RegistryKey registryKey : field_25415) {
            class_5363 lv3 = arg.get(registryKey);
            if (lv3 == null) continue;
            lv.add(registryKey, lv3);
            if (!arg.method_29723(registryKey)) continue;
            lv.method_29725(registryKey);
        }
        for (Map.Entry entry : arg.method_29722()) {
            RegistryKey lv4 = (RegistryKey)entry.getKey();
            if (field_25415.contains(lv4)) continue;
            lv.add(lv4, entry.getValue());
            if (!arg.method_29723(lv4)) continue;
            lv.method_29725(lv4);
        }
        return lv;
    }

    public static boolean method_29567(long l, SimpleRegistry<class_5363> arg) {
        ArrayList list = Lists.newArrayList(arg.method_29722());
        if (list.size() != field_25415.size()) {
            return false;
        }
        Map.Entry entry = (Map.Entry)list.get(0);
        Map.Entry entry2 = (Map.Entry)list.get(1);
        Map.Entry entry3 = (Map.Entry)list.get(2);
        if (entry.getKey() != field_25412 || entry2.getKey() != field_25413 || entry3.getKey() != field_25414) {
            return false;
        }
        if (!(((class_5363)entry.getValue()).method_29570().isOverworld() && ((class_5363)entry2.getValue()).method_29570().isNether() && ((class_5363)entry3.getValue()).method_29570().isEnd())) {
            return false;
        }
        if (!(((class_5363)entry2.getValue()).method_29571() instanceof SurfaceChunkGenerator) || !(((class_5363)entry3.getValue()).method_29571() instanceof SurfaceChunkGenerator)) {
            return false;
        }
        SurfaceChunkGenerator lv = (SurfaceChunkGenerator)((class_5363)entry2.getValue()).method_29571();
        SurfaceChunkGenerator lv2 = (SurfaceChunkGenerator)((class_5363)entry3.getValue()).method_29571();
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

