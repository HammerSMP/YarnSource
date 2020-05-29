/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StonecutterScreen
extends HandledScreen<StonecutterScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/stonecutter.png");
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;

    public StonecutterScreen(StonecutterScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
        arg.setContentsChangedListener(this::onInventoryChange);
        --this.field_25268;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        super.render(arg, i, j, f);
        this.drawMouseoverTooltip(arg, i, j);
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        this.renderBackground(arg);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = this.x;
        int l = this.y;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int m = (int)(41.0f * this.scrollAmount);
        this.drawTexture(arg, k + 119, l + 15 + m, 176 + (this.shouldScroll() ? 0 : 12), 0, 12, 15);
        int n = this.x + 52;
        int o = this.y + 14;
        int p = this.scrollOffset + 12;
        this.renderRecipeBackground(arg, i, j, n, o, p);
        this.renderRecipeIcons(n, o, p);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack arg, int i, int j) {
        super.drawMouseoverTooltip(arg, i, j);
        if (this.canCraft) {
            int k = this.x + 52;
            int l = this.y + 14;
            int m = this.scrollOffset + 12;
            List<StonecuttingRecipe> list = ((StonecutterScreenHandler)this.handler).getAvailableRecipes();
            for (int n = this.scrollOffset; n < m && n < ((StonecutterScreenHandler)this.handler).getAvailableRecipeCount(); ++n) {
                int o = n - this.scrollOffset;
                int p = k + o % 4 * 16;
                int q = l + o / 4 * 18 + 2;
                if (i < p || i >= p + 16 || j < q || j >= q + 18) continue;
                this.renderTooltip(arg, list.get(n).getOutput(), i, j);
            }
        }
    }

    private void renderRecipeBackground(MatrixStack arg, int i, int j, int k, int l, int m) {
        for (int n = this.scrollOffset; n < m && n < ((StonecutterScreenHandler)this.handler).getAvailableRecipeCount(); ++n) {
            int o = n - this.scrollOffset;
            int p = k + o % 4 * 16;
            int q = o / 4;
            int r = l + q * 18 + 2;
            int s = this.backgroundHeight;
            if (n == ((StonecutterScreenHandler)this.handler).getSelectedRecipe()) {
                s += 18;
            } else if (i >= p && j >= r && i < p + 16 && j < r + 18) {
                s += 36;
            }
            this.drawTexture(arg, p, r - 1, 0, s, 16, 18);
        }
    }

    private void renderRecipeIcons(int i, int j, int k) {
        List<StonecuttingRecipe> list = ((StonecutterScreenHandler)this.handler).getAvailableRecipes();
        for (int l = this.scrollOffset; l < k && l < ((StonecutterScreenHandler)this.handler).getAvailableRecipeCount(); ++l) {
            int m = l - this.scrollOffset;
            int n = i + m % 4 * 16;
            int o = m / 4;
            int p = j + o * 18 + 2;
            this.client.getItemRenderer().renderGuiItem(list.get(l).getOutput(), n, p);
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        this.mouseClicked = false;
        if (this.canCraft) {
            int j = this.x + 52;
            int k = this.y + 14;
            int l = this.scrollOffset + 12;
            for (int m = this.scrollOffset; m < l; ++m) {
                int n = m - this.scrollOffset;
                double f = d - (double)(j + n % 4 * 16);
                double g = e - (double)(k + n / 4 * 18);
                if (!(f >= 0.0) || !(g >= 0.0) || !(f < 16.0) || !(g < 18.0) || !((StonecutterScreenHandler)this.handler).onButtonClick(this.client.player, m)) continue;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                this.client.interactionManager.clickButton(((StonecutterScreenHandler)this.handler).syncId, m);
                return true;
            }
            j = this.x + 119;
            k = this.y + 9;
            if (d >= (double)j && d < (double)(j + 12) && e >= (double)k && e < (double)(k + 54)) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.mouseClicked && this.shouldScroll()) {
            int j = this.y + 14;
            int k = j + 54;
            this.scrollAmount = ((float)e - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * 4;
            return true;
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            this.scrollAmount = (float)((double)this.scrollAmount - f / (double)i);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * 4;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && ((StonecutterScreenHandler)this.handler).getAvailableRecipeCount() > 12;
    }

    protected int getMaxScroll() {
        return (((StonecutterScreenHandler)this.handler).getAvailableRecipeCount() + 4 - 1) / 4 - 3;
    }

    private void onInventoryChange() {
        this.canCraft = ((StonecutterScreenHandler)this.handler).canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0f;
            this.scrollOffset = 0;
        }
    }
}

