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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_5321;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap(256);
    protected final BiMap<Identifier, T> entries = HashBiMap.create();
    protected final BiMap<class_5321<T>, T> field_25067 = HashBiMap.create();
    protected Object[] randomEntries;
    private int nextId;

    public SimpleRegistry(class_5321<Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
    }

    @Override
    public <V extends T> V set(int i, class_5321<T> arg, V object) {
        this.indexedEntries.put(object, i);
        Validate.notNull(arg);
        Validate.notNull(object);
        this.randomEntries = null;
        if (this.field_25067.containsKey(arg)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", arg);
        }
        this.entries.put((Object)arg.method_29177(), object);
        this.field_25067.put(arg, object);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }
        return object;
    }

    @Override
    public <V extends T> V add(class_5321<T> arg, V object) {
        return this.set(this.nextId, arg, object);
    }

    @Override
    @Nullable
    public Identifier getId(T object) {
        return (Identifier)this.entries.inverse().get(object);
    }

    @Override
    public class_5321<T> method_29113(T object) {
        class_5321 lv = (class_5321)this.field_25067.inverse().get(object);
        if (lv == null) {
            throw new IllegalStateException("Unregistered registry element: " + object + " in " + this);
        }
        return lv;
    }

    @Override
    public int getRawId(@Nullable T object) {
        return this.indexedEntries.getId(object);
    }

    @Override
    @Nullable
    public T method_29107(@Nullable class_5321<T> arg) {
        return (T)this.field_25067.get(arg);
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
        return (T)this.entries.get((Object)arg);
    }

    @Override
    public Optional<T> getOrEmpty(@Nullable Identifier arg) {
        return Optional.ofNullable(this.entries.get((Object)arg));
    }

    @Override
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entries.keySet());
    }

    @Nullable
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Set collection = this.entries.values();
            if (collection.isEmpty()) {
                return null;
            }
            this.randomEntries = collection.toArray(new Object[collection.size()]);
        }
        return (T)Util.getRandom(this.randomEntries, random);
    }

    @Override
    public boolean containsId(Identifier arg) {
        return this.entries.containsKey((Object)arg);
    }

    @Override
    public boolean method_29112(class_5321<T> arg) {
        return this.field_25067.containsKey(arg);
    }

    @Override
    public boolean method_29111(int i) {
        return this.indexedEntries.method_28138(i);
    }

    public static <T> Codec<SimpleRegistry<T>> method_29098(class_5321<Registry<T>> arg2, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.mapPair((MapCodec)Identifier.field_25139.xmap(class_5321.method_29178(arg2), class_5321::method_29177).fieldOf("key"), (MapCodec)codec.fieldOf("element")).codec().listOf().xmap(list -> {
            SimpleRegistry lv = new SimpleRegistry(arg2, lifecycle);
            for (Pair pair : list) {
                lv.add((class_5321)pair.getFirst(), pair.getSecond());
            }
            return lv;
        }, arg -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : arg) {
                builder.add((Object)Pair.of(arg.method_29113(object), object));
            }
            return builder.build();
        });
    }
}

