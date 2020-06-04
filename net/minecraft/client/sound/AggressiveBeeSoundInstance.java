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
import net.minecraft.client.sound.AbstractBeeSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public class AggressiveBeeSoundInstance
extends AbstractBeeSoundInstance {
    public AggressiveBeeSoundInstance(BeeEntity arg) {
        super(arg, SoundEvents.ENTITY_BEE_LOOP_AGGRESSIVE, SoundCategory.NEUTRAL);
        this.repeatDelay = 0;
    }

    @Override
    protected MovingSoundInstance getReplacement() {
        return new PassiveBeeSoundInstance(this.bee);
    }

    @Override
    protected boolean shouldReplace() {
        return !this.bee.method_29511();
    }
}

