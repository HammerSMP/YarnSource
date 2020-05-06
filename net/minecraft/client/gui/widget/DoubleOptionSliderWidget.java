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
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

@Environment(value=EnvType.CLIENT)
public class DoubleOptionSliderWidget
extends OptionSliderWidget {
    private final DoubleOption option;

    public DoubleOptionSliderWidget(GameOptions arg, int i, int j, int k, int l, DoubleOption arg2) {
        super(arg, i, j, k, l, (double)((float)arg2.getRatio(arg2.get(arg))));
        this.option = arg2;
        this.updateMessage();
    }

    @Override
    protected void applyValue() {
        this.option.set(this.options, this.option.getValue(this.value));
        this.options.write();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.option.getDisplayString(this.options));
    }
}

