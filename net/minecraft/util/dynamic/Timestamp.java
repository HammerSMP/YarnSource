/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;

public final class Timestamp {
    public static final Codec<Timestamp> field_25121 = Codec.LONG.xmap(Timestamp::new, arg -> arg.time);
    private final long time;

    private Timestamp(long l) {
        this.time = l;
    }

    public long getTime() {
        return this.time;
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

