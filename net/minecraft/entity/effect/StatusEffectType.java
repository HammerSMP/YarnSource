/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum StatusEffectType {
    BENEFICIAL(Formatting.BLUE),
    HARMFUL(Formatting.RED),
    NEUTRAL(Formatting.BLUE);

    private final Formatting formatting;

    private StatusEffectType(Formatting arg) {
        this.formatting = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public Formatting getFormatting() {
        return this.formatting;
    }
}

