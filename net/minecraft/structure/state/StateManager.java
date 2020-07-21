/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.AbstractState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.MapUtil;

public class StateManager<O, S extends State<S>> {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> properties;
    private final ImmutableList<S> states;

    protected <A extends AbstractState<O, S>> StateManager(O object, Factory<O, S, A> arg, Map<String, Property<?>> map) {
        this.owner = object;
        this.properties = ImmutableSortedMap.copyOf(map);
        LinkedHashMap map2 = Maps.newLinkedHashMap();
        ArrayList list3 = Lists.newArrayList();
        Stream<List<List<Object>>> stream = Stream.of(Collections.emptyList());
        for (Property lv : this.properties.values()) {
            stream = stream.flatMap(list -> lv.getValues().stream().map(comparable -> {
                ArrayList list2 = Lists.newArrayList((Iterable)list);
                list2.add(comparable);
                return list2;
            }));
        }
        stream.forEach(list2 -> {
            Map map2 = MapUtil.createMap(this.properties.values(), list2);
            Object lv = arg.create(object, ImmutableMap.copyOf(map2));
            map2.put(map2, lv);
            list3.add(lv);
        });
        for (AbstractState lv2 : list3) {
            lv2.createWithTable(map2);
        }
        this.states = ImmutableList.copyOf((Iterable)list3);
    }

    public ImmutableList<S> getStates() {
        return this.states;
    }

    public S getDefaultState() {
        return (S)((State)this.states.get(0));
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.properties.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("block", this.owner).add("properties", this.properties.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String string) {
        return (Property)this.properties.get((Object)string);
    }

    public static class Builder<O, S extends State<S>> {
        private final O owner;
        private final Map<String, Property<?>> namedProperties = Maps.newHashMap();

        public Builder(O object) {
            this.owner = object;
        }

        public Builder<O, S> add(Property<?> ... args) {
            for (Property<?> lv : args) {
                this.validate(lv);
                this.namedProperties.put(lv.getName(), lv);
            }
            return this;
        }

        private <T extends Comparable<T>> void validate(Property<T> arg) {
            String string = arg.getName();
            if (!VALID_NAME_PATTERN.matcher(string).matches()) {
                throw new IllegalArgumentException(this.owner + " has invalidly named property: " + string);
            }
            Collection<T> collection = arg.getValues();
            if (collection.size() <= 1) {
                throw new IllegalArgumentException(this.owner + " attempted use property " + string + " with <= 1 possible values");
            }
            for (Comparable comparable : collection) {
                String string2 = arg.name(comparable);
                if (VALID_NAME_PATTERN.matcher(string2).matches()) continue;
                throw new IllegalArgumentException(this.owner + " has property: " + string + " with invalidly named value: " + string2);
            }
            if (this.namedProperties.containsKey(string)) {
                throw new IllegalArgumentException(this.owner + " has duplicate property: " + string);
            }
        }

        public <A extends AbstractState<O, S>> StateManager<O, S> build(Factory<O, S, A> arg) {
            return new StateManager<O, S>(this.owner, arg, this.namedProperties);
        }
    }

    public static interface Factory<O, S extends State<S>, A extends AbstractState<O, S>> {
        public A create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2);
    }
}

