/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.village;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

public interface Trader {
    public void setCurrentCustomer(@Nullable PlayerEntity var1);

    @Nullable
    public PlayerEntity getCurrentCustomer();

    public TraderOfferList getOffers();

    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TraderOfferList var1);

    public void trade(TradeOffer var1);

    public void onSellingItem(ItemStack var1);

    public World getTraderWorld();

    public int getExperience();

    public void setExperienceFromServer(int var1);

    public boolean isLeveledTrader();

    public SoundEvent getYesSound();

    default public boolean canRefreshTrades() {
        return false;
    }

    default public void sendOffers(PlayerEntity arg3, Text arg22, int i2) {
        TraderOfferList lv;
        OptionalInt optionalInt = arg3.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, arg, arg2) -> new MerchantScreenHandler(i, arg, this), arg22));
        if (optionalInt.isPresent() && !(lv = this.getOffers()).isEmpty()) {
            arg3.sendTradeOffers(optionalInt.getAsInt(), lv, i2, this.getExperience(), this.isLeveledTrader(), this.canRefreshTrades());
        }
    }
}

