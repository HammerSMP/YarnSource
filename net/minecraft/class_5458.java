/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Lifecycle
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.class_5463;
import net.minecraft.class_5464;
import net.minecraft.class_5468;
import net.minecraft.class_5469;
import net.minecraft.class_5470;
import net.minecraft.class_5471;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5458 {
    protected static final Logger field_25925 = LogManager.getLogger();
    private static final Map<Identifier, Supplier<?>> field_25934 = Maps.newLinkedHashMap();
    private static final MutableRegistry<MutableRegistry<?>> field_25935 = new SimpleRegistry(RegistryKey.ofRegistry(new Identifier("root")), Lifecycle.experimental());
    public static final Registry<? extends Registry<?>> field_25926 = field_25935;
    public static final Registry<ConfiguredSurfaceBuilder<?>> field_25927 = class_5458.method_30565(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, () -> class_5471.NOPE);
    public static final Registry<ConfiguredCarver<?>> field_25928 = class_5458.method_30565(Registry.CONFIGURED_CARVER_WORLDGEN, () -> class_5463.CAVE);
    public static final Registry<ConfiguredFeature<?, ?>> field_25929 = class_5458.method_30565(Registry.CONFIGURED_FEATURE_WORLDGEN, () -> class_5464.NOPE);
    public static final Registry<ConfiguredStructureFeature<?, ?>> field_25930 = class_5458.method_30565(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, () -> class_5470.MINESHAFT);
    public static final Registry<ImmutableList<StructureProcessor>> field_25931 = class_5458.method_30565(Registry.PROCESSOR_LIST_WORLDGEN, () -> class_5469.ZOMBIE_PLAINS);
    public static final Registry<StructurePool> field_25932 = class_5458.method_30565(Registry.TEMPLATE_POOL_WORLDGEN, () -> class_5468.field_26254);
    public static final Registry<Biome> field_25933 = class_5458.method_30565(Registry.BIOME_KEY, () -> Biomes.DEFAULT);

    private static <T> Registry<T> method_30565(RegistryKey<? extends Registry<T>> arg, Supplier<T> supplier) {
        return class_5458.method_30563(arg, Lifecycle.experimental(), supplier);
    }

    private static <T> Registry<T> method_30563(RegistryKey<? extends Registry<T>> arg, Lifecycle lifecycle, Supplier<T> supplier) {
        return class_5458.method_30564(arg, new SimpleRegistry(arg, lifecycle), supplier);
    }

    private static <T, R extends MutableRegistry<T>> R method_30564(RegistryKey<? extends Registry<T>> arg, R arg2, Supplier<T> supplier) {
        Identifier lv = arg.getValue();
        field_25934.put(lv, supplier);
        MutableRegistry<MutableRegistry<?>> lv2 = field_25935;
        return lv2.add(arg, arg2);
    }

    public static <T> T method_30561(Registry<? super T> arg, String string, T object) {
        return class_5458.method_30562(arg, new Identifier(string), object);
    }

    public static <V, T extends V> T method_30562(Registry<V> arg, Identifier arg2, T object) {
        return ((MutableRegistry)arg).add(RegistryKey.of(arg.method_30517(), arg2), object);
    }

    public static <V, T extends V> T method_30560(Registry<V> arg, int i, String string, T object) {
        return ((MutableRegistry)arg).set(i, RegistryKey.of(arg.method_30517(), new Identifier(string)), object);
    }

    public static void method_30559() {
    }

    static {
        field_25934.forEach((arg, supplier) -> {
            if (supplier.get() == null) {
                field_25925.error("Unable to bootstrap registry '{}'", arg);
            }
        });
        Registry.validate(field_25935);
    }
}

