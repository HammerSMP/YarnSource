/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer field_22692;
    private final Text title1;
    private final Text title2;
    private int delayTicker;

    public RealmsConfirmScreen(BooleanConsumer booleanConsumer, Text arg, Text arg2) {
        this.field_22692 = booleanConsumer;
        this.title1 = arg;
        this.title2 = arg2;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.YES, arg -> this.field_22692.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.NO, arg -> this.field_22692.accept(false)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 0xFFFFFF);
        this.drawCenteredText(arg, this.textRenderer, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.delayTicker == 0) {
            for (AbstractButtonWidget lv : this.buttons) {
                lv.active = true;
            }
        }
    }
}

