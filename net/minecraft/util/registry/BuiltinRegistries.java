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
package net.minecraft.util.registry;

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

public class BuiltinRegistries {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Identifier, Supplier<?>> DEFAULT_VALUE_SUPPLIERS = Maps.newLinkedHashMap();
    private static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry(RegistryKey.ofRegistry(new Identifier("root")), Lifecycle.experimental());
    public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
    public static final Registry<ConfiguredSurfaceBuilder<?>> CONGINURED_SURFACE_BUILDER = BuiltinRegistries.addRegistry(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, () -> class_5471.NOPE);
    public static final Registry<ConfiguredCarver<?>> CONFIGURED_CARVER = BuiltinRegistries.addRegistry(Registry.CONFIGURED_CARVER_WORLDGEN, () -> class_5463.CAVE);
    public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = BuiltinRegistries.addRegistry(Registry.CONFIGURED_FEATURE_WORLDGEN, () -> class_5464.NOPE);
    public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = BuiltinRegistries.addRegistry(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, () -> class_5470.MINESHAFT);
    public static final Registry<ImmutableList<StructureProcessor>> STRUCTURE_PROCESSORS = BuiltinRegistries.addRegistry(Registry.PROCESSOR_LIST_WORLDGEN, () -> class_5469.ZOMBIE_PLAINS);
    public static final Registry<StructurePool> STRUCTURE_POOL = BuiltinRegistries.addRegistry(Registry.TEMPLATE_POOL_WORLDGEN, () -> class_5468.field_26254);
    public static final Registry<Biome> BIOME = BuiltinRegistries.addRegistry(Registry.BIOME_KEY, () -> Biomes.DEFAULT);

    private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> arg, Supplier<T> supplier) {
        return BuiltinRegistries.addRegistry(arg, Lifecycle.experimental(), supplier);
    }

    private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> arg, Lifecycle lifecycle, Supplier<T> supplier) {
        return BuiltinRegistries.addRegistry(arg, new SimpleRegistry(arg, lifecycle), supplier);
    }

    private static <T, R extends MutableRegistry<T>> R addRegistry(RegistryKey<? extends Registry<T>> arg, R arg2, Supplier<T> supplier) {
        Identifier lv = arg.getValue();
        DEFAULT_VALUE_SUPPLIERS.put(lv, supplier);
        MutableRegistry<MutableRegistry<?>> lv2 = ROOT;
        return lv2.add(arg, arg2);
    }

    public static <T> T add(Registry<? super T> arg, String string, T object) {
        return BuiltinRegistries.add(arg, new Identifier(string), object);
    }

    public static <V, T extends V> T add(Registry<V> arg, Identifier arg2, T object) {
        return ((MutableRegistry)arg).add(RegistryKey.of(arg.getKey(), arg2), object);
    }

    public static <V, T extends V> T set(Registry<V> arg, int i, String string, T object) {
        return ((MutableRegistry)arg).set(i, RegistryKey.of(arg.getKey(), new Identifier(string)), object);
    }

    public static void init() {
    }

    static {
        DEFAULT_VALUE_SUPPLIERS.forEach((arg, supplier) -> {
            if (supplier.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", arg);
            }
        });
        Registry.validate(ROOT);
    }
}

