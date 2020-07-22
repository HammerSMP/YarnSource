/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class OptionButtonWidget
extends ButtonWidget {
    private final Option option;

    public OptionButtonWidget(int x, int y, int width, int height, Option option, Text arg2, ButtonWidget.PressAction pressAction) {
        super(x, y, width, height, arg2, pressAction);
        this.option = option;
    }

    public Option getOption() {
        return this.option;
    }
}

