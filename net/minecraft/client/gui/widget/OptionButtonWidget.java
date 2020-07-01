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

    public OptionButtonWidget(int i, int j, int k, int l, Option arg, Text arg2, ButtonWidget.PressAction arg3) {
        super(i, j, k, l, arg2, arg3);
        this.option = arg;
    }

    public Option getOption() {
        return this.option;
    }
}

