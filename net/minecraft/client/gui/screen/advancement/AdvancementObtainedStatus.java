/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum AdvancementObtainedStatus {
    OBTAINED(0),
    UNOBTAINED(1);

    private final int spriteIndex;

    private AdvancementObtainedStatus(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }

    public int getSpriteIndex() {
        return this.spriteIndex;
    }
}

