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
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public class GuardianAttackSoundInstance
extends MovingSoundInstance {
    private final GuardianEntity guardian;

    public GuardianAttackSoundInstance(GuardianEntity arg) {
        super(SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE);
        this.guardian = arg;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    @Override
    public boolean canPlay() {
        return !this.guardian.isSilent();
    }

    @Override
    public void tick() {
        if (this.guardian.removed || this.guardian.getTarget() != null) {
            this.setDone();
            return;
        }
        this.x = (float)this.guardian.getX();
        this.y = (float)this.guardian.getY();
        this.z = (float)this.guardian.getZ();
        float f = this.guardian.getBeamProgress(0.0f);
        this.volume = 0.0f + 1.0f * f * f;
        this.pitch = 0.7f + 0.5f * f;
    }
}

