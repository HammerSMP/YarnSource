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
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class NoticeScreen
extends Screen {
    private final Runnable actionHandler;
    protected final Text notice;
    private final List<StringRenderable> noticeLines = Lists.newArrayList();
    protected final Text buttonString;
    private int field_2347;

    public NoticeScreen(Runnable runnable, Text arg, Text arg2) {
        this(runnable, arg, arg2, ScreenTexts.BACK);
    }

    public NoticeScreen(Runnable runnable, Text arg, Text arg2, Text arg3) {
        super(arg);
        this.actionHandler = runnable;
        this.notice = arg2;
        this.buttonString = arg3;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonString, arg -> this.actionHandler.run()));
        this.noticeLines.clear();
        this.noticeLines.addAll(this.textRenderer.wrapLines(this.notice, this.width - 50));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 70, 0xFFFFFF);
        int k = 90;
        for (StringRenderable lv : this.noticeLines) {
            this.drawCenteredText(arg, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(arg, i, j, f);
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.field_2347 == 0) {
            for (AbstractButtonWidget lv : this.buttons) {
                lv.active = true;
            }
        }
    }
}

