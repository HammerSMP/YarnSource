/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.village;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.MathHelper;

public class TradeOffer {
    private final ItemStack firstBuyItem;
    private final ItemStack secondBuyItem;
    private final ItemStack sellItem;
    private int uses;
    private final int maxUses;
    private boolean rewardingPlayerExperience = true;
    private int specialPrice;
    private int demandBonus;
    private float priceMultiplier;
    private int traderExperience = 1;

    public TradeOffer(CompoundTag arg) {
        this.firstBuyItem = ItemStack.fromTag(arg.getCompound("buy"));
        this.secondBuyItem = ItemStack.fromTag(arg.getCompound("buyB"));
        this.sellItem = ItemStack.fromTag(arg.getCompound("sell"));
        this.uses = arg.getInt("uses");
        this.maxUses = arg.contains("maxUses", 99) ? arg.getInt("maxUses") : 4;
        if (arg.contains("rewardExp", 1)) {
            this.rewardingPlayerExperience = arg.getBoolean("rewardExp");
        }
        if (arg.contains("xp", 3)) {
            this.traderExperience = arg.getInt("xp");
        }
        if (arg.contains("priceMultiplier", 5)) {
            this.priceMultiplier = arg.getFloat("priceMultiplier");
        }
        this.specialPrice = arg.getInt("specialPrice");
        this.demandBonus = arg.getInt("demand");
    }

    public TradeOffer(ItemStack buyItem, ItemStack sellItem, int maxUses, int rewardedExp, float priceMultiplier) {
        this(buyItem, ItemStack.EMPTY, sellItem, maxUses, rewardedExp, priceMultiplier);
    }

    public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int maxUses, int rewardedExp, float priceMultiplier) {
        this(firstBuyItem, secondBuyItem, sellItem, 0, maxUses, rewardedExp, priceMultiplier);
    }

    public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int rewardedExp, float priceMultiplier) {
        this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, rewardedExp, priceMultiplier, 0);
    }

    public TradeOffer(ItemStack arg, ItemStack arg2, ItemStack arg3, int i, int j, int k, float f, int l) {
        this.firstBuyItem = arg;
        this.secondBuyItem = arg2;
        this.sellItem = arg3;
        this.uses = i;
        this.maxUses = j;
        this.traderExperience = k;
        this.priceMultiplier = f;
        this.demandBonus = l;
    }

    public ItemStack getOriginalFirstBuyItem() {
        return this.firstBuyItem;
    }

    public ItemStack getAdjustedFirstBuyItem() {
        int i = this.firstBuyItem.getCount();
        ItemStack lv = this.firstBuyItem.copy();
        int j = Math.max(0, MathHelper.floor((float)(i * this.demandBonus) * this.priceMultiplier));
        lv.setCount(MathHelper.clamp(i + j + this.specialPrice, 1, this.firstBuyItem.getItem().getMaxCount()));
        return lv;
    }

    public ItemStack getSecondBuyItem() {
        return this.secondBuyItem;
    }

    public ItemStack getMutableSellItem() {
        return this.sellItem;
    }

    public void updatePriceOnDemand() {
        this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack getSellItem() {
        return this.sellItem.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void use() {
        ++this.uses;
    }

    public int getDemandBonus() {
        return this.demandBonus;
    }

    public void increaseSpecialPrice(int i) {
        this.specialPrice += i;
    }

    public void clearSpecialPrice() {
        this.specialPrice = 0;
    }

    public int getSpecialPrice() {
        return this.specialPrice;
    }

    public void setSpecialPrice(int i) {
        this.specialPrice = i;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getTraderExperience() {
        return this.traderExperience;
    }

    public boolean isDisabled() {
        return this.uses >= this.maxUses;
    }

    public void clearUses() {
        this.uses = this.maxUses;
    }

    public boolean method_21834() {
        return this.uses > 0;
    }

    public boolean shouldRewardPlayerExperience() {
        return this.rewardingPlayerExperience;
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        lv.put("buy", this.firstBuyItem.toTag(new CompoundTag()));
        lv.put("sell", this.sellItem.toTag(new CompoundTag()));
        lv.put("buyB", this.secondBuyItem.toTag(new CompoundTag()));
        lv.putInt("uses", this.uses);
        lv.putInt("maxUses", this.maxUses);
        lv.putBoolean("rewardExp", this.rewardingPlayerExperience);
        lv.putInt("xp", this.traderExperience);
        lv.putFloat("priceMultiplier", this.priceMultiplier);
        lv.putInt("specialPrice", this.specialPrice);
        lv.putInt("demand", this.demandBonus);
        return lv;
    }

    public boolean matchesBuyItems(ItemStack first, ItemStack second) {
        return this.acceptsBuy(first, this.getAdjustedFirstBuyItem()) && first.getCount() >= this.getAdjustedFirstBuyItem().getCount() && this.acceptsBuy(second, this.secondBuyItem) && second.getCount() >= this.secondBuyItem.getCount();
    }

    private boolean acceptsBuy(ItemStack given, ItemStack sample) {
        if (sample.isEmpty() && given.isEmpty()) {
            return true;
        }
        ItemStack lv = given.copy();
        if (lv.getItem().isDamageable()) {
            lv.setDamage(lv.getDamage());
        }
        return ItemStack.areItemsEqualIgnoreDamage(lv, sample) && (!sample.hasTag() || lv.hasTag() && NbtHelper.matches(sample.getTag(), lv.getTag(), false));
    }

    public boolean depleteBuyItems(ItemStack arg, ItemStack arg2) {
        if (!this.matchesBuyItems(arg, arg2)) {
            return false;
        }
        arg.decrement(this.getAdjustedFirstBuyItem().getCount());
        if (!this.getSecondBuyItem().isEmpty()) {
            arg2.decrement(this.getSecondBuyItem().getCount());
        }
        return true;
    }
}

