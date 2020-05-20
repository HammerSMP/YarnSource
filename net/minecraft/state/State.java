/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayTable
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Table
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 */
package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;

public abstract class State<O, S> {
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> field_24737 = new Function<Map.Entry<Property<?>, Comparable<?>>, String>(){

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
    protected final O field_24739;
    private final ImmutableMap<Property<?>, Comparable<?>> field_24738;
    private Table<Property<?>, Comparable<?>, S> field_24741;
    protected final MapCodec<S> field_24740;

    protected State(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<S> mapCodec) {
        this.field_24739 = object;
        this.field_24738 = immutableMap;
        this.field_24740 = mapCodec;
    }

    public <T extends Comparable<T>> S method_28493(Property<T> arg) {
        return this.with(arg, (Comparable)State.method_28495(arg.getValues(), this.get(arg)));
    }

    protected static <T> T method_28495(Collection<T> collection, T object) {
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
        stringBuilder.append(this.field_24739);
        if (!this.getEntries().isEmpty()) {
            stringBuilder.append('[');
            stringBuilder.append(this.getEntries().entrySet().stream().map(field_24737).collect(Collectors.joining(",")));
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }

    public Collection<Property<?>> method_28501() {
        return Collections.unmodifiableCollection(this.field_24738.keySet());
    }

    public <T extends Comparable<T>> boolean method_28498(Property<T> arg) {
        return this.field_24738.containsKey(arg);
    }

    public <T extends Comparable<T>> T get(Property<T> arg) {
        Comparable comparable = (Comparable)this.field_24738.get(arg);
        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + arg + " as it does not exist in " + this.field_24739);
        }
        return (T)((Comparable)arg.getType().cast(comparable));
    }

    public <T extends Comparable<T>> Optional<T> method_28500(Property<T> arg) {
        Comparable comparable = (Comparable)this.field_24738.get(arg);
        if (comparable == null) {
            return Optional.empty();
        }
        return Optional.of(arg.getType().cast(comparable));
    }

    public <T extends Comparable<T>, V extends T> S with(Property<T> arg, V comparable) {
        Comparable comparable2 = (Comparable)this.field_24738.get(arg);
        if (comparable2 == null) {
            throw new IllegalArgumentException("Cannot set property " + arg + " as it does not exist in " + this.field_24739);
        }
        if (comparable2 == comparable) {
            return (S)this;
        }
        Object object = this.field_24741.get(arg, comparable);
        if (object == null) {
            throw new IllegalArgumentException("Cannot set property " + arg + " to " + comparable + " on " + this.field_24739 + ", it is not an allowed value");
        }
        return (S)object;
    }

    public void method_28496(Map<Map<Property<?>, Comparable<?>>, S> map) {
        if (this.field_24741 != null) {
            throw new IllegalStateException();
        }
        HashBasedTable table = HashBasedTable.create();
        for (Map.Entry entry : this.field_24738.entrySet()) {
            Property lv = (Property)entry.getKey();
            for (Comparable comparable : lv.getValues()) {
                if (comparable == entry.getValue()) continue;
                table.put((Object)lv, (Object)comparable, map.get(this.method_28499(lv, comparable)));
            }
        }
        this.field_24741 = table.isEmpty() ? table : ArrayTable.create((Table)table);
    }

    private Map<Property<?>, Comparable<?>> method_28499(Property<?> arg, Comparable<?> comparable) {
        HashMap map = Maps.newHashMap(this.field_24738);
        map.put(arg, comparable);
        return map;
    }

    public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
        return this.field_24738;
    }

    protected static <O, S extends State<O, S>> Codec<S> method_28494(Codec<O> codec, Function<O, S> function) {
        return codec.dispatch("Name", arg -> arg.field_24739, object -> {
            State lv = (State)function.apply(object);
            if (lv.getEntries().isEmpty()) {
                return Codec.unit((Object)lv);
            }
            return lv.field_24740.fieldOf("Properties").codec();
        });
    }
}

