/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSoundInstance
implements SoundInstance {
    protected Sound sound;
    protected final SoundCategory category;
    protected final Identifier id;
    protected float volume = 1.0f;
    protected float pitch = 1.0f;
    protected double x;
    protected double y;
    protected double z;
    protected boolean repeat;
    protected int repeatDelay;
    protected SoundInstance.AttenuationType attenuationType = SoundInstance.AttenuationType.LINEAR;
    protected boolean field_18935;
    protected boolean looping;

    protected AbstractSoundInstance(SoundEvent arg, SoundCategory arg2) {
        this(arg.getId(), arg2);
    }

    protected AbstractSoundInstance(Identifier arg, SoundCategory arg2) {
        this.id = arg;
        this.category = arg2;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public WeightedSoundSet getSoundSet(SoundManager arg) {
        WeightedSoundSet lv = arg.get(this.id);
        this.sound = lv == null ? SoundManager.MISSING_SOUND : lv.getSound();
        return lv;
    }

    @Override
    public Sound getSound() {
        return this.sound;
    }

    @Override
    public SoundCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean isRepeatable() {
        return this.repeat;
    }

    @Override
    public int getRepeatDelay() {
        return this.repeatDelay;
    }

    @Override
    public float getVolume() {
        return this.volume * this.sound.getVolume();
    }

    @Override
    public float getPitch() {
        return this.pitch * this.sound.getPitch();
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public SoundInstance.AttenuationType getAttenuationType() {
        return this.attenuationType;
    }

    @Override
    public boolean isLooping() {
        return this.looping;
    }
}

