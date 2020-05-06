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
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.screen.options.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ControlsOptionsScreen
extends GameOptionsScreen {
    public KeyBinding focusedBinding;
    public long time;
    private ControlsListWidget keyBindingListWidget;
    private ButtonWidget resetButton;

    public ControlsOptionsScreen(Screen arg, GameOptions arg2) {
        super(arg, arg2, new TranslatableText("controls.title"));
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, 18, 150, 20, new TranslatableText("options.mouse_settings"), arg -> this.client.openScreen(new MouseOptionsScreen(this, this.gameOptions))));
        this.addButton(Option.AUTO_JUMP.createButton(this.gameOptions, this.width / 2 - 155 + 160, 18, 150));
        this.keyBindingListWidget = new ControlsListWidget(this, this.client);
        this.children.add(this.keyBindingListWidget);
        this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), arg -> {
            for (KeyBinding lv : this.gameOptions.keysAll) {
                lv.setKeyCode(lv.getDefaultKeyCode());
            }
            KeyBinding.updateKeysByCode();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE, arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.focusedBinding != null) {
            this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.Type.MOUSE.createFromCode(i));
            this.focusedBinding = null;
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.focusedBinding != null) {
            if (i == 256) {
                this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
            } else {
                this.gameOptions.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(i, j));
            }
            this.focusedBinding = null;
            this.time = Util.getMeasuringTimeMs();
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.keyBindingListWidget.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        boolean bl = false;
        for (KeyBinding lv : this.gameOptions.keysAll) {
            if (lv.isDefault()) continue;
            bl = true;
            break;
        }
        this.resetButton.active = bl;
        super.render(arg, i, j, f);
    }
}

