/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DisconnectedRealmsScreen
extends RealmsScreen {
    private final String title;
    private final Text reason;
    @Nullable
    private List<StringRenderable> lines;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen parent, String titleTranslationKey, Text reason) {
        this.parent = parent;
        this.title = I18n.translate(titleTranslationKey, new Object[0]);
        this.reason = reason;
    }

    @Override
    public void init() {
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.setConnectedToRealms(false);
        lv.getResourcePackDownloader().clear();
        Realms.narrateNow(this.title + ": " + this.reason.getString());
        this.lines = this.textRenderer.wrapLines(this.reason, this.width - 50);
        this.textRenderer.getClass();
        this.textHeight = this.lines.size() * 9;
        this.textRenderer.getClass();
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20, ScreenTexts.BACK, arg2 -> lv.openScreen(this.parent)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            MinecraftClient.getInstance().openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.textRenderer.getClass();
        this.drawCenteredString(matrices, this.textRenderer, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 0xAAAAAA);
        int k = this.height / 2 - this.textHeight / 2;
        if (this.lines != null) {
            for (StringRenderable lv : this.lines) {
                this.drawCenteredText(matrices, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
                this.textRenderer.getClass();
                k += 9;
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

