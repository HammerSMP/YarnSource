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

    public TradeOutputSlot(PlayerEntity player, Trader trader, TraderInventory traderInventory, int index, int x, int y) {
        super(traderInventory, index, x, y);
        this.player = player;
        this.trader = trader;
        this.traderInventory = traderInventory;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }
        return super.takeStack(amount);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        stack.onCraft(this.player.world, this.player, this.amount);
        this.amount = 0;
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        TradeOffer lv = this.traderInventory.getTradeOffer();
        if (lv != null) {
            ItemStack lv3;
            ItemStack lv2 = this.traderInventory.getStack(0);
            if (lv.depleteBuyItems(lv2, lv3 = this.traderInventory.getStack(1)) || lv.depleteBuyItems(lv3, lv2)) {
                this.trader.trade(lv);
                player.incrementStat(Stats.TRADED_WITH_VILLAGER);
                this.traderInventory.setStack(0, lv2);
                this.traderInventory.setStack(1, lv3);
            }
            this.trader.setExperienceFromServer(this.trader.getExperience() + lv.getTraderExperience());
        }
        return stack;
    }
}

