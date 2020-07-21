/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    private final Screen parent;
    private Text line1;
    private Text line2;

    public RealmsGenericErrorScreen(RealmsServiceException arg, Screen arg2) {
        this.parent = arg2;
        this.errorMessage(arg);
    }

    public RealmsGenericErrorScreen(Text arg, Screen arg2) {
        this.parent = arg2;
        this.errorMessage(arg);
    }

    public RealmsGenericErrorScreen(Text arg, Text arg2, Screen arg3) {
        this.parent = arg3;
        this.errorMessage(arg, arg2);
    }

    private void errorMessage(RealmsServiceException arg) {
        if (arg.errorCode == -1) {
            this.line1 = new LiteralText("An error occurred (" + arg.httpResultCode + "):");
            this.line2 = new LiteralText(arg.httpResponseContent);
        } else {
            this.line1 = new LiteralText("Realms (" + arg.errorCode + "):");
            String string = "mco.errorMessage." + arg.errorCode;
            this.line2 = I18n.hasTranslation(string) ? new TranslatableText(string) : Text.method_30163(arg.errorMsg);
        }
    }

    private void errorMessage(Text arg) {
        this.line1 = new LiteralText("An error occurred: ");
        this.line2 = arg;
    }

    private void errorMessage(Text arg, Text arg2) {
        this.line1 = arg;
        this.line2 = arg2;
    }

    @Override
    public void init() {
        Realms.narrateNow(this.line1.getString() + ": " + this.line2.getString());
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 52, 200, 20, new LiteralText("Ok"), arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.line1, this.width / 2, 80, 0xFFFFFF);
        this.drawCenteredText(arg, this.textRenderer, this.line2, this.width / 2, 100, 0xFF0000);
        super.render(arg, i, j, f);
    }
}
