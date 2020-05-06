/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5195;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MusicTracker {
    private final Random random = new Random();
    private final MinecraftClient client;
    @Nullable
    private SoundInstance current;
    private int timeUntilNextSong = 100;

    public MusicTracker(MinecraftClient arg) {
        this.client = arg;
    }

    public void tick() {
        class_5195 lv = this.client.getMusicType();
        if (this.current != null) {
            if (!lv.method_27279().getId().equals(this.current.getId()) && lv.method_27282()) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, lv.method_27280() / 2);
            }
            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong, MathHelper.nextInt(this.random, lv.method_27280(), lv.method_27281()));
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, lv.method_27281());
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            this.play(lv);
        }
    }

    public void play(class_5195 arg) {
        this.current = PositionedSoundInstance.music(arg.method_27279());
        if (this.current.getSound() != SoundManager.MISSING_SOUND) {
            this.client.getSoundManager().play(this.current);
        }
        this.timeUntilNextSong = Integer.MAX_VALUE;
    }

    public void stop() {
        if (this.current != null) {
            this.client.getSoundManager().stop(this.current);
            this.current = null;
            this.timeUntilNextSong = 100;
        }
    }

    public boolean isPlayingType(class_5195 arg) {
        if (this.current == null) {
            return false;
        }
        return arg.method_27279().getId().equals(this.current.getId());
    }
}

