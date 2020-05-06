/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SaveLevelScreen
extends Screen {
    public SaveLevelScreen(Text arg) {
        super(arg);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderDirtBackground(0);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 70, 0xFFFFFF);
        super.render(arg, i, j, f);
    }
}

