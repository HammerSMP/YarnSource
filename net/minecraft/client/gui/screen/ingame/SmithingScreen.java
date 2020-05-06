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
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SmithingScreen
extends ForgingScreen<SmithingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/smithing.png");

    public SmithingScreen(SmithingScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3, TEXTURE);
    }

    @Override
    protected void drawForeground(MatrixStack arg, int i, int j) {
        RenderSystem.disableBlend();
        this.textRenderer.draw(arg, this.title, 60.0f, 20.0f, 0x404040);
    }
}

