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
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer field_22692;
    private final Text title1;
    private final Text title2;
    private int delayTicker;

    public RealmsConfirmScreen(BooleanConsumer booleanConsumer, Text title1, Text title2) {
        this.field_22692 = booleanConsumer;
        this.title1 = title1;
        this.title2 = title2;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.YES, arg -> this.field_22692.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.NO, arg -> this.field_22692.accept(false)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredText(matrices, this.textRenderer, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 0xFFFFFF);
        this.drawCenteredText(matrices, this.textRenderer, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
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

