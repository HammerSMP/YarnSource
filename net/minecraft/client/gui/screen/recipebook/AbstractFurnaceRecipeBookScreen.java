/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen
extends RecipeBookWidget {
    private Iterator<Item> fuelIterator;
    private Set<Item> fuels;
    private Slot outputSlot;
    private Item currentItem;
    private float frameTime;

    @Override
    protected boolean toggleFilteringCraftable() {
        boolean bl = !this.isFilteringCraftable();
        this.setFilteringCraftable(bl);
        return bl;
    }

    protected abstract boolean isFilteringCraftable();

    protected abstract void setFilteringCraftable(boolean var1);

    @Override
    public boolean isOpen() {
        return this.isGuiOpen();
    }

    protected abstract boolean isGuiOpen();

    @Override
    protected void setOpen(boolean bl) {
        this.setGuiOpen(bl);
        if (!bl) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }

    protected abstract void setGuiOpen(boolean var1);

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, TEXTURE);
    }

    @Override
    protected Text getCraftableButtonText() {
        return this.toggleCraftableButton.isToggled() ? this.getToggleCraftableButtonText() : new TranslatableText("gui.recipebook.toggleRecipes.all");
    }

    protected abstract Text getToggleCraftableButtonText();

    @Override
    public void slotClicked(@Nullable Slot arg) {
        super.slotClicked(arg);
        if (arg != null && arg.id < this.craftingScreenHandler.getCraftingSlotCount()) {
            this.outputSlot = null;
        }
    }

    @Override
    public void showGhostRecipe(Recipe<?> arg, List<Slot> list) {
        ItemStack lv = arg.getOutput();
        this.ghostSlots.setRecipe(arg);
        this.ghostSlots.addSlot(Ingredient.ofStacks(lv), list.get((int)2).x, list.get((int)2).y);
        DefaultedList<Ingredient> lv2 = arg.getPreviewInputs();
        this.outputSlot = list.get(1);
        if (this.fuels == null) {
            this.fuels = this.getAllowedFuels();
        }
        this.fuelIterator = this.fuels.iterator();
        this.currentItem = null;
        Iterator iterator = lv2.iterator();
        for (int i = 0; i < 2; ++i) {
            if (!iterator.hasNext()) {
                return;
            }
            Ingredient lv3 = (Ingredient)iterator.next();
            if (lv3.isEmpty()) continue;
            Slot lv4 = list.get(i);
            this.ghostSlots.addSlot(lv3, lv4.x, lv4.y);
        }
    }

    protected abstract Set<Item> getAllowedFuels();

    @Override
    public void drawGhostSlots(MatrixStack arg, int i, int j, boolean bl, float f) {
        super.drawGhostSlots(arg, i, j, bl, f);
        if (this.outputSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.frameTime += f;
        }
        int k = this.outputSlot.x + i;
        int l = this.outputSlot.y + j;
        DrawableHelper.fill(arg, k, l, k + 16, l + 16, 0x30FF0000);
        this.client.getItemRenderer().renderInGuiWithOverrides(this.client.player, this.getItem().getStackForRender(), k, l);
        RenderSystem.depthFunc(516);
        DrawableHelper.fill(arg, k, l, k + 16, l + 16, 0x30FFFFFF);
        RenderSystem.depthFunc(515);
    }

    private Item getItem() {
        if (this.currentItem == null || this.frameTime > 30.0f) {
            this.frameTime = 0.0f;
            if (this.fuelIterator == null || !this.fuelIterator.hasNext()) {
                if (this.fuels == null) {
                    this.fuels = this.getAllowedFuels();
                }
                this.fuelIterator = this.fuels.iterator();
            }
            this.currentItem = this.fuelIterator.next();
        }
        return this.currentItem;
    }
}

