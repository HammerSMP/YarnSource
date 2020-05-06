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
import net.minecraft.sound.SoundEvent;

public class BiomeAdditionsSound {
    private SoundEvent event;
    private double chance;

    public BiomeAdditionsSound(SoundEvent arg, double d) {
        this.event = arg;
        this.chance = d;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    @Environment(value=EnvType.CLIENT)
    public double getChance() {
        return this.chance;
    }
}

