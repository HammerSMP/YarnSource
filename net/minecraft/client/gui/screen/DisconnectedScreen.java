/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    private final Text reason;
    @Nullable
    private List<class_5348> reasonFormatted;
    private final Screen parent;
    private int reasonHeight;

    public DisconnectedScreen(Screen arg, String string, Text arg2) {
        super(new TranslatableText(string));
        this.parent = arg;
        this.reason = arg2;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.reasonFormatted = this.textRenderer.wrapLines(this.reason, this.width - 50);
        this.textRenderer.getClass();
        this.reasonHeight = this.reasonFormatted.size() * 9;
        this.textRenderer.getClass();
        this.addButton(new ButtonWidget(this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30), 200, 20, new TranslatableText("gui.toMenu"), arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.textRenderer.getClass();
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.reasonHeight / 2 - 9 * 2, 0xAAAAAA);
        int k = this.height / 2 - this.reasonHeight / 2;
        if (this.reasonFormatted != null) {
            for (class_5348 lv : this.reasonFormatted) {
                this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
                this.textRenderer.getClass();
                k += 9;
            }
        }
        super.render(arg, i, j, f);
    }
}

