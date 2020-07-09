/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface Toast {
    public static final Identifier TOASTS_TEX = new Identifier("textures/gui/toasts.png");
    public static final Object TYPE = new Object();

    public Visibility draw(MatrixStack var1, ToastManager var2, long var3);

    default public Object getType() {
        return TYPE;
    }

    default public int getWidth() {
        return 160;
    }

    default public int getHeight() {
        return 32;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Visibility {
        SHOW(SoundEvents.UI_TOAST_IN),
        HIDE(SoundEvents.UI_TOAST_OUT);

        private final SoundEvent sound;

        private Visibility(SoundEvent arg) {
            this.sound = arg;
        }

        public void playSound(SoundManager arg) {
            arg.play(PositionedSoundInstance.master(this.sound, 1.0f, 1.0f));
        }
    }
}

