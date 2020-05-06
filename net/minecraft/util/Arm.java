/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Arm {
    LEFT(new TranslatableText("options.mainHand.left")),
    RIGHT(new TranslatableText("options.mainHand.right"));

    private final Text optionName;

    private Arm(Text arg) {
        this.optionName = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public Arm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    public String toString() {
        return this.optionName.getString();
    }

    @Environment(value=EnvType.CLIENT)
    public Text method_27301() {
        return this.optionName;
    }
}

