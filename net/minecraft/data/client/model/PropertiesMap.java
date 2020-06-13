/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.state.property.Property;

public final class PropertiesMap {
    private static final PropertiesMap EMPTY = new PropertiesMap((List<Property.class_4933<?>>)ImmutableList.of());
    private static final Comparator<Property.class_4933<?>> COMPARATOR = Comparator.comparing(arg -> arg.method_25815().getName());
    private final List<Property.class_4933<?>> propertyValues;

    public PropertiesMap method_25819(Property.class_4933<?> arg) {
        return new PropertiesMap((List<Property.class_4933<?>>)ImmutableList.builder().addAll(this.propertyValues).add(arg).build());
    }

    public PropertiesMap with(PropertiesMap arg) {
        return new PropertiesMap((List<Property.class_4933<?>>)ImmutableList.builder().addAll(this.propertyValues).addAll(arg.propertyValues).build());
    }

    private PropertiesMap(List<Property.class_4933<?>> list) {
        this.propertyValues = list;
    }

    public static PropertiesMap empty() {
        return EMPTY;
    }

    public static PropertiesMap method_25821(Property.class_4933<?> ... args) {
        return new PropertiesMap((List<Property.class_4933<?>>)ImmutableList.copyOf((Object[])args));
    }

    public boolean equals(Object object) {
        return this == object || object instanceof PropertiesMap && this.propertyValues.equals(((PropertiesMap)object).propertyValues);
    }

    public int hashCode() {
        return this.propertyValues.hashCode();
    }

    public String asString() {
        return this.propertyValues.stream().sorted(COMPARATOR).map(Property.class_4933::toString).collect(Collectors.joining(","));
    }

    public String toString() {
        return this.asString();
    }
}

