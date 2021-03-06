/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class BackupPromptScreen
extends Screen {
    @Nullable
    private final Screen parent;
    protected final Callback callback;
    private final Text subtitle;
    private final boolean showEraseCacheCheckbox;
    private final List<StringRenderable> wrappedText = Lists.newArrayList();
    private CheckboxWidget eraseCacheCheckbox;

    public BackupPromptScreen(@Nullable Screen parent, Callback callback, Text title, Text subtitle, boolean showEraseCacheCheckBox) {
        super(title);
        this.parent = parent;
        this.callback = callback;
        this.subtitle = subtitle;
        this.showEraseCacheCheckbox = showEraseCacheCheckBox;
    }

    @Override
    protected void init() {
        super.init();
        this.wrappedText.clear();
        this.wrappedText.addAll(this.textRenderer.wrapLines(this.subtitle, this.width - 50));
        this.textRenderer.getClass();
        int i = (this.wrappedText.size() + 1) * 9;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, new TranslatableText("selectWorld.backupJoinConfirmButton"), arg -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked())));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, new TranslatableText("selectWorld.backupJoinSkipButton"), arg -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 80, 124 + i, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.eraseCacheCheckbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, new TranslatableText("selectWorld.backupEraseCache"), false);
        if (this.showEraseCacheCheckbox) {
            this.addButton(this.eraseCacheCheckbox);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 50, 0xFFFFFF);
        int k = 70;
        for (StringRenderable lv : this.wrappedText) {
            this.drawCenteredText(matrices, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
            this.textRenderer.getClass();
            k += 9;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Callback {
        public void proceed(boolean var1, boolean var2);
    }
}

