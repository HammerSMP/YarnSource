/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractPressableButtonWidget
extends AbstractButtonWidget {
    public AbstractPressableButtonWidget(int i, int j, int k, int l, Text arg) {
        super(i, j, k, l, arg);
    }

    public abstract void onPress();

    @Override
    public void onClick(double d, double e) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (i == 257 || i == 32 || i == 335) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}

