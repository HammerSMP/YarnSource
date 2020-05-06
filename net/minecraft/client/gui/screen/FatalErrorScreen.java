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
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class FatalErrorScreen
extends Screen {
    private final Text message;

    public FatalErrorScreen(Text arg, Text arg2) {
        super(arg);
        this.message = arg2;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 100, 140, 200, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(null)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.fillGradient(arg, 0, 0, this.width, this.height, -12574688, -11530224);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 90, 0xFFFFFF);
        this.drawStringWithShadow(arg, this.textRenderer, this.message, this.width / 2, 110, 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

