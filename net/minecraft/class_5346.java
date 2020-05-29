/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelInfo;

@Environment(value=EnvType.CLIENT)
public class class_5346
extends Screen {
    private final String field_25264;
    private final List<class_5348> field_25265 = Lists.newArrayList();
    @Nullable
    private final LevelInfo field_25266;

    public class_5346(String string, @Nullable LevelInfo arg) {
        super(new TranslatableText("datapackFailure.title"));
        this.field_25264 = string;
        this.field_25266 = arg;
    }

    @Override
    protected void init() {
        super.init();
        this.field_25265.clear();
        this.field_25265.addAll(this.textRenderer.wrapLines(this.getTitle(), this.width - 50));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableText("datapackFailure.safeMode"), arg -> this.client.method_29337(this.field_25264, this.field_25266, true)));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableText("gui.toTitle"), arg -> this.client.openScreen(null)));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        int k = 70;
        for (class_5348 lv : this.field_25265) {
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

