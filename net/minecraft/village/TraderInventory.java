/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;

public class TraderInventory
implements Inventory {
    private final Trader trader;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    @Nullable
    private TradeOffer traderRecipe;
    private int recipeIndex;
    private int traderRewardedExperience;

    public TraderInventory(Trader arg) {
        this.trader = arg;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack lv : this.inventory) {
            if (lv.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int i) {
        return this.inventory.get(i);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        ItemStack lv = this.inventory.get(i);
        if (i == 2 && !lv.isEmpty()) {
            return Inventories.splitStack(this.inventory, i, lv.getCount());
        }
        ItemStack lv2 = Inventories.splitStack(this.inventory, i, j);
        if (!lv2.isEmpty() && this.needRecipeUpdate(i)) {
            this.updateRecipes();
        }
        return lv2;
    }

    private boolean needRecipeUpdate(int i) {
        return i == 0 || i == 1;
    }

    @Override
    public ItemStack removeStack(int i) {
        return Inventories.removeStack(this.inventory, i);
    }

    @Override
    public void setStack(int i, ItemStack arg) {
        this.inventory.set(i, arg);
        if (!arg.isEmpty() && arg.getCount() > this.getMaxCountPerStack()) {
            arg.setCount(this.getMaxCountPerStack());
        }
        if (this.needRecipeUpdate(i)) {
            this.updateRecipes();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        return this.trader.getCurrentCustomer() == arg;
    }

    @Override
    public void markDirty() {
        this.updateRecipes();
    }

    public void updateRecipes() {
        ItemStack lv4;
        ItemStack lv3;
        this.traderRecipe = null;
        if (this.inventory.get(0).isEmpty()) {
            ItemStack lv = this.inventory.get(1);
            ItemStack lv2 = ItemStack.EMPTY;
        } else {
            lv3 = this.inventory.get(0);
            lv4 = this.inventory.get(1);
        }
        if (lv3.isEmpty()) {
            this.setStack(2, ItemStack.EMPTY);
            this.traderRewardedExperience = 0;
            return;
        }
        TraderOfferList lv5 = this.trader.getOffers();
        if (!lv5.isEmpty()) {
            TradeOffer lv6 = lv5.getValidRecipe(lv3, lv4, this.recipeIndex);
            if (lv6 == null || lv6.isDisabled()) {
                this.traderRecipe = lv6;
                lv6 = lv5.getValidRecipe(lv4, lv3, this.recipeIndex);
            }
            if (lv6 != null && !lv6.isDisabled()) {
                this.traderRecipe = lv6;
                this.setStack(2, lv6.getSellItem());
                this.traderRewardedExperience = lv6.getTraderExperience();
            } else {
                this.setStack(2, ItemStack.EMPTY);
                this.traderRewardedExperience = 0;
            }
        }
        this.trader.onSellingItem(this.getStack(2));
    }

    @Nullable
    public TradeOffer getTradeOffer() {
        return this.traderRecipe;
    }

    public void setRecipeIndex(int i) {
        this.recipeIndex = i;
        this.updateRecipes();
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public int getTraderRewardedExperience() {
        return this.traderRewardedExperience;
    }
}

