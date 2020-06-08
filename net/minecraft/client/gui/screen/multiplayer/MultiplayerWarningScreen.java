/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class MultiplayerWarningScreen
extends Screen {
    private final Screen parent;
    private static final Text header = new TranslatableText("multiplayerWarning.header").formatted(Formatting.BOLD);
    private static final Text message = new TranslatableText("multiplayerWarning.message");
    private static final Text checkMessage = new TranslatableText("multiplayerWarning.check");
    private static final Text proceedText = header.shallowCopy().append("\n").append(message);
    private CheckboxWidget checkbox;
    private final List<StringRenderable> lines = Lists.newArrayList();

    public MultiplayerWarningScreen(Screen arg) {
        super(NarratorManager.EMPTY);
        this.parent = arg;
    }

    @Override
    protected void init() {
        super.init();
        this.lines.clear();
        this.lines.addAll(this.textRenderer.wrapLines(message, this.width - 50));
        this.textRenderer.getClass();
        int i = (this.lines.size() + 1) * 9;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, ScreenTexts.PROCEED, arg -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipMultiplayerWarning = true;
                this.client.options.write();
            }
            this.client.openScreen(new MultiplayerScreen(this.parent));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.parent)));
        this.checkbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, checkMessage, false);
        this.addButton(this.checkbox);
    }

    @Override
    public String getNarrationMessage() {
        return proceedText.getString();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.drawCenteredText(arg, this.textRenderer, header, this.width / 2, 30, 0xFFFFFF);
        int k = 70;
        for (StringRenderable lv : this.lines) {
            this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(arg, i, j, f);
    }
}

