/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.dynamic.DynamicSerializable;

public final class DynamicSerializableBoolean
implements DynamicSerializable {
    private final boolean value;

    private DynamicSerializableBoolean(boolean bl) {
        this.value = bl;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createBoolean(this.value);
    }

    public static DynamicSerializableBoolean of(Dynamic<?> dynamic) {
        return new DynamicSerializableBoolean(dynamic.asBoolean(false));
    }

    public static DynamicSerializableBoolean of(boolean bl) {
        return new DynamicSerializableBoolean(bl);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        DynamicSerializableBoolean lv = (DynamicSerializableBoolean)object;
        return this.value == lv.value;
    }

    public int hashCode() {
        return Boolean.hashCode(this.value);
    }

    public String toString() {
        return Boolean.toString(this.value);
    }
}

