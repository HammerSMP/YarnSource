/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DialogScreen
extends Screen {
    private final StringRenderable message;
    private final ImmutableList<ChoiceButton> choiceButtons;
    private List<StringRenderable> lines;
    private int linesY;
    private int buttonWidth;

    protected DialogScreen(Text title, List<StringRenderable> messageParts, ImmutableList<ChoiceButton> choiceButtons) {
        super(title);
        this.message = StringRenderable.concat(messageParts);
        this.choiceButtons = choiceButtons;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.message.getString();
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
        for (ChoiceButton lv : this.choiceButtons) {
            this.buttonWidth = Math.max(this.buttonWidth, 20 + this.textRenderer.getWidth(lv.message) + 20);
        }
        int k = 5 + this.buttonWidth + 5;
        int l = k * this.choiceButtons.size();
        this.lines = this.textRenderer.wrapLines(this.message, l);
        this.textRenderer.getClass();
        int m = this.lines.size() * 9;
        this.linesY = (int)((double)height / 2.0 - (double)m / 2.0);
        this.textRenderer.getClass();
        int n = this.linesY + m + 9 * 2;
        int o = (int)((double)width / 2.0 - (double)l / 2.0);
        for (ChoiceButton lv2 : this.choiceButtons) {
            this.addButton(new ButtonWidget(o, n, this.buttonWidth, 20, lv2.message, lv2.pressAction));
            o += k;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.textRenderer.getClass();
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.linesY - 9 * 2, -1);
        int k = this.linesY;
        for (StringRenderable lv : this.lines) {
            this.drawCenteredText(matrices, this.textRenderer, lv, this.width / 2, k, -1);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ChoiceButton {
        private final Text message;
        private final ButtonWidget.PressAction pressAction;

        public ChoiceButton(Text message, ButtonWidget.PressAction pressAction) {
            this.message = message;
            this.pressAction = pressAction;
        }
    }
}

