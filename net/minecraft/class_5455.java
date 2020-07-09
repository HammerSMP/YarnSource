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
package net.minecraft;

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
import net.minecraft.class_5458;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface class_5455 {
    public static final Logger field_25918 = LogManager.getLogger();
    public static final Map<RegistryKey<? extends Registry<?>>, class_5456<?>> field_25919 = (Map)Util.make(() -> {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        class_5455.method_30520(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, true);
        class_5455.method_30520(builder, Registry.BIOME_KEY, Biome.field_25819, true);
        class_5455.method_30520(builder, Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, ConfiguredSurfaceBuilder.field_25878, false);
        class_5455.method_30520(builder, Registry.CONFIGURED_CARVER_WORLDGEN, ConfiguredCarver.field_25832, false);
        class_5455.method_30520(builder, Registry.CONFIGURED_FEATURE_WORLDGEN, ConfiguredFeature.field_25833, false);
        class_5455.method_30520(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, ConfiguredStructureFeature.field_25834, false);
        class_5455.method_30520(builder, Registry.PROCESSOR_LIST_WORLDGEN, StructureProcessorType.field_25876, false);
        class_5455.method_30520(builder, Registry.TEMPLATE_POOL_WORLDGEN, StructurePool.field_25853, false);
        return builder.build();
    });

    public <E> Optional<MutableRegistry<E>> method_30527(RegistryKey<? extends Registry<E>> var1);

    default public <E> MutableRegistry<E> method_30530(RegistryKey<? extends Registry<E>> arg) {
        return this.method_30527(arg).orElseThrow(() -> new IllegalStateException("Missing registry: " + arg));
    }

    default public Registry<DimensionType> method_30518() {
        return this.method_30530(Registry.DIMENSION_TYPE_KEY);
    }

    public static <E> ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, class_5456<?>> method_30520(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, class_5456<?>> builder, RegistryKey<? extends Registry<E>> arg, MapCodec<E> mapCodec, boolean bl) {
        return builder.put(arg, new class_5456<E>(arg, mapCodec, bl));
    }

    public static class_5457 method_30528() {
        class_5457 lv = new class_5457();
        DimensionType.addRegistryDefaults(lv);
        field_25919.keySet().stream().filter(arg -> !arg.equals(Registry.DIMENSION_TYPE_KEY)).forEach(arg2 -> class_5455.method_30525(lv, arg2));
        return lv;
    }

    public static <R extends Registry<?>> void method_30525(class_5457 arg, RegistryKey<R> arg2) {
        Registry<Registry<?>> lv = class_5458.field_25926;
        Registry<?> lv2 = lv.get(arg2);
        if (lv2 == null) {
            throw new IllegalStateException("Missing builtin registry: " + arg2);
        }
        class_5455.method_30524(arg, lv2);
    }

    public static <E> void method_30524(class_5457 arg, Registry<E> arg2) {
        MutableRegistry<E> lv = arg.method_30527(arg2.method_30517()).orElseThrow(() -> new IllegalStateException("Missing registry: " + arg2.method_30517()));
        for (Map.Entry<RegistryKey<E>, E> entry : arg2.getEntries()) {
            lv.add(entry.getKey(), entry.getValue());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5457 method_30519(ResourceManager arg) {
        class_5457 lv = class_5455.method_30528();
        RegistryOps<JsonElement> lv2 = RegistryOps.of(JsonOps.INSTANCE, arg, lv);
        for (class_5456<?> lv3 : field_25919.values()) {
            class_5455.method_30526(lv2, lv, lv3);
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static <E> void method_30526(RegistryOps<JsonElement> arg2, class_5457 arg22, class_5456<E> arg3) {
        RegistryKey lv = arg3.method_30535();
        SimpleRegistry lv2 = Optional.ofNullable(arg22.field_25924.get(lv)).map(arg -> arg).orElseThrow(() -> new IllegalStateException("Missing registry: " + lv));
        DataResult<SimpleRegistry<E>> dataResult = arg2.loadToRegistry(lv2, arg3.method_30535(), arg3.method_30536());
        dataResult.error().ifPresent(partialResult -> field_25918.error("Error loading registry data: {}", (Object)partialResult.message()));
    }

    public static final class class_5457
    implements class_5455 {
        public static final Codec<class_5457> field_25923 = class_5457.method_30546();
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> field_25924;

        private static <E> Codec<class_5457> method_30546() {
            Codec codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
            Codec codec2 = codec.partialDispatch("type", arg -> DataResult.success(arg.method_30517()), arg -> class_5457.method_30547(arg).map(mapCodec -> SimpleRegistry.method_29098(arg, Lifecycle.experimental(), mapCodec)));
            UnboundedMapCodec unboundedMapCodec = Codec.unboundedMap((Codec)codec, (Codec)codec2);
            return class_5457.method_30538(unboundedMapCodec);
        }

        private static <K extends RegistryKey<? extends Registry<?>>, V extends SimpleRegistry<?>> Codec<class_5457> method_30538(UnboundedMapCodec<K, V> unboundedMapCodec) {
            return unboundedMapCodec.xmap(class_5457::new, arg -> (ImmutableMap)arg.field_25924.entrySet().stream().filter(entry -> ((class_5456)field_25919.get(entry.getKey())).method_30537()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static <E> DataResult<? extends MapCodec<E>> method_30547(RegistryKey<? extends Registry<E>> arg2) {
            return Optional.ofNullable(field_25919.get(arg2)).map(arg -> DataResult.success(arg.method_30536())).orElseGet(() -> DataResult.error((String)("Unknown registry: " + arg2)));
        }

        public class_5457() {
            this(field_25919.keySet().stream().collect(Collectors.toMap(Function.identity(), class_5457::method_30548)));
        }

        private class_5457(Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> map) {
            this.field_25924 = map;
        }

        private static <E> SimpleRegistry<?> method_30548(RegistryKey<? extends Registry<?>> arg) {
            return new SimpleRegistry(arg, Lifecycle.experimental());
        }

        @Override
        public <E> Optional<MutableRegistry<E>> method_30527(RegistryKey<? extends Registry<E>> arg2) {
            return Optional.ofNullable(this.field_25924.get(arg2)).map(arg -> arg);
        }
    }

    public static final class class_5456<E> {
        private final RegistryKey<? extends Registry<E>> field_25920;
        private final MapCodec<E> field_25921;
        private final boolean field_25922;

        public class_5456(RegistryKey<? extends Registry<E>> arg, MapCodec<E> mapCodec, boolean bl) {
            this.field_25920 = arg;
            this.field_25921 = mapCodec;
            this.field_25922 = bl;
        }

        @Environment(value=EnvType.CLIENT)
        public RegistryKey<? extends Registry<E>> method_30535() {
            return this.field_25920;
        }

        public MapCodec<E> method_30536() {
            return this.field_25921;
        }

        public boolean method_30537() {
            return this.field_25922;
        }
    }
}

