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
    public static final class_5316 field_25035 = (arg, arg2, i, j) -> {};
    protected final PressAction onPress;
    protected final class_5316 field_25036;

    public ButtonWidget(int i, int j, int k, int l, Text arg, PressAction arg2) {
        this(i, j, k, l, arg, arg2, field_25035);
    }

    public ButtonWidget(int i, int j, int k, int l, Text arg, PressAction arg2, class_5316 arg3) {
        super(i, j, k, l, arg);
        this.onPress = arg2;
        this.field_25036 = arg3;
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
        this.field_25036.onTooltip(this, arg, i, j);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface class_5316 {
        public void onTooltip(ButtonWidget var1, MatrixStack var2, int var3, int var4);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface PressAction {
        public void onPress(ButtonWidget var1);
    }
}

