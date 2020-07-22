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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SoundOptionsScreen
extends GameOptionsScreen {
    public SoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, new TranslatableText("options.sounds.title"));
    }

    @Override
    protected void init() {
        int i = 0;
        this.addButton(new SoundSliderWidget(this.client, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, 310));
        i += 2;
        for (SoundCategory lv : SoundCategory.values()) {
            if (lv == SoundCategory.MASTER) continue;
            this.addButton(new SoundSliderWidget(this.client, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), lv, 150));
            ++i;
        }
        this.addButton(new OptionButtonWidget(this.width / 2 - 75, this.height / 6 - 12 + 24 * (++i >> 1), 150, 20, Option.SUBTITLES, Option.SUBTITLES.getDisplayString(this.gameOptions), arg -> {
            Option.SUBTITLES.set(this.client.options);
            arg.setMessage(Option.SUBTITLES.getDisplayString(this.client.options));
            this.client.options.write();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, ScreenTexts.DONE, arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

