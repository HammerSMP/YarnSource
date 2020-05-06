/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.stat.Stats;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;

public class TradeOutputSlot
extends Slot {
    private final TraderInventory traderInventory;
    private final PlayerEntity player;
    private int amount;
    private final Trader trader;

    public TradeOutputSlot(PlayerEntity arg, Trader arg2, TraderInventory arg3, int i, int j, int k) {
        super(arg3, i, j, k);
        this.player = arg;
        this.trader = arg2;
        this.traderInventory = arg3;
    }

    @Override
    public boolean canInsert(ItemStack arg) {
        return false;
    }

    @Override
    public ItemStack takeStack(int i) {
        if (this.hasStack()) {
            this.amount += Math.min(i, this.getStack().getCount());
        }
        return super.takeStack(i);
    }

    @Override
    protected void onCrafted(ItemStack arg, int i) {
        this.amount += i;
        this.onCrafted(arg);
    }

    @Override
    protected void onCrafted(ItemStack arg) {
        arg.onCraft(this.player.world, this.player, this.amount);
        this.amount = 0;
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity arg, ItemStack arg2) {
        this.onCrafted(arg2);
        TradeOffer lv = this.traderInventory.getTradeOffer();
        if (lv != null) {
            ItemStack lv3;
            ItemStack lv2 = this.traderInventory.getStack(0);
            if (lv.depleteBuyItems(lv2, lv3 = this.traderInventory.getStack(1)) || lv.depleteBuyItems(lv3, lv2)) {
                this.trader.trade(lv);
                arg.incrementStat(Stats.TRADED_WITH_VILLAGER);
                this.traderInventory.setStack(0, lv2);
                this.traderInventory.setStack(1, lv3);
            }
            this.trader.setExperienceFromServer(this.trader.getExperience() + lv.getTraderExperience());
        }
        return arg2;
    }
}

