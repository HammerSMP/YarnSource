/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayTable
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Table
 *  javax.annotation.Nullable
 */
package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

public abstract class AbstractState<O, S>
implements State<S> {
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<Map.Entry<Property<?>, Comparable<?>>, String>(){

        @Override
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            }
            Property<?> lv = entry.getKey();
            return lv.getName() + "=" + this.nameValue(lv, entry.getValue());
        }

        private <T extends Comparable<T>> String nameValue(Property<T> arg, Comparable<?> comparable) {
            return arg.name(comparable);
        }

        @Override
        public /* synthetic */ Object apply(@Nullable Object object) {
            return this.apply((Map.Entry)object);
        }
    };
    protected final O owner;
    private final ImmutableMap<Property<?>, Comparable<?>> entries;
    private Table<Property<?>, Comparable<?>, S> withTable;

    protected AbstractState(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        this.owner = object;
        this.entries = immutableMap;
    }

    public <T extends Comparable<T>> S cycle(Property<T> arg) {
        return this.with(arg, (Comparable)AbstractState.getNext(arg.getValues(), this.get(arg)));
    }

    protected static <T> T getNext(Collection<T> collection, T object) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().equals(object)) continue;
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return collection.iterator().next();
        }
        return iterator.next();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.owner);
        if (!this.getEntries().isEmpty()) {
            stringBuilder.append('[');
            stringBuilder.append(this.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")));
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }

    public Collection<Property<?>> getProperties() {
        return Collections.unmodifiableCollection(this.entries.keySet());
    }

    public <T extends Comparable<T>> boolean contains(Property<T> arg) {
        return this.entries.containsKey(arg);
    }

    @Override
    public <T extends Comparable<T>> T get(Property<T> arg) {
        Comparable comparable = (Comparable)this.entries.get(arg);
        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + arg + " as it does not exist in " + this.owner);
        }
        return (T)((Comparable)arg.getType().cast(comparable));
    }

    @Override
    public <T extends Comparable<T>, V extends T> S with(Property<T> arg, V comparable) {
        Comparable comparable2 = (Comparable)this.entries.get(arg);
        if (comparable2 == null) {
            throw new IllegalArgumentException("Cannot set property " + arg + " as it does not exist in " + this.owner);
        }
        if (comparable2 == comparable) {
            return (S)this;
        }
        Object object = this.withTable.get(arg, comparable);
        if (object == null) {
            throw new IllegalArgumentException("Cannot set property " + arg + " to " + comparable + " on " + this.owner + ", it is not an allowed value");
        }
        return (S)object;
    }

    public void createWithTable(Map<Map<Property<?>, Comparable<?>>, S> map) {
        if (this.withTable != null) {
            throw new IllegalStateException();
        }
        HashBasedTable table = HashBasedTable.create();
        for (Map.Entry entry : this.entries.entrySet()) {
            Property lv = (Property)entry.getKey();
            for (Comparable comparable : lv.getValues()) {
                if (comparable == entry.getValue()) continue;
                table.put((Object)lv, (Object)comparable, map.get(this.toMapWith(lv, comparable)));
            }
        }
        this.withTable = table.isEmpty() ? table : ArrayTable.create((Table)table);
    }

    private Map<Property<?>, Comparable<?>> toMapWith(Property<?> arg, Comparable<?> comparable) {
        HashMap map = Maps.newHashMap(this.entries);
        map.put(arg, comparable);
        return map;
    }

    @Override
    public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
        return this.entries;
    }
}

