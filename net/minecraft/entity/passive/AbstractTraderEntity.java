/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.class_5425;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class AbstractTraderEntity
extends PassiveEntity
implements Npc,
Trader {
    private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(AbstractTraderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TraderOfferList offers;
    private final SimpleInventory inventory = new SimpleInventory(8);

    public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> arg, World arg2) {
        super((EntityType<? extends PassiveEntity>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
    }

    @Override
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        if (entityData == null) {
            entityData = new PassiveEntity.PassiveData(false);
        }
        return super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
    }

    public int getHeadRollingTimeLeft() {
        return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
    }

    public void setHeadRollingTimeLeft(int ticks) {
        this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, ticks);
    }

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        if (this.isBaby()) {
            return 0.81f;
        }
        return 1.62f;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HEAD_ROLLING_TIME_LEFT, 0);
    }

    @Override
    public void setCurrentCustomer(@Nullable PlayerEntity customer) {
        this.customer = customer;
    }

    @Override
    @Nullable
    public PlayerEntity getCurrentCustomer() {
        return this.customer;
    }

    public boolean hasCustomer() {
        return this.customer != null;
    }

    @Override
    public TraderOfferList getOffers() {
        if (this.offers == null) {
            this.offers = new TraderOfferList();
            this.fillRecipes();
        }
        return this.offers;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setOffersFromServer(@Nullable TraderOfferList offers) {
    }

    @Override
    public void setExperienceFromServer(int experience) {
    }

    @Override
    public void trade(TradeOffer offer) {
        offer.use();
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.afterUsing(offer);
        if (this.customer instanceof ServerPlayerEntity) {
            Criteria.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, offer.getMutableSellItem());
        }
    }

    protected abstract void afterUsing(TradeOffer var1);

    @Override
    public boolean isLeveledTrader() {
        return true;
    }

    @Override
    public void onSellingItem(ItemStack stack) {
        if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
            this.ambientSoundChance = -this.getMinAmbientSoundDelay();
            this.playSound(this.getTradingSound(!stack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    protected SoundEvent getTradingSound(boolean sold) {
        return sold ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
    }

    public void playCelebrateSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        TraderOfferList lv = this.getOffers();
        if (!lv.isEmpty()) {
            tag.put("Offers", lv.toTag());
        }
        tag.put("Inventory", this.inventory.getTags());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("Offers", 10)) {
            this.offers = new TraderOfferList(tag.getCompound("Offers"));
        }
        this.inventory.readTags(tag.getList("Inventory", 10));
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        this.resetCustomer();
        return super.moveToWorld(destination);
    }

    protected void resetCustomer() {
        this.setCurrentCustomer(null);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.resetCustomer();
    }

    @Environment(value=EnvType.CLIENT)
    protected void produceParticles(ParticleEffect parameters) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (super.equip(slot, item)) {
            return true;
        }
        int j = slot - 300;
        if (j >= 0 && j < this.inventory.size()) {
            this.inventory.setStack(j, item);
            return true;
        }
        return false;
    }

    @Override
    public World getTraderWorld() {
        return this.world;
    }

    protected abstract void fillRecipes();

    protected void fillRecipesFromPool(TraderOfferList recipeList, TradeOffers.Factory[] pool, int count) {
        HashSet set = Sets.newHashSet();
        if (pool.length > count) {
            while (set.size() < count) {
                set.add(this.random.nextInt(pool.length));
            }
        } else {
            for (int j = 0; j < pool.length; ++j) {
                set.add(j);
            }
        }
        for (Integer integer : set) {
            TradeOffers.Factory lv = pool[integer];
            TradeOffer lv2 = lv.create(this, this.random);
            if (lv2 == null) continue;
            recipeList.add(lv2);
        }
    }
}

