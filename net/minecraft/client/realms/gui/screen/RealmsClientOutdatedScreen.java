/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsClientOutdatedScreen
extends RealmsScreen {
    private final Screen parent;
    private final boolean outdated;

    public RealmsClientOutdatedScreen(Screen parent, boolean outdated) {
        this.parent = parent;
        this.outdated = outdated;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsClientOutdatedScreen.row(12), 200, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        TranslatableText lv = new TranslatableText(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
        this.drawCenteredText(matrices, this.textRenderer, lv, this.width / 2, RealmsClientOutdatedScreen.row(3), 0xFF0000);
        int k = this.outdated ? 2 : 3;
        for (int l = 0; l < k; ++l) {
            String string = (this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (l + 1);
            this.drawCenteredText(matrices, this.textRenderer, new TranslatableText(string), this.width / 2, RealmsClientOutdatedScreen.row(5) + l * 12, 0xFFFFFF);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335 || keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

