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
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceScreenHandler>
extends HandledScreen<T>
implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    public final AbstractFurnaceRecipeBookScreen recipeBook;
    private boolean narrow;
    private final Identifier background;

    public AbstractFurnaceScreen(T arg, AbstractFurnaceRecipeBookScreen arg2, PlayerInventory arg3, Text arg4, Identifier arg5) {
        super(arg, arg3, arg4);
        this.recipeBook = arg2;
        this.background = arg5;
    }

    @Override
    public void init() {
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, (AbstractRecipeScreenHandler)this.handler);
        this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
        this.addButton(new TexturedButtonWidget(this.x + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, arg -> {
            this.recipeBook.reset(this.narrow);
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
            ((TexturedButtonWidget)arg).setPos(this.x + 20, this.height / 2 - 49);
        }));
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBook.update();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(arg, f, i, j);
            this.recipeBook.render(arg, i, j, f);
        } else {
            this.recipeBook.render(arg, i, j, f);
            super.render(arg, i, j, f);
            this.recipeBook.drawGhostSlots(arg, this.x, this.y, true, f);
        }
        this.drawMouseoverTooltip(arg, i, j);
        this.recipeBook.drawTooltip(arg, this.x, this.y, i, j);
    }

    @Override
    protected void drawForeground(MatrixStack arg, int i, int j) {
        this.textRenderer.draw(arg, this.title, (float)(this.backgroundWidth / 2 - this.textRenderer.getStringWidth(this.title) / 2), 6.0f, 0x404040);
        this.textRenderer.draw(arg, this.playerInventory.getDisplayName(), 8.0f, (float)(this.backgroundHeight - 96 + 2), 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(this.background);
        int k = this.x;
        int l = this.y;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (((AbstractFurnaceScreenHandler)this.handler).isBurning()) {
            int m = ((AbstractFurnaceScreenHandler)this.handler).getFuelProgress();
            this.drawTexture(arg, k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
        }
        int n = ((AbstractFurnaceScreenHandler)this.handler).getCookProgress();
        this.drawTexture(arg, k + 79, l + 34, 176, 14, n + 1, 16);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.recipeBook.mouseClicked(d, e, i)) {
            return true;
        }
        if (this.narrow && this.recipeBook.isOpen()) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void onMouseClick(Slot arg, int i, int j, SlotActionType arg2) {
        super.onMouseClick(arg, i, j, arg2);
        this.recipeBook.slotClicked(arg);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.recipeBook.keyPressed(i, j, k)) {
            return false;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(d, e, this.x, this.y, this.backgroundWidth, this.backgroundHeight, k) && bl;
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.recipeBook.charTyped(c, i)) {
            return true;
        }
        return super.charTyped(c, i);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }

    @Override
    public void removed() {
        this.recipeBook.close();
        super.removed();
    }
}

