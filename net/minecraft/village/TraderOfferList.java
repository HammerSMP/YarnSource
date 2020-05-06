/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.village;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.village.TradeOffer;

public class TraderOfferList
extends ArrayList<TradeOffer> {
    public TraderOfferList() {
    }

    public TraderOfferList(CompoundTag arg) {
        ListTag lv = arg.getList("Recipes", 10);
        for (int i = 0; i < lv.size(); ++i) {
            this.add(new TradeOffer(lv.getCompound(i)));
        }
    }

    @Nullable
    public TradeOffer getValidRecipe(ItemStack arg, ItemStack arg2, int i) {
        if (i > 0 && i < this.size()) {
            TradeOffer lv = (TradeOffer)this.get(i);
            if (lv.matchesBuyItems(arg, arg2)) {
                return lv;
            }
            return null;
        }
        for (int j = 0; j < this.size(); ++j) {
            TradeOffer lv2 = (TradeOffer)this.get(j);
            if (!lv2.matchesBuyItems(arg, arg2)) continue;
            return lv2;
        }
        return null;
    }

    public void toPacket(PacketByteBuf arg) {
        arg.writeByte((byte)(this.size() & 0xFF));
        for (int i = 0; i < this.size(); ++i) {
            TradeOffer lv = (TradeOffer)this.get(i);
            arg.writeItemStack(lv.getOriginalFirstBuyItem());
            arg.writeItemStack(lv.getMutableSellItem());
            ItemStack lv2 = lv.getSecondBuyItem();
            arg.writeBoolean(!lv2.isEmpty());
            if (!lv2.isEmpty()) {
                arg.writeItemStack(lv2);
            }
            arg.writeBoolean(lv.isDisabled());
            arg.writeInt(lv.getUses());
            arg.writeInt(lv.getMaxUses());
            arg.writeInt(lv.getTraderExperience());
            arg.writeInt(lv.getSpecialPrice());
            arg.writeFloat(lv.getPriceMultiplier());
            arg.writeInt(lv.getDemandBonus());
        }
    }

    public static TraderOfferList fromPacket(PacketByteBuf arg) {
        TraderOfferList lv = new TraderOfferList();
        int i = arg.readByte() & 0xFF;
        for (int j = 0; j < i; ++j) {
            ItemStack lv2 = arg.readItemStack();
            ItemStack lv3 = arg.readItemStack();
            ItemStack lv4 = ItemStack.EMPTY;
            if (arg.readBoolean()) {
                lv4 = arg.readItemStack();
            }
            boolean bl = arg.readBoolean();
            int k = arg.readInt();
            int l = arg.readInt();
            int m = arg.readInt();
            int n = arg.readInt();
            float f = arg.readFloat();
            int o = arg.readInt();
            TradeOffer lv5 = new TradeOffer(lv2, lv4, lv3, k, l, m, f, o);
            if (bl) {
                lv5.clearUses();
            }
            lv5.setSpecialPrice(n);
            lv.add(lv5);
        }
        return lv;
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        ListTag lv2 = new ListTag();
        for (int i = 0; i < this.size(); ++i) {
            TradeOffer lv3 = (TradeOffer)this.get(i);
            lv2.add(lv3.toTag());
        }
        lv.put("Recipes", lv2);
        return lv;
    }
}

