/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nullable
 */
package net.minecraft.util.collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.MathHelper;

public class Int2ObjectBiMap<K>
implements IndexedIterable<K> {
    private static final Object empty = null;
    private K[] values;
    private int[] ids;
    private K[] idToValues;
    private int nextId;
    private int size;

    public Int2ObjectBiMap(int size) {
        size = (int)((float)size / 0.8f);
        this.values = new Object[size];
        this.ids = new int[size];
        this.idToValues = new Object[size];
    }

    @Override
    public int getRawId(@Nullable K object) {
        return this.getIdFromIndex(this.findIndex(object, this.getIdealIndex(object)));
    }

    @Override
    @Nullable
    public K get(int index) {
        if (index < 0 || index >= this.idToValues.length) {
            return null;
        }
        return this.idToValues[index];
    }

    private int getIdFromIndex(int index) {
        if (index == -1) {
            return -1;
        }
        return this.ids[index];
    }

    public boolean containsId(int id) {
        return this.get(id) != null;
    }

    public int add(K value) {
        int i = this.nextId();
        this.put(value, i);
        return i;
    }

    private int nextId() {
        while (this.nextId < this.idToValues.length && this.idToValues[this.nextId] != null) {
            ++this.nextId;
        }
        return this.nextId;
    }

    private void resize(int newSize) {
        K[] objects = this.values;
        int[] is = this.ids;
        this.values = new Object[newSize];
        this.ids = new int[newSize];
        this.idToValues = new Object[newSize];
        this.nextId = 0;
        this.size = 0;
        for (int j = 0; j < objects.length; ++j) {
            if (objects[j] == null) continue;
            this.put(objects[j], is[j]);
        }
    }

    public void put(K value, int id) {
        int j = Math.max(id, this.size + 1);
        if ((float)j >= (float)this.values.length * 0.8f) {
            int k;
            for (k = this.values.length << 1; k < id; k <<= 1) {
            }
            this.resize(k);
        }
        int l = this.findFree(this.getIdealIndex(value));
        this.values[l] = value;
        this.ids[l] = id;
        this.idToValues[id] = value;
        ++this.size;
        if (id == this.nextId) {
            ++this.nextId;
        }
    }

    private int getIdealIndex(@Nullable K value) {
        return (MathHelper.idealHash(System.identityHashCode(value)) & Integer.MAX_VALUE) % this.values.length;
    }

    private int findIndex(@Nullable K value, int id) {
        for (int j = id; j < this.values.length; ++j) {
            if (this.values[j] == value) {
                return j;
            }
            if (this.values[j] != empty) continue;
            return -1;
        }
        for (int k = 0; k < id; ++k) {
            if (this.values[k] == value) {
                return k;
            }
            if (this.values[k] != empty) continue;
            return -1;
        }
        return -1;
    }

    private int findFree(int size) {
        for (int j = size; j < this.values.length; ++j) {
            if (this.values[j] != empty) continue;
            return j;
        }
        for (int k = 0; k < size; ++k) {
            if (this.values[k] != empty) continue;
            return k;
        }
        throw new RuntimeException("Overflowed :(");
    }

    @Override
    public Iterator<K> iterator() {
        return Iterators.filter((Iterator)Iterators.forArray((Object[])this.idToValues), (Predicate)Predicates.notNull());
    }

    public void clear() {
        Arrays.fill(this.values, null);
        Arrays.fill(this.idToValues, null);
        this.nextId = 0;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }
}

