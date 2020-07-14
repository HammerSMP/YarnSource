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

    public CraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public CraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ScreenHandlerType.CRAFTING, syncId);
        this.context = context;
        this.player = playerInventory.player;
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.input, this.result, 0, 124, 35));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                this.addSlot(new Slot(this.input, k + j * 3, 30 + k * 18, 17 + j * 18));
            }
        }
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 84 + l * 18));
            }
        }
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 142));
        }
    }

    protected static void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        CraftingRecipe lv3;
        if (world.isClient) {
            return;
        }
        ServerPlayerEntity lv = (ServerPlayerEntity)player;
        ItemStack lv2 = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (optional.isPresent() && resultInventory.shouldCraftRecipe(world, lv, lv3 = optional.get())) {
            lv2 = lv3.craft(craftingInventory);
        }
        resultInventory.setStack(0, lv2);
        lv.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, 0, lv2));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run((arg, arg2) -> CraftingScreenHandler.updateResult(this.syncId, arg, this.player, this.input, this.result));
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        this.input.provideRecipeInputs(finder);
    }

    @Override
    public void clearCraftingSlots() {
        this.input.clear();
        this.result.clear();
    }

    @Override
    public boolean matches(Recipe<? super CraftingInventory> recipe) {
        return recipe.matches(this.input, this.player.world);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((arg2, arg3) -> this.dropInventory(player, (World)arg2, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return CraftingScreenHandler.canUse(this.context, player, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(index);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (index == 0) {
                this.context.run((arg3, arg4) -> lv3.getItem().onCraft(lv3, (World)arg3, player));
                if (!this.insertItem(lv3, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (index >= 10 && index < 46 ? !this.insertItem(lv3, 1, 10, false) && (index < 37 ? !this.insertItem(lv3, 37, 46, false) : !this.insertItem(lv3, 10, 37, false)) : !this.insertItem(lv3, 10, 46, false)) {
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
            ItemStack lv4 = lv2.onTakeItem(player, lv3);
            if (index == 0) {
                player.dropItem(lv4, false);
            }
        }
        return lv;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.result && super.canInsertIntoSlot(stack, slot);
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

