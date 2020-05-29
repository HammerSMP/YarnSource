/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    protected final BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
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
    @Environment(value=EnvType.CLIENT)
    public Optional<RegistryKey<T>> getKey(T object) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(object));
    }

    @Override
    public int getRawId(@Nullable T object) {
        return this.indexedEntries.getId(object);
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
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

    public static <T> Codec<SimpleRegistry<T>> method_29098(RegistryKey<Registry<T>> arg2, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.mapPair((MapCodec)Identifier.field_25139.xmap(RegistryKey.createKeyFactory(arg2), RegistryKey::getValue).fieldOf("key"), (MapCodec)codec.fieldOf("element")).codec().listOf().xmap(list -> {
            SimpleRegistry lv = new SimpleRegistry(arg2, lifecycle);
            for (Pair pair : list) {
                lv.add((RegistryKey)pair.getFirst(), pair.getSecond());
            }
            return lv;
        }, arg -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Map.Entry entry : arg.entriesByKey.entrySet()) {
                builder.add((Object)Pair.of(entry.getKey(), entry.getValue()));
            }
            return builder.build();
        });
    }
}

