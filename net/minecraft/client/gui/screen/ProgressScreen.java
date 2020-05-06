/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;

@Environment(value=EnvType.CLIENT)
public class ProgressScreen
extends Screen
implements ProgressListener {
    @Nullable
    private Text title;
    @Nullable
    private Text task;
    private int progress;
    private boolean done;

    public ProgressScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void method_15412(Text arg) {
        this.method_15413(arg);
    }

    @Override
    public void method_15413(Text arg) {
        this.title = arg;
        this.method_15414(new TranslatableText("progress.working"));
    }

    @Override
    public void method_15414(Text arg) {
        this.task = arg;
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int i) {
        this.progress = i;
    }

    @Override
    public void setDone() {
        this.done = true;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (this.done) {
            if (!this.client.isConnectedToRealms()) {
                this.client.openScreen(null);
            }
            return;
        }
        this.renderBackground(arg);
        if (this.title != null) {
            this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 70, 0xFFFFFF);
        }
        if (this.task != null && this.progress != 0) {
            this.drawStringWithShadow(arg, this.textRenderer, new LiteralText("").append(this.task).append(" " + this.progress + "%"), this.width / 2, 90, 0xFFFFFF);
        }
        super.render(arg, i, j, f);
    }
}

