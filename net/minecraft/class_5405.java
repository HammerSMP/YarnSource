/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class class_5405
extends Screen {
    private final StringRenderable field_25675;
    private final ImmutableList<class_5406> field_25676;
    private List<StringRenderable> field_25677;
    private int field_25678;
    private int field_25679;

    protected class_5405(Text arg, List<StringRenderable> list, ImmutableList<class_5406> immutableList) {
        super(arg);
        this.field_25675 = StringRenderable.concat(list);
        this.field_25676 = immutableList;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.field_25675.getString();
    }

    @Override
    public void init(MinecraftClient arg, int i, int j) {
        super.init(arg, i, j);
        for (class_5406 lv : this.field_25676) {
            this.field_25679 = Math.max(this.field_25679, 20 + this.textRenderer.getWidth(lv.field_25680) + 20);
        }
        int k = 5 + this.field_25679 + 5;
        int l = k * this.field_25676.size();
        this.field_25677 = this.textRenderer.wrapLines(this.field_25675, l);
        this.textRenderer.getClass();
        int m = this.field_25677.size() * 9;
        this.field_25678 = (int)((double)j / 2.0 - (double)m / 2.0);
        this.textRenderer.getClass();
        int n = this.field_25678 + m + 9 * 2;
        int o = (int)((double)i / 2.0 - (double)l / 2.0);
        for (class_5406 lv2 : this.field_25676) {
            this.addButton(new ButtonWidget(o, n, this.field_25679, 20, lv2.field_25680, lv2.field_25681));
            o += k;
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.textRenderer.getClass();
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, this.field_25678 - 9 * 2, -1);
        int k = this.field_25678;
        for (StringRenderable lv : this.field_25677) {
            this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, k, -1);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(arg, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class class_5406 {
        private final Text field_25680;
        private final ButtonWidget.PressAction field_25681;

        public class_5406(Text arg, ButtonWidget.PressAction arg2) {
            this.field_25680 = arg;
            this.field_25681 = arg2;
        }
    }
}

