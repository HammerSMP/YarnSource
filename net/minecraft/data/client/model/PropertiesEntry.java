/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.client.model;

import java.util.stream.Stream;
import net.minecraft.state.property.Property;

public final class PropertiesEntry<T extends Comparable<T>> {
    private final Property<T> property;
    private final T value;

    public PropertiesEntry(Property<T> arg, T comparable) {
        if (!arg.getValues().contains(comparable)) {
            throw new IllegalArgumentException("Value " + comparable + " does not belong to property " + arg);
        }
        this.property = arg;
        this.value = comparable;
    }

    public Property<T> getProperty() {
        return this.property;
    }

    public String toString() {
        return this.property.getName() + "=" + this.property.name(this.value);
    }

    public static <T extends Comparable<T>> Stream<PropertiesEntry<T>> streamAllFor(Property<T> arg) {
        return arg.getValues().stream().map(comparable -> new PropertiesEntry<Comparable>(arg, (Comparable)comparable));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PropertiesEntry)) {
            return false;
        }
        PropertiesEntry lv = (PropertiesEntry)object;
        return this.property == lv.property && this.value.equals(lv.value);
    }

    public int hashCode() {
        int i = this.property.hashCode();
        i = 31 * i + this.value.hashCode();
        return i;
    }
}

