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
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

public class SimpleTrader
implements Trader {
    private final TraderInventory traderInventory;
    private final PlayerEntity player;
    private TraderOfferList recipeList = new TraderOfferList();
    private int experience;

    public SimpleTrader(PlayerEntity arg) {
        this.player = arg;
        this.traderInventory = new TraderInventory(this);
    }

    @Override
    @Nullable
    public PlayerEntity getCurrentCustomer() {
        return this.player;
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity arg) {
    }

    @Override
    public TraderOfferList getOffers() {
        return this.recipeList;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TraderOfferList arg) {
        this.recipeList = arg;
    }

    @Override
    public void trade(TradeOffer arg) {
        arg.use();
    }

    @Override
    public void onSellingItem(ItemStack arg) {
    }

    @Override
    public World getTraderWorld() {
        return this.player.world;
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    @Override
    public void setExperienceFromServer(int i) {
        this.experience = i;
    }

    @Override
    public boolean isLeveledTrader() {
        return true;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }
}

