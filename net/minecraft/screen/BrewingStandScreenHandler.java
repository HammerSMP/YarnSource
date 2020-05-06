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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class BrewingStandScreenHandler
extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final Slot ingredientSlot;

    public BrewingStandScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, new BasicInventory(5), new ArrayPropertyDelegate(2));
    }

    public BrewingStandScreenHandler(int i, PlayerInventory arg, Inventory arg2, PropertyDelegate arg3) {
        super(ScreenHandlerType.BREWING_STAND, i);
        BrewingStandScreenHandler.checkSize(arg2, 5);
        BrewingStandScreenHandler.checkDataCount(arg3, 2);
        this.inventory = arg2;
        this.propertyDelegate = arg3;
        this.addSlot(new PotionSlot(arg2, 0, 56, 51));
        this.addSlot(new PotionSlot(arg2, 1, 79, 58));
        this.addSlot(new PotionSlot(arg2, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new IngredientSlot(arg2, 3, 79, 17));
        this.addSlot(new FuelSlot(arg2, 4, 17, 17));
        this.addProperties(arg3);
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(arg, l, 8 + l * 18, 142));
        }
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
            if (i >= 0 && i <= 2 || i == 3 || i == 4) {
                if (!this.insertItem(lv3, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (FuelSlot.matches(lv) ? this.insertItem(lv3, 4, 5, false) || this.ingredientSlot.canInsert(lv3) && !this.insertItem(lv3, 3, 4, false) : (this.ingredientSlot.canInsert(lv3) ? !this.insertItem(lv3, 3, 4, false) : (PotionSlot.matches(lv) && lv.getCount() == 1 ? !this.insertItem(lv3, 0, 3, false) : (i >= 5 && i < 32 ? !this.insertItem(lv3, 32, 41, false) : (i >= 32 && i < 41 ? !this.insertItem(lv3, 5, 32, false) : !this.insertItem(lv3, 5, 41, false)))))) {
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

    @Environment(value=EnvType.CLIENT)
    public int getFuel() {
        return this.propertyDelegate.get(1);
    }

    @Environment(value=EnvType.CLIENT)
    public int getBrewTime() {
        return this.propertyDelegate.get(0);
    }

    static class FuelSlot
    extends Slot {
        public FuelSlot(Inventory arg, int i, int j, int k) {
            super(arg, i, j, k);
        }

        @Override
        public boolean canInsert(ItemStack arg) {
            return FuelSlot.matches(arg);
        }

        public static boolean matches(ItemStack arg) {
            return arg.getItem() == Items.BLAZE_POWDER;
        }

        @Override
        public int getMaxStackAmount() {
            return 64;
        }
    }

    static class IngredientSlot
    extends Slot {
        public IngredientSlot(Inventory arg, int i, int j, int k) {
            super(arg, i, j, k);
        }

        @Override
        public boolean canInsert(ItemStack arg) {
            return BrewingRecipeRegistry.isValidIngredient(arg);
        }

        @Override
        public int getMaxStackAmount() {
            return 64;
        }
    }

    static class PotionSlot
    extends Slot {
        public PotionSlot(Inventory arg, int i, int j, int k) {
            super(arg, i, j, k);
        }

        @Override
        public boolean canInsert(ItemStack arg) {
            return PotionSlot.matches(arg);
        }

        @Override
        public int getMaxStackAmount() {
            return 1;
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity arg, ItemStack arg2) {
            Potion lv = PotionUtil.getPotion(arg2);
            if (arg instanceof ServerPlayerEntity) {
                Criteria.BREWED_POTION.trigger((ServerPlayerEntity)arg, lv);
            }
            super.onTakeItem(arg, arg2);
            return arg2;
        }

        public static boolean matches(ItemStack arg) {
            Item lv = arg.getItem();
            return lv == Items.POTION || lv == Items.SPLASH_POTION || lv == Items.LINGERING_POTION || lv == Items.GLASS_BOTTLE;
        }
    }
}

