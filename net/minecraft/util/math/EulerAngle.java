/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class EulerAngle {
    protected final float pitch;
    protected final float yaw;
    protected final float roll;

    public EulerAngle(float f, float g, float h) {
        this.pitch = Float.isInfinite(f) || Float.isNaN(f) ? 0.0f : f % 360.0f;
        this.yaw = Float.isInfinite(g) || Float.isNaN(g) ? 0.0f : g % 360.0f;
        this.roll = Float.isInfinite(h) || Float.isNaN(h) ? 0.0f : h % 360.0f;
    }

    public EulerAngle(ListTag arg) {
        this(arg.getFloat(0), arg.getFloat(1), arg.getFloat(2));
    }

    public ListTag serialize() {
        ListTag lv = new ListTag();
        lv.add(FloatTag.of(this.pitch));
        lv.add(FloatTag.of(this.yaw));
        lv.add(FloatTag.of(this.roll));
        return lv;
    }

    public boolean equals(Object object) {
        if (!(object instanceof EulerAngle)) {
            return false;
        }
        EulerAngle lv = (EulerAngle)object;
        return this.pitch == lv.pitch && this.yaw == lv.yaw && this.roll == lv.roll;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getRoll() {
        return this.roll;
    }
}

