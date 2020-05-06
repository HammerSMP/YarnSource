/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum AdvancementFrame {
    TASK("task", 0, Formatting.GREEN),
    CHALLENGE("challenge", 26, Formatting.DARK_PURPLE),
    GOAL("goal", 52, Formatting.GREEN);

    private final String id;
    private final int texV;
    private final Formatting titleFormat;

    private AdvancementFrame(String string2, int j, Formatting arg) {
        this.id = string2;
        this.texV = j;
        this.titleFormat = arg;
    }

    public String getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public int texV() {
        return this.texV;
    }

    public static AdvancementFrame forName(String string) {
        for (AdvancementFrame lv : AdvancementFrame.values()) {
            if (!lv.id.equals(string)) continue;
            return lv;
        }
        throw new IllegalArgumentException("Unknown frame type '" + string + "'");
    }

    public Formatting getTitleFormat() {
        return this.titleFormat;
    }
}

