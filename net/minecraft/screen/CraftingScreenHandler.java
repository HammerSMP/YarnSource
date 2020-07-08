/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class CraftingScreenHandler
extends AbstractRecipeScreenHandler<CraftingInventory> {
    private final CraftingInventory input = new CraftingInventory(this, 3, 3);
    private final CraftingResultInventory result = new CraftingResultInventory();
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public CraftingScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public CraftingScreenHandler(int i, PlayerInventory arg, ScreenHandlerContext arg2) {
        super(ScreenHandlerType.CRAFTING, i);
        this.context = arg2;
        this.player = arg.player;
        this.addSlot(new CraftingResultSlot(arg.player, this.input, this.result, 0, 124, 35));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                this.addSlot(new Slot(this.input, k + j * 3, 30 + k * 18, 17 + j * 18));
            }
        }
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(arg, m + l * 9 + 9, 8 + m * 18, 84 + l * 18));
            }
        }
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(arg, n, 8 + n * 18, 142));
        }
    }

    protected static void updateResult(int i, World arg, PlayerEntity arg2, CraftingInventory arg3, CraftingResultInventory arg4) {
        CraftingRecipe lv3;
        if (arg.isClient) {
            return;
        }
        ServerPlayerEntity lv = (ServerPlayerEntity)arg2;
        ItemStack lv2 = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = arg.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, arg3, arg);
        if (optional.isPresent() && arg4.shouldCraftRecipe(arg, lv, lv3 = optional.get())) {
            lv2 = lv3.craft(arg3);
        }
        arg4.setStack(0, lv2);
        lv.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(i, 0, lv2));
    }

    @Override
    public void onContentChanged(Inventory arg3) {
        this.context.run((arg, arg2) -> CraftingScreenHandler.updateResult(this.syncId, arg, this.player, this.input, this.result));
    }

    @Override
    public void populateRecipeFinder(RecipeFinder arg) {
        this.input.provideRecipeInputs(arg);
    }

    @Override
    public void clearCraftingSlots() {
        this.input.clear();
        this.result.clear();
    }

    @Override
    public boolean matches(Recipe<? super CraftingInventory> arg) {
        return arg.matches(this.input, this.player.world);
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.context.run((arg2, arg3) -> this.dropInventory(arg, (World)arg2, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return CraftingScreenHandler.canUse(this.context, arg, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i == 0) {
                this.context.run((arg3, arg4) -> lv3.getItem().onCraft(lv3, (World)arg3, arg));
                if (!this.insertItem(lv3, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (i >= 10 && i < 46 ? !this.insertItem(lv3, 1, 10, false) && (i < 37 ? !this.insertItem(lv3, 37, 46, false) : !this.insertItem(lv3, 10, 37, false)) : !this.insertItem(lv3, 10, 46, false)) {
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
            ItemStack lv4 = lv2.onTakeItem(arg, lv3);
            if (i == 0) {
                arg.dropItem(lv4, false);
            }
        }
        return lv;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack arg, Slot arg2) {
        return arg2.inventory != this.result && super.canInsertIntoSlot(arg, arg2);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return this.input.getWidth();
    }

    @Override
    public int getCraftingHeight() {
        return this.input.getHeight();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 10;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.CRAFTING;
    }
}

