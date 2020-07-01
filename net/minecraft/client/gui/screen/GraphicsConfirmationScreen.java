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
public class GraphicsConfirmationScreen
extends Screen {
    private final StringRenderable message;
    private final ImmutableList<ChoiceButton> choiceButtons;
    private List<StringRenderable> lines;
    private int field_25678;
    private int field_25679;

    protected GraphicsConfirmationScreen(Text arg, List<StringRenderable> list, ImmutableList<ChoiceButton> immutableList) {
        super(arg);
        this.message = StringRenderable.concat(list);
        this.choiceButtons = immutableList;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.message.getString();
    }

    @Override
    public void init(MinecraftClient arg, int i, int j) {
        super.init(arg, i, j);
        for (ChoiceButton lv : this.choiceButtons) {
            this.field_25679 = Math.max(this.field_25679, 20 + this.textRenderer.getWidth(lv.message) + 20);
        }
        int k = 5 + this.field_25679 + 5;
        int l = k * this.choiceButtons.size();
        this.lines = this.textRenderer.wrapLines(this.message, l);
        this.textRenderer.getClass();
        int m = this.lines.size() * 9;
        this.field_25678 = (int)((double)j / 2.0 - (double)m / 2.0);
        this.textRenderer.getClass();
        int n = this.field_25678 + m + 9 * 2;
        int o = (int)((double)i / 2.0 - (double)l / 2.0);
        for (ChoiceButton lv2 : this.choiceButtons) {
            this.addButton(new ButtonWidget(o, n, this.field_25679, 20, lv2.message, lv2.pressAction));
            o += k;
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.textRenderer.getClass();
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, this.field_25678 - 9 * 2, -1);
        int k = this.field_25678;
        for (StringRenderable lv : this.lines) {
            this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, k, -1);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(arg, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ChoiceButton {
        private final Text message;
        private final ButtonWidget.PressAction pressAction;

        public ChoiceButton(Text arg, ButtonWidget.PressAction arg2) {
            this.message = arg;
            this.pressAction = arg2;
        }
    }
}

