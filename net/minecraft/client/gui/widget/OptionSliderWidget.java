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
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.LiteralText;

@Environment(value=EnvType.CLIENT)
public abstract class OptionSliderWidget
extends SliderWidget {
    protected final GameOptions options;

    protected OptionSliderWidget(GameOptions arg, int i, int j, int k, int l, double d) {
        super(i, j, k, l, LiteralText.EMPTY, d);
        this.options = arg;
    }
}

