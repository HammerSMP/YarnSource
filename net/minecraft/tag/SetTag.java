/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.tag;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.tag.Tag;

public class SetTag<T>
implements Tag<T> {
    private final ImmutableList<T> valueList;
    private final Set<T> valueSet;
    @VisibleForTesting
    protected final Class<?> type;

    protected SetTag(Set<T> set, Class<?> class_) {
        this.type = class_;
        this.valueSet = set;
        this.valueList = ImmutableList.copyOf(set);
    }

    public static <T> SetTag<T> empty() {
        return new SetTag<T>(ImmutableSet.of(), Void.class);
    }

    public static <T> SetTag<T> of(Set<T> set) {
        return new SetTag<T>(set, SetTag.getCommonType(set));
    }

    @Override
    public boolean contains(T object) {
        return this.type.isInstance(object) && this.valueSet.contains(object);
    }

    @Override
    public List<T> values() {
        return this.valueList;
    }

    private static <T> Class<?> getCommonType(Set<T> set) {
        if (set.isEmpty()) {
            return Void.class;
        }
        Class<?> class_ = null;
        for (T object : set) {
            if (class_ == null) {
                class_ = object.getClass();
                continue;
            }
            class_ = SetTag.getCommonType(class_, object.getClass());
        }
        return class_;
    }

    private static Class<?> getCommonType(Class<?> class_, Class<?> class2) {
        while (!class_.isAssignableFrom(class2)) {
            class_ = class_.getSuperclass();
        }
        return class_;
    }
}

