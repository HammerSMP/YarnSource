/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;

public class TrackedData<T> {
    private final int id;
    private final TrackedDataHandler<T> dataType;

    public TrackedData(int i, TrackedDataHandler<T> arg) {
        this.id = i;
        this.dataType = arg;
    }

    public int getId() {
        return this.id;
    }

    public TrackedDataHandler<T> getType() {
        return this.dataType;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TrackedData lv = (TrackedData)object;
        return this.id == lv.id;
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "<entity data: " + this.id + ">";
    }
}

