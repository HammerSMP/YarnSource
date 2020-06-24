/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoToEntityGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HoldInHandsGoal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopFollowingCustomerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

public class WanderingTraderEntity
extends AbstractTraderEntity {
    @Nullable
    private BlockPos wanderTarget;
    private int despawnDelay;

    public WanderingTraderEntity(EntityType<? extends WanderingTraderEntity> arg, World arg2) {
        super((EntityType<? extends AbstractTraderEntity>)arg, arg2);
        this.teleporting = true;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new HoldInHandsGoal<WanderingTraderEntity>(this, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEvents.ENTITY_WANDERING_TRADER_DISAPPEARED, arg -> !this.world.isDay() && !arg.isInvisible()));
        this.goalSelector.add(0, new HoldInHandsGoal<WanderingTraderEntity>(this, new ItemStack(Items.MILK_BUCKET), SoundEvents.ENTITY_WANDERING_TRADER_REAPPEARED, arg -> this.world.isDay() && arg.isInvisible()));
        this.goalSelector.add(1, new StopFollowingCustomerGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<ZombieEntity>(this, ZombieEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<EvokerEntity>(this, EvokerEntity.class, 12.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<VindicatorEntity>(this, VindicatorEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<VexEntity>(this, VexEntity.class, 8.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<PillagerEntity>(this, PillagerEntity.class, 15.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<IllusionerEntity>(this, IllusionerEntity.class, 12.0f, 0.5, 0.5));
        this.goalSelector.add(1, new FleeEntityGoal<ZoglinEntity>(this, ZoglinEntity.class, 10.0f, 0.5, 0.5));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5));
        this.goalSelector.add(1, new LookAtCustomerGoal(this));
        this.goalSelector.add(2, new WanderToTargetGoal(this, 2.0, 0.35));
        this.goalSelector.add(4, new GoToWalkTargetGoal(this, 0.35));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35));
        this.goalSelector.add(9, new GoToEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity arg) {
        return null;
    }

    @Override
    public boolean isLeveledTrader() {
        return false;
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
            if (arg2 == Hand.MAIN_HAND) {
                arg.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (this.getOffers().isEmpty()) {
                return ActionResult.success(this.world.isClient);
            }
            if (!this.world.isClient) {
                this.setCurrentCustomer(arg);
                this.sendOffers(arg, this.getDisplayName(), 1);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(arg, arg2);
    }

    @Override
    protected void fillRecipes() {
        TradeOffers.Factory[] lvs = (TradeOffers.Factory[])TradeOffers.WANDERING_TRADER_TRADES.get(1);
        TradeOffers.Factory[] lvs2 = (TradeOffers.Factory[])TradeOffers.WANDERING_TRADER_TRADES.get(2);
        if (lvs == null || lvs2 == null) {
            return;
        }
        TraderOfferList lv = this.getOffers();
        this.fillRecipesFromPool(lv, lvs, 5);
        int i = this.random.nextInt(lvs2.length);
        TradeOffers.Factory lv2 = lvs2[i];
        TradeOffer lv3 = lv2.create(this, this.random);
        if (lv3 != null) {
            lv.add(lv3);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("DespawnDelay", this.despawnDelay);
        if (this.wanderTarget != null) {
            arg.put("WanderTarget", NbtHelper.fromBlockPos(this.wanderTarget));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("DespawnDelay", 99)) {
            this.despawnDelay = arg.getInt("DespawnDelay");
        }
        if (arg.contains("WanderTarget")) {
            this.wanderTarget = NbtHelper.toBlockPos(arg.getCompound("WanderTarget"));
        }
        this.setBreedingAge(Math.max(0, this.getBreedingAge()));
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return false;
    }

    @Override
    protected void afterUsing(TradeOffer arg) {
        if (arg.shouldRewardPlayerExperience()) {
            int i = 3 + this.random.nextInt(4);
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_WANDERING_TRADER_TRADE;
        }
        return SoundEvents.ENTITY_WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEvent getDrinkSound(ItemStack arg) {
        Item lv = arg.getItem();
        if (lv == Items.MILK_BUCKET) {
            return SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK;
        }
        return SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION;
    }

    @Override
    protected SoundEvent getTradingSound(boolean bl) {
        return bl ? SoundEvents.ENTITY_WANDERING_TRADER_YES : SoundEvents.ENTITY_WANDERING_TRADER_NO;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_WANDERING_TRADER_YES;
    }

    public void setDespawnDelay(int i) {
        this.despawnDelay = i;
    }

    public int getDespawnDelay() {
        return this.despawnDelay;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            this.tickDespawnDelay();
        }
    }

    private void tickDespawnDelay() {
        if (this.despawnDelay > 0 && !this.hasCustomer() && --this.despawnDelay == 0) {
            this.remove();
        }
    }

    public void setWanderTarget(@Nullable BlockPos arg) {
        this.wanderTarget = arg;
    }

    @Nullable
    private BlockPos getWanderTarget() {
        return this.wanderTarget;
    }

    class WanderToTargetGoal
    extends Goal {
        final WanderingTraderEntity trader;
        final double proximityDistance;
        final double speed;

        WanderToTargetGoal(WanderingTraderEntity arg2, double d, double e) {
            this.trader = arg2;
            this.proximityDistance = d;
            this.speed = e;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public void stop() {
            this.trader.setWanderTarget(null);
            WanderingTraderEntity.this.navigation.stop();
        }

        @Override
        public boolean canStart() {
            BlockPos lv = this.trader.getWanderTarget();
            return lv != null && this.isTooFarFrom(lv, this.proximityDistance);
        }

        @Override
        public void tick() {
            BlockPos lv = this.trader.getWanderTarget();
            if (lv != null && WanderingTraderEntity.this.navigation.isIdle()) {
                if (this.isTooFarFrom(lv, 10.0)) {
                    Vec3d lv2 = new Vec3d((double)lv.getX() - this.trader.getX(), (double)lv.getY() - this.trader.getY(), (double)lv.getZ() - this.trader.getZ()).normalize();
                    Vec3d lv3 = lv2.multiply(10.0).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());
                    WanderingTraderEntity.this.navigation.startMovingTo(lv3.x, lv3.y, lv3.z, this.speed);
                } else {
                    WanderingTraderEntity.this.navigation.startMovingTo(lv.getX(), lv.getY(), lv.getZ(), this.speed);
                }
            }
        }

        private boolean isTooFarFrom(BlockPos arg, double d) {
            return !arg.isWithinDistance(this.trader.getPos(), d);
        }
    }
}

