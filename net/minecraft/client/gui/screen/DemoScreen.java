/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DemoScreen
extends Screen {
    private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");

    public DemoScreen() {
        super(new TranslatableText("demo.help.title"));
    }

    @Override
    protected void init() {
        int i = -16;
        this.addButton(new ButtonWidget(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, new TranslatableText("demo.help.buy"), arg -> {
            arg.active = false;
            Util.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, new TranslatableText("demo.help.later"), arg -> {
            this.client.openScreen(null);
            this.client.mouse.lockCursor();
        }));
    }

    @Override
    public void renderBackground(MatrixStack arg) {
        super.renderBackground(arg);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(DEMO_BG);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.drawTexture(arg, i, j, 0, 0, 248, 166);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        int k = (this.width - 248) / 2 + 10;
        int l = (this.height - 166) / 2 + 8;
        this.textRenderer.draw(arg, this.title, (float)k, (float)l, 0x1F1F1F);
        GameOptions lv = this.client.options;
        this.textRenderer.draw(arg, new TranslatableText("demo.help.movementShort", lv.keyForward.getBoundKeyLocalizedText(), lv.keyLeft.getBoundKeyLocalizedText(), lv.keyBack.getBoundKeyLocalizedText(), lv.keyRight.getBoundKeyLocalizedText()), (float)k, (float)(l += 12), 0x4F4F4F);
        this.textRenderer.draw(arg, new TranslatableText("demo.help.movementMouse"), (float)k, (float)(l + 12), 0x4F4F4F);
        this.textRenderer.draw(arg, new TranslatableText("demo.help.jump", lv.keyJump.getBoundKeyLocalizedText()), (float)k, (float)(l + 24), 0x4F4F4F);
        this.textRenderer.draw(arg, new TranslatableText("demo.help.inventory", lv.keyInventory.getBoundKeyLocalizedText()), (float)k, (float)(l + 36), 0x4F4F4F);
        this.textRenderer.drawTrimmed(new TranslatableText("demo.help.fullWrapped"), k, l + 68, 218, 0x1F1F1F);
        super.render(arg, i, j, f);
    }
}

