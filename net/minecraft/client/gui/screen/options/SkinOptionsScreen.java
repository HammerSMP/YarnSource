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
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SkinOptionsScreen
extends GameOptionsScreen {
    public SkinOptionsScreen(Screen arg, GameOptions arg2) {
        super(arg, arg2, new TranslatableText("options.skinCustomisation.title"));
    }

    @Override
    protected void init() {
        int i = 0;
        for (PlayerModelPart lv : PlayerModelPart.values()) {
            this.addButton(new ButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.getPlayerModelPartDisplayString(lv), arg2 -> {
                this.gameOptions.togglePlayerModelPart(lv);
                arg2.setMessage(this.getPlayerModelPartDisplayString(lv));
            }));
            ++i;
        }
        this.addButton(new OptionButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, Option.MAIN_HAND, Option.MAIN_HAND.getMessage(this.gameOptions), arg -> {
            Option.MAIN_HAND.cycle(this.gameOptions, 1);
            this.gameOptions.write();
            arg.setMessage(Option.MAIN_HAND.getMessage(this.gameOptions));
            this.gameOptions.onPlayerModelPartChange();
        }));
        if (++i % 2 == 1) {
            ++i;
        }
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, ScreenTexts.DONE, arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    private Text getPlayerModelPartDisplayString(PlayerModelPart arg) {
        return arg.getOptionName().shallowCopy().append(": ").append(ScreenTexts.getToggleText(this.gameOptions.getEnabledPlayerModelParts().contains((Object)arg)));
    }
}

