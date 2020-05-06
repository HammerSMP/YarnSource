/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ChatOptionsScreen
extends GameOptionsScreen {
    private static final Option[] OPTIONS = new Option[]{Option.VISIBILITY, Option.CHAT_COLOR, Option.CHAT_LINKS, Option.CHAT_LINKS_PROMPT, Option.CHAT_OPACITY, Option.TEXT_BACKGROUND_OPACITY, Option.CHAT_SCALE, Option.CHAT_LINE_SPACING, Option.CHAT_WIDTH, Option.CHAT_HEIGHT_FOCUSED, Option.SATURATION, Option.NARRATOR, Option.AUTO_SUGGESTIONS, Option.REDUCED_DEBUG_INFO};
    private AbstractButtonWidget narratorOptionButton;

    public ChatOptionsScreen(Screen arg, GameOptions arg2) {
        super(arg, arg2, new TranslatableText("options.chat.title"));
    }

    @Override
    protected void init() {
        int i = 0;
        for (Option lv : OPTIONS) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 24 * (i >> 1);
            AbstractButtonWidget lv2 = this.addButton(lv.createButton(this.client.options, j, k, 150));
            if (lv == Option.NARRATOR) {
                this.narratorOptionButton = lv2;
                lv2.active = NarratorManager.INSTANCE.isActive();
            }
            ++i;
        }
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (i + 1) / 2, 200, 20, ScreenTexts.DONE, arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    public void setNarratorMessage() {
        this.narratorOptionButton.setMessage(Option.NARRATOR.getMessage(this.gameOptions));
    }
}

