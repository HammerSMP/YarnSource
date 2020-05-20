/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ConfirmChatLinkScreen
extends ConfirmScreen {
    private final Text warning;
    private final Text copy;
    private final String link;
    private final boolean drawWarning;

    public ConfirmChatLinkScreen(BooleanConsumer booleanConsumer, String string, boolean bl) {
        super(booleanConsumer, new TranslatableText(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), new LiteralText(string));
        this.yesTranslated = bl ? new TranslatableText("chat.link.open") : ScreenTexts.YES;
        this.noTranslated = bl ? ScreenTexts.CANCEL : ScreenTexts.NO;
        this.copy = new TranslatableText("chat.copy");
        this.warning = new TranslatableText("chat.link.warning");
        this.drawWarning = !bl;
        this.link = string;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.addButton(new ButtonWidget(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated, arg -> this.callback.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copy, arg -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noTranslated, arg -> this.callback.accept(false)));
    }

    public void copyToClipboard() {
        this.client.keyboard.setClipboard(this.link);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        super.render(arg, i, j, f);
        if (this.drawWarning) {
            this.drawCenteredText(arg, this.textRenderer, this.warning, this.width / 2, 110, 0xFFCCCC);
        }
    }
}

