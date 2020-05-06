/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ConfirmScreen
extends Screen {
    private final Text message;
    private final List<Text> messageSplit = Lists.newArrayList();
    protected Text yesTranslated;
    protected Text noTranslated;
    private int buttonEnableTimer;
    protected final BooleanConsumer callback;

    public ConfirmScreen(BooleanConsumer booleanConsumer, Text arg, Text arg2) {
        this(booleanConsumer, arg, arg2, ScreenTexts.YES, ScreenTexts.NO);
    }

    public ConfirmScreen(BooleanConsumer booleanConsumer, Text arg, Text arg2, Text arg3, Text arg4) {
        super(arg);
        this.callback = booleanConsumer;
        this.message = arg2;
        this.yesTranslated = arg3;
        this.noTranslated = arg4;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.message.getString();
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesTranslated, arg -> this.callback.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noTranslated, arg -> this.callback.accept(false)));
        this.messageSplit.clear();
        this.messageSplit.addAll(this.textRenderer.wrapLines(this.message, this.width - 50));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 70, 0xFFFFFF);
        int k = 90;
        for (Text lv : this.messageSplit) {
            this.drawStringWithShadow(arg, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(arg, i, j, f);
    }

    public void disableButtons(int i) {
        this.buttonEnableTimer = i;
        for (AbstractButtonWidget lv : this.buttons) {
            lv.active = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.buttonEnableTimer == 0) {
            for (AbstractButtonWidget lv : this.buttons) {
                lv.active = true;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(i, j, k);
    }
}

