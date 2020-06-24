/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class FoxEntity
extends AnimalEntity {
    private static final TrackedData<Integer> TYPE = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> FOX_FLAGS = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Optional<UUID>> OTHER_TRUSTED = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = arg -> !arg.cannotPickup() && arg.isAlive();
    private static final Predicate<Entity> JUST_ATTACKED_SOMETHING_FILTER = arg -> {
        if (arg instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)arg;
            return lv.getAttacking() != null && lv.getLastAttackTime() < lv.age + 600;
        }
        return false;
    };
    private static final Predicate<Entity> CHICKEN_AND_RABBIT_FILTER = arg -> arg instanceof ChickenEntity || arg instanceof RabbitEntity;
    private static final Predicate<Entity> NOTICEABLE_PLAYER_FILTER = arg -> !arg.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test((Entity)arg);
    private Goal followChickenAndRabbitGoal;
    private Goal followBabyTurtleGoal;
    private Goal followFishGoal;
    private float headRollProgress;
    private float lastHeadRollProgress;
    private float extraRollingHeight;
    private float lastExtraRollingHeight;
    private int eatingTime;

    public FoxEntity(EntityType<? extends FoxEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.lookControl = new FoxLookControl();
        this.moveControl = new FoxMoveControl();
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0f);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(OTHER_TRUSTED, Optional.empty());
        this.dataTracker.startTracking(TYPE, 0);
        this.dataTracker.startTracking(FOX_FLAGS, (byte)0);
    }

    @Override
    protected void initGoals() {
        this.followChickenAndRabbitGoal = new FollowTargetGoal<AnimalEntity>(this, AnimalEntity.class, 10, false, false, arg -> arg instanceof ChickenEntity || arg instanceof RabbitEntity);
        this.followBabyTurtleGoal = new FollowTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
        this.followFishGoal = new FollowTargetGoal<FishEntity>(this, FishEntity.class, 20, false, false, arg -> arg instanceof SchoolingFishEntity);
        this.goalSelector.add(0, new FoxSwimGoal());
        this.goalSelector.add(1, new StopWanderingGoal());
        this.goalSelector.add(2, new EscapeWhenNotAggressiveGoal(2.2));
        this.goalSelector.add(3, new MateGoal(1.0));
        this.goalSelector.add(4, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 1.6, 1.4, arg -> NOTICEABLE_PLAYER_FILTER.test((Entity)arg) && !this.canTrust(arg.getUuid()) && !this.isAggressive()));
        this.goalSelector.add(4, new FleeEntityGoal<WolfEntity>(this, WolfEntity.class, 8.0f, 1.6, 1.4, arg -> !((WolfEntity)arg).isTamed() && !this.isAggressive()));
        this.goalSelector.add(4, new FleeEntityGoal<PolarBearEntity>(this, PolarBearEntity.class, 8.0f, 1.6, 1.4, arg -> !this.isAggressive()));
        this.goalSelector.add(5, new MoveToHuntGoal());
        this.goalSelector.add(6, new JumpChasingGoal());
        this.goalSelector.add(6, new AvoidDaylightGoal(1.25));
        this.goalSelector.add(7, new AttackGoal((double)1.2f, true));
        this.goalSelector.add(7, new DelayedCalmDownGoal());
        this.goalSelector.add(8, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(9, new GoToVillageGoal(32, 200));
        this.goalSelector.add(10, new EatSweetBerriesGoal((double)1.2f, 12, 2));
        this.goalSelector.add(10, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(11, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(11, new PickupItemGoal());
        this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
        this.goalSelector.add(13, new SitDownAndLookAroundGoal());
        this.targetSelector.add(3, new DefendFriendGoal(LivingEntity.class, false, false, arg -> JUST_ATTACKED_SOMETHING_FILTER.test((Entity)arg) && !this.canTrust(arg.getUuid())));
    }

    @Override
    public SoundEvent getEatSound(ItemStack arg) {
        return SoundEvents.ENTITY_FOX_EAT;
    }

    @Override
    public void tickMovement() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            LivingEntity lv3;
            ++this.eatingTime;
            ItemStack lv = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (this.canEat(lv)) {
                if (this.eatingTime > 600) {
                    ItemStack lv2 = lv.finishUsing(this.world, this);
                    if (!lv2.isEmpty()) {
                        this.equipStack(EquipmentSlot.MAINHAND, lv2);
                    }
                    this.eatingTime = 0;
                } else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1f) {
                    this.playSound(this.getEatSound(lv), 1.0f, 1.0f);
                    this.world.sendEntityStatus(this, (byte)45);
                }
            }
            if ((lv3 = this.getTarget()) == null || !lv3.isAlive()) {
                this.setCrouching(false);
                this.setRollingHead(false);
            }
        }
        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0f;
            this.forwardSpeed = 0.0f;
        }
        super.tickMovement();
        if (this.isAggressive() && this.random.nextFloat() < 0.05f) {
            this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0f, 1.0f);
        }
    }

    @Override
    protected boolean isImmobile() {
        return this.isDead();
    }

    private boolean canEat(ItemStack arg) {
        return arg.getItem().isFood() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }

    @Override
    protected void initEquipment(LocalDifficulty arg) {
        if (this.random.nextFloat() < 0.2f) {
            ItemStack lv6;
            float f = this.random.nextFloat();
            if (f < 0.05f) {
                ItemStack lv = new ItemStack(Items.EMERALD);
            } else if (f < 0.2f) {
                ItemStack lv2 = new ItemStack(Items.EGG);
            } else if (f < 0.4f) {
                ItemStack lv3 = this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
            } else if (f < 0.6f) {
                ItemStack lv4 = new ItemStack(Items.WHEAT);
            } else if (f < 0.8f) {
                ItemStack lv5 = new ItemStack(Items.LEATHER);
            } else {
                lv6 = new ItemStack(Items.FEATHER);
            }
            this.equipStack(EquipmentSlot.MAINHAND, lv6);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 45) {
            ItemStack lv = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!lv.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3d lv2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0).rotateX(-this.pitch * ((float)Math.PI / 180)).rotateY(-this.yaw * ((float)Math.PI / 180));
                    this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, lv), this.getX() + this.getRotationVector().x / 2.0, this.getY(), this.getZ() + this.getRotationVector().z / 2.0, lv2.x, lv2.y + 0.05, lv2.z);
                }
            }
        } else {
            super.handleStatus(b);
        }
    }

    public static DefaultAttributeContainer.Builder createFoxAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    public FoxEntity createChild(PassiveEntity arg) {
        FoxEntity lv = EntityType.FOX.create(this.world);
        lv.setType(this.random.nextBoolean() ? this.getFoxType() : ((FoxEntity)arg).getFoxType());
        return lv;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        Biome lv = arg.getBiome(this.getBlockPos());
        Type lv2 = Type.fromBiome(lv);
        boolean bl = false;
        if (arg4 instanceof FoxData) {
            lv2 = ((FoxData)arg4).type;
            if (((FoxData)arg4).getSpawnedCount() >= 2) {
                bl = true;
            }
        } else {
            arg4 = new FoxData(lv2);
        }
        this.setType(lv2);
        if (bl) {
            this.setBreedingAge(-24000);
        }
        if (arg instanceof ServerWorld) {
            this.addTypeSpecificGoals();
        }
        this.initEquipment(arg2);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    private void addTypeSpecificGoals() {
        if (this.getFoxType() == Type.RED) {
            this.targetSelector.add(4, this.followChickenAndRabbitGoal);
            this.targetSelector.add(4, this.followBabyTurtleGoal);
            this.targetSelector.add(6, this.followFishGoal);
        } else {
            this.targetSelector.add(4, this.followFishGoal);
            this.targetSelector.add(6, this.followChickenAndRabbitGoal);
            this.targetSelector.add(6, this.followBabyTurtleGoal);
        }
    }

    @Override
    protected void eat(PlayerEntity arg, ItemStack arg2) {
        if (this.isBreedingItem(arg2)) {
            this.playSound(this.getEatSound(arg2), 1.0f, 1.0f);
        }
        super.eat(arg, arg2);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        if (this.isBaby()) {
            return arg2.height * 0.85f;
        }
        return 0.4f;
    }

    public Type getFoxType() {
        return Type.fromId(this.dataTracker.get(TYPE));
    }

    private void setType(Type arg) {
        this.dataTracker.set(TYPE, arg.getId());
    }

    private List<UUID> getTrustedUuids() {
        ArrayList list = Lists.newArrayList();
        list.add(this.dataTracker.get(OWNER).orElse(null));
        list.add(this.dataTracker.get(OTHER_TRUSTED).orElse(null));
        return list;
    }

    private void addTrustedUuid(@Nullable UUID uUID) {
        if (this.dataTracker.get(OWNER).isPresent()) {
            this.dataTracker.set(OTHER_TRUSTED, Optional.ofNullable(uUID));
        } else {
            this.dataTracker.set(OWNER, Optional.ofNullable(uUID));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        List<UUID> list = this.getTrustedUuids();
        ListTag lv = new ListTag();
        for (UUID uUID : list) {
            if (uUID == null) continue;
            lv.add(NbtHelper.fromUuidNew(uUID));
        }
        arg.put("Trusted", lv);
        arg.putBoolean("Sleeping", this.isSleeping());
        arg.putString("Type", this.getFoxType().getKey());
        arg.putBoolean("Sitting", this.isSitting());
        arg.putBoolean("Crouching", this.isInSneakingPose());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        ListTag lv = arg.getList("Trusted", 11);
        for (int i = 0; i < lv.size(); ++i) {
            this.addTrustedUuid(NbtHelper.toUuidNew(lv.get(i)));
        }
        this.setSleeping(arg.getBoolean("Sleeping"));
        this.setType(Type.byName(arg.getString("Type")));
        this.setSitting(arg.getBoolean("Sitting"));
        this.setCrouching(arg.getBoolean("Crouching"));
        if (this.world instanceof ServerWorld) {
            this.addTypeSpecificGoals();
        }
    }

    public boolean isSitting() {
        return this.getFoxFlag(1);
    }

    public void setSitting(boolean bl) {
        this.setFoxFlag(1, bl);
    }

    public boolean isWalking() {
        return this.getFoxFlag(64);
    }

    private void setWalking(boolean bl) {
        this.setFoxFlag(64, bl);
    }

    private boolean isAggressive() {
        return this.getFoxFlag(128);
    }

    private void setAggressive(boolean bl) {
        this.setFoxFlag(128, bl);
    }

    @Override
    public boolean isSleeping() {
        return this.getFoxFlag(32);
    }

    private void setSleeping(boolean bl) {
        this.setFoxFlag(32, bl);
    }

    private void setFoxFlag(int i, boolean bl) {
        if (bl) {
            this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) | i));
        } else {
            this.dataTracker.set(FOX_FLAGS, (byte)(this.dataTracker.get(FOX_FLAGS) & ~i));
        }
    }

    private boolean getFoxFlag(int i) {
        return (this.dataTracker.get(FOX_FLAGS) & i) != 0;
    }

    @Override
    public boolean canPickUp(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        if (!this.getEquippedStack(lv).isEmpty()) {
            return false;
        }
        return lv == EquipmentSlot.MAINHAND && super.canPickUp(arg);
    }

    @Override
    public boolean canPickupItem(ItemStack arg) {
        Item lv = arg.getItem();
        ItemStack lv2 = this.getEquippedStack(EquipmentSlot.MAINHAND);
        return lv2.isEmpty() || this.eatingTime > 0 && lv.isFood() && !lv2.getItem().isFood();
    }

    private void spit(ItemStack arg) {
        if (arg.isEmpty() || this.world.isClient) {
            return;
        }
        ItemEntity lv = new ItemEntity(this.world, this.getX() + this.getRotationVector().x, this.getY() + 1.0, this.getZ() + this.getRotationVector().z, arg);
        lv.setPickupDelay(40);
        lv.setThrower(this.getUuid());
        this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0f, 1.0f);
        this.world.spawnEntity(lv);
    }

    private void dropItem(ItemStack arg) {
        ItemEntity lv = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), arg);
        this.world.spawnEntity(lv);
    }

    @Override
    protected void loot(ItemEntity arg) {
        ItemStack lv = arg.getStack();
        if (this.canPickupItem(lv)) {
            int i = lv.getCount();
            if (i > 1) {
                this.dropItem(lv.split(i - 1));
            }
            this.spit(this.getEquippedStack(EquipmentSlot.MAINHAND));
            this.method_29499(arg);
            this.equipStack(EquipmentSlot.MAINHAND, lv.split(1));
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(arg, lv.getCount());
            arg.remove();
            this.eatingTime = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.canMoveVoluntarily()) {
            boolean bl = this.isTouchingWater();
            if (bl || this.getTarget() != null || this.world.isThundering()) {
                this.stopSleeping();
            }
            if (bl || this.isSleeping()) {
                this.setSitting(false);
            }
            if (this.isWalking() && this.world.random.nextFloat() < 0.2f) {
                BlockPos lv = this.getBlockPos();
                BlockState lv2 = this.world.getBlockState(lv);
                this.world.syncWorldEvent(2001, lv, Block.getRawIdFromState(lv2));
            }
        }
        this.lastHeadRollProgress = this.headRollProgress;
        this.headRollProgress = this.isRollingHead() ? (this.headRollProgress += (1.0f - this.headRollProgress) * 0.4f) : (this.headRollProgress += (0.0f - this.headRollProgress) * 0.4f);
        this.lastExtraRollingHeight = this.extraRollingHeight;
        if (this.isInSneakingPose()) {
            this.extraRollingHeight += 0.2f;
            if (this.extraRollingHeight > 3.0f) {
                this.extraRollingHeight = 3.0f;
            }
        } else {
            this.extraRollingHeight = 0.0f;
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return arg.getItem() == Items.SWEET_BERRIES;
    }

    @Override
    protected void onPlayerSpawnedChild(PlayerEntity arg, MobEntity arg2) {
        ((FoxEntity)arg2).addTrustedUuid(arg.getUuid());
    }

    public boolean isChasing() {
        return this.getFoxFlag(16);
    }

    public void setChasing(boolean bl) {
        this.setFoxFlag(16, bl);
    }

    public boolean isFullyCrouched() {
        return this.extraRollingHeight == 3.0f;
    }

    public void setCrouching(boolean bl) {
        this.setFoxFlag(4, bl);
    }

    @Override
    public boolean isInSneakingPose() {
        return this.getFoxFlag(4);
    }

    public void setRollingHead(boolean bl) {
        this.setFoxFlag(8, bl);
    }

    public boolean isRollingHead() {
        return this.getFoxFlag(8);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadRoll(float f) {
        return MathHelper.lerp(f, this.lastHeadRollProgress, this.headRollProgress) * 0.11f * (float)Math.PI;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBodyRotationHeightOffset(float f) {
        return MathHelper.lerp(f, this.lastExtraRollingHeight, this.extraRollingHeight);
    }

    @Override
    public void setTarget(@Nullable LivingEntity arg) {
        if (this.isAggressive() && arg == null) {
            this.setAggressive(false);
        }
        super.setTarget(arg);
    }

    @Override
    protected int computeFallDamage(float f, float g) {
        return MathHelper.ceil((f - 5.0f) * g);
    }

    private void stopSleeping() {
        this.setSleeping(false);
    }

    private void stopActions() {
        this.setRollingHead(false);
        this.setCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setAggressive(false);
        this.setWalking(false);
    }

    private boolean wantsToPickupItem() {
        return !this.isSleeping() && !this.isSitting() && !this.isWalking();
    }

    @Override
    public void playAmbientSound() {
        SoundEvent lv = this.getAmbientSound();
        if (lv == SoundEvents.ENTITY_FOX_SCREECH) {
            this.playSound(lv, 2.0f, this.getSoundPitch());
        } else {
            super.playAmbientSound();
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        List<Entity> list;
        if (this.isSleeping()) {
            return SoundEvents.ENTITY_FOX_SLEEP;
        }
        if (!this.world.isDay() && this.random.nextFloat() < 0.1f && (list = this.world.getEntities(PlayerEntity.class, this.getBoundingBox().expand(16.0, 16.0, 16.0), EntityPredicates.EXCEPT_SPECTATOR)).isEmpty()) {
            return SoundEvents.ENTITY_FOX_SCREECH;
        }
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }

    private boolean canTrust(UUID uUID) {
        return this.getTrustedUuids().contains(uUID);
    }

    @Override
    protected void drop(DamageSource arg) {
        ItemStack lv = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!lv.isEmpty()) {
            this.dropStack(lv);
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        super.drop(arg);
    }

    public static boolean canJumpChase(FoxEntity arg, LivingEntity arg2) {
        double d = arg2.getZ() - arg.getZ();
        double e = arg2.getX() - arg.getX();
        double f = d / e;
        int i = 6;
        for (int j = 0; j < 6; ++j) {
            double g = f == 0.0 ? 0.0 : d * (double)((float)j / 6.0f);
            double h = f == 0.0 ? e * (double)((float)j / 6.0f) : g / f;
            for (int k = 1; k < 4; ++k) {
                if (arg.world.getBlockState(new BlockPos(arg.getX() + h, arg.getY() + (double)k, arg.getZ() + g)).getMaterial().isReplaceable()) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.55f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    class LookAtEntityGoal
    extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
        public LookAtEntityGoal(MobEntity arg2, Class<? extends LivingEntity> class_, float f) {
            super(arg2, class_, f);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
        }
    }

    class FollowParentGoal
    extends net.minecraft.entity.ai.goal.FollowParentGoal {
        private final FoxEntity fox;

        public FollowParentGoal(FoxEntity arg2, double d) {
            super(arg2, d);
            this.fox = arg2;
        }

        @Override
        public boolean canStart() {
            return !this.fox.isAggressive() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.fox.isAggressive() && super.shouldContinue();
        }

        @Override
        public void start() {
            this.fox.stopActions();
            super.start();
        }
    }

    public class FoxLookControl
    extends LookControl {
        public FoxLookControl() {
            super(FoxEntity.this);
        }

        @Override
        public void tick() {
            if (!FoxEntity.this.isSleeping()) {
                super.tick();
            }
        }

        @Override
        protected boolean shouldStayHorizontal() {
            return !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() & !FoxEntity.this.isWalking();
        }
    }

    public class JumpChasingGoal
    extends DiveJumpingGoal {
        @Override
        public boolean canStart() {
            if (!FoxEntity.this.isFullyCrouched()) {
                return false;
            }
            LivingEntity lv = FoxEntity.this.getTarget();
            if (lv == null || !lv.isAlive()) {
                return false;
            }
            if (lv.getMovementDirection() != lv.getHorizontalFacing()) {
                return false;
            }
            boolean bl = FoxEntity.canJumpChase(FoxEntity.this, lv);
            if (!bl) {
                FoxEntity.this.getNavigation().findPathTo(lv, 0);
                FoxEntity.this.setCrouching(false);
                FoxEntity.this.setRollingHead(false);
            }
            return bl;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity lv = FoxEntity.this.getTarget();
            if (lv == null || !lv.isAlive()) {
                return false;
            }
            double d = FoxEntity.this.getVelocity().y;
            return !(d * d < (double)0.05f && Math.abs(FoxEntity.this.pitch) < 15.0f && FoxEntity.this.onGround || FoxEntity.this.isWalking());
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public void start() {
            FoxEntity.this.setJumping(true);
            FoxEntity.this.setChasing(true);
            FoxEntity.this.setRollingHead(false);
            LivingEntity lv = FoxEntity.this.getTarget();
            FoxEntity.this.getLookControl().lookAt(lv, 60.0f, 30.0f);
            Vec3d lv2 = new Vec3d(lv.getX() - FoxEntity.this.getX(), lv.getY() - FoxEntity.this.getY(), lv.getZ() - FoxEntity.this.getZ()).normalize();
            FoxEntity.this.setVelocity(FoxEntity.this.getVelocity().add(lv2.x * 0.8, 0.9, lv2.z * 0.8));
            FoxEntity.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.extraRollingHeight = 0.0f;
            FoxEntity.this.lastExtraRollingHeight = 0.0f;
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setChasing(false);
        }

        @Override
        public void tick() {
            LivingEntity lv = FoxEntity.this.getTarget();
            if (lv != null) {
                FoxEntity.this.getLookControl().lookAt(lv, 60.0f, 30.0f);
            }
            if (!FoxEntity.this.isWalking()) {
                Vec3d lv2 = FoxEntity.this.getVelocity();
                if (lv2.y * lv2.y < (double)0.03f && FoxEntity.this.pitch != 0.0f) {
                    FoxEntity.this.pitch = MathHelper.lerpAngle(FoxEntity.this.pitch, 0.0f, 0.2f);
                } else {
                    double d = Math.sqrt(Entity.squaredHorizontalLength(lv2));
                    double e = Math.signum(-lv2.y) * Math.acos(d / lv2.length()) * 57.2957763671875;
                    FoxEntity.this.pitch = (float)e;
                }
            }
            if (lv != null && FoxEntity.this.distanceTo(lv) <= 2.0f) {
                FoxEntity.this.tryAttack(lv);
            } else if (FoxEntity.this.pitch > 0.0f && FoxEntity.this.onGround && (float)FoxEntity.this.getVelocity().y != 0.0f && FoxEntity.this.world.getBlockState(FoxEntity.this.getBlockPos()).isOf(Blocks.SNOW)) {
                FoxEntity.this.pitch = 60.0f;
                FoxEntity.this.setTarget(null);
                FoxEntity.this.setWalking(true);
            }
        }
    }

    class FoxSwimGoal
    extends SwimGoal {
        public FoxSwimGoal() {
            super(FoxEntity.this);
        }

        @Override
        public void start() {
            super.start();
            FoxEntity.this.stopActions();
        }

        @Override
        public boolean canStart() {
            return FoxEntity.this.isTouchingWater() && FoxEntity.this.getFluidHeight(FluidTags.WATER) > 0.25 || FoxEntity.this.isInLava();
        }
    }

    class GoToVillageGoal
    extends net.minecraft.entity.ai.goal.GoToVillageGoal {
        public GoToVillageGoal(int i, int j) {
            super(FoxEntity.this, j);
        }

        @Override
        public void start() {
            FoxEntity.this.stopActions();
            super.start();
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.canGoToVillage();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.canGoToVillage();
        }

        private boolean canGoToVillage() {
            return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
        }
    }

    class EscapeWhenNotAggressiveGoal
    extends EscapeDangerGoal {
        public EscapeWhenNotAggressiveGoal(double d) {
            super(FoxEntity.this, d);
        }

        @Override
        public boolean canStart() {
            return !FoxEntity.this.isAggressive() && super.canStart();
        }
    }

    class StopWanderingGoal
    extends Goal {
        int timer;

        public StopWanderingGoal() {
            this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return FoxEntity.this.isWalking();
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart() && this.timer > 0;
        }

        @Override
        public void start() {
            this.timer = 40;
        }

        @Override
        public void stop() {
            FoxEntity.this.setWalking(false);
        }

        @Override
        public void tick() {
            --this.timer;
        }
    }

    public static class FoxData
    extends PassiveEntity.PassiveData {
        public final Type type;

        public FoxData(Type arg) {
            this.setBabyAllowed(false);
            this.type = arg;
        }
    }

    public class EatSweetBerriesGoal
    extends MoveToTargetPosGoal {
        protected int timer;

        public EatSweetBerriesGoal(double d, int i, int j) {
            super(FoxEntity.this, d, i, j);
        }

        @Override
        public double getDesiredSquaredDistanceToTarget() {
            return 2.0;
        }

        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 100 == 0;
        }

        @Override
        protected boolean isTargetPos(WorldView arg, BlockPos arg2) {
            BlockState lv = arg.getBlockState(arg2);
            return lv.isOf(Blocks.SWEET_BERRY_BUSH) && lv.get(SweetBerryBushBlock.AGE) >= 2;
        }

        @Override
        public void tick() {
            if (this.hasReached()) {
                if (this.timer >= 40) {
                    this.eatSweetBerry();
                } else {
                    ++this.timer;
                }
            } else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05f) {
                FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0f, 1.0f);
            }
            super.tick();
        }

        protected void eatSweetBerry() {
            if (!FoxEntity.this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return;
            }
            BlockState lv = FoxEntity.this.world.getBlockState(this.targetPos);
            if (!lv.isOf(Blocks.SWEET_BERRY_BUSH)) {
                return;
            }
            int i = lv.get(SweetBerryBushBlock.AGE);
            lv.with(SweetBerryBushBlock.AGE, 1);
            int j = 1 + FoxEntity.this.world.random.nextInt(2) + (i == 3 ? 1 : 0);
            ItemStack lv2 = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (lv2.isEmpty()) {
                FoxEntity.this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                --j;
            }
            if (j > 0) {
                Block.dropStack(FoxEntity.this.world, this.targetPos, new ItemStack(Items.SWEET_BERRIES, j));
            }
            FoxEntity.this.playSound(SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, 1.0f, 1.0f);
            FoxEntity.this.world.setBlockState(this.targetPos, (BlockState)lv.with(SweetBerryBushBlock.AGE, 1), 2);
        }

        @Override
        public boolean canStart() {
            return !FoxEntity.this.isSleeping() && super.canStart();
        }

        @Override
        public void start() {
            this.timer = 0;
            FoxEntity.this.setSitting(false);
            super.start();
        }
    }

    class SitDownAndLookAroundGoal
    extends CalmDownGoal {
        private double lookX;
        private double lookZ;
        private int timer;
        private int counter;

        public SitDownAndLookAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return FoxEntity.this.getAttacker() == null && FoxEntity.this.getRandom().nextFloat() < 0.02f && !FoxEntity.this.isSleeping() && FoxEntity.this.getTarget() == null && FoxEntity.this.getNavigation().isIdle() && !this.canCalmDown() && !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose();
        }

        @Override
        public boolean shouldContinue() {
            return this.counter > 0;
        }

        @Override
        public void start() {
            this.chooseNewAngle();
            this.counter = 2 + FoxEntity.this.getRandom().nextInt(3);
            FoxEntity.this.setSitting(true);
            FoxEntity.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            FoxEntity.this.setSitting(false);
        }

        @Override
        public void tick() {
            --this.timer;
            if (this.timer <= 0) {
                --this.counter;
                this.chooseNewAngle();
            }
            FoxEntity.this.getLookControl().lookAt(FoxEntity.this.getX() + this.lookX, FoxEntity.this.getEyeY(), FoxEntity.this.getZ() + this.lookZ, FoxEntity.this.getBodyYawSpeed(), FoxEntity.this.getLookPitchSpeed());
        }

        private void chooseNewAngle() {
            double d = Math.PI * 2 * FoxEntity.this.getRandom().nextDouble();
            this.lookX = Math.cos(d);
            this.lookZ = Math.sin(d);
            this.timer = 80 + FoxEntity.this.getRandom().nextInt(20);
        }
    }

    class DelayedCalmDownGoal
    extends CalmDownGoal {
        private int timer;

        public DelayedCalmDownGoal() {
            this.timer = FoxEntity.this.random.nextInt(140);
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (FoxEntity.this.sidewaysSpeed != 0.0f || FoxEntity.this.upwardSpeed != 0.0f || FoxEntity.this.forwardSpeed != 0.0f) {
                return false;
            }
            return this.canNotCalmDown() || FoxEntity.this.isSleeping();
        }

        @Override
        public boolean shouldContinue() {
            return this.canNotCalmDown();
        }

        private boolean canNotCalmDown() {
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            return FoxEntity.this.world.isDay() && this.isAtFavoredLocation() && !this.canCalmDown();
        }

        @Override
        public void stop() {
            this.timer = FoxEntity.this.random.nextInt(140);
            FoxEntity.this.stopActions();
        }

        @Override
        public void start() {
            FoxEntity.this.setSitting(false);
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setJumping(false);
            FoxEntity.this.setSleeping(true);
            FoxEntity.this.getNavigation().stop();
            FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.getX(), FoxEntity.this.getY(), FoxEntity.this.getZ(), 0.0);
        }
    }

    abstract class CalmDownGoal
    extends Goal {
        private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;

        private CalmDownGoal() {
            this.WORRIABLE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(12.0).includeHidden().setPredicate(new WorriableEntityFilter());
        }

        protected boolean isAtFavoredLocation() {
            BlockPos lv = new BlockPos(FoxEntity.this.getX(), FoxEntity.this.getBoundingBox().maxY, FoxEntity.this.getZ());
            return !FoxEntity.this.world.isSkyVisible(lv) && FoxEntity.this.getPathfindingFavor(lv) >= 0.0f;
        }

        protected boolean canCalmDown() {
            return !FoxEntity.this.world.getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, FoxEntity.this, FoxEntity.this.getBoundingBox().expand(12.0, 6.0, 12.0)).isEmpty();
        }
    }

    public class WorriableEntityFilter
    implements Predicate<LivingEntity> {
        @Override
        public boolean test(LivingEntity arg) {
            if (arg instanceof FoxEntity) {
                return false;
            }
            if (arg instanceof ChickenEntity || arg instanceof RabbitEntity || arg instanceof HostileEntity) {
                return true;
            }
            if (arg instanceof TameableEntity) {
                return !((TameableEntity)arg).isTamed();
            }
            if (arg instanceof PlayerEntity && (arg.isSpectator() || ((PlayerEntity)arg).isCreative())) {
                return false;
            }
            if (FoxEntity.this.canTrust(arg.getUuid())) {
                return false;
            }
            return !arg.isSleeping() && !arg.isSneaky();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((LivingEntity)object);
        }
    }

    class AvoidDaylightGoal
    extends EscapeSunlightGoal {
        private int timer;

        public AvoidDaylightGoal(double d) {
            super(FoxEntity.this, d);
            this.timer = 100;
        }

        @Override
        public boolean canStart() {
            if (FoxEntity.this.isSleeping() || this.mob.getTarget() != null) {
                return false;
            }
            if (FoxEntity.this.world.isThundering()) {
                return true;
            }
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            this.timer = 100;
            BlockPos lv = this.mob.getBlockPos();
            return FoxEntity.this.world.isDay() && FoxEntity.this.world.isSkyVisible(lv) && !((ServerWorld)FoxEntity.this.world).isNearOccupiedPointOfInterest(lv) && this.targetShadedPos();
        }

        @Override
        public void start() {
            FoxEntity.this.stopActions();
            super.start();
        }
    }

    class DefendFriendGoal
    extends FollowTargetGoal<LivingEntity> {
        @Nullable
        private LivingEntity offender;
        private LivingEntity friend;
        private int lastAttackedTime;

        public DefendFriendGoal(Class<LivingEntity> class_, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
            super(FoxEntity.this, class_, 10, bl, bl2, predicate);
        }

        @Override
        public boolean canStart() {
            if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
                return false;
            }
            for (UUID uUID : FoxEntity.this.getTrustedUuids()) {
                LivingEntity lv2;
                Entity lv;
                if (uUID == null || !(FoxEntity.this.world instanceof ServerWorld) || !((lv = ((ServerWorld)FoxEntity.this.world).getEntity(uUID)) instanceof LivingEntity)) continue;
                this.friend = lv2 = (LivingEntity)lv;
                this.offender = lv2.getAttacker();
                int i = lv2.getLastAttackedTime();
                return i != this.lastAttackedTime && this.canTrack(this.offender, this.targetPredicate);
            }
            return false;
        }

        @Override
        public void start() {
            this.setTargetEntity(this.offender);
            this.targetEntity = this.offender;
            if (this.friend != null) {
                this.lastAttackedTime = this.friend.getLastAttackedTime();
            }
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0f, 1.0f);
            FoxEntity.this.setAggressive(true);
            FoxEntity.this.stopSleeping();
            super.start();
        }
    }

    class MateGoal
    extends AnimalMateGoal {
        public MateGoal(double d) {
            super(FoxEntity.this, d);
        }

        @Override
        public void start() {
            ((FoxEntity)this.animal).stopActions();
            ((FoxEntity)this.mate).stopActions();
            super.start();
        }

        @Override
        protected void breed() {
            FoxEntity lv = (FoxEntity)this.animal.createChild(this.mate);
            if (lv == null) {
                return;
            }
            ServerPlayerEntity lv2 = this.animal.getLovingPlayer();
            ServerPlayerEntity lv3 = this.mate.getLovingPlayer();
            ServerPlayerEntity lv4 = lv2;
            if (lv2 != null) {
                lv.addTrustedUuid(lv2.getUuid());
            } else {
                lv4 = lv3;
            }
            if (lv3 != null && lv2 != lv3) {
                lv.addTrustedUuid(lv3.getUuid());
            }
            if (lv4 != null) {
                lv4.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(lv4, this.animal, this.mate, lv);
            }
            this.animal.setBreedingAge(6000);
            this.mate.setBreedingAge(6000);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();
            lv.setBreedingAge(-24000);
            lv.refreshPositionAndAngles(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0f, 0.0f);
            this.world.spawnEntity(lv);
            this.world.sendEntityStatus(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
            }
        }
    }

    class AttackGoal
    extends MeleeAttackGoal {
        public AttackGoal(double d, boolean bl) {
            super(FoxEntity.this, d, bl);
        }

        @Override
        protected void attack(LivingEntity arg, double d) {
            double e = this.getSquaredMaxAttackDistance(arg);
            if (d <= e && this.method_28347()) {
                this.method_28346();
                this.mob.tryAttack(arg);
                FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0f, 1.0f);
            }
        }

        @Override
        public void start() {
            FoxEntity.this.setRollingHead(false);
            super.start();
        }

        @Override
        public boolean canStart() {
            return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isWalking() && super.canStart();
        }
    }

    class MoveToHuntGoal
    extends Goal {
        public MoveToHuntGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (FoxEntity.this.isSleeping()) {
                return false;
            }
            LivingEntity lv = FoxEntity.this.getTarget();
            return lv != null && lv.isAlive() && CHICKEN_AND_RABBIT_FILTER.test(lv) && FoxEntity.this.squaredDistanceTo(lv) > 36.0 && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.jumping;
        }

        @Override
        public void start() {
            FoxEntity.this.setSitting(false);
            FoxEntity.this.setWalking(false);
        }

        @Override
        public void stop() {
            LivingEntity lv = FoxEntity.this.getTarget();
            if (lv != null && FoxEntity.canJumpChase(FoxEntity.this, lv)) {
                FoxEntity.this.setRollingHead(true);
                FoxEntity.this.setCrouching(true);
                FoxEntity.this.getNavigation().stop();
                FoxEntity.this.getLookControl().lookAt(lv, FoxEntity.this.getBodyYawSpeed(), FoxEntity.this.getLookPitchSpeed());
            } else {
                FoxEntity.this.setRollingHead(false);
                FoxEntity.this.setCrouching(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity lv = FoxEntity.this.getTarget();
            FoxEntity.this.getLookControl().lookAt(lv, FoxEntity.this.getBodyYawSpeed(), FoxEntity.this.getLookPitchSpeed());
            if (FoxEntity.this.squaredDistanceTo(lv) <= 36.0) {
                FoxEntity.this.setRollingHead(true);
                FoxEntity.this.setCrouching(true);
                FoxEntity.this.getNavigation().stop();
            } else {
                FoxEntity.this.getNavigation().startMovingTo(lv, 1.5);
            }
        }
    }

    class FoxMoveControl
    extends MoveControl {
        public FoxMoveControl() {
            super(FoxEntity.this);
        }

        @Override
        public void tick() {
            if (FoxEntity.this.wantsToPickupItem()) {
                super.tick();
            }
        }
    }

    class PickupItemGoal
    extends Goal {
        public PickupItemGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            }
            if (FoxEntity.this.getTarget() != null || FoxEntity.this.getAttacker() != null) {
                return false;
            }
            if (!FoxEntity.this.wantsToPickupItem()) {
                return false;
            }
            if (FoxEntity.this.getRandom().nextInt(10) != 0) {
                return false;
            }
            List<ItemEntity> list = FoxEntity.this.world.getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            return !list.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public void tick() {
            List<ItemEntity> list = FoxEntity.this.world.getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            ItemStack lv = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (lv.isEmpty() && !list.isEmpty()) {
                FoxEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = FoxEntity.this.world.getEntities(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            if (!list.isEmpty()) {
                FoxEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            }
        }
    }

    public static enum Type {
        RED(0, "red", Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS),
        SNOW(1, "snow", Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);

        private static final Type[] TYPES;
        private static final Map<String, Type> NAME_TYPE_MAP;
        private final int id;
        private final String key;
        private final List<Biome> biomes;

        private Type(int j, String string2, Biome ... args) {
            this.id = j;
            this.key = string2;
            this.biomes = Arrays.asList(args);
        }

        public String getKey() {
            return this.key;
        }

        public List<Biome> getBiomes() {
            return this.biomes;
        }

        public int getId() {
            return this.id;
        }

        public static Type byName(String string) {
            return NAME_TYPE_MAP.getOrDefault(string, RED);
        }

        public static Type fromId(int i) {
            if (i < 0 || i > TYPES.length) {
                i = 0;
            }
            return TYPES[i];
        }

        public static Type fromBiome(Biome arg) {
            return SNOW.getBiomes().contains(arg) ? SNOW : RED;
        }

        static {
            TYPES = (Type[])Arrays.stream(Type.values()).sorted(Comparator.comparingInt(Type::getId)).toArray(Type[]::new);
            NAME_TYPE_MAP = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getKey, arg -> arg));
        }
    }
}

