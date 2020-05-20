/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SpectatorHud
extends DrawableHelper
implements SpectatorMenuCloseCallback {
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
    public static final Identifier SPECTATOR_TEX = new Identifier("textures/gui/spectator_widgets.png");
    private final MinecraftClient client;
    private long lastInteractionTime;
    private SpectatorMenu spectatorMenu;

    public SpectatorHud(MinecraftClient arg) {
        this.client = arg;
    }

    public void selectSlot(int i) {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.spectatorMenu != null) {
            this.spectatorMenu.useCommand(i);
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }

    private float getSpectatorMenuHeight() {
        long l = this.lastInteractionTime - Util.getMeasuringTimeMs() + 5000L;
        return MathHelper.clamp((float)l / 2000.0f, 0.0f, 1.0f);
    }

    public void render(MatrixStack arg, float f) {
        if (this.spectatorMenu == null) {
            return;
        }
        float g = this.getSpectatorMenuHeight();
        if (g <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        int i = this.client.getWindow().getScaledWidth() / 2;
        int j = this.getZOffset();
        this.setZOffset(-90);
        int k = MathHelper.floor((float)this.client.getWindow().getScaledHeight() - 22.0f * g);
        SpectatorMenuState lv = this.spectatorMenu.getCurrentState();
        this.renderSpectatorMenu(arg, g, i, k, lv);
        this.setZOffset(j);
    }

    protected void renderSpectatorMenu(MatrixStack arg, float f, int i, int j, SpectatorMenuState arg2) {
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f);
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        this.drawTexture(arg, i - 91, j, 0, 0, 182, 22);
        if (arg2.getSelectedSlot() >= 0) {
            this.drawTexture(arg, i - 91 - 1 + arg2.getSelectedSlot() * 20, j - 1, 0, 22, 24, 22);
        }
        for (int k = 0; k < 9; ++k) {
            this.renderSpectatorCommand(arg, k, this.client.getWindow().getScaledWidth() / 2 - 90 + k * 20 + 2, j + 3, f, arg2.getCommand(k));
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void renderSpectatorCommand(MatrixStack arg, int i, int j, float f, float g, SpectatorMenuCommand arg2) {
        this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
        if (arg2 != SpectatorMenu.BLANK_COMMAND) {
            int k = (int)(g * 255.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(j, f, 0.0f);
            float h = arg2.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.color4f(h, h, h, g);
            arg2.renderIcon(arg, h, k);
            RenderSystem.popMatrix();
            if (k > 3 && arg2.isEnabled()) {
                Text lv = this.client.options.keysHotbar[i].getBoundKeyLocalizedText();
                this.client.textRenderer.drawWithShadow(arg, lv, (float)(j + 19 - 2 - this.client.textRenderer.getWidth(lv)), f + 6.0f + 3.0f, 0xFFFFFF + (k << 24));
            }
        }
    }

    public void render(MatrixStack arg) {
        int i = (int)(this.getSpectatorMenuHeight() * 255.0f);
        if (i > 3 && this.spectatorMenu != null) {
            Text lv2;
            SpectatorMenuCommand lv = this.spectatorMenu.getSelectedCommand();
            Text text = lv2 = lv == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt() : lv.getName();
            if (lv2 != null) {
                int j = (this.client.getWindow().getScaledWidth() - this.client.textRenderer.getWidth(lv2)) / 2;
                int k = this.client.getWindow().getScaledHeight() - 35;
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.client.textRenderer.drawWithShadow(arg, lv2, (float)j, (float)k, 0xFFFFFF + (i << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public void close(SpectatorMenu arg) {
        this.spectatorMenu = null;
        this.lastInteractionTime = 0L;
    }

    public boolean isOpen() {
        return this.spectatorMenu != null;
    }

    public void cycleSlot(double d) {
        int i = this.spectatorMenu.getSelectedSlot() + (int)d;
        while (!(i < 0 || i > 8 || this.spectatorMenu.getCommand(i) != SpectatorMenu.BLANK_COMMAND && this.spectatorMenu.getCommand(i).isEnabled())) {
            i = (int)((double)i + d);
        }
        if (i >= 0 && i <= 8) {
            this.spectatorMenu.useCommand(i);
            this.lastInteractionTime = Util.getMeasuringTimeMs();
        }
    }

    public void useSelectedCommand() {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.isOpen()) {
            int i = this.spectatorMenu.getSelectedSlot();
            if (i != -1) {
                this.spectatorMenu.useCommand(i);
            }
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }
}

