/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.util.collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.collection.IndexedIterable;

public class IdList<T>
implements IndexedIterable<T> {
    private int nextId;
    private final IdentityHashMap<T, Integer> idMap;
    private final List<T> list;

    public IdList() {
        this(512);
    }

    public IdList(int initialSize) {
        this.list = Lists.newArrayListWithExpectedSize((int)initialSize);
        this.idMap = new IdentityHashMap(initialSize);
    }

    public void set(T value, int id) {
        this.idMap.put(value, id);
        while (this.list.size() <= id) {
            this.list.add(null);
        }
        this.list.set(id, value);
        if (this.nextId <= id) {
            this.nextId = id + 1;
        }
    }

    public void add(T value) {
        this.set(value, this.nextId);
    }

    @Override
    public int getRawId(T object) {
        Integer integer = this.idMap.get(object);
        return integer == null ? -1 : integer;
    }

    @Override
    @Nullable
    public final T get(int index) {
        if (index >= 0 && index < this.list.size()) {
            return this.list.get(index);
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.list.iterator(), (Predicate)Predicates.notNull());
    }

    public int size() {
        return this.idMap.size();
    }
}

