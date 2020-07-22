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

    public ForgingScreen(T handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title);
        this.texture = texture;
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        this.renderForeground(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void renderForeground(MatrixStack arg, int mouseY, int j, float f) {
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(this.texture);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(matrices, k + 59, l + 20, 0, this.backgroundHeight + (((ForgingScreenHandler)this.handler).getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((((ForgingScreenHandler)this.handler).getSlot(0).hasStack() || ((ForgingScreenHandler)this.handler).getSlot(1).hasStack()) && !((ForgingScreenHandler)this.handler).getSlot(2).hasStack()) {
            this.drawTexture(matrices, k + 99, l + 45, this.backgroundWidth, 0, 28, 21);
        }
    }

    @Override
    public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks) {
        this.onSlotUpdate(handler, 0, handler.getSlot(0).getStack());
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
    }
}

