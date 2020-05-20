/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public enum ParticlesOption {
    ALL(0, "options.particles.all"),
    DECREASED(1, "options.particles.decreased"),
    MINIMAL(2, "options.particles.minimal");

    private static final ParticlesOption[] VALUES;
    private final int id;
    private final String translationKey;

    private ParticlesOption(int j, String string2) {
        this.id = j;
        this.translationKey = string2;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public int getId() {
        return this.id;
    }

    public static ParticlesOption byId(int i) {
        return VALUES[MathHelper.floorMod(i, VALUES.length)];
    }

    static {
        VALUES = (ParticlesOption[])Arrays.stream(ParticlesOption.values()).sorted(Comparator.comparingInt(ParticlesOption::getId)).toArray(ParticlesOption[]::new);
    }
}
