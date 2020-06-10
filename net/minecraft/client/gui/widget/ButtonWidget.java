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
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ButtonWidget
extends AbstractPressableButtonWidget {
    public static final TooltipSupplier EMPTY = (arg, arg2, i, j) -> {};
    protected final PressAction onPress;
    protected final TooltipSupplier tooltipSupplier;

    public ButtonWidget(int i, int j, int k, int l, Text arg, PressAction arg2) {
        this(i, j, k, l, arg, arg2, EMPTY);
    }

    public ButtonWidget(int i, int j, int k, int l, Text arg, PressAction arg2, TooltipSupplier arg3) {
        super(i, j, k, l, arg);
        this.onPress = arg2;
        this.tooltipSupplier = arg3;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void renderButton(MatrixStack arg, int i, int j, float f) {
        super.renderButton(arg, i, j, f);
        if (this.isHovered()) {
            this.renderToolTip(arg, i, j);
        }
    }

    @Override
    public void renderToolTip(MatrixStack arg, int i, int j) {
        this.tooltipSupplier.onTooltip(this, arg, i, j);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface TooltipSupplier {
        public void onTooltip(ButtonWidget var1, MatrixStack var2, int var3, int var4);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface PressAction {
        public void onPress(ButtonWidget var1);
    }
}

