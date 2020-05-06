/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GenericContainerScreen
extends HandledScreen<GenericContainerScreenHandler>
implements ScreenHandlerProvider<GenericContainerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private final int rows;

    public GenericContainerScreen(GenericContainerScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
        this.passEvents = false;
        int i = 222;
        int j = 114;
        this.rows = arg.getRows();
        this.backgroundHeight = 114 + this.rows * 18;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        super.render(arg, i, j, f);
        this.drawMouseoverTooltip(arg, i, j);
    }

    @Override
    protected void drawForeground(MatrixStack arg, int i, int j) {
        this.textRenderer.draw(arg, this.title, 8.0f, 6.0f, 0x404040);
        this.textRenderer.draw(arg, this.playerInventory.getDisplayName(), 8.0f, (float)(this.backgroundHeight - 96 + 2), 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        this.drawTexture(arg, k, l + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }
}

