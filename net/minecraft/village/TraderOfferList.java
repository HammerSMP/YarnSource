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
    public TradeOffer getValidRecipe(ItemStack firstBuyItem, ItemStack secondBuyItem, int index) {
        if (index > 0 && index < this.size()) {
            TradeOffer lv = (TradeOffer)this.get(index);
            if (lv.matchesBuyItems(firstBuyItem, secondBuyItem)) {
                return lv;
            }
            return null;
        }
        for (int j = 0; j < this.size(); ++j) {
            TradeOffer lv2 = (TradeOffer)this.get(j);
            if (!lv2.matchesBuyItems(firstBuyItem, secondBuyItem)) continue;
            return lv2;
        }
        return null;
    }

    public void toPacket(PacketByteBuf buffer) {
        buffer.writeByte((byte)(this.size() & 0xFF));
        for (int i = 0; i < this.size(); ++i) {
            TradeOffer lv = (TradeOffer)this.get(i);
            buffer.writeItemStack(lv.getOriginalFirstBuyItem());
            buffer.writeItemStack(lv.getMutableSellItem());
            ItemStack lv2 = lv.getSecondBuyItem();
            buffer.writeBoolean(!lv2.isEmpty());
            if (!lv2.isEmpty()) {
                buffer.writeItemStack(lv2);
            }
            buffer.writeBoolean(lv.isDisabled());
            buffer.writeInt(lv.getUses());
            buffer.writeInt(lv.getMaxUses());
            buffer.writeInt(lv.getTraderExperience());
            buffer.writeInt(lv.getSpecialPrice());
            buffer.writeFloat(lv.getPriceMultiplier());
            buffer.writeInt(lv.getDemandBonus());
        }
    }

    public static TraderOfferList fromPacket(PacketByteBuf byteBuf) {
        TraderOfferList lv = new TraderOfferList();
        int i = byteBuf.readByte() & 0xFF;
        for (int j = 0; j < i; ++j) {
            ItemStack lv2 = byteBuf.readItemStack();
            ItemStack lv3 = byteBuf.readItemStack();
            ItemStack lv4 = ItemStack.EMPTY;
            if (byteBuf.readBoolean()) {
                lv4 = byteBuf.readItemStack();
            }
            boolean bl = byteBuf.readBoolean();
            int k = byteBuf.readInt();
            int l = byteBuf.readInt();
            int m = byteBuf.readInt();
            int n = byteBuf.readInt();
            float f = byteBuf.readFloat();
            int o = byteBuf.readInt();
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

