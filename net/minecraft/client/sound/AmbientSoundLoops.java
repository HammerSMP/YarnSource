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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public class AmbientSoundLoops {

    @Environment(value=EnvType.CLIENT)
    public static class Underwater
    extends MovingSoundInstance {
        private final ClientPlayerEntity player;
        private int transitionTimer;

        public Underwater(ClientPlayerEntity arg) {
            super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundCategory.AMBIENT);
            this.player = arg;
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.field_18935 = true;
            this.looping = true;
        }

        @Override
        public void tick() {
            if (this.player.removed || this.transitionTimer < 0) {
                this.setDone();
                return;
            }
            this.transitionTimer = this.player.isSubmergedInWater() ? ++this.transitionTimer : (this.transitionTimer -= 2);
            this.transitionTimer = Math.min(this.transitionTimer, 40);
            this.volume = Math.max(0.0f, Math.min((float)this.transitionTimer / 40.0f, 1.0f));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class MusicLoop
    extends MovingSoundInstance {
        private final ClientPlayerEntity player;

        protected MusicLoop(ClientPlayerEntity arg, SoundEvent arg2) {
            super(arg2, SoundCategory.AMBIENT);
            this.player = arg;
            this.repeat = false;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.field_18935 = true;
            this.looping = true;
        }

        @Override
        public void tick() {
            if (this.player.removed || !this.player.isSubmergedInWater()) {
                this.setDone();
            }
        }
    }
}

