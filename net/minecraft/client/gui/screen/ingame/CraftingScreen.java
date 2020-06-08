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
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CraftingScreen
extends HandledScreen<CraftingScreenHandler>
implements RecipeBookProvider {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/crafting_table.png");
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean isNarrow;

    public CraftingScreen(CraftingScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
    }

    @Override
    protected void init() {
        super.init();
        this.isNarrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.isNarrow, (AbstractRecipeScreenHandler)this.handler);
        this.x = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.backgroundWidth);
        this.children.add(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
        this.addButton(new TexturedButtonWidget(this.x + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, arg -> {
            this.recipeBook.reset(this.isNarrow);
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.backgroundWidth);
            ((TexturedButtonWidget)arg).setPos(this.x + 5, this.height / 2 - 49);
        }));
        this.titleX = 29;
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBook.update();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        if (this.recipeBook.isOpen() && this.isNarrow) {
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
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = this.x;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
        return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.recipeBook.mouseClicked(d, e, i)) {
            return true;
        }
        if (this.isNarrow && this.recipeBook.isOpen()) {
            return true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(d, e, this.x, this.y, this.backgroundWidth, this.backgroundHeight, k) && bl;
    }

    @Override
    protected void onMouseClick(Slot arg, int i, int j, SlotActionType arg2) {
        super.onMouseClick(arg, i, j, arg2);
        this.recipeBook.slotClicked(arg);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public void removed() {
        this.recipeBook.close();
        super.removed();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}

