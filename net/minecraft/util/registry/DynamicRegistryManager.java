/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DynamicRegistryManager {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Map<RegistryKey<? extends Registry<?>>, Info<?>> INFOS = (Map)Util.make(() -> {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        DynamicRegistryManager.register(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, true);
        DynamicRegistryManager.register(builder, Registry.BIOME_KEY, Biome.field_25819, true);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, ConfiguredSurfaceBuilder.field_25878, false);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_CARVER_WORLDGEN, ConfiguredCarver.field_25832, false);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_FEATURE_WORLDGEN, ConfiguredFeature.field_25833, false);
        DynamicRegistryManager.register(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, ConfiguredStructureFeature.field_25834, false);
        DynamicRegistryManager.register(builder, Registry.PROCESSOR_LIST_WORLDGEN, StructureProcessorType.PROCESSORS, false);
        DynamicRegistryManager.register(builder, Registry.TEMPLATE_POOL_WORLDGEN, StructurePool.field_25853, false);
        DynamicRegistryManager.register(builder, Registry.NOISE_SETTINGS_WORLDGEN, ChunkGeneratorType.field_24780, false);
        return builder.build();
    });

    public <E> Optional<MutableRegistry<E>> getOptional(RegistryKey<? extends Registry<E>> var1);

    default public <E> MutableRegistry<E> get(RegistryKey<? extends Registry<E>> key) {
        return this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    default public Registry<DimensionType> getDimensionTypes() {
        return this.get(Registry.DIMENSION_TYPE_KEY);
    }

    public static <E> ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, MapCodec<E> elementCodec, boolean synced) {
        return infosBuilder.put(registryRef, new Info<E>(registryRef, elementCodec, synced));
    }

    public static Impl create() {
        Impl lv = new Impl();
        DimensionType.addRegistryDefaults(lv);
        INFOS.keySet().stream().filter(arg -> !arg.equals(Registry.DIMENSION_TYPE_KEY)).forEach(arg2 -> DynamicRegistryManager.setupBuiltin(lv, arg2));
        return lv;
    }

    public static <R extends Registry<?>> void setupBuiltin(Impl manager, RegistryKey<R> registryRef) {
        Registry<Registry<?>> lv = BuiltinRegistries.REGISTRIES;
        Registry<?> lv2 = lv.get(registryRef);
        if (lv2 == null) {
            throw new IllegalStateException("Missing builtin registry: " + registryRef);
        }
        DynamicRegistryManager.addBuiltinEntries(manager, lv2);
    }

    public static <E> void addBuiltinEntries(Impl manager, Registry<E> registry) {
        MutableRegistry<E> lv = manager.getOptional(registry.getKey()).orElseThrow(() -> new IllegalStateException("Missing registry: " + registry.getKey()));
        for (Map.Entry<RegistryKey<E>, E> entry : registry.getEntries()) {
            lv.add(entry.getKey(), entry.getValue());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static Impl load(ResourceManager resourceManager) {
        Impl lv = DynamicRegistryManager.create();
        RegistryOps<JsonElement> lv2 = RegistryOps.of(JsonOps.INSTANCE, resourceManager, lv);
        for (Info<?> lv3 : INFOS.values()) {
            DynamicRegistryManager.load(lv2, lv, lv3);
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static <E> void load(RegistryOps<JsonElement> ops, Impl manager, Info<E> info) {
        RegistryKey lv = info.getRegistry();
        SimpleRegistry lv2 = Optional.ofNullable(manager.registries.get(lv)).map(arg -> arg).orElseThrow(() -> new IllegalStateException("Missing registry: " + lv));
        DataResult<SimpleRegistry<E>> dataResult = ops.loadToRegistry(lv2, info.getRegistry(), info.getElementCodec());
        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error loading registry data: {}", (Object)partialResult.message()));
    }

    public static final class Impl
    implements DynamicRegistryManager {
        public static final Codec<Impl> CODEC = Impl.setupCodec();
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries;

        private static <E> Codec<Impl> setupCodec() {
            Codec codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
            Codec codec2 = codec.partialDispatch("type", arg -> DataResult.success(arg.getKey()), arg -> Impl.getDataResultForCodec(arg).map(mapCodec -> SimpleRegistry.method_29098(arg, Lifecycle.experimental(), mapCodec)));
            UnboundedMapCodec unboundedMapCodec = Codec.unboundedMap((Codec)codec, (Codec)codec2);
            return Impl.fromRegistryCodecs(unboundedMapCodec);
        }

        private static <K extends RegistryKey<? extends Registry<?>>, V extends SimpleRegistry<?>> Codec<Impl> fromRegistryCodecs(UnboundedMapCodec<K, V> unboundedMapCodec) {
            return unboundedMapCodec.xmap(Impl::new, arg -> (ImmutableMap)arg.registries.entrySet().stream().filter(entry -> ((Info)INFOS.get(entry.getKey())).isSynced()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static <E> DataResult<? extends MapCodec<E>> getDataResultForCodec(RegistryKey<? extends Registry<E>> registryRef) {
            return Optional.ofNullable(INFOS.get(registryRef)).map(arg -> DataResult.success(arg.getElementCodec())).orElseGet(() -> DataResult.error((String)("Unknown registry: " + registryRef)));
        }

        public Impl() {
            this(INFOS.keySet().stream().collect(Collectors.toMap(Function.identity(), Impl::createRegistry)));
        }

        private Impl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> registries) {
            this.registries = registries;
        }

        private static <E> SimpleRegistry<?> createRegistry(RegistryKey<? extends Registry<?>> registryRef) {
            return new SimpleRegistry(registryRef, Lifecycle.experimental());
        }

        @Override
        public <E> Optional<MutableRegistry<E>> getOptional(RegistryKey<? extends Registry<E>> key) {
            return Optional.ofNullable(this.registries.get(key)).map(arg -> arg);
        }
    }

    public static final class Info<E> {
        private final RegistryKey<? extends Registry<E>> registry;
        private final MapCodec<E> elementCodec;
        private final boolean synced;

        public Info(RegistryKey<? extends Registry<E>> registry, MapCodec<E> elementCodec, boolean synced) {
            this.registry = registry;
            this.elementCodec = elementCodec;
            this.synced = synced;
        }

        @Environment(value=EnvType.CLIENT)
        public RegistryKey<? extends Registry<E>> getRegistry() {
            return this.registry;
        }

        public MapCodec<E> getElementCodec() {
            return this.elementCodec;
        }

        public boolean isSynced() {
            return this.synced;
        }
    }
}

