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
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuiltinRegistries {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Identifier, Supplier<?>> DEFAULT_VALUE_SUPPLIERS = Maps.newLinkedHashMap();
    private static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry(RegistryKey.ofRegistry(new Identifier("root")), Lifecycle.experimental());
    public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
    public static final Registry<ConfiguredSurfaceBuilder<?>> CONFIGURED_SURFACE_BUILDER = BuiltinRegistries.addRegistry(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, () -> ConfiguredSurfaceBuilders.NOPE);
    public static final Registry<ConfiguredCarver<?>> CONFIGURED_CARVER = BuiltinRegistries.addRegistry(Registry.CONFIGURED_CARVER_WORLDGEN, () -> ConfiguredCarvers.CAVE);
    public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = BuiltinRegistries.addRegistry(Registry.CONFIGURED_FEATURE_WORLDGEN, () -> ConfiguredFeatures.NOPE);
    public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = BuiltinRegistries.addRegistry(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, () -> ConfiguredStructureFeatures.MINESHAFT);
    public static final Registry<ImmutableList<StructureProcessor>> PROCESSOR_LIST = BuiltinRegistries.addRegistry(Registry.PROCESSOR_LIST_WORLDGEN, () -> ProcessorLists.ZOMBIE_PLAINS);
    public static final Registry<StructurePool> TEMPLATE_POOL = BuiltinRegistries.addRegistry(Registry.TEMPLATE_POOL_WORLDGEN, () -> TemplatePools.EMPTY);
    public static final Registry<Biome> BIOME = BuiltinRegistries.addRegistry(Registry.BIOME_KEY, () -> Biomes.DEFAULT);

    private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Supplier<T> defaultValueSupplier) {
        return BuiltinRegistries.addRegistry(registryRef, Lifecycle.experimental(), defaultValueSupplier);
    }

    private static <T> Registry<T> addRegistry(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Supplier<T> defaultValueSupplier) {
        return BuiltinRegistries.addRegistry(registryRef, new SimpleRegistry(registryRef, lifecycle), defaultValueSupplier);
    }

    private static <T, R extends MutableRegistry<T>> R addRegistry(RegistryKey<? extends Registry<T>> registryRef, R registry, Supplier<T> defaultValueSupplier) {
        Identifier lv = registryRef.getValue();
        DEFAULT_VALUE_SUPPLIERS.put(lv, defaultValueSupplier);
        MutableRegistry<MutableRegistry<?>> lv2 = ROOT;
        return lv2.add(registryRef, registry);
    }

    public static <T> T add(Registry<? super T> registry, String id, T object) {
        return BuiltinRegistries.add(registry, new Identifier(id), object);
    }

    public static <V, T extends V> T add(Registry<V> registry, Identifier id, T object) {
        return ((MutableRegistry)registry).add(RegistryKey.of(registry.getKey(), id), object);
    }

    public static <V, T extends V> T set(Registry<V> registry, int rawId, String id, T object) {
        return ((MutableRegistry)registry).set(rawId, RegistryKey.of(registry.getKey(), new Identifier(id)), object);
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

