/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
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
import java.util.Collections;
import java.util.Iterator;
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
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T>
extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap(256);
    protected final BiMap<Identifier, T> entries = HashBiMap.create();
    protected Object[] randomEntries;
    private int nextId;

    @Override
    public <V extends T> V set(int i, Identifier arg, V object) {
        this.indexedEntries.put(object, i);
        Validate.notNull((Object)arg);
        Validate.notNull(object);
        this.randomEntries = null;
        if (this.entries.containsKey((Object)arg)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", (Object)arg);
        }
        this.entries.put((Object)arg, object);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }
        return object;
    }

    @Override
    public <V extends T> V add(Identifier arg, V object) {
        return this.set(this.nextId, arg, object);
    }

    @Override
    @Nullable
    public Identifier getId(T object) {
        return (Identifier)this.entries.inverse().get(object);
    }

    @Override
    public int getRawId(@Nullable T object) {
        return this.indexedEntries.getId(object);
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

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
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
    @Environment(value=EnvType.CLIENT)
    public boolean containsId(Identifier arg) {
        return this.entries.containsKey((Object)arg);
    }
}

