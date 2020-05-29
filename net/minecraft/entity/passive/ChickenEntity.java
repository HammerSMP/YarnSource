/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChickenEntity
extends AnimalEntity {
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0f;
    public int eggLayTime = this.random.nextInt(6000) + 6000;
    public boolean jockey;

    public ChickenEntity(EntityType<? extends ChickenEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal((MobEntityWithAi)this, 1.0, false, BREEDING_INGREDIENT));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return this.isBaby() ? arg2.height * 0.85f : arg2.height * 0.92f;
    }

    public static DefaultAttributeContainer.Builder createChickenAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(this.onGround ? -1 : 4) * 0.3);
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0f, 1.0f);
        if (!this.onGround && this.flapSpeed < 1.0f) {
            this.flapSpeed = 1.0f;
        }
        this.flapSpeed = (float)((double)this.flapSpeed * 0.9);
        Vec3d lv = this.getVelocity();
        if (!this.onGround && lv.y < 0.0) {
            this.setVelocity(lv.multiply(1.0, 0.6, 1.0));
        }
        this.flapProgress += this.flapSpeed * 2.0f;
        if (!this.world.isClient && this.isAlive() && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            this.dropItem(Items.EGG);
            this.eggLayTime = this.random.nextInt(6000) + 6000;
        }
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CHICKEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15f, 1.0f);
    }

    @Override
    public ChickenEntity createChild(PassiveEntity arg) {
        return EntityType.CHICKEN.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return BREEDING_INGREDIENT.test(arg);
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        if (this.hasJockey()) {
            return 10;
        }
        return super.getCurrentExperience(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.jockey = arg.getBoolean("IsChickenJockey");
        if (arg.contains("EggLayTime")) {
            this.eggLayTime = arg.getInt("EggLayTime");
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putBoolean("IsChickenJockey", this.jockey);
        arg.putInt("EggLayTime", this.eggLayTime);
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return this.hasJockey();
    }

    @Override
    public void updatePassengerPosition(Entity arg) {
        super.updatePassengerPosition(arg);
        float f = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
        float g = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
        float h = 0.1f;
        float i = 0.0f;
        arg.updatePosition(this.getX() + (double)(0.1f * f), this.getBodyY(0.5) + arg.getHeightOffset() + 0.0, this.getZ() - (double)(0.1f * g));
        if (arg instanceof LivingEntity) {
            ((LivingEntity)arg).bodyYaw = this.bodyYaw;
        }
    }

    public boolean hasJockey() {
        return this.jockey;
    }

    public void setHasJockey(boolean bl) {
        this.jockey = bl;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }
}

