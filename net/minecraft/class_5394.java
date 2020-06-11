/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.tag.Tag;

public class class_5394<T>
implements Tag<T> {
    private final ImmutableList<T> field_25593;
    private final Set<T> field_25594;
    @VisibleForTesting
    protected final Class<?> field_25591;

    protected class_5394(Set<T> set, Class<?> class_) {
        this.field_25591 = class_;
        this.field_25594 = set;
        this.field_25593 = ImmutableList.copyOf(set);
    }

    public static <T> class_5394<T> method_29898() {
        return new class_5394<T>(ImmutableSet.of(), Void.class);
    }

    public static <T> class_5394<T> method_29900(Set<T> set) {
        return new class_5394<T>(set, class_5394.method_29901(set));
    }

    @Override
    public boolean contains(T object) {
        return this.field_25591.isInstance(object) && this.field_25594.contains(object);
    }

    @Override
    public List<T> values() {
        return this.field_25593;
    }

    private static <T> Class<?> method_29901(Set<T> set) {
        if (set.isEmpty()) {
            return Void.class;
        }
        Class<?> class_ = null;
        for (T object : set) {
            if (class_ == null) {
                class_ = object.getClass();
                continue;
            }
            class_ = class_5394.method_29899(class_, object.getClass());
        }
        return class_;
    }

    private static Class<?> method_29899(Class<?> class_, Class<?> class2) {
        while (!class_.isAssignableFrom(class2)) {
            class_ = class_.getSuperclass();
        }
        return class_;
    }
}

