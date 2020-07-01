/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import net.minecraft.util.math.Box;

public class EntityDimensions {
    public final float width;
    public final float height;
    public final boolean fixed;

    public EntityDimensions(float f, float g, boolean bl) {
        this.width = f;
        this.height = g;
        this.fixed = bl;
    }

    public Box method_30231(double d, double e, double f) {
        float g = this.width / 2.0f;
        float h = this.height;
        return new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g);
    }

    public EntityDimensions scaled(float f) {
        return this.scaled(f, f);
    }

    public EntityDimensions scaled(float f, float g) {
        if (this.fixed || f == 1.0f && g == 1.0f) {
            return this;
        }
        return EntityDimensions.changing(this.width * f, this.height * g);
    }

    public static EntityDimensions changing(float f, float g) {
        return new EntityDimensions(f, g, false);
    }

    public static EntityDimensions fixed(float f, float g) {
        return new EntityDimensions(f, g, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}

