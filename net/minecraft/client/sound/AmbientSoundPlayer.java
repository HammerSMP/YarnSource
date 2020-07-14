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
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public class AmbientSoundPlayer
implements ClientPlayerTickable {
    private final ClientPlayerEntity player;
    private final SoundManager soundManager;
    private int ticksUntilPlay = 0;

    public AmbientSoundPlayer(ClientPlayerEntity player, SoundManager arg2) {
        this.player = player;
        this.soundManager = arg2;
    }

    @Override
    public void tick() {
        --this.ticksUntilPlay;
        if (this.ticksUntilPlay <= 0 && this.player.isSubmergedInWater()) {
            float f = this.player.world.random.nextFloat();
            if (f < 1.0E-4f) {
                this.ticksUntilPlay = 0;
                this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
            } else if (f < 0.001f) {
                this.ticksUntilPlay = 0;
                this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
            } else if (f < 0.01f) {
                this.ticksUntilPlay = 0;
                this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
            }
        }
    }
}

