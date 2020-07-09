/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5425;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class HorseBaseEntity
extends AnimalEntity
implements InventoryChangedListener,
JumpingMount,
Saddleable {
    private static final Predicate<LivingEntity> IS_BRED_HORSE = arg -> arg instanceof HorseBaseEntity && ((HorseBaseEntity)arg).isBred();
    private static final TargetPredicate PARENT_HORSE_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().includeTeammates().includeHidden().setPredicate(IS_BRED_HORSE);
    private static final Ingredient field_25374 = Ingredient.ofItems(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private int eatingGrassTicks;
    private int eatingTicks;
    private int angryTicks;
    public int tailWagTicks;
    public int field_6958;
    protected boolean inAir;
    protected SimpleInventory items;
    protected int temper;
    protected float jumpStrength;
    private boolean jumping;
    private float eatingGrassAnimationProgress;
    private float lastEatingGrassAnimationProgress;
    private float angryAnimationProgress;
    private float lastAngryAnimationProgress;
    private float eatingAnimationProgress;
    private float lastEatingAnimationProgress;
    protected boolean playExtraHorseSounds = true;
    protected int soundTicks;

    protected HorseBaseEntity(EntityType<? extends HorseBaseEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.stepHeight = 1.0f;
        this.onChestedStatusChanged();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, HorseBaseEntity.class));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HORSE_FLAGS, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    protected boolean getHorseFlag(int i) {
        return (this.dataTracker.get(HORSE_FLAGS) & i) != 0;
    }

    protected void setHorseFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(HORSE_FLAGS);
        if (bl) {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b | i));
        } else {
            this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~i));
        }
    }

    public boolean isTame() {
        return this.getHorseFlag(2);
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uUID) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
    }

    public boolean isInAir() {
        return this.inAir;
    }

    public void setTame(boolean bl) {
        this.setHorseFlag(2, bl);
    }

    public void setInAir(boolean bl) {
        this.inAir = bl;
    }

    @Override
    protected void updateForLeashLength(float f) {
        if (f > 6.0f && this.isEatingGrass()) {
            this.setEatingGrass(false);
        }
    }

    public boolean isEatingGrass() {
        return this.getHorseFlag(16);
    }

    public boolean isAngry() {
        return this.getHorseFlag(32);
    }

    public boolean isBred() {
        return this.getHorseFlag(8);
    }

    public void setBred(boolean bl) {
        this.setHorseFlag(8, bl);
    }

    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    @Override
    public void saddle(@Nullable SoundCategory arg) {
        this.items.setStack(0, new ItemStack(Items.SADDLE));
        if (arg != null) {
            this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_HORSE_SADDLE, arg, 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.getHorseFlag(4);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int i) {
        this.temper = i;
    }

    public int addTemper(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxTemper());
        this.setTemper(j);
        return j;
    }

    @Override
    public boolean isPushable() {
        return !this.hasPassengers();
    }

    private void playEatingAnimation() {
        SoundEvent lv;
        this.setEating();
        if (!this.isSilent() && (lv = this.getEatSound()) != null) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), lv, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        int i;
        if (f > 1.0f) {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4f, 1.0f);
        }
        if ((i = this.computeFallDamage(f, g)) <= 0) {
            return false;
        }
        this.damage(DamageSource.FALL, i);
        if (this.hasPassengers()) {
            for (Entity lv : this.getPassengersDeep()) {
                lv.damage(DamageSource.FALL, i);
            }
        }
        this.playBlockFallSound();
        return true;
    }

    @Override
    protected int computeFallDamage(float f, float g) {
        return MathHelper.ceil((f * 0.5f - 3.0f) * g);
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void onChestedStatusChanged() {
        SimpleInventory lv = this.items;
        this.items = new SimpleInventory(this.getInventorySize());
        if (lv != null) {
            lv.removeListener(this);
            int i = Math.min(lv.size(), this.items.size());
            for (int j = 0; j < i; ++j) {
                ItemStack lv2 = lv.getStack(j);
                if (lv2.isEmpty()) continue;
                this.items.setStack(j, lv2.copy());
            }
        }
        this.items.addListener(this);
        this.updateSaddle();
    }

    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        this.setHorseFlag(4, !this.items.getStack(0).isEmpty());
    }

    @Override
    public void onInventoryChanged(Inventory arg) {
        boolean bl = this.isSaddled();
        this.updateSaddle();
        if (this.age > 20 && !bl && this.isSaddled()) {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getJumpStrength() {
        return this.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
    }

    @Nullable
    protected SoundEvent getEatSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource arg) {
        if (this.random.nextInt(3) == 0) {
            this.updateAnger();
        }
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.updateAnger();
        }
        return null;
    }

    @Nullable
    protected SoundEvent getAngrySound() {
        this.updateAnger();
        return null;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        if (arg2.getMaterial().isLiquid()) {
            return;
        }
        BlockState lv = this.world.getBlockState(arg.up());
        BlockSoundGroup lv2 = arg2.getSoundGroup();
        if (lv.isOf(Blocks.SNOW)) {
            lv2 = lv.getSoundGroup();
        }
        if (this.hasPassengers() && this.playExtraHorseSounds) {
            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                this.playWalkSound(lv2);
            } else if (this.soundTicks <= 5) {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, lv2.getVolume() * 0.15f, lv2.getPitch());
            }
        } else if (lv2 == BlockSoundGroup.WOOD) {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, lv2.getVolume() * 0.15f, lv2.getPitch());
        } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP, lv2.getVolume() * 0.15f, lv2.getPitch());
        }
    }

    protected void playWalkSound(BlockSoundGroup arg) {
        this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, arg.getVolume() * 0.15f, arg.getPitch());
    }

    public static DefaultAttributeContainer.Builder createBaseHorseAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.HORSE_JUMP_STRENGTH).add(EntityAttributes.GENERIC_MAX_HEALTH, 53.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225f);
    }

    @Override
    public int getLimitPerChunk() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 400;
    }

    public void openInventory(PlayerEntity arg) {
        if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(arg)) && this.isTame()) {
            arg.openHorseInventory(this, this.items);
        }
    }

    public ActionResult method_30009(PlayerEntity arg, ItemStack arg2) {
        boolean bl = this.receiveFood(arg, arg2);
        if (!arg.abilities.creativeMode) {
            arg2.decrement(1);
        }
        if (this.world.isClient) {
            return ActionResult.CONSUME;
        }
        return bl ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    protected boolean receiveFood(PlayerEntity arg, ItemStack arg2) {
        boolean bl = false;
        float f = 0.0f;
        int i = 0;
        int j = 0;
        Item lv = arg2.getItem();
        if (lv == Items.WHEAT) {
            f = 2.0f;
            i = 20;
            j = 3;
        } else if (lv == Items.SUGAR) {
            f = 1.0f;
            i = 30;
            j = 3;
        } else if (lv == Blocks.HAY_BLOCK.asItem()) {
            f = 20.0f;
            i = 180;
        } else if (lv == Items.APPLE) {
            f = 3.0f;
            i = 60;
            j = 3;
        } else if (lv == Items.GOLDEN_CARROT) {
            f = 4.0f;
            i = 60;
            j = 5;
            if (!this.world.isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(arg);
            }
        } else if (lv == Items.GOLDEN_APPLE || lv == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0f;
            i = 240;
            j = 10;
            if (!this.world.isClient && this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
                bl = true;
                this.lovePlayer(arg);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(i);
            }
            bl = true;
        }
        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.world.isClient) {
                this.addTemper(j);
            }
        }
        if (bl) {
            this.playEatingAnimation();
        }
        return bl;
    }

    protected void putPlayerOnBack(PlayerEntity arg) {
        this.setEatingGrass(false);
        this.setAngry(false);
        if (!this.world.isClient) {
            arg.yaw = this.yaw;
            arg.pitch = this.pitch;
            arg.startRiding(this);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.hasPassengers() && this.isSaddled() || this.isEatingGrass() || this.isAngry();
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return field_25374.test(arg);
    }

    private void wagTail() {
        this.tailWagTicks = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.items == null) {
            return;
        }
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack lv = this.items.getStack(i);
            if (lv.isEmpty() || EnchantmentHelper.hasVanishingCurse(lv)) continue;
            this.dropStack(lv);
        }
    }

    @Override
    public void tickMovement() {
        if (this.random.nextInt(200) == 0) {
            this.wagTail();
        }
        super.tickMovement();
        if (this.world.isClient || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.eatsGrass()) {
            if (!this.isEatingGrass() && !this.hasPassengers() && this.random.nextInt(300) == 0 && this.world.getBlockState(this.getBlockPos().down()).isOf(Blocks.GRASS_BLOCK)) {
                this.setEatingGrass(true);
            }
            if (this.isEatingGrass() && ++this.eatingGrassTicks > 50) {
                this.eatingGrassTicks = 0;
                this.setEatingGrass(false);
            }
        }
        this.walkToParent();
    }

    protected void walkToParent() {
        HorseBaseEntity lv;
        if (this.isBred() && this.isBaby() && !this.isEatingGrass() && (lv = this.world.getClosestEntity(HorseBaseEntity.class, PARENT_HORSE_PREDICATE, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().expand(16.0))) != null && this.squaredDistanceTo(lv) > 4.0) {
            this.navigation.findPathTo(lv, 0);
        }
    }

    public boolean eatsGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
            this.eatingTicks = 0;
            this.setHorseFlag(64, false);
        }
        if ((this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) && this.angryTicks > 0 && ++this.angryTicks > 20) {
            this.angryTicks = 0;
            this.setAngry(false);
        }
        if (this.tailWagTicks > 0 && ++this.tailWagTicks > 8) {
            this.tailWagTicks = 0;
        }
        if (this.field_6958 > 0) {
            ++this.field_6958;
            if (this.field_6958 > 300) {
                this.field_6958 = 0;
            }
        }
        this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
        if (this.isEatingGrass()) {
            this.eatingGrassAnimationProgress += (1.0f - this.eatingGrassAnimationProgress) * 0.4f + 0.05f;
            if (this.eatingGrassAnimationProgress > 1.0f) {
                this.eatingGrassAnimationProgress = 1.0f;
            }
        } else {
            this.eatingGrassAnimationProgress += (0.0f - this.eatingGrassAnimationProgress) * 0.4f - 0.05f;
            if (this.eatingGrassAnimationProgress < 0.0f) {
                this.eatingGrassAnimationProgress = 0.0f;
            }
        }
        this.lastAngryAnimationProgress = this.angryAnimationProgress;
        if (this.isAngry()) {
            this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress = 0.0f;
            this.angryAnimationProgress += (1.0f - this.angryAnimationProgress) * 0.4f + 0.05f;
            if (this.angryAnimationProgress > 1.0f) {
                this.angryAnimationProgress = 1.0f;
            }
        } else {
            this.jumping = false;
            this.angryAnimationProgress += (0.8f * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress) * 0.6f - 0.05f;
            if (this.angryAnimationProgress < 0.0f) {
                this.angryAnimationProgress = 0.0f;
            }
        }
        this.lastEatingAnimationProgress = this.eatingAnimationProgress;
        if (this.getHorseFlag(64)) {
            this.eatingAnimationProgress += (1.0f - this.eatingAnimationProgress) * 0.7f + 0.05f;
            if (this.eatingAnimationProgress > 1.0f) {
                this.eatingAnimationProgress = 1.0f;
            }
        } else {
            this.eatingAnimationProgress += (0.0f - this.eatingAnimationProgress) * 0.7f - 0.05f;
            if (this.eatingAnimationProgress < 0.0f) {
                this.eatingAnimationProgress = 0.0f;
            }
        }
    }

    private void setEating() {
        if (!this.world.isClient) {
            this.eatingTicks = 1;
            this.setHorseFlag(64, true);
        }
    }

    public void setEatingGrass(boolean bl) {
        this.setHorseFlag(16, bl);
    }

    public void setAngry(boolean bl) {
        if (bl) {
            this.setEatingGrass(false);
        }
        this.setHorseFlag(32, bl);
    }

    private void updateAnger() {
        if (this.isLogicalSideForUpdatingMovement() || this.canMoveVoluntarily()) {
            this.angryTicks = 1;
            this.setAngry(true);
        }
    }

    public void playAngrySound() {
        if (!this.isAngry()) {
            this.updateAnger();
            SoundEvent lv = this.getAngrySound();
            if (lv != null) {
                this.playSound(lv, this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    public boolean bondWithPlayer(PlayerEntity arg) {
        this.setOwnerUuid(arg.getUuid());
        this.setTame(true);
        if (arg instanceof ServerPlayerEntity) {
            Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)arg, this);
        }
        this.world.sendEntityStatus(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vec3d arg) {
        if (!this.isAlive()) {
            return;
        }
        if (!(this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled())) {
            this.flyingSpeed = 0.02f;
            super.travel(arg);
            return;
        }
        LivingEntity lv = (LivingEntity)this.getPrimaryPassenger();
        this.prevYaw = this.yaw = lv.yaw;
        this.pitch = lv.pitch * 0.5f;
        this.setRotation(this.yaw, this.pitch);
        this.headYaw = this.bodyYaw = this.yaw;
        float f = lv.sidewaysSpeed * 0.5f;
        float g = lv.forwardSpeed;
        if (g <= 0.0f) {
            g *= 0.25f;
            this.soundTicks = 0;
        }
        if (this.onGround && this.jumpStrength == 0.0f && this.isAngry() && !this.jumping) {
            f = 0.0f;
            g = 0.0f;
        }
        if (this.jumpStrength > 0.0f && !this.isInAir() && this.onGround) {
            double h;
            double d = this.getJumpStrength() * (double)this.jumpStrength * (double)this.getJumpVelocityMultiplier();
            if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
                double e = d + (double)((float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f);
            } else {
                h = d;
            }
            Vec3d lv2 = this.getVelocity();
            this.setVelocity(lv2.x, h, lv2.z);
            this.setInAir(true);
            this.velocityDirty = true;
            if (g > 0.0f) {
                float i = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
                float j = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
                this.setVelocity(this.getVelocity().add(-0.4f * i * this.jumpStrength, 0.0, 0.4f * j * this.jumpStrength));
            }
            this.jumpStrength = 0.0f;
        }
        this.flyingSpeed = this.getMovementSpeed() * 0.1f;
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setMovementSpeed((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            super.travel(new Vec3d(f, arg.y, g));
        } else if (lv instanceof PlayerEntity) {
            this.setVelocity(Vec3d.ZERO);
        }
        if (this.onGround) {
            this.jumpStrength = 0.0f;
            this.setInAir(false);
        }
        this.method_29242(this, false);
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putBoolean("EatingHaystack", this.isEatingGrass());
        arg.putBoolean("Bred", this.isBred());
        arg.putInt("Temper", this.getTemper());
        arg.putBoolean("Tame", this.isTame());
        if (this.getOwnerUuid() != null) {
            arg.putUuid("Owner", this.getOwnerUuid());
        }
        if (!this.items.getStack(0).isEmpty()) {
            arg.put("SaddleItem", this.items.getStack(0).toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        ItemStack lv;
        UUID uUID2;
        super.readCustomDataFromTag(arg);
        this.setEatingGrass(arg.getBoolean("EatingHaystack"));
        this.setBred(arg.getBoolean("Bred"));
        this.setTemper(arg.getInt("Temper"));
        this.setTame(arg.getBoolean("Tame"));
        if (arg.containsUuid("Owner")) {
            UUID uUID = arg.getUuid("Owner");
        } else {
            String string = arg.getString("Owner");
            uUID2 = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID2 != null) {
            this.setOwnerUuid(uUID2);
        }
        if (arg.contains("SaddleItem", 10) && (lv = ItemStack.fromTag(arg.getCompound("SaddleItem"))).getItem() == Items.SADDLE) {
            this.items.setStack(0, lv);
        }
        this.updateSaddle();
    }

    @Override
    public boolean canBreedWith(AnimalEntity arg) {
        return false;
    }

    protected boolean canBreed() {
        return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        return null;
    }

    protected void setChildAttributes(PassiveEntity arg, HorseBaseEntity arg2) {
        double d = this.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + arg.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + (double)this.getChildHealthBonus();
        arg2.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(d / 3.0);
        double e = this.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH) + arg.getAttributeBaseValue(EntityAttributes.HORSE_JUMP_STRENGTH) + this.getChildJumpStrengthBonus();
        arg2.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(e / 3.0);
        double f = this.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + arg.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + this.getChildMovementSpeedBonus();
        arg2.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(f / 3.0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getPrimaryPassenger() instanceof LivingEntity;
    }

    @Environment(value=EnvType.CLIENT)
    public float getEatingGrassAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastEatingGrassAnimationProgress, this.eatingGrassAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getAngryAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastAngryAnimationProgress, this.angryAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getEatingAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastEatingAnimationProgress, this.eatingAnimationProgress);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setJumpStrength(int i) {
        if (!this.isSaddled()) {
            return;
        }
        if (i < 0) {
            i = 0;
        } else {
            this.jumping = true;
            this.updateAnger();
        }
        this.jumpStrength = i >= 90 ? 1.0f : 0.4f + 0.4f * (float)i / 90.0f;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void startJumping(int i) {
        this.jumping = true;
        this.updateAnger();
        this.playJumpSound();
    }

    @Override
    public void stopJumping() {
    }

    @Environment(value=EnvType.CLIENT)
    protected void spawnPlayerReactionParticles(boolean bl) {
        DefaultParticleType lv = bl ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(lv, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 7) {
            this.spawnPlayerReactionParticles(true);
        } else if (b == 6) {
            this.spawnPlayerReactionParticles(false);
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    public void updatePassengerPosition(Entity arg) {
        super.updatePassengerPosition(arg);
        if (arg instanceof MobEntity) {
            MobEntity lv = (MobEntity)arg;
            this.bodyYaw = lv.bodyYaw;
        }
        if (this.lastAngryAnimationProgress > 0.0f) {
            float f = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
            float g = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
            float h = 0.7f * this.lastAngryAnimationProgress;
            float i = 0.15f * this.lastAngryAnimationProgress;
            arg.updatePosition(this.getX() + (double)(h * f), this.getY() + this.getMountedHeightOffset() + arg.getHeightOffset() + (double)i, this.getZ() - (double)(h * g));
            if (arg instanceof LivingEntity) {
                ((LivingEntity)arg).bodyYaw = this.bodyYaw;
            }
        }
    }

    protected float getChildHealthBonus() {
        return 15.0f + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
    }

    protected double getChildJumpStrengthBonus() {
        return (double)0.4f + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
    }

    protected double getChildMovementSpeedBonus() {
        return ((double)0.45f + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.95f;
    }

    public boolean canEquip() {
        return false;
    }

    public boolean setSaddled() {
        return !this.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
    }

    public boolean canEquip(ItemStack arg) {
        return false;
    }

    @Override
    public boolean equip(int i, ItemStack arg) {
        int j = i - 400;
        if (j >= 0 && j < 2 && j < this.items.size()) {
            if (j == 0 && arg.getItem() != Items.SADDLE) {
                return false;
            }
            if (!(j != 1 || this.canEquip() && this.canEquip(arg))) {
                return false;
            }
            this.items.setStack(j, arg);
            this.updateSaddle();
            return true;
        }
        int k = i - 500 + 2;
        if (k >= 2 && k < this.items.size()) {
            this.items.setStack(k, arg);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Nullable
    private Vec3d method_27930(Vec3d arg, LivingEntity arg2) {
        double d = this.getX() + arg.x;
        double e = this.getBoundingBox().minY;
        double f = this.getZ() + arg.z;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        block0: for (EntityPose lv2 : arg2.getPoses()) {
            lv.set(d, e, f);
            double g = this.getBoundingBox().maxY + 0.75;
            do {
                Vec3d lv4;
                Box lv3;
                double h = this.world.method_30347(lv);
                if ((double)lv.getY() + h > g) continue block0;
                if (Dismounting.canDismountInBlock(h) && Dismounting.canPlaceEntityAt(this.world, arg2, (lv3 = arg2.getBoundingBox(lv2)).offset(lv4 = new Vec3d(d, (double)lv.getY() + h, f)))) {
                    arg2.setPose(lv2);
                    return lv4;
                }
                lv.move(Direction.UP);
            } while ((double)lv.getY() < g);
        }
        return null;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity arg) {
        Vec3d lv = HorseBaseEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), this.yaw + (arg.getMainArm() == Arm.RIGHT ? 90.0f : -90.0f));
        Vec3d lv2 = this.method_27930(lv, arg);
        if (lv2 != null) {
            return lv2;
        }
        Vec3d lv3 = HorseBaseEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), this.yaw + (arg.getMainArm() == Arm.LEFT ? 90.0f : -90.0f));
        Vec3d lv4 = this.method_27930(lv3, arg);
        if (lv4 != null) {
            return lv4;
        }
        return this.getPos();
    }

    protected void initAttributes() {
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData(0.2f);
        }
        this.initAttributes();
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }
}

