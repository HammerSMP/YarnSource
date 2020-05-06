/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.FurnaceInputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class AbstractFurnaceScreenHandler
extends AbstractRecipeScreenHandler<Inventory> {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractFurnaceScreenHandler(ScreenHandlerType<?> arg, RecipeType<? extends AbstractCookingRecipe> arg2, int i, PlayerInventory arg3) {
        this(arg, arg2, i, arg3, new BasicInventory(3), new ArrayPropertyDelegate(4));
    }

    protected AbstractFurnaceScreenHandler(ScreenHandlerType<?> arg, RecipeType<? extends AbstractCookingRecipe> arg2, int i, PlayerInventory arg3, Inventory arg4, PropertyDelegate arg5) {
        super(arg, i);
        this.recipeType = arg2;
        AbstractFurnaceScreenHandler.checkSize(arg4, 3);
        AbstractFurnaceScreenHandler.checkDataCount(arg5, 4);
        this.inventory = arg4;
        this.propertyDelegate = arg5;
        this.world = arg3.player.world;
        this.addSlot(new Slot(arg4, 0, 56, 17));
        this.addSlot(new FurnaceFuelSlot(this, arg4, 1, 56, 53));
        this.addSlot(new FurnaceOutputSlot(arg3.player, arg4, 2, 116, 35));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(arg3, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(arg3, l, 8 + l * 18, 142));
        }
        this.addProperties(arg5);
    }

    @Override
    public void populateRecipeFinder(RecipeFinder arg) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider)((Object)this.inventory)).provideRecipeInputs(arg);
        }
    }

    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
    }

    @Override
    public void fillInputSlots(boolean bl, Recipe<?> arg, ServerPlayerEntity arg2) {
        new FurnaceInputSlotFiller<Inventory>(this).fillInputSlots(arg2, arg, bl);
    }

    @Override
    public boolean matches(Recipe<? super Inventory> arg) {
        return arg.matches(this.inventory, this.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 2;
    }

    @Override
    public int getCraftingWidth() {
        return 1;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 3;
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return this.inventory.canPlayerUse(arg);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i == 2) {
                if (!this.insertItem(lv3, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (i == 1 || i == 0 ? !this.insertItem(lv3, 3, 39, false) : (this.isSmeltable(lv3) ? !this.insertItem(lv3, 0, 1, false) : (this.isFuel(lv3) ? !this.insertItem(lv3, 1, 2, false) : (i >= 3 && i < 30 ? !this.insertItem(lv3, 30, 39, false) : i >= 30 && i < 39 && !this.insertItem(lv3, 3, 30, false))))) {
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            } else {
                lv2.markDirty();
            }
            if (lv3.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
            lv2.onTakeItem(arg, lv3);
        }
        return lv;
    }

    protected boolean isSmeltable(ItemStack arg) {
        return this.world.getRecipeManager().getFirstMatch(this.recipeType, new BasicInventory(arg), this.world).isPresent();
    }

    protected boolean isFuel(ItemStack arg) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public int getCookProgress() {
        int i = this.propertyDelegate.get(2);
        int j = this.propertyDelegate.get(3);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 24 / j;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.propertyDelegate.get(0) * 13 / i;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }
}

