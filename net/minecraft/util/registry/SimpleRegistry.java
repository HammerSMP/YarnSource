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
import net.minecraft.class_5380;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
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
    private final Set<RegistryKey<T>> field_25489 = Sets.newIdentityHashSet();
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(RegistryKey<Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
    }

    @Override
    public <V extends T> V set(int i, RegistryKey<T> arg, V object) {
        this.indexedEntries.put(object, i);
        Validate.notNull(arg);
        Validate.notNull(object);
        this.randomEntries = null;
        if (this.entriesByKey.containsKey(arg)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", arg);
        }
        this.entriesById.put((Object)arg.getValue(), object);
        this.entriesByKey.put(arg, object);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }
        return object;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> arg, V object) {
        return this.set(this.nextId, arg, object);
    }

    @Override
    @Nullable
    public Identifier getId(T object) {
        return (Identifier)this.entriesById.inverse().get(object);
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T object) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(object));
    }

    @Override
    public int getRawId(@Nullable T object) {
        return this.indexedEntries.getId(object);
    }

    @Override
    @Nullable
    public T get(@Nullable RegistryKey<T> arg) {
        return (T)this.entriesByKey.get(arg);
    }

    @Override
    @Nullable
    public T get(int i) {
        return this.indexedEntries.get(i);
    }

    @Override
    public Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }

    @Override
    @Nullable
    public T get(@Nullable Identifier arg) {
        return (T)this.entriesById.get((Object)arg);
    }

    @Override
    public Optional<T> getOrEmpty(@Nullable Identifier arg) {
        return Optional.ofNullable(this.entriesById.get((Object)arg));
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entriesById.keySet());
    }

    public Set<Map.Entry<RegistryKey<T>, T>> method_29722() {
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
    public boolean containsId(Identifier arg) {
        return this.entriesById.containsKey((Object)arg);
    }

    @Override
    public boolean containsId(int i) {
        return this.indexedEntries.containsId(i);
    }

    public boolean method_29723(RegistryKey<T> arg) {
        return this.field_25489.contains(arg);
    }

    public void method_29725(RegistryKey<T> arg) {
        this.field_25489.add(arg);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<Registry<T>> arg2, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.mapPair((MapCodec)Identifier.field_25139.xmap(RegistryKey.createKeyFactory(arg2), RegistryKey::getValue).fieldOf("key"), (MapCodec)codec.fieldOf("element")).codec().listOf().xmap(list -> {
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

    public static <T> Codec<SimpleRegistry<T>> method_29721(RegistryKey<Registry<T>> arg, Lifecycle lifecycle, Codec<T> codec) {
        return class_5380.method_29745(arg, lifecycle, codec);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29724(RegistryKey<Registry<T>> arg2, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.unboundedMap((Codec)Identifier.field_25139.xmap(RegistryKey.createKeyFactory(arg2), RegistryKey::getValue), codec).xmap(map -> {
            SimpleRegistry lv = new SimpleRegistry(arg2, lifecycle);
            map.forEach((? super K arg2, ? super V object) -> {
                lv.set(arg.nextId, (RegistryKey)arg2, (Object)object);
                lv.method_29725((RegistryKey)arg2);
            });
            return lv;
        }, arg -> {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            arg.entriesByKey.entrySet().stream().filter(entry -> arg.method_29723((RegistryKey)entry.getKey())).forEach(((ImmutableMap.Builder)builder)::put);
            return builder.build();
        });
    }
}

