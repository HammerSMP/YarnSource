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
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HorseScreen
extends HandledScreen<HorseScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
    private final HorseBaseEntity entity;
    private float mouseX;
    private float mouseY;

    public HorseScreen(HorseScreenHandler arg, PlayerInventory arg2, HorseBaseEntity arg3) {
        super(arg, arg2, arg3.getDisplayName());
        this.entity = arg3;
        this.passEvents = false;
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        AbstractDonkeyEntity lv;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.entity instanceof AbstractDonkeyEntity && (lv = (AbstractDonkeyEntity)this.entity).hasChest()) {
            this.drawTexture(arg, k + 79, l + 17, 0, this.backgroundHeight, lv.getInventoryColumns() * 18, 54);
        }
        if (this.entity.canBeSaddled()) {
            this.drawTexture(arg, k + 7, l + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
        }
        if (this.entity.canEquip()) {
            if (this.entity instanceof LlamaEntity) {
                this.drawTexture(arg, k + 7, l + 35, 36, this.backgroundHeight + 54, 18, 18);
            } else {
                this.drawTexture(arg, k + 7, l + 35, 0, this.backgroundHeight + 54, 18, 18);
            }
        }
        InventoryScreen.drawEntity(k + 51, l + 60, 17, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.entity);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.mouseX = i;
        this.mouseY = j;
        super.render(arg, i, j, f);
        this.drawMouseoverTooltip(arg, i, j);
    }
}

