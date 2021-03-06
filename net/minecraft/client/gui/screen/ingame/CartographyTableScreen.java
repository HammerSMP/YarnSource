/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CartographyTableScreen
extends HandledScreen<CartographyTableScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/cartography_table.png");

    public CartographyTableScreen(CartographyTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.titleY -= 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        MapState lv4;
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = this.x;
        int l = this.y;
        this.drawTexture(matrices, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        Item lv = ((CartographyTableScreenHandler)this.handler).getSlot(1).getStack().getItem();
        boolean bl = lv == Items.MAP;
        boolean bl2 = lv == Items.PAPER;
        boolean bl3 = lv == Items.GLASS_PANE;
        ItemStack lv2 = ((CartographyTableScreenHandler)this.handler).getSlot(0).getStack();
        boolean bl4 = false;
        if (lv2.getItem() == Items.FILLED_MAP) {
            MapState lv3 = FilledMapItem.getMapState(lv2, this.client.world);
            if (lv3 != null) {
                if (lv3.locked) {
                    bl4 = true;
                    if (bl2 || bl3) {
                        this.drawTexture(matrices, k + 35, l + 31, this.backgroundWidth + 50, 132, 28, 21);
                    }
                }
                if (bl2 && lv3.scale >= 4) {
                    bl4 = true;
                    this.drawTexture(matrices, k + 35, l + 31, this.backgroundWidth + 50, 132, 28, 21);
                }
            }
        } else {
            lv4 = null;
        }
        this.drawMap(matrices, lv4, bl, bl2, bl3, bl4);
    }

    private void drawMap(MatrixStack arg, @Nullable MapState arg2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = this.x;
        int j = this.y;
        if (bl2 && !bl4) {
            this.drawTexture(arg, i + 67, j + 13, this.backgroundWidth, 66, 66, 66);
            this.drawMap(arg2, i + 85, j + 31, 0.226f);
        } else if (bl) {
            this.drawTexture(arg, i + 67 + 16, j + 13, this.backgroundWidth, 132, 50, 66);
            this.drawMap(arg2, i + 86, j + 16, 0.34f);
            this.client.getTextureManager().bindTexture(TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 1.0f);
            this.drawTexture(arg, i + 67, j + 13 + 16, this.backgroundWidth, 132, 50, 66);
            this.drawMap(arg2, i + 70, j + 32, 0.34f);
            RenderSystem.popMatrix();
        } else if (bl3) {
            this.drawTexture(arg, i + 67, j + 13, this.backgroundWidth, 0, 66, 66);
            this.drawMap(arg2, i + 71, j + 17, 0.45f);
            this.client.getTextureManager().bindTexture(TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 1.0f);
            this.drawTexture(arg, i + 66, j + 12, 0, this.backgroundHeight, 66, 66);
            RenderSystem.popMatrix();
        } else {
            this.drawTexture(arg, i + 67, j + 13, this.backgroundWidth, 0, 66, 66);
            this.drawMap(arg2, i + 71, j + 17, 0.45f);
        }
    }

    private void drawMap(@Nullable MapState state, int x, int y, float size) {
        if (state != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(x, y, 1.0f);
            RenderSystem.scalef(size, size, 1.0f);
            VertexConsumerProvider.Immediate lv = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            this.client.gameRenderer.getMapRenderer().draw(new MatrixStack(), lv, state, true, 0xF000F0);
            lv.draw();
            RenderSystem.popMatrix();
        }
    }
}

