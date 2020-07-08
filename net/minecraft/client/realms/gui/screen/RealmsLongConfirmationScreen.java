/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsLongConfirmationScreen
extends RealmsScreen {
    private final Type type;
    private final Text line2;
    private final Text line3;
    protected final BooleanConsumer field_22697;
    private final boolean yesNoQuestion;

    public RealmsLongConfirmationScreen(BooleanConsumer booleanConsumer, Type arg, Text arg2, Text arg3, boolean bl) {
        this.field_22697 = booleanConsumer;
        this.type = arg;
        this.line2 = arg2;
        this.line3 = arg3;
        this.yesNoQuestion = bl;
    }

    @Override
    public void init() {
        Realms.narrateNow(this.type.text, this.line2.getString(), this.line3.getString());
        if (this.yesNoQuestion) {
            this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsLongConfirmationScreen.row(8), 100, 20, ScreenTexts.YES, arg -> this.field_22697.accept(true)));
            this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsLongConfirmationScreen.row(8), 100, 20, ScreenTexts.NO, arg -> this.field_22697.accept(false)));
        } else {
            this.addButton(new ButtonWidget(this.width / 2 - 50, RealmsLongConfirmationScreen.row(8), 100, 20, new TranslatableText("mco.gui.ok"), arg -> this.field_22697.accept(true)));
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.field_22697.accept(false);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredString(arg, this.textRenderer, this.type.text, this.width / 2, RealmsLongConfirmationScreen.row(2), this.type.colorCode);
        this.drawCenteredText(arg, this.textRenderer, this.line2, this.width / 2, RealmsLongConfirmationScreen.row(4), 0xFFFFFF);
        this.drawCenteredText(arg, this.textRenderer, this.line3, this.width / 2, RealmsLongConfirmationScreen.row(6), 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        Warning("Warning!", 0xFF0000),
        Info("Info!", 8226750);

        public final int colorCode;
        public final String text;

        private Type(String string2, int j) {
            this.text = string2;
            this.colorCode = j;
        }
    }
}

