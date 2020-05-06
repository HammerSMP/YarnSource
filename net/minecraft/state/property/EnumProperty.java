/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.state.property;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.util.StringIdentifiable;

public class EnumProperty<T extends Enum<T>>
extends AbstractProperty<T> {
    private final ImmutableSet<T> values;
    private final Map<String, T> byName = Maps.newHashMap();

    protected EnumProperty(String string, Class<T> arg, Collection<T> collection) {
        super(string, arg);
        this.values = ImmutableSet.copyOf(collection);
        for (Enum lv : collection) {
            String string2 = ((StringIdentifiable)((Object)lv)).asString();
            if (this.byName.containsKey(string2)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
            }
            this.byName.put(string2, lv);
        }
    }

    @Override
    public Collection<T> getValues() {
        return this.values;
    }

    @Override
    public Optional<T> parse(String string) {
        return Optional.ofNullable(this.byName.get(string));
    }

    @Override
    public String name(T arg) {
        return ((StringIdentifiable)arg).asString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof EnumProperty && super.equals(object)) {
            EnumProperty lv = (EnumProperty)object;
            return this.values.equals(lv.values) && this.byName.equals(lv.byName);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int i = super.computeHashCode();
        i = 31 * i + this.values.hashCode();
        i = 31 * i + this.byName.hashCode();
        return i;
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> arg) {
        return EnumProperty.of(string, arg, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> arg, Predicate<T> predicate) {
        return EnumProperty.of(string, arg, Arrays.stream(arg.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> arg, T ... args) {
        return EnumProperty.of(string, arg, Lists.newArrayList((Object[])args));
    }

    public static <T extends Enum<T>> EnumProperty<T> of(String string, Class<T> arg, Collection<T> collection) {
        return new EnumProperty<T>(string, arg, collection);
    }
}

