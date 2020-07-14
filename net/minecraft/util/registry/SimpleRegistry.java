/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.dynamic.RegistryCodec;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap(256);
    protected final BiMap<Identifier, T> entriesById = HashBiMap.create();
    private final BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
    private final Set<RegistryKey<T>> loadedKeys = Sets.newIdentityHashSet();
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<? extends Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
    }

    public static <T> MapCodec<Pair<RegistryKey<T>, T>> method_30516(RegistryKey<? extends Registry<T>> arg, MapCodec<T> mapCodec) {
        return Codec.mapPair((MapCodec)Identifier.CODEC.xmap(RegistryKey.createKeyFactory(arg), RegistryKey::getValue).fieldOf("name"), mapCodec);
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry) {
        this.indexedEntries.put(entry, rawId);
        Validate.notNull(key);
        Validate.notNull(entry);
        this.randomEntries = null;
        if (this.entriesByKey.containsKey(key)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", key);
        }
        this.entriesById.put((Object)key.getValue(), entry);
        this.entriesByKey.put(key, entry);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        return entry;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> key, V entry) {
        return this.set(this.nextId, key, entry);
    }

    @Override
    @Nullable
    public Identifier getId(T entry) {
        return (Identifier)this.entriesById.inverse().get(entry);
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T value) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(value));
    }

    @Override
    public int getRawId(@Nullable T object) {
        return this.indexedEntries.getRawId(object);
    }

    @Override
    @Nullable
    public T get(@Nullable RegistryKey<T> key) {
        return (T)this.entriesByKey.get(key);
    }

    @Override
    @Nullable
    public T get(int index) {
        return this.indexedEntries.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }

    @Override
    @Nullable
    public T get(@Nullable Identifier id) {
        return (T)this.entriesById.get((Object)id);
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entriesById.keySet());
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntries() {
        return Collections.unmodifiableMap(this.entriesByKey).entrySet();
    }

    @Nullable
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Set collection = this.entriesById.values();
            if (collection.isEmpty()) {
                return null;
            }
            this.randomEntries = collection.toArray(new Object[collection.size()]);
        }
        return (T)Util.getRandom(this.randomEntries, random);
    }

    @Override
    public boolean containsId(Identifier id) {
        return this.entriesById.containsKey((Object)id);
    }

    @Override
    public boolean containsId(int id) {
        return this.indexedEntries.containsId(id);
    }

    @Override
    public boolean isLoaded(RegistryKey<T> arg) {
        return this.loadedKeys.contains(arg);
    }

    @Override
    public void markLoaded(RegistryKey<T> arg) {
        this.loadedKeys.add(arg);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<? extends Registry<T>> arg2, Lifecycle lifecycle, MapCodec<T> mapCodec) {
        return SimpleRegistry.method_30516(arg2, mapCodec).codec().listOf().xmap(list -> {
            SimpleRegistry lv = new SimpleRegistry(arg2, lifecycle);
            for (Pair pair : list) {
                lv.add((RegistryKey)pair.getFirst(), pair.getSecond());
            }
            return lv;
        }, arg -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : arg.indexedEntries) {
                builder.add((Object)Pair.of(arg.getKey(object).get(), object));
            }
            return builder.build();
        });
    }

    public static <T> Codec<SimpleRegistry<T>> createCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, MapCodec<T> mapCodec) {
        return RegistryCodec.of(registryRef, lifecycle, mapCodec);
    }

    public static <T> Codec<SimpleRegistry<T>> createEmptyCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, MapCodec<T> mapCodec) {
        return Codec.unboundedMap((Codec)Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryRef), RegistryKey::getValue), (Codec)mapCodec.codec()).xmap(map -> {
            SimpleRegistry lv = new SimpleRegistry(registryRef, lifecycle);
            map.forEach((? super K arg2, ? super V object) -> {
                lv.set(arg.nextId, (RegistryKey)arg2, (Object)object);
                lv.markLoaded((RegistryKey)arg2);
            });
            return lv;
        }, arg -> {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            arg.entriesByKey.entrySet().stream().filter(entry -> arg.isLoaded((RegistryKey)entry.getKey())).forEach(((ImmutableMap.Builder)builder)::put);
            return builder.build();
        });
    }
}

