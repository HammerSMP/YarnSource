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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.village.SimpleTrader;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;

public class MerchantScreenHandler
extends ScreenHandler {
    private final Trader trader;
    private final TraderInventory traderInventory;
    @Environment(value=EnvType.CLIENT)
    private int levelProgress;
    @Environment(value=EnvType.CLIENT)
    private boolean leveled;
    @Environment(value=EnvType.CLIENT)
    private boolean canRefreshTrades;

    public MerchantScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleTrader(playerInventory.player));
    }

    public MerchantScreenHandler(int syncId, PlayerInventory playerInventory, Trader trader) {
        super(ScreenHandlerType.MERCHANT, syncId);
        this.trader = trader;
        this.traderInventory = new TraderInventory(trader);
        this.addSlot(new Slot(this.traderInventory, 0, 136, 37));
        this.addSlot(new Slot(this.traderInventory, 1, 162, 37));
        this.addSlot(new TradeOutputSlot(playerInventory.player, trader, this.traderInventory, 2, 220, 37));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 108 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 108 + l * 18, 142));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void setCanLevel(boolean canLevel) {
        this.leveled = canLevel;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.traderInventory.updateRecipes();
        super.onContentChanged(inventory);
    }

    public void setRecipeIndex(int index) {
        this.traderInventory.setRecipeIndex(index);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.trader.getCurrentCustomer() == player;
    }

    @Environment(value=EnvType.CLIENT)
    public int getExperience() {
        return this.trader.getExperience();
    }

    @Environment(value=EnvType.CLIENT)
    public int getTraderRewardedExperience() {
        return this.traderInventory.getTraderRewardedExperience();
    }

    @Environment(value=EnvType.CLIENT)
    public void setExperienceFromServer(int experience) {
        this.trader.setExperienceFromServer(experience);
    }

    @Environment(value=EnvType.CLIENT)
    public int getLevelProgress() {
        return this.levelProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public void setLevelProgress(int progress) {
        this.levelProgress = progress;
    }

    @Environment(value=EnvType.CLIENT)
    public void setRefreshTrades(boolean refreshable) {
        this.canRefreshTrades = refreshable;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean canRefreshTrades() {
        return this.canRefreshTrades;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(index);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (index == 2) {
                if (!this.insertItem(lv3, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
                this.playYesSound();
            } else if (index == 0 || index == 1 ? !this.insertItem(lv3, 3, 39, false) : (index >= 3 && index < 30 ? !this.insertItem(lv3, 30, 39, false) : index >= 30 && index < 39 && !this.insertItem(lv3, 3, 30, false))) {
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
            lv2.onTakeItem(player, lv3);
        }
        return lv;
    }

    private void playYesSound() {
        if (!this.trader.getTraderWorld().isClient) {
            Entity lv = (Entity)((Object)this.trader);
            this.trader.getTraderWorld().playSound(lv.getX(), lv.getY(), lv.getZ(), this.trader.getYesSound(), SoundCategory.NEUTRAL, 1.0f, 1.0f, false);
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.trader.setCurrentCustomer(null);
        if (this.trader.getTraderWorld().isClient) {
            return;
        }
        if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
            ItemStack lv = this.traderInventory.removeStack(0);
            if (!lv.isEmpty()) {
                player.dropItem(lv, false);
            }
            if (!(lv = this.traderInventory.removeStack(1)).isEmpty()) {
                player.dropItem(lv, false);
            }
        } else {
            player.inventory.offerOrDrop(player.world, this.traderInventory.removeStack(0));
            player.inventory.offerOrDrop(player.world, this.traderInventory.removeStack(1));
        }
    }

    public void switchTo(int recipeIndex) {
        ItemStack lv2;
        if (this.getRecipes().size() <= recipeIndex) {
            return;
        }
        ItemStack lv = this.traderInventory.getStack(0);
        if (!lv.isEmpty()) {
            if (!this.insertItem(lv, 3, 39, true)) {
                return;
            }
            this.traderInventory.setStack(0, lv);
        }
        if (!(lv2 = this.traderInventory.getStack(1)).isEmpty()) {
            if (!this.insertItem(lv2, 3, 39, true)) {
                return;
            }
            this.traderInventory.setStack(1, lv2);
        }
        if (this.traderInventory.getStack(0).isEmpty() && this.traderInventory.getStack(1).isEmpty()) {
            ItemStack lv3 = ((TradeOffer)this.getRecipes().get(recipeIndex)).getAdjustedFirstBuyItem();
            this.autofill(0, lv3);
            ItemStack lv4 = ((TradeOffer)this.getRecipes().get(recipeIndex)).getSecondBuyItem();
            this.autofill(1, lv4);
        }
    }

    private void autofill(int slot, ItemStack stack) {
        if (!stack.isEmpty()) {
            for (int j = 3; j < 39; ++j) {
                ItemStack lv = ((Slot)this.slots.get(j)).getStack();
                if (lv.isEmpty() || !this.equals(stack, lv)) continue;
                ItemStack lv2 = this.traderInventory.getStack(slot);
                int k = lv2.isEmpty() ? 0 : lv2.getCount();
                int l = Math.min(stack.getMaxCount() - k, lv.getCount());
                ItemStack lv3 = lv.copy();
                int m = k + l;
                lv.decrement(l);
                lv3.setCount(m);
                this.traderInventory.setStack(slot, lv3);
                if (m >= stack.getMaxCount()) break;
            }
        }
    }

    private boolean equals(ItemStack itemStack, ItemStack otherItemStack) {
        return itemStack.getItem() == otherItemStack.getItem() && ItemStack.areTagsEqual(itemStack, otherItemStack);
    }

    @Environment(value=EnvType.CLIENT)
    public void setOffers(TraderOfferList offers) {
        this.trader.setOffersFromServer(offers);
    }

    public TraderOfferList getRecipes() {
        return this.trader.getOffers();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLeveled() {
        return this.leveled;
    }
}

