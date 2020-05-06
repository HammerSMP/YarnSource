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
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(value=EnvType.CLIENT)
public abstract class MovingSoundInstance
extends AbstractSoundInstance
implements TickableSoundInstance {
    private boolean done;

    protected MovingSoundInstance(SoundEvent arg, SoundCategory arg2) {
        super(arg, arg2);
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    protected final void setDone() {
        this.done = true;
        this.repeat = false;
    }
}

