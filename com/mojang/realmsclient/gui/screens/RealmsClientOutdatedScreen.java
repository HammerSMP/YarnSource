/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsClientOutdatedScreen
extends RealmsScreen {
    private final Screen lastScreen;
    private final boolean outdated;

    public RealmsClientOutdatedScreen(Screen arg, boolean bl) {
        this.lastScreen = arg;
        this.outdated = bl;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsClientOutdatedScreen.row(12), 200, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        TranslatableText lv = new TranslatableText(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
        this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, RealmsClientOutdatedScreen.row(3), 0xFF0000);
        int k = this.outdated ? 2 : 3;
        for (int l = 0; l < k; ++l) {
            String string = (this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (l + 1);
            this.drawCenteredText(arg, this.textRenderer, new TranslatableText(string), this.width / 2, RealmsClientOutdatedScreen.row(5) + l * 12, 0xFFFFFF);
        }
        super.render(arg, i, j, f);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 257 || i == 335 || i == 256) {
            this.client.openScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(i, j, k);
    }
}

