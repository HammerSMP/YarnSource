/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class SoundEvent {
    private final Identifier id;

    public SoundEvent(Identifier arg) {
        this.id = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getId() {
        return this.id;
    }
}

