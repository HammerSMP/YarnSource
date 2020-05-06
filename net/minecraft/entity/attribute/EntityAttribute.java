/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.attribute;

public class EntityAttribute {
    private final double fallback;
    private boolean tracked;
    private final String translationKey;

    protected EntityAttribute(String string, double d) {
        this.fallback = d;
        this.translationKey = string;
    }

    public double getDefaultValue() {
        return this.fallback;
    }

    public boolean isTracked() {
        return this.tracked;
    }

    public EntityAttribute setTracked(boolean bl) {
        this.tracked = bl;
        return this;
    }

    public double clamp(double d) {
        return d;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }
}

