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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Npc;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;

public abstract class AbstractTraderEntity
extends PassiveEntity
implements Npc,
Trader {
    private static final TrackedData<Integer> HEAD_ROLLING_TIME_LEFT = DataTracker.registerData(AbstractTraderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TraderOfferList offers;
    private final BasicInventory inventory = new BasicInventory(8);

    public AbstractTraderEntity(EntityType<? extends AbstractTraderEntity> arg, World arg2) {
        super((EntityType<? extends PassiveEntity>)arg, arg2);
    }

    @Override
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData();
            ((PassiveEntity.PassiveData)arg4).setBabyAllowed(false);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public int getHeadRollingTimeLeft() {
        return this.dataTracker.get(HEAD_ROLLING_TIME_LEFT);
    }

    public void setHeadRollingTimeLeft(int i) {
        this.dataTracker.set(HEAD_ROLLING_TIME_LEFT, i);
    }

    @Override
    public int getExperience() {
        return 0;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
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
    public void setCurrentCustomer(@Nullable PlayerEntity arg) {
        this.customer = arg;
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
    public void setOffersFromServer(@Nullable TraderOfferList arg) {
    }

    @Override
    public void setExperienceFromServer(int i) {
    }

    @Override
    public void trade(TradeOffer arg) {
        arg.use();
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
        this.afterUsing(arg);
        if (this.customer instanceof ServerPlayerEntity) {
            Criteria.VILLAGER_TRADE.handle((ServerPlayerEntity)this.customer, this, arg.getMutableSellItem());
        }
    }

    protected abstract void afterUsing(TradeOffer var1);

    @Override
    public boolean isLevelledTrader() {
        return true;
    }

    @Override
    public void onSellingItem(ItemStack arg) {
        if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
            this.ambientSoundChance = -this.getMinAmbientSoundDelay();
            this.playSound(this.getTradingSound(!arg.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    protected SoundEvent getTradingSound(boolean bl) {
        return bl ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
    }

    public void playCelebrateSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        TraderOfferList lv = this.getOffers();
        if (!lv.isEmpty()) {
            arg.put("Offers", lv.toTag());
        }
        arg.put("Inventory", this.inventory.getTags());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("Offers", 10)) {
            this.offers = new TraderOfferList(arg.getCompound("Offers"));
        }
        this.inventory.readTags(arg.getList("Inventory", 10));
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType arg) {
        this.resetCustomer();
        return super.changeDimension(arg);
    }

    protected void resetCustomer() {
        this.setCurrentCustomer(null);
    }

    @Override
    public void onDeath(DamageSource arg) {
        super.onDeath(arg);
        this.resetCustomer();
    }

    @Environment(value=EnvType.CLIENT)
    protected void produceParticles(ParticleEffect arg) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(arg, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return false;
    }

    public BasicInventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean equip(int i, ItemStack arg) {
        if (super.equip(i, arg)) {
            return true;
        }
        int j = i - 300;
        if (j >= 0 && j < this.inventory.size()) {
            this.inventory.setStack(j, arg);
            return true;
        }
        return false;
    }

    @Override
    public World getTraderWorld() {
        return this.world;
    }

    protected abstract void fillRecipes();

    protected void fillRecipesFromPool(TraderOfferList arg, TradeOffers.Factory[] args, int i) {
        HashSet set = Sets.newHashSet();
        if (args.length > i) {
            while (set.size() < i) {
                set.add(this.random.nextInt(args.length));
            }
        } else {
            for (int j = 0; j < args.length; ++j) {
                set.add(j);
            }
        }
        for (Integer integer : set) {
            TradeOffers.Factory lv = args[integer];
            TradeOffer lv2 = lv.create(this, this.random);
            if (lv2 == null) continue;
            arg.add(lv2);
        }
    }
}
