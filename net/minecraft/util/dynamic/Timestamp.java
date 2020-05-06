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

public final class Timestamp
implements DynamicSerializable {
    private final long time;

    private Timestamp(long l) {
        this.time = l;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createLong(this.time);
    }

    public static Timestamp of(Dynamic<?> dynamic) {
        return new Timestamp(dynamic.asNumber((Number)0).longValue());
    }

    public static Timestamp of(long l) {
        return new Timestamp(l);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Timestamp lv = (Timestamp)object;
        return this.time == lv.time;
    }

    public int hashCode() {
        return Long.hashCode(this.time);
    }

    public String toString() {
        return Long.toString(this.time);
    }
}

