/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.state.property.Property;

public class BooleanProperty
extends Property<Boolean> {
    private final ImmutableSet<Boolean> values = ImmutableSet.of((Object)true, (Object)false);

    protected BooleanProperty(String string) {
        super(string, Boolean.class);
    }

    @Override
    public Collection<Boolean> getValues() {
        return this.values;
    }

    public static BooleanProperty of(String string) {
        return new BooleanProperty(string);
    }

    @Override
    public Optional<Boolean> parse(String string) {
        if ("true".equals(string) || "false".equals(string)) {
            return Optional.of(Boolean.valueOf(string));
        }
        return Optional.empty();
    }

    @Override
    public String name(Boolean boolean_) {
        return boolean_.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BooleanProperty && super.equals(object)) {
            BooleanProperty lv = (BooleanProperty)object;
            return this.values.equals(lv.values);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }
}

