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

    public MerchantScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, new SimpleTrader(arg.player));
    }

    public MerchantScreenHandler(int i, PlayerInventory arg, Trader arg2) {
        super(ScreenHandlerType.MERCHANT, i);
        this.trader = arg2;
        this.traderInventory = new TraderInventory(arg2);
        this.addSlot(new Slot(this.traderInventory, 0, 136, 37));
        this.addSlot(new Slot(this.traderInventory, 1, 162, 37));
        this.addSlot(new TradeOutputSlot(arg.player, arg2, this.traderInventory, 2, 220, 37));
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(arg, k + j * 9 + 9, 108 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(arg, l, 108 + l * 18, 142));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void setCanLevel(boolean bl) {
        this.leveled = bl;
    }

    @Override
    public void onContentChanged(Inventory arg) {
        this.traderInventory.updateRecipes();
        super.onContentChanged(arg);
    }

    public void setRecipeIndex(int i) {
        this.traderInventory.setRecipeIndex(i);
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return this.trader.getCurrentCustomer() == arg;
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
    public void setExperienceFromServer(int i) {
        this.trader.setExperienceFromServer(i);
    }

    @Environment(value=EnvType.CLIENT)
    public int getLevelProgress() {
        return this.levelProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public void setLevelProgress(int i) {
        this.levelProgress = i;
    }

    @Environment(value=EnvType.CLIENT)
    public void setRefreshTrades(boolean bl) {
        this.canRefreshTrades = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean canRefreshTrades() {
        return this.canRefreshTrades;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack arg, Slot arg2) {
        return false;
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
                this.playYesSound();
            } else if (i == 0 || i == 1 ? !this.insertItem(lv3, 3, 39, false) : (i >= 3 && i < 30 ? !this.insertItem(lv3, 30, 39, false) : i >= 30 && i < 39 && !this.insertItem(lv3, 3, 30, false))) {
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

    private void playYesSound() {
        if (!this.trader.getTraderWorld().isClient) {
            Entity lv = (Entity)((Object)this.trader);
            this.trader.getTraderWorld().playSound(lv.getX(), lv.getY(), lv.getZ(), this.trader.getYesSound(), SoundCategory.NEUTRAL, 1.0f, 1.0f, false);
        }
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.trader.setCurrentCustomer(null);
        if (this.trader.getTraderWorld().isClient) {
            return;
        }
        if (!arg.isAlive() || arg instanceof ServerPlayerEntity && ((ServerPlayerEntity)arg).isDisconnected()) {
            ItemStack lv = this.traderInventory.removeStack(0);
            if (!lv.isEmpty()) {
                arg.dropItem(lv, false);
            }
            if (!(lv = this.traderInventory.removeStack(1)).isEmpty()) {
                arg.dropItem(lv, false);
            }
        } else {
            arg.inventory.offerOrDrop(arg.world, this.traderInventory.removeStack(0));
            arg.inventory.offerOrDrop(arg.world, this.traderInventory.removeStack(1));
        }
    }

    public void switchTo(int i) {
        ItemStack lv2;
        if (this.getRecipes().size() <= i) {
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
            ItemStack lv3 = ((TradeOffer)this.getRecipes().get(i)).getAdjustedFirstBuyItem();
            this.autofill(0, lv3);
            ItemStack lv4 = ((TradeOffer)this.getRecipes().get(i)).getSecondBuyItem();
            this.autofill(1, lv4);
        }
    }

    private void autofill(int i, ItemStack arg) {
        if (!arg.isEmpty()) {
            for (int j = 3; j < 39; ++j) {
                ItemStack lv = ((Slot)this.slots.get(j)).getStack();
                if (lv.isEmpty() || !this.equals(arg, lv)) continue;
                ItemStack lv2 = this.traderInventory.getStack(i);
                int k = lv2.isEmpty() ? 0 : lv2.getCount();
                int l = Math.min(arg.getMaxCount() - k, lv.getCount());
                ItemStack lv3 = lv.copy();
                int m = k + l;
                lv.decrement(l);
                lv3.setCount(m);
                this.traderInventory.setStack(i, lv3);
                if (m >= arg.getMaxCount()) break;
            }
        }
    }

    private boolean equals(ItemStack arg, ItemStack arg2) {
        return arg.getItem() == arg2.getItem() && ItemStack.areTagsEqual(arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public void setOffers(TraderOfferList arg) {
        this.trader.setOffersFromServer(arg);
    }

    public TraderOfferList getRecipes() {
        return this.trader.getOffers();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLeveled() {
        return this.leveled;
    }
}

