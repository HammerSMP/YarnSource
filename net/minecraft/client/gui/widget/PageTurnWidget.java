/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;

@Environment(value=EnvType.CLIENT)
public class PageTurnWidget
extends ButtonWidget {
    private final boolean isNextPageButton;
    private final boolean playPageTurnSound;

    public PageTurnWidget(int i, int j, boolean bl, ButtonWidget.PressAction arg, boolean bl2) {
        super(i, j, 23, 13, LiteralText.EMPTY, arg);
        this.isNextPageButton = bl;
        this.playPageTurnSound = bl2;
    }

    @Override
    public void renderButton(MatrixStack arg, int i, int j, float f) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
        int k = 0;
        int l = 192;
        if (this.isHovered()) {
            k += 23;
        }
        if (!this.isNextPageButton) {
            l += 13;
        }
        this.drawTexture(arg, this.x, this.y, k, l, 23, 13);
    }

    @Override
    public void playDownSound(SoundManager arg) {
        if (this.playPageTurnSound) {
            arg.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f));
        }
    }
}

