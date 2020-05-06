/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.attribute;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.math.MathHelper;

public class ClampedEntityAttribute
extends EntityAttribute {
    private final double minValue;
    private final double maxValue;

    public ClampedEntityAttribute(String string, double d, double e, double f) {
        super(string, d);
        this.minValue = e;
        this.maxValue = f;
        if (e > f) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        if (d < e) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        if (d > f) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    @Override
    public double clamp(double d) {
        d = MathHelper.clamp(d, this.minValue, this.maxValue);
        return d;
    }
}

