/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class DatapackFailureScreen
extends Screen {
    private final List<StringRenderable> wrappedText = Lists.newArrayList();
    private final Runnable field_25452;

    public DatapackFailureScreen(Runnable runnable) {
        super(new TranslatableText("datapackFailure.title"));
        this.field_25452 = runnable;
    }

    @Override
    protected void init() {
        super.init();
        this.wrappedText.clear();
        this.wrappedText.addAll(this.textRenderer.wrapLines(this.getTitle(), this.width - 50));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableText("datapackFailure.safeMode"), arg -> this.field_25452.run()));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableText("gui.toTitle"), arg -> this.client.openScreen(null)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        int k = 70;
        for (StringRenderable lv : this.wrappedText) {
            this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(arg, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

