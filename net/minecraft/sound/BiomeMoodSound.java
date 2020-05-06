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
import net.minecraft.sound.SoundEvents;

public class BiomeMoodSound {
    public static final BiomeMoodSound CAVE = new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
    private SoundEvent event;
    private int cultivationTicks;
    private int spawnRange;
    private double extraDistance;

    public BiomeMoodSound(SoundEvent arg, int i, int j, double d) {
        this.event = arg;
        this.cultivationTicks = i;
        this.spawnRange = j;
        this.extraDistance = d;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCultivationTicks() {
        return this.cultivationTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSpawnRange() {
        return this.spawnRange;
    }

    @Environment(value=EnvType.CLIENT)
    public double getExtraDistance() {
        return this.extraDistance;
    }
}

