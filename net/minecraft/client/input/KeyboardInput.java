/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.options.GameOptions;

@Environment(value=EnvType.CLIENT)
public class KeyboardInput
extends Input {
    private final GameOptions settings;

    public KeyboardInput(GameOptions arg) {
        this.settings = arg;
    }

    @Override
    public void tick(boolean bl) {
        this.pressingForward = this.settings.keyForward.isPressed();
        this.pressingBack = this.settings.keyBack.isPressed();
        this.pressingLeft = this.settings.keyLeft.isPressed();
        this.pressingRight = this.settings.keyRight.isPressed();
        float f = this.pressingForward == this.pressingBack ? 0.0f : (this.movementForward = this.pressingForward ? 1.0f : -1.0f);
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0f : (this.pressingLeft ? 1.0f : -1.0f);
        this.jumping = this.settings.keyJump.isPressed();
        this.sneaking = this.settings.keySneak.isPressed();
        if (bl) {
            this.movementSideways = (float)((double)this.movementSideways * 0.3);
            this.movementForward = (float)((double)this.movementForward * 0.3);
        }
    }
}

