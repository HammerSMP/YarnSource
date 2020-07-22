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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ElytraSoundInstance
extends MovingSoundInstance {
    private final ClientPlayerEntity player;
    private int tickCount;

    public ElytraSoundInstance(ClientPlayerEntity player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1f;
    }

    @Override
    public void tick() {
        ++this.tickCount;
        if (this.player.removed || this.tickCount > 20 && !this.player.isFallFlying()) {
            this.setDone();
            return;
        }
        this.x = (float)this.player.getX();
        this.y = (float)this.player.getY();
        this.z = (float)this.player.getZ();
        float f = (float)this.player.getVelocity().lengthSquared();
        this.volume = (double)f >= 1.0E-7 ? MathHelper.clamp(f / 4.0f, 0.0f, 1.0f) : 0.0f;
        if (this.tickCount < 20) {
            this.volume = 0.0f;
        } else if (this.tickCount < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.tickCount - 20) / 20.0));
        }
        float g = 0.8f;
        this.pitch = this.volume > 0.8f ? 1.0f + (this.volume - 0.8f) : 1.0f;
    }
}

