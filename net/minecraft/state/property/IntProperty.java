/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 */
package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import net.minecraft.state.property.AbstractProperty;

public class IntProperty
extends AbstractProperty<Integer> {
    private final ImmutableSet<Integer> values;

    protected IntProperty(String string, int i, int j) {
        super(string, Integer.class);
        if (i < 0) {
            throw new IllegalArgumentException("Min value of " + string + " must be 0 or greater");
        }
        if (j <= i) {
            throw new IllegalArgumentException("Max value of " + string + " must be greater than min (" + i + ")");
        }
        HashSet set = Sets.newHashSet();
        for (int k = i; k <= j; ++k) {
            set.add(k);
        }
        this.values = ImmutableSet.copyOf((Collection)set);
    }

    @Override
    public Collection<Integer> getValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof IntProperty && super.equals(object)) {
            IntProperty lv = (IntProperty)object;
            return this.values.equals(lv.values);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }

    public static IntProperty of(String string, int i, int j) {
        return new IntProperty(string, i, j);
    }

    @Override
    public Optional<Integer> parse(String string) {
        try {
            Integer integer = Integer.valueOf(string);
            return this.values.contains((Object)integer) ? Optional.of(integer) : Optional.empty();
        }
        catch (NumberFormatException numberFormatException) {
            return Optional.empty();
        }
    }

    @Override
    public String name(Integer integer) {
        return integer.toString();
    }
}

