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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class ForgingScreen<T extends ForgingScreenHandler>
extends HandledScreen<T>
implements ScreenHandlerListener {
    private Identifier texture;

    public ForgingScreen(T arg, PlayerInventory arg2, Text arg3, Identifier arg4) {
        super(arg, arg2, arg3);
        this.texture = arg4;
    }

    protected void setup() {
    }

    @Override
    protected void init() {
        super.init();
        this.setup();
        ((ForgingScreenHandler)this.handler).addListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        ((ForgingScreenHandler)this.handler).removeListener(this);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        super.render(arg, i, j, f);
        RenderSystem.disableBlend();
        this.renderForeground(arg, i, j, f);
        this.drawMouseoverTooltip(arg, i, j);
    }

    protected void renderForeground(MatrixStack arg, int i, int j, float f) {
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(this.texture);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(arg, k + 59, l + 20, 0, this.backgroundHeight + (((ForgingScreenHandler)this.handler).getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((((ForgingScreenHandler)this.handler).getSlot(0).hasStack() || ((ForgingScreenHandler)this.handler).getSlot(1).hasStack()) && !((ForgingScreenHandler)this.handler).getSlot(2).hasStack()) {
            this.drawTexture(arg, k + 99, l + 45, this.backgroundWidth, 0, 28, 21);
        }
    }

    @Override
    public void onHandlerRegistered(ScreenHandler arg, DefaultedList<ItemStack> arg2) {
        this.onSlotUpdate(arg, 0, arg.getSlot(0).getStack());
    }

    @Override
    public void onPropertyUpdate(ScreenHandler arg, int i, int j) {
    }

    @Override
    public void onSlotUpdate(ScreenHandler arg, int i, ItemStack arg2) {
    }
}

