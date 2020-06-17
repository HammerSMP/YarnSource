/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntity
extends Entity {
    private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final UUID SOUL_SPEED_BOOST_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
    private static final EntityAttributeModifier SPRINTING_SPEED_BOOST = new EntityAttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", (double)0.3f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> POTION_SWIRLS_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> STINGER_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2f, 0.2f);
    private final AttributeContainer attributes;
    private final DamageTracker damageTracker = new DamageTracker(this);
    private final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = Maps.newHashMap();
    private final DefaultedList<ItemStack> equippedHand = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> equippedArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public boolean handSwinging;
    public Hand preferredHand;
    public int handSwingTicks;
    public int stuckArrowTimer;
    public int stuckStingerTimer;
    public int hurtTime;
    public int maxHurtTime;
    public float knockbackVelocity;
    public int deathTime;
    public float lastHandSwingProgress;
    public float handSwingProgress;
    protected int lastAttackedTicks;
    public float lastLimbDistance;
    public float limbDistance;
    public float limbAngle;
    public final int defaultMaxHealth = 20;
    public final float randomLargeSeed;
    public final float randomSmallSeed;
    public float bodyYaw;
    public float prevBodyYaw;
    public float headYaw;
    public float prevHeadYaw;
    public float flyingSpeed = 0.02f;
    @Nullable
    protected PlayerEntity attackingPlayer;
    protected int playerHitTimer;
    protected boolean dead;
    protected int despawnCounter;
    protected float prevStepBobbingAmount;
    protected float stepBobbingAmount;
    protected float lookDirection;
    protected float prevLookDirection;
    protected float field_6215;
    protected int scoreAmount;
    protected float lastDamageTaken;
    protected boolean jumping;
    public float sidewaysSpeed;
    public float upwardSpeed;
    public float forwardSpeed;
    protected int bodyTrackingIncrements;
    protected double serverX;
    protected double serverY;
    protected double serverZ;
    protected double serverYaw;
    protected double serverPitch;
    protected double serverHeadYaw;
    protected int headTrackingIncrements;
    private boolean effectsChanged = true;
    @Nullable
    private LivingEntity attacker;
    private int lastAttackedTime;
    private LivingEntity attacking;
    private int lastAttackTime;
    private float movementSpeed;
    private int jumpingCooldown;
    private float absorptionAmount;
    protected ItemStack activeItemStack = ItemStack.EMPTY;
    protected int itemUseTimeLeft;
    protected int roll;
    private BlockPos lastBlockPos;
    private Optional<BlockPos> climbingPos = Optional.empty();
    private DamageSource lastDamageSource;
    private long lastDamageTime;
    protected int pushCooldown;
    private float leaningPitch;
    private float lastLeaningPitch;
    protected Brain<?> brain;

    protected LivingEntity(EntityType<? extends LivingEntity> arg, World arg2) {
        super(arg, arg2);
        this.attributes = new AttributeContainer(DefaultAttributeRegistry.get(arg));
        this.setHealth(this.getMaxHealth());
        this.inanimate = true;
        this.randomSmallSeed = (float)((Math.random() + 1.0) * (double)0.01f);
        this.refreshPosition();
        this.randomLargeSeed = (float)Math.random() * 12398.0f;
        this.headYaw = this.yaw = (float)(Math.random() * 6.2831854820251465);
        this.stepHeight = 0.6f;
        NbtOps lv = NbtOps.INSTANCE;
        this.brain = this.deserializeBrain(new Dynamic((DynamicOps)lv, lv.createMap((Map)ImmutableMap.of((Object)lv.createString("memories"), (Object)lv.emptyMap()))));
    }

    public Brain<?> getBrain() {
        return this.brain;
    }

    protected Brain.Profile<?> createBrainProfile() {
        return Brain.createProfile(ImmutableList.of(), ImmutableList.of());
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return this.createBrainProfile().deserialize(dynamic);
    }

    @Override
    public void kill() {
        this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canTarget(EntityType<?> arg) {
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LIVING_FLAGS, (byte)0);
        this.dataTracker.startTracking(POTION_SWIRLS_COLOR, 0);
        this.dataTracker.startTracking(POTION_SWIRLS_AMBIENT, false);
        this.dataTracker.startTracking(STUCK_ARROW_COUNT, 0);
        this.dataTracker.startTracking(STINGER_COUNT, 0);
        this.dataTracker.startTracking(HEALTH, Float.valueOf(1.0f));
        this.dataTracker.startTracking(SLEEPING_POSITION, Optional.empty());
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes() {
        return DefaultAttributeContainer.builder().add(EntityAttributes.GENERIC_MAX_HEALTH).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).add(EntityAttributes.GENERIC_MOVEMENT_SPEED).add(EntityAttributes.GENERIC_ARMOR).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
    }

    @Override
    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
        if (!this.isTouchingWater()) {
            this.checkWaterState();
        }
        if (!this.world.isClient && bl && this.fallDistance > 0.0f) {
            this.removeSoulSpeedBoost();
            this.addSoulSpeedBoostIfNeeded();
        }
        if (!this.world.isClient && this.fallDistance > 3.0f && bl) {
            float f = MathHelper.ceil(this.fallDistance - 3.0f);
            if (!arg.isAir()) {
                double e = Math.min((double)(0.2f + f / 15.0f), 2.5);
                int i = (int)(150.0 * e);
                ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, arg), this.getX(), this.getY(), this.getZ(), i, 0.0, 0.0, 0.0, 0.15f);
            }
        }
        super.fall(d, bl, arg, arg2);
    }

    public boolean canBreatheInWater() {
        return this.getGroup() == EntityGroup.UNDEAD;
    }

    @Environment(value=EnvType.CLIENT)
    public float getLeaningPitch(float f) {
        return MathHelper.lerp(f, this.lastLeaningPitch, this.leaningPitch);
    }

    @Override
    public void baseTick() {
        boolean bl2;
        this.lastHandSwingProgress = this.handSwingProgress;
        if (this.firstUpdate) {
            this.getSleepingPosition().ifPresent(this::setPositionInBed);
        }
        if (this.shouldDisplaySoulSpeedEffects()) {
            this.displaySoulSpeedEffects();
        }
        super.baseTick();
        this.world.getProfiler().push("livingEntityBaseTick");
        boolean bl = this instanceof PlayerEntity;
        if (this.isAlive()) {
            double e;
            double d;
            if (this.isInsideWall()) {
                this.damage(DamageSource.IN_WALL, 1.0f);
            } else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox()) && (d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getBuffer()) < 0.0 && (e = this.world.getWorldBorder().getDamagePerBlock()) > 0.0) {
                this.damage(DamageSource.IN_WALL, Math.max(1, MathHelper.floor(-d * e)));
            }
        }
        if (this.isFireImmune() || this.world.isClient) {
            this.extinguish();
        }
        boolean bl3 = bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
        if (this.isAlive()) {
            BlockPos lv2;
            if (this.isSubmergedIn(FluidTags.WATER) && !this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
                if (!(this.canBreatheInWater() || StatusEffectUtil.hasWaterBreathing(this) || bl2)) {
                    this.setAir(this.getNextAirUnderwater(this.getAir()));
                    if (this.getAir() == -20) {
                        this.setAir(0);
                        Vec3d lv = this.getVelocity();
                        for (int i = 0; i < 8; ++i) {
                            double f = this.random.nextDouble() - this.random.nextDouble();
                            double g = this.random.nextDouble() - this.random.nextDouble();
                            double h = this.random.nextDouble() - this.random.nextDouble();
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + f, this.getY() + g, this.getZ() + h, lv.x, lv.y, lv.z);
                        }
                        this.damage(DamageSource.DROWN, 2.0f);
                    }
                }
                if (!this.world.isClient && this.hasVehicle() && this.getVehicle() != null && !this.getVehicle().canBeRiddenInWater()) {
                    this.stopRiding();
                }
            } else if (this.getAir() < this.getMaxAir()) {
                this.setAir(this.getNextAirOnLand(this.getAir()));
            }
            if (!this.world.isClient && !Objects.equal((Object)this.lastBlockPos, (Object)(lv2 = this.getBlockPos()))) {
                this.lastBlockPos = lv2;
                this.applyMovementEffects(lv2);
            }
        }
        if (this.isAlive() && this.isWet()) {
            this.extinguish();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
            --this.timeUntilRegen;
        }
        if (this.isDead()) {
            this.updatePostDeath();
        }
        if (this.playerHitTimer > 0) {
            --this.playerHitTimer;
        } else {
            this.attackingPlayer = null;
        }
        if (this.attacking != null && !this.attacking.isAlive()) {
            this.attacking = null;
        }
        if (this.attacker != null) {
            if (!this.attacker.isAlive()) {
                this.setAttacker(null);
            } else if (this.age - this.lastAttackedTime > 100) {
                this.setAttacker(null);
            }
        }
        this.tickStatusEffects();
        this.prevLookDirection = this.lookDirection;
        this.prevBodyYaw = this.bodyYaw;
        this.prevHeadYaw = this.headYaw;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.world.getProfiler().pop();
    }

    public boolean shouldDisplaySoulSpeedEffects() {
        return this.age % 5 == 0 && this.getVelocity().x != 0.0 && this.getVelocity().z != 0.0 && !this.isSpectator() && EnchantmentHelper.hasSoulSpeed(this) && this.isOnSoulSpeedBlock();
    }

    protected void displaySoulSpeedEffects() {
        Vec3d lv = this.getVelocity();
        this.world.addParticle(ParticleTypes.SOUL, this.getX() + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), this.getY() + 0.1, this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), lv.x * -0.2, 0.1, lv.z * -0.2);
        float f = this.random.nextFloat() * 0.4f + this.random.nextFloat() > 0.9f ? 0.6f : 0.0f;
        this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, f, 0.6f + this.random.nextFloat() * 0.4f);
    }

    protected boolean isOnSoulSpeedBlock() {
        return this.getLandingBlockState().isIn(BlockTags.SOUL_SPEED_BLOCKS);
    }

    @Override
    protected float getVelocityMultiplier() {
        if (this.isOnSoulSpeedBlock() && EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this) > 0) {
            return 1.0f;
        }
        return super.getVelocityMultiplier();
    }

    protected boolean method_29500(BlockState arg) {
        return !arg.isAir() || this.isFallFlying();
    }

    protected void removeSoulSpeedBoost() {
        EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (lv == null) {
            return;
        }
        if (lv.getModifier(SOUL_SPEED_BOOST_ID) != null) {
            lv.removeModifier(SOUL_SPEED_BOOST_ID);
        }
    }

    protected void addSoulSpeedBoostIfNeeded() {
        int i;
        if (!this.getLandingBlockState().isAir() && (i = EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this)) > 0 && this.isOnSoulSpeedBlock()) {
            EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (lv == null) {
                return;
            }
            lv.addTemporaryModifier(new EntityAttributeModifier(SOUL_SPEED_BOOST_ID, "Soul speed boost", (double)(0.03f * (1.0f + (float)i * 0.35f)), EntityAttributeModifier.Operation.ADDITION));
            if (this.getRandom().nextFloat() < 0.04f) {
                ItemStack lv2 = this.getEquippedStack(EquipmentSlot.FEET);
                lv2.damage(1, this, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.FEET));
            }
        }
    }

    protected void applyMovementEffects(BlockPos arg) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, this);
        if (i > 0) {
            FrostWalkerEnchantment.freezeWater(this, this.world, arg, i);
        }
        if (this.method_29500(this.getLandingBlockState())) {
            this.removeSoulSpeedBoost();
        }
        this.addSoulSpeedBoostIfNeeded();
    }

    public boolean isBaby() {
        return false;
    }

    public float getScaleFactor() {
        return this.isBaby() ? 0.5f : 1.0f;
    }

    protected boolean method_29920() {
        return true;
    }

    @Override
    public boolean canBeRiddenInWater() {
        return false;
    }

    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove();
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.POOF, this.getParticleX(1.0), this.getRandomBodyY(), this.getParticleZ(1.0), d, e, f);
            }
        }
    }

    protected boolean canDropLootAndXp() {
        return !this.isBaby();
    }

    protected boolean shouldDropLoot() {
        return !this.isBaby();
    }

    protected int getNextAirUnderwater(int i) {
        int j = EnchantmentHelper.getRespiration(this);
        if (j > 0 && this.random.nextInt(j + 1) > 0) {
            return i;
        }
        return i - 1;
    }

    protected int getNextAirOnLand(int i) {
        return Math.min(i + 4, this.getMaxAir());
    }

    protected int getCurrentExperience(PlayerEntity arg) {
        return 0;
    }

    protected boolean shouldAlwaysDropXp() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return this.attacker;
    }

    public int getLastAttackedTime() {
        return this.lastAttackedTime;
    }

    public void setAttacking(@Nullable PlayerEntity arg) {
        this.attackingPlayer = arg;
        this.playerHitTimer = this.age;
    }

    public void setAttacker(@Nullable LivingEntity arg) {
        this.attacker = arg;
        this.lastAttackedTime = this.age;
    }

    @Nullable
    public LivingEntity getAttacking() {
        return this.attacking;
    }

    public int getLastAttackTime() {
        return this.lastAttackTime;
    }

    public void onAttacking(Entity arg) {
        this.attacking = arg instanceof LivingEntity ? (LivingEntity)arg : null;
        this.lastAttackTime = this.age;
    }

    public int getDespawnCounter() {
        return this.despawnCounter;
    }

    public void setDespawnCounter(int i) {
        this.despawnCounter = i;
    }

    protected void onEquipStack(ItemStack arg) {
        if (arg.isEmpty()) {
            return;
        }
        SoundEvent lv = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        Item lv2 = arg.getItem();
        if (lv2 instanceof ArmorItem) {
            lv = ((ArmorItem)lv2).getMaterial().getEquipSound();
        } else if (lv2 == Items.ELYTRA) {
            lv = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
        }
        this.playSound(lv, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        arg.putFloat("Health", this.getHealth());
        arg.putShort("HurtTime", (short)this.hurtTime);
        arg.putInt("HurtByTimestamp", this.lastAttackedTime);
        arg.putShort("DeathTime", (short)this.deathTime);
        arg.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        arg.put("Attributes", this.getAttributes().toTag());
        if (!this.activeStatusEffects.isEmpty()) {
            ListTag lv = new ListTag();
            for (StatusEffectInstance lv2 : this.activeStatusEffects.values()) {
                lv.add(lv2.toTag(new CompoundTag()));
            }
            arg.put("ActiveEffects", lv);
        }
        arg.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPosition().ifPresent(arg2 -> {
            arg.putInt("SleepingX", arg2.getX());
            arg.putInt("SleepingY", arg2.getY());
            arg.putInt("SleepingZ", arg2.getZ());
        });
        DataResult<Tag> dataResult = this.brain.encode(NbtOps.INSTANCE);
        dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(arg2 -> arg.put("Brain", (Tag)arg2));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        this.setAbsorptionAmount(arg.getFloat("AbsorptionAmount"));
        if (arg.contains("Attributes", 9) && this.world != null && !this.world.isClient) {
            this.getAttributes().fromTag(arg.getList("Attributes", 10));
        }
        if (arg.contains("ActiveEffects", 9)) {
            ListTag lv = arg.getList("ActiveEffects", 10);
            for (int i = 0; i < lv.size(); ++i) {
                CompoundTag lv2 = lv.getCompound(i);
                StatusEffectInstance lv3 = StatusEffectInstance.fromTag(lv2);
                if (lv3 == null) continue;
                this.activeStatusEffects.put(lv3.getEffectType(), lv3);
            }
        }
        if (arg.contains("Health", 99)) {
            this.setHealth(arg.getFloat("Health"));
        }
        this.hurtTime = arg.getShort("HurtTime");
        this.deathTime = arg.getShort("DeathTime");
        this.lastAttackedTime = arg.getInt("HurtByTimestamp");
        if (arg.contains("Team", 8)) {
            boolean bl;
            String string = arg.getString("Team");
            Team lv4 = this.world.getScoreboard().getTeam(string);
            boolean bl2 = bl = lv4 != null && this.world.getScoreboard().addPlayerToTeam(this.getUuidAsString(), lv4);
            if (!bl) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object)string);
            }
        }
        if (arg.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }
        if (arg.contains("SleepingX", 99) && arg.contains("SleepingY", 99) && arg.contains("SleepingZ", 99)) {
            BlockPos lv5 = new BlockPos(arg.getInt("SleepingX"), arg.getInt("SleepingY"), arg.getInt("SleepingZ"));
            this.setSleepingPosition(lv5);
            this.dataTracker.set(POSE, EntityPose.SLEEPING);
            if (!this.firstUpdate) {
                this.setPositionInBed(lv5);
            }
        }
        if (arg.contains("Brain", 10)) {
            this.brain = this.deserializeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)arg.get("Brain")));
        }
    }

    protected void tickStatusEffects() {
        Iterator<StatusEffect> iterator = this.activeStatusEffects.keySet().iterator();
        try {
            while (iterator.hasNext()) {
                StatusEffect lv = iterator.next();
                StatusEffectInstance lv2 = this.activeStatusEffects.get(lv);
                if (!lv2.update(this, () -> this.onStatusEffectUpgraded(lv2, true))) {
                    if (this.world.isClient) continue;
                    iterator.remove();
                    this.onStatusEffectRemoved(lv2);
                    continue;
                }
                if (lv2.getDuration() % 600 != 0) continue;
                this.onStatusEffectUpgraded(lv2, false);
            }
        }
        catch (ConcurrentModificationException lv) {
            // empty catch block
        }
        if (this.effectsChanged) {
            if (!this.world.isClient) {
                this.updatePotionVisibility();
            }
            this.effectsChanged = false;
        }
        int i = this.dataTracker.get(POTION_SWIRLS_COLOR);
        boolean bl = this.dataTracker.get(POTION_SWIRLS_AMBIENT);
        if (i > 0) {
            boolean bl3;
            if (this.isInvisible()) {
                boolean bl2 = this.random.nextInt(15) == 0;
            } else {
                bl3 = this.random.nextBoolean();
            }
            if (bl) {
                bl3 &= this.random.nextInt(5) == 0;
            }
            if (bl3 && i > 0) {
                double d = (double)(i >> 16 & 0xFF) / 255.0;
                double e = (double)(i >> 8 & 0xFF) / 255.0;
                double f = (double)(i >> 0 & 0xFF) / 255.0;
                this.world.addParticle(bl ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
            }
        }
    }

    protected void updatePotionVisibility() {
        if (this.activeStatusEffects.isEmpty()) {
            this.clearPotionSwirls();
            this.setInvisible(false);
        } else {
            Collection<StatusEffectInstance> collection = this.activeStatusEffects.values();
            this.dataTracker.set(POTION_SWIRLS_AMBIENT, LivingEntity.containsOnlyAmbientEffects(collection));
            this.dataTracker.set(POTION_SWIRLS_COLOR, PotionUtil.getColor(collection));
            this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
        }
    }

    public double getAttackDistanceScalingFactor(@Nullable Entity arg) {
        double d = 1.0;
        if (this.isSneaky()) {
            d *= 0.8;
        }
        if (this.isInvisible()) {
            float f = this.getArmorVisibility();
            if (f < 0.1f) {
                f = 0.1f;
            }
            d *= 0.7 * (double)f;
        }
        if (arg != null) {
            ItemStack lv = this.getEquippedStack(EquipmentSlot.HEAD);
            Item lv2 = lv.getItem();
            EntityType<?> lv3 = arg.getType();
            if (lv3 == EntityType.SKELETON && lv2 == Items.SKELETON_SKULL || lv3 == EntityType.ZOMBIE && lv2 == Items.ZOMBIE_HEAD || lv3 == EntityType.CREEPER && lv2 == Items.CREEPER_HEAD) {
                d *= 0.5;
            }
        }
        return d;
    }

    public boolean canTarget(LivingEntity arg) {
        return true;
    }

    public boolean isTarget(LivingEntity arg, TargetPredicate arg2) {
        return arg2.test(this, arg);
    }

    public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> collection) {
        for (StatusEffectInstance lv : collection) {
            if (lv.isAmbient()) continue;
            return false;
        }
        return true;
    }

    protected void clearPotionSwirls() {
        this.dataTracker.set(POTION_SWIRLS_AMBIENT, false);
        this.dataTracker.set(POTION_SWIRLS_COLOR, 0);
    }

    public boolean clearStatusEffects() {
        if (this.world.isClient) {
            return false;
        }
        Iterator<StatusEffectInstance> iterator = this.activeStatusEffects.values().iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            this.onStatusEffectRemoved(iterator.next());
            iterator.remove();
            bl = true;
        }
        return bl;
    }

    public Collection<StatusEffectInstance> getStatusEffects() {
        return this.activeStatusEffects.values();
    }

    public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects() {
        return this.activeStatusEffects;
    }

    public boolean hasStatusEffect(StatusEffect arg) {
        return this.activeStatusEffects.containsKey(arg);
    }

    @Nullable
    public StatusEffectInstance getStatusEffect(StatusEffect arg) {
        return this.activeStatusEffects.get(arg);
    }

    public boolean addStatusEffect(StatusEffectInstance arg) {
        if (!this.canHaveStatusEffect(arg)) {
            return false;
        }
        StatusEffectInstance lv = this.activeStatusEffects.get(arg.getEffectType());
        if (lv == null) {
            this.activeStatusEffects.put(arg.getEffectType(), arg);
            this.onStatusEffectApplied(arg);
            return true;
        }
        if (lv.upgrade(arg)) {
            this.onStatusEffectUpgraded(lv, true);
            return true;
        }
        return false;
    }

    public boolean canHaveStatusEffect(StatusEffectInstance arg) {
        StatusEffect lv;
        return this.getGroup() != EntityGroup.UNDEAD || (lv = arg.getEffectType()) != StatusEffects.REGENERATION && lv != StatusEffects.POISON;
    }

    @Environment(value=EnvType.CLIENT)
    public void applyStatusEffect(StatusEffectInstance arg) {
        if (!this.canHaveStatusEffect(arg)) {
            return;
        }
        StatusEffectInstance lv = this.activeStatusEffects.put(arg.getEffectType(), arg);
        if (lv == null) {
            this.onStatusEffectApplied(arg);
        } else {
            this.onStatusEffectUpgraded(arg, true);
        }
    }

    public boolean isUndead() {
        return this.getGroup() == EntityGroup.UNDEAD;
    }

    @Nullable
    public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect arg) {
        return this.activeStatusEffects.remove(arg);
    }

    public boolean removeStatusEffect(StatusEffect arg) {
        StatusEffectInstance lv = this.removeStatusEffectInternal(arg);
        if (lv != null) {
            this.onStatusEffectRemoved(lv);
            return true;
        }
        return false;
    }

    protected void onStatusEffectApplied(StatusEffectInstance arg) {
        this.effectsChanged = true;
        if (!this.world.isClient) {
            arg.getEffectType().onApplied(this, this.getAttributes(), arg.getAmplifier());
        }
    }

    protected void onStatusEffectUpgraded(StatusEffectInstance arg, boolean bl) {
        this.effectsChanged = true;
        if (bl && !this.world.isClient) {
            StatusEffect lv = arg.getEffectType();
            lv.onRemoved(this, this.getAttributes(), arg.getAmplifier());
            lv.onApplied(this, this.getAttributes(), arg.getAmplifier());
        }
    }

    protected void onStatusEffectRemoved(StatusEffectInstance arg) {
        this.effectsChanged = true;
        if (!this.world.isClient) {
            arg.getEffectType().onRemoved(this, this.getAttributes(), arg.getAmplifier());
        }
    }

    public void heal(float f) {
        float g = this.getHealth();
        if (g > 0.0f) {
            this.setHealth(g + f);
        }
    }

    public float getHealth() {
        return this.dataTracker.get(HEALTH).floatValue();
    }

    public void setHealth(float f) {
        this.dataTracker.set(HEALTH, Float.valueOf(MathHelper.clamp(f, 0.0f, this.getMaxHealth())));
    }

    public boolean isDead() {
        return this.getHealth() <= 0.0f;
    }

    @Override
    public boolean damage(DamageSource arg2, float f) {
        boolean bl3;
        if (this.isInvulnerableTo(arg2)) {
            return false;
        }
        if (this.world.isClient) {
            return false;
        }
        if (this.isDead()) {
            return false;
        }
        if (arg2.isFire() && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.world.isClient) {
            this.wakeUp();
        }
        this.despawnCounter = 0;
        float g = f;
        if (!(arg2 != DamageSource.ANVIL && arg2 != DamageSource.FALLING_BLOCK || this.getEquippedStack(EquipmentSlot.HEAD).isEmpty())) {
            this.getEquippedStack(EquipmentSlot.HEAD).damage((int)(f * 4.0f + this.random.nextFloat() * f * 2.0f), this, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.HEAD));
            f *= 0.75f;
        }
        boolean bl = false;
        float h = 0.0f;
        if (f > 0.0f && this.blockedByShield(arg2)) {
            Entity lv;
            this.damageShield(f);
            h = f;
            f = 0.0f;
            if (!arg2.isProjectile() && (lv = arg2.getSource()) instanceof LivingEntity) {
                this.takeShieldHit((LivingEntity)lv);
            }
            bl = true;
        }
        this.limbDistance = 1.5f;
        boolean bl2 = true;
        if ((float)this.timeUntilRegen > 10.0f) {
            if (f <= this.lastDamageTaken) {
                return false;
            }
            this.applyDamage(arg2, f - this.lastDamageTaken);
            this.lastDamageTaken = f;
            bl2 = false;
        } else {
            this.lastDamageTaken = f;
            this.timeUntilRegen = 20;
            this.applyDamage(arg2, f);
            this.hurtTime = this.maxHurtTime = 10;
        }
        this.knockbackVelocity = 0.0f;
        Entity lv2 = arg2.getAttacker();
        if (lv2 != null) {
            WolfEntity lv3;
            if (lv2 instanceof LivingEntity) {
                this.setAttacker((LivingEntity)lv2);
            }
            if (lv2 instanceof PlayerEntity) {
                this.playerHitTimer = 100;
                this.attackingPlayer = (PlayerEntity)lv2;
            } else if (lv2 instanceof WolfEntity && (lv3 = (WolfEntity)lv2).isTamed()) {
                this.playerHitTimer = 100;
                LivingEntity lv4 = lv3.getOwner();
                this.attackingPlayer = lv4 != null && lv4.getType() == EntityType.PLAYER ? (PlayerEntity)lv4 : null;
            }
        }
        if (bl2) {
            if (bl) {
                this.world.sendEntityStatus(this, (byte)29);
            } else if (arg2 instanceof EntityDamageSource && ((EntityDamageSource)arg2).isThorns()) {
                this.world.sendEntityStatus(this, (byte)33);
            } else {
                int e;
                if (arg2 == DamageSource.DROWN) {
                    int b = 36;
                } else if (arg2.isFire()) {
                    int c = 37;
                } else if (arg2 == DamageSource.SWEET_BERRY_BUSH) {
                    int d = 44;
                } else {
                    e = 2;
                }
                this.world.sendEntityStatus(this, (byte)e);
            }
            if (arg2 != DamageSource.DROWN && (!bl || f > 0.0f)) {
                this.scheduleVelocityUpdate();
            }
            if (lv2 != null) {
                double i = lv2.getX() - this.getX();
                double j = lv2.getZ() - this.getZ();
                while (i * i + j * j < 1.0E-4) {
                    i = (Math.random() - Math.random()) * 0.01;
                    j = (Math.random() - Math.random()) * 0.01;
                }
                this.knockbackVelocity = (float)(MathHelper.atan2(j, i) * 57.2957763671875 - (double)this.yaw);
                this.takeKnockback(0.4f, i, j);
            } else {
                this.knockbackVelocity = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.isDead()) {
            if (!this.tryUseTotem(arg2)) {
                SoundEvent lv5 = this.getDeathSound();
                if (bl2 && lv5 != null) {
                    this.playSound(lv5, this.getSoundVolume(), this.getSoundPitch());
                }
                this.onDeath(arg2);
            }
        } else if (bl2) {
            this.playHurtSound(arg2);
        }
        boolean bl4 = bl3 = !bl || f > 0.0f;
        if (bl3) {
            this.lastDamageSource = arg2;
            this.lastDamageTime = this.world.getTime();
        }
        if (this instanceof ServerPlayerEntity) {
            Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, arg2, g, f, bl);
            if (h > 0.0f && h < 3.4028235E37f) {
                ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(h * 10.0f));
            }
        }
        if (lv2 instanceof ServerPlayerEntity) {
            Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)lv2, this, arg2, g, f, bl);
        }
        return bl3;
    }

    protected void takeShieldHit(LivingEntity arg) {
        arg.knockback(this);
    }

    protected void knockback(LivingEntity arg) {
        arg.takeKnockback(0.5f, arg.getX() - this.getX(), arg.getZ() - this.getZ());
    }

    private boolean tryUseTotem(DamageSource arg) {
        if (arg.isOutOfWorld()) {
            return false;
        }
        ItemStack lv = null;
        for (Hand lv2 : Hand.values()) {
            ItemStack lv3 = this.getStackInHand(lv2);
            if (lv3.getItem() != Items.TOTEM_OF_UNDYING) continue;
            lv = lv3.copy();
            lv3.decrement(1);
            break;
        }
        if (lv != null) {
            if (this instanceof ServerPlayerEntity) {
                ServerPlayerEntity lv4 = (ServerPlayerEntity)this;
                lv4.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                Criteria.USED_TOTEM.trigger(lv4, lv);
            }
            this.setHealth(1.0f);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            this.world.sendEntityStatus(this, (byte)35);
        }
        return lv != null;
    }

    @Nullable
    public DamageSource getRecentDamageSource() {
        if (this.world.getTime() - this.lastDamageTime > 40L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }

    protected void playHurtSound(DamageSource arg) {
        SoundEvent lv = this.getHurtSound(arg);
        if (lv != null) {
            this.playSound(lv, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private boolean blockedByShield(DamageSource arg) {
        Vec3d lv3;
        PersistentProjectileEntity lv2;
        Entity lv = arg.getSource();
        boolean bl = false;
        if (lv instanceof PersistentProjectileEntity && (lv2 = (PersistentProjectileEntity)lv).getPierceLevel() > 0) {
            bl = true;
        }
        if (!arg.bypassesArmor() && this.isBlocking() && !bl && (lv3 = arg.getPosition()) != null) {
            Vec3d lv4 = this.getRotationVec(1.0f);
            Vec3d lv5 = lv3.reverseSubtract(this.getPos()).normalize();
            lv5 = new Vec3d(lv5.x, 0.0, lv5.z);
            if (lv5.dotProduct(lv4) < 0.0) {
                return true;
            }
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    private void playEquipmentBreakEffects(ItemStack arg) {
        if (!arg.isEmpty()) {
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f, false);
            }
            this.spawnItemParticles(arg, 5);
        }
    }

    public void onDeath(DamageSource arg) {
        if (this.removed || this.dead) {
            return;
        }
        Entity lv = arg.getAttacker();
        LivingEntity lv2 = this.getPrimeAdversary();
        if (this.scoreAmount >= 0 && lv2 != null) {
            lv2.updateKilledAdvancementCriterion(this, this.scoreAmount, arg);
        }
        if (lv != null) {
            lv.onKilledOther(this);
        }
        if (this.isSleeping()) {
            this.wakeUp();
        }
        this.dead = true;
        this.getDamageTracker().update();
        if (!this.world.isClient) {
            this.drop(arg);
            this.onKilledBy(lv2);
        }
        this.world.sendEntityStatus(this, (byte)3);
        this.setPose(EntityPose.DYING);
    }

    protected void onKilledBy(@Nullable LivingEntity arg) {
        if (this.world.isClient) {
            return;
        }
        boolean bl = false;
        if (arg instanceof WitherEntity) {
            if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                BlockPos lv = this.getBlockPos();
                BlockState lv2 = Blocks.WITHER_ROSE.getDefaultState();
                if (this.world.getBlockState(lv).isAir() && lv2.canPlaceAt(this.world, lv)) {
                    this.world.setBlockState(lv, lv2, 3);
                    bl = true;
                }
            }
            if (!bl) {
                ItemEntity lv3 = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
                this.world.spawnEntity(lv3);
            }
        }
    }

    protected void drop(DamageSource arg) {
        boolean bl;
        int j;
        Entity lv = arg.getAttacker();
        if (lv instanceof PlayerEntity) {
            int i = EnchantmentHelper.getLooting((LivingEntity)lv);
        } else {
            j = 0;
        }
        boolean bl2 = bl = this.playerHitTimer > 0;
        if (this.shouldDropLoot() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropLoot(arg, bl);
            this.dropEquipment(arg, j, bl);
        }
        this.dropInventory();
        this.dropXp();
    }

    protected void dropInventory() {
    }

    protected void dropXp() {
        if (!this.world.isClient && (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
            int j;
            for (int i = this.getCurrentExperience(this.attackingPlayer); i > 0; i -= j) {
                j = ExperienceOrbEntity.roundToOrbSize(i);
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
            }
        }
    }

    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
    }

    public Identifier getLootTable() {
        return this.getType().getLootTableId();
    }

    protected void dropLoot(DamageSource arg, boolean bl) {
        Identifier lv = this.getLootTable();
        LootTable lv2 = this.world.getServer().getLootManager().getTable(lv);
        LootContext.Builder lv3 = this.getLootContextBuilder(bl, arg);
        lv2.generateLoot(lv3.build(LootContextTypes.ENTITY), this::dropStack);
    }

    protected LootContext.Builder getLootContextBuilder(boolean bl, DamageSource arg) {
        LootContext.Builder lv = new LootContext.Builder((ServerWorld)this.world).random(this.random).parameter(LootContextParameters.THIS_ENTITY, this).parameter(LootContextParameters.POSITION, this.getBlockPos()).parameter(LootContextParameters.DAMAGE_SOURCE, arg).optionalParameter(LootContextParameters.KILLER_ENTITY, arg.getAttacker()).optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, arg.getSource());
        if (bl && this.attackingPlayer != null) {
            lv = lv.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck());
        }
        return lv;
    }

    public void takeKnockback(float f, double d, double e) {
        if ((f = (float)((double)f * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)))) <= 0.0f) {
            return;
        }
        this.velocityDirty = true;
        Vec3d lv = this.getVelocity();
        Vec3d lv2 = new Vec3d(d, 0.0, e).normalize().multiply(f);
        this.setVelocity(lv.x / 2.0 - lv2.x, this.onGround ? Math.min(0.4, lv.y / 2.0 + (double)f) : lv.y, lv.z / 2.0 - lv2.z);
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    }

    protected SoundEvent getFallSound(int i) {
        if (i > 4) {
            return SoundEvents.ENTITY_GENERIC_BIG_FALL;
        }
        return SoundEvents.ENTITY_GENERIC_SMALL_FALL;
    }

    protected SoundEvent getDrinkSound(ItemStack arg) {
        return arg.getDrinkSound();
    }

    public SoundEvent getEatSound(ItemStack arg) {
        return arg.getEatSound();
    }

    @Override
    public void setOnGround(boolean bl) {
        super.setOnGround(bl);
        if (bl) {
            this.climbingPos = Optional.empty();
        }
    }

    public Optional<BlockPos> getClimbingPos() {
        return this.climbingPos;
    }

    public boolean isClimbing() {
        if (this.isSpectator()) {
            return false;
        }
        BlockPos lv = this.getBlockPos();
        BlockState lv2 = this.getBlockState();
        Block lv3 = lv2.getBlock();
        if (lv3.isIn(BlockTags.CLIMBABLE)) {
            this.climbingPos = Optional.of(lv);
            return true;
        }
        if (lv3 instanceof TrapdoorBlock && this.canEnterTrapdoor(lv, lv2)) {
            this.climbingPos = Optional.of(lv);
            return true;
        }
        return false;
    }

    public BlockState getBlockState() {
        return this.world.getBlockState(this.getBlockPos());
    }

    private boolean canEnterTrapdoor(BlockPos arg, BlockState arg2) {
        BlockState lv;
        return arg2.get(TrapdoorBlock.OPEN) != false && (lv = this.world.getBlockState(arg.down())).isOf(Blocks.LADDER) && lv.get(LadderBlock.FACING) == arg2.get(TrapdoorBlock.FACING);
    }

    @Override
    public boolean isAlive() {
        return !this.removed && this.getHealth() > 0.0f;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        boolean bl = super.handleFallDamage(f, g);
        int i = this.computeFallDamage(f, g);
        if (i > 0) {
            this.playSound(this.getFallSound(i), 1.0f, 1.0f);
            this.playBlockFallSound();
            this.damage(DamageSource.FALL, i);
            return true;
        }
        return bl;
    }

    protected int computeFallDamage(float f, float g) {
        StatusEffectInstance lv = this.getStatusEffect(StatusEffects.JUMP_BOOST);
        float h = lv == null ? 0.0f : (float)(lv.getAmplifier() + 1);
        return MathHelper.ceil((f - 3.0f - h) * g);
    }

    protected void playBlockFallSound() {
        int k;
        int j;
        if (this.isSilent()) {
            return;
        }
        int i = MathHelper.floor(this.getX());
        BlockState lv = this.world.getBlockState(new BlockPos(i, j = MathHelper.floor(this.getY() - (double)0.2f), k = MathHelper.floor(this.getZ())));
        if (!lv.isAir()) {
            BlockSoundGroup lv2 = lv.getSoundGroup();
            this.playSound(lv2.getFallSound(), lv2.getVolume() * 0.5f, lv2.getPitch() * 0.75f);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
        this.hurtTime = this.maxHurtTime = 10;
        this.knockbackVelocity = 0.0f;
    }

    public int getArmor() {
        return MathHelper.floor(this.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
    }

    protected void damageArmor(DamageSource arg, float f) {
    }

    protected void damageShield(float f) {
    }

    protected float applyArmorToDamage(DamageSource arg, float f) {
        if (!arg.bypassesArmor()) {
            this.damageArmor(arg, f);
            f = DamageUtil.getDamageLeft(f, this.getArmor(), (float)this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        return f;
    }

    protected float applyEnchantmentsToDamage(DamageSource arg, float f) {
        int i;
        int j;
        float g;
        float h;
        float k;
        if (arg.isUnblockable()) {
            return f;
        }
        if (this.hasStatusEffect(StatusEffects.RESISTANCE) && arg != DamageSource.OUT_OF_WORLD && (k = (h = f) - (f = Math.max((g = f * (float)(j = 25 - (i = (this.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5))) / 25.0f, 0.0f))) > 0.0f && k < 3.4028235E37f) {
            if (this instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_RESISTED, Math.round(k * 10.0f));
            } else if (arg.getAttacker() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)arg.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(k * 10.0f));
            }
        }
        if (f <= 0.0f) {
            return 0.0f;
        }
        int l = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), arg);
        if (l > 0) {
            f = DamageUtil.getInflictedDamage(f, l);
        }
        return f;
    }

    protected void applyDamage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return;
        }
        f = this.applyArmorToDamage(arg, f);
        float g = f = this.applyEnchantmentsToDamage(arg, f);
        f = Math.max(f - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (g - f));
        float h = g - f;
        if (h > 0.0f && h < 3.4028235E37f && arg.getAttacker() instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)arg.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(h * 10.0f));
        }
        if (f == 0.0f) {
            return;
        }
        float i = this.getHealth();
        this.setHealth(i - f);
        this.getDamageTracker().onDamage(arg, i, f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - f);
    }

    public DamageTracker getDamageTracker() {
        return this.damageTracker;
    }

    @Nullable
    public LivingEntity getPrimeAdversary() {
        if (this.damageTracker.getBiggestAttacker() != null) {
            return this.damageTracker.getBiggestAttacker();
        }
        if (this.attackingPlayer != null) {
            return this.attackingPlayer;
        }
        if (this.attacker != null) {
            return this.attacker;
        }
        return null;
    }

    public final float getMaxHealth() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    public final int getStuckArrowCount() {
        return this.dataTracker.get(STUCK_ARROW_COUNT);
    }

    public final void setStuckArrowCount(int i) {
        this.dataTracker.set(STUCK_ARROW_COUNT, i);
    }

    public final int getStingerCount() {
        return this.dataTracker.get(STINGER_COUNT);
    }

    public final void setStingerCount(int i) {
        this.dataTracker.set(STINGER_COUNT, i);
    }

    private int getHandSwingDuration() {
        if (StatusEffectUtil.hasHaste(this)) {
            return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
        }
        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            return 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2;
        }
        return 6;
    }

    public void swingHand(Hand arg) {
        this.swingHand(arg, false);
    }

    public void swingHand(Hand arg, boolean bl) {
        if (!this.handSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
            this.handSwingTicks = -1;
            this.handSwinging = true;
            this.preferredHand = arg;
            if (this.world instanceof ServerWorld) {
                EntityAnimationS2CPacket lv = new EntityAnimationS2CPacket(this, arg == Hand.MAIN_HAND ? 0 : 3);
                ServerChunkManager lv2 = ((ServerWorld)this.world).getChunkManager();
                if (bl) {
                    lv2.sendToNearbyPlayers(this, lv);
                } else {
                    lv2.sendToOtherNearbyPlayers(this, lv);
                }
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        switch (b) {
            case 2: 
            case 33: 
            case 36: 
            case 37: 
            case 44: {
                DamageSource lv4;
                boolean bl = b == 33;
                boolean bl2 = b == 36;
                boolean bl3 = b == 37;
                boolean bl4 = b == 44;
                this.limbDistance = 1.5f;
                this.timeUntilRegen = 20;
                this.hurtTime = this.maxHurtTime = 10;
                this.knockbackVelocity = 0.0f;
                if (bl) {
                    this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if (bl3) {
                    DamageSource lv = DamageSource.ON_FIRE;
                } else if (bl2) {
                    DamageSource lv2 = DamageSource.DROWN;
                } else if (bl4) {
                    DamageSource lv3 = DamageSource.SWEET_BERRY_BUSH;
                } else {
                    lv4 = DamageSource.GENERIC;
                }
                SoundEvent lv5 = this.getHurtSound(lv4);
                if (lv5 != null) {
                    this.playSound(lv5, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.damage(DamageSource.GENERIC, 0.0f);
                break;
            }
            case 3: {
                SoundEvent lv6 = this.getDeathSound();
                if (lv6 != null) {
                    this.playSound(lv6, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if (this instanceof PlayerEntity) break;
                this.setHealth(0.0f);
                this.onDeath(DamageSource.GENERIC);
                break;
            }
            case 30: {
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f);
                break;
            }
            case 29: {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 0.8f + this.world.random.nextFloat() * 0.4f);
                break;
            }
            case 46: {
                int i = 128;
                for (int j = 0; j < 128; ++j) {
                    double d = (double)j / 127.0;
                    float f = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float g = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float h = (this.random.nextFloat() - 0.5f) * 0.2f;
                    double e = MathHelper.lerp(d, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
                    double k = MathHelper.lerp(d, this.prevY, this.getY()) + this.random.nextDouble() * (double)this.getHeight();
                    double l = MathHelper.lerp(d, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
                    this.world.addParticle(ParticleTypes.PORTAL, e, k, l, f, g, h);
                }
                break;
            }
            case 47: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.MAINHAND));
                break;
            }
            case 48: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.OFFHAND));
                break;
            }
            case 49: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.HEAD));
                break;
            }
            case 50: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.CHEST));
                break;
            }
            case 51: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.LEGS));
                break;
            }
            case 52: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.FEET));
                break;
            }
            case 54: {
                HoneyBlock.addRichParticles(this);
                break;
            }
            case 55: {
                this.method_30127();
                break;
            }
            default: {
                super.handleStatus(b);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void method_30127() {
        ItemStack lv = this.getEquippedStack(EquipmentSlot.OFFHAND);
        this.equipStack(EquipmentSlot.OFFHAND, this.getEquippedStack(EquipmentSlot.MAINHAND));
        this.equipStack(EquipmentSlot.MAINHAND, lv);
    }

    @Override
    protected void destroy() {
        this.damage(DamageSource.OUT_OF_WORLD, 4.0f);
    }

    protected void tickHandSwing() {
        int i = this.getHandSwingDuration();
        if (this.handSwinging) {
            ++this.handSwingTicks;
            if (this.handSwingTicks >= i) {
                this.handSwingTicks = 0;
                this.handSwinging = false;
            }
        } else {
            this.handSwingTicks = 0;
        }
        this.handSwingProgress = (float)this.handSwingTicks / (float)i;
    }

    @Nullable
    public EntityAttributeInstance getAttributeInstance(EntityAttribute arg) {
        return this.getAttributes().getCustomInstance(arg);
    }

    public double getAttributeValue(EntityAttribute arg) {
        return this.getAttributes().getValue(arg);
    }

    public double getAttributeBaseValue(EntityAttribute arg) {
        return this.getAttributes().getBaseValue(arg);
    }

    public AttributeContainer getAttributes() {
        return this.attributes;
    }

    public EntityGroup getGroup() {
        return EntityGroup.DEFAULT;
    }

    public ItemStack getMainHandStack() {
        return this.getEquippedStack(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffHandStack() {
        return this.getEquippedStack(EquipmentSlot.OFFHAND);
    }

    public boolean isHolding(Item arg) {
        return this.isHolding((Item arg2) -> arg2 == arg);
    }

    public boolean isHolding(Predicate<Item> predicate) {
        return predicate.test(this.getMainHandStack().getItem()) || predicate.test(this.getOffHandStack().getItem());
    }

    public ItemStack getStackInHand(Hand arg) {
        if (arg == Hand.MAIN_HAND) {
            return this.getEquippedStack(EquipmentSlot.MAINHAND);
        }
        if (arg == Hand.OFF_HAND) {
            return this.getEquippedStack(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + (Object)((Object)arg));
    }

    public void setStackInHand(Hand arg, ItemStack arg2) {
        if (arg == Hand.MAIN_HAND) {
            this.equipStack(EquipmentSlot.MAINHAND, arg2);
        } else if (arg == Hand.OFF_HAND) {
            this.equipStack(EquipmentSlot.OFFHAND, arg2);
        } else {
            throw new IllegalArgumentException("Invalid hand " + (Object)((Object)arg));
        }
    }

    public boolean hasStackEquipped(EquipmentSlot arg) {
        return !this.getEquippedStack(arg).isEmpty();
    }

    @Override
    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Override
    public abstract void equipStack(EquipmentSlot var1, ItemStack var2);

    public float getArmorVisibility() {
        Iterable<ItemStack> iterable = this.getArmorItems();
        int i = 0;
        int j = 0;
        for (ItemStack lv : iterable) {
            if (!lv.isEmpty()) {
                ++j;
            }
            ++i;
        }
        return i > 0 ? (float)j / (float)i : 0.0f;
    }

    @Override
    public void setSprinting(boolean bl) {
        super.setSprinting(bl);
        EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (lv.getModifier(SPRINTING_SPEED_BOOST_ID) != null) {
            lv.removeModifier(SPRINTING_SPEED_BOOST);
        }
        if (bl) {
            lv.addTemporaryModifier(SPRINTING_SPEED_BOOST);
        }
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    protected float getSoundPitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isImmobile() {
        return this.isDead();
    }

    @Override
    public void pushAwayFrom(Entity arg) {
        if (!this.isSleeping()) {
            super.pushAwayFrom(arg);
        }
    }

    private void onDismounted(Entity arg) {
        Vec3d lv2;
        if (arg.removed || this.world.getBlockState(arg.getBlockPos()).getBlock().isIn(BlockTags.PORTALS)) {
            Vec3d lv = new Vec3d(arg.getX(), arg.getY() + (double)arg.getHeight(), arg.getZ());
        } else {
            lv2 = arg.updatePassengerForDismount(this);
        }
        this.requestTeleport(lv2.x, lv2.y, lv2.z);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    protected float getJumpVelocity() {
        return 0.42f * this.getJumpVelocityMultiplier();
    }

    protected void jump() {
        float f = this.getJumpVelocity();
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            f += 0.1f * (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1);
        }
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.x, f, lv.z);
        if (this.isSprinting()) {
            float g = this.yaw * ((float)Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(g) * 0.2f, 0.0, MathHelper.cos(g) * 0.2f));
        }
        this.velocityDirty = true;
    }

    @Environment(value=EnvType.CLIENT)
    protected void knockDownwards() {
        this.setVelocity(this.getVelocity().add(0.0, -0.04f, 0.0));
    }

    protected void swimUpward(net.minecraft.tag.Tag<Fluid> arg) {
        this.setVelocity(this.getVelocity().add(0.0, 0.04f, 0.0));
    }

    protected float getBaseMovementSpeedMultiplier() {
        return 0.8f;
    }

    public boolean canWalkOnFluid(Fluid arg) {
        return false;
    }

    public void travel(Vec3d arg) {
        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            boolean bl;
            double d = 0.08;
            boolean bl2 = bl = this.getVelocity().y <= 0.0;
            if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                d = 0.01;
                this.fallDistance = 0.0f;
            }
            FluidState lv = this.world.getFluidState(this.getBlockPos());
            if (this.isTouchingWater() && this.method_29920() && !this.canWalkOnFluid(lv.getFluid())) {
                double e = this.getY();
                float f = this.isSprinting() ? 0.9f : this.getBaseMovementSpeedMultiplier();
                float g = 0.02f;
                float h = EnchantmentHelper.getDepthStrider(this);
                if (h > 3.0f) {
                    h = 3.0f;
                }
                if (!this.onGround) {
                    h *= 0.5f;
                }
                if (h > 0.0f) {
                    f += (0.54600006f - f) * h / 3.0f;
                    g += (this.getMovementSpeed() - g) * h / 3.0f;
                }
                if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                    f = 0.96f;
                }
                this.updateVelocity(g, arg);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d lv2 = this.getVelocity();
                if (this.horizontalCollision && this.isClimbing()) {
                    lv2 = new Vec3d(lv2.x, 0.2, lv2.z);
                }
                this.setVelocity(lv2.multiply(f, 0.8f, f));
                Vec3d lv3 = this.method_26317(d, bl, this.getVelocity());
                this.setVelocity(lv3);
                if (this.horizontalCollision && this.doesNotCollide(lv3.x, lv3.y + (double)0.6f - this.getY() + e, lv3.z)) {
                    this.setVelocity(lv3.x, 0.3f, lv3.z);
                }
            } else if (this.isInLava() && this.method_29920() && !this.canWalkOnFluid(lv.getFluid())) {
                double i = this.getY();
                this.updateVelocity(0.02f, arg);
                this.move(MovementType.SELF, this.getVelocity());
                if (this.getFluidHeight(FluidTags.LAVA) <= this.method_29241()) {
                    this.setVelocity(this.getVelocity().multiply(0.5, 0.8f, 0.5));
                    Vec3d lv4 = this.method_26317(d, bl, this.getVelocity());
                    this.setVelocity(lv4);
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.5));
                }
                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
                }
                Vec3d lv5 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(lv5.x, lv5.y + (double)0.6f - this.getY() + i, lv5.z)) {
                    this.setVelocity(lv5.x, 0.3f, lv5.z);
                }
            } else if (this.isFallFlying()) {
                double q;
                double r;
                float s;
                Vec3d lv6 = this.getVelocity();
                if (lv6.y > -0.5) {
                    this.fallDistance = 1.0f;
                }
                Vec3d lv7 = this.getRotationVector();
                float j = this.pitch * ((float)Math.PI / 180);
                double k = Math.sqrt(lv7.x * lv7.x + lv7.z * lv7.z);
                double l = Math.sqrt(LivingEntity.squaredHorizontalLength(lv6));
                double m = lv7.length();
                float n = MathHelper.cos(j);
                n = (float)((double)n * ((double)n * Math.min(1.0, m / 0.4)));
                lv6 = this.getVelocity().add(0.0, d * (-1.0 + (double)n * 0.75), 0.0);
                if (lv6.y < 0.0 && k > 0.0) {
                    double o = lv6.y * -0.1 * (double)n;
                    lv6 = lv6.add(lv7.x * o / k, o, lv7.z * o / k);
                }
                if (j < 0.0f && k > 0.0) {
                    double p = l * (double)(-MathHelper.sin(j)) * 0.04;
                    lv6 = lv6.add(-lv7.x * p / k, p * 3.2, -lv7.z * p / k);
                }
                if (k > 0.0) {
                    lv6 = lv6.add((lv7.x / k * l - lv6.x) * 0.1, 0.0, (lv7.z / k * l - lv6.z) * 0.1);
                }
                this.setVelocity(lv6.multiply(0.99f, 0.98f, 0.99f));
                this.move(MovementType.SELF, this.getVelocity());
                if (this.horizontalCollision && !this.world.isClient && (s = (float)((r = l - (q = Math.sqrt(LivingEntity.squaredHorizontalLength(this.getVelocity())))) * 10.0 - 3.0)) > 0.0f) {
                    this.playSound(this.getFallSound((int)s), 1.0f, 1.0f);
                    this.damage(DamageSource.FLY_INTO_WALL, s);
                }
                if (this.onGround && !this.world.isClient) {
                    this.setFlag(7, false);
                }
            } else {
                BlockPos lv8 = this.getVelocityAffectingPos();
                float t = this.world.getBlockState(lv8).getBlock().getSlipperiness();
                float u = this.onGround ? t * 0.91f : 0.91f;
                Vec3d lv9 = this.method_26318(arg, t);
                double v = lv9.y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    v += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - lv9.y) * 0.2;
                    this.fallDistance = 0.0f;
                } else if (!this.world.isClient || this.world.isChunkLoaded(lv8)) {
                    if (!this.hasNoGravity()) {
                        v -= d;
                    }
                } else {
                    v = this.getY() > 0.0 ? -0.1 : 0.0;
                }
                this.setVelocity(lv9.x * (double)u, v * (double)0.98f, lv9.z * (double)u);
            }
        }
        this.method_29242(this, this instanceof Flutterer);
    }

    public void method_29242(LivingEntity arg, boolean bl) {
        double f;
        double e;
        arg.lastLimbDistance = arg.limbDistance;
        double d = arg.getX() - arg.prevX;
        float g = MathHelper.sqrt(d * d + (e = bl ? arg.getY() - arg.prevY : 0.0) * e + (f = arg.getZ() - arg.prevZ) * f) * 4.0f;
        if (g > 1.0f) {
            g = 1.0f;
        }
        arg.limbDistance += (g - arg.limbDistance) * 0.4f;
        arg.limbAngle += arg.limbDistance;
    }

    public Vec3d method_26318(Vec3d arg, float f) {
        this.updateVelocity(this.getMovementSpeed(f), arg);
        this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
        this.move(MovementType.SELF, this.getVelocity());
        Vec3d lv = this.getVelocity();
        if ((this.horizontalCollision || this.jumping) && this.isClimbing()) {
            lv = new Vec3d(lv.x, 0.2, lv.z);
        }
        return lv;
    }

    public Vec3d method_26317(double d, boolean bl, Vec3d arg) {
        if (!this.hasNoGravity() && !this.isSprinting()) {
            double f;
            if (bl && Math.abs(arg.y - 0.005) >= 0.003 && Math.abs(arg.y - d / 16.0) < 0.003) {
                double e = -0.003;
            } else {
                f = arg.y - d / 16.0;
            }
            return new Vec3d(arg.x, f, arg.z);
        }
        return arg;
    }

    private Vec3d applyClimbingSpeed(Vec3d arg) {
        if (this.isClimbing()) {
            this.fallDistance = 0.0f;
            float f = 0.15f;
            double d = MathHelper.clamp(arg.x, (double)-0.15f, (double)0.15f);
            double e = MathHelper.clamp(arg.z, (double)-0.15f, (double)0.15f);
            double g = Math.max(arg.y, (double)-0.15f);
            if (g < 0.0 && !this.getBlockState().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder() && this instanceof PlayerEntity) {
                g = 0.0;
            }
            arg = new Vec3d(d, g, e);
        }
        return arg;
    }

    private float getMovementSpeed(float f) {
        if (this.onGround) {
            return this.getMovementSpeed() * (0.21600002f / (f * f * f));
        }
        return this.flyingSpeed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(float f) {
        this.movementSpeed = f;
    }

    public boolean tryAttack(Entity arg) {
        this.onAttacking(arg);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickActiveItemStack();
        this.updateLeaningPitch();
        if (!this.world.isClient) {
            int j;
            int i = this.getStuckArrowCount();
            if (i > 0) {
                if (this.stuckArrowTimer <= 0) {
                    this.stuckArrowTimer = 20 * (30 - i);
                }
                --this.stuckArrowTimer;
                if (this.stuckArrowTimer <= 0) {
                    this.setStuckArrowCount(i - 1);
                }
            }
            if ((j = this.getStingerCount()) > 0) {
                if (this.stuckStingerTimer <= 0) {
                    this.stuckStingerTimer = 20 * (30 - j);
                }
                --this.stuckStingerTimer;
                if (this.stuckStingerTimer <= 0) {
                    this.setStingerCount(j - 1);
                }
            }
            this.method_30128();
            if (this.age % 20 == 0) {
                this.getDamageTracker().update();
            }
            if (!this.glowing) {
                boolean bl = this.hasStatusEffect(StatusEffects.GLOWING);
                if (this.getFlag(6) != bl) {
                    this.setFlag(6, bl);
                }
            }
            if (this.isSleeping() && !this.isSleepingInBed()) {
                this.wakeUp();
            }
        }
        this.tickMovement();
        double d = this.getX() - this.prevX;
        double e = this.getZ() - this.prevZ;
        float f = (float)(d * d + e * e);
        float g = this.bodyYaw;
        float h = 0.0f;
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        float k = 0.0f;
        if (f > 0.0025000002f) {
            k = 1.0f;
            h = (float)Math.sqrt(f) * 3.0f;
            float l = (float)MathHelper.atan2(e, d) * 57.295776f - 90.0f;
            float m = MathHelper.abs(MathHelper.wrapDegrees(this.yaw) - l);
            g = 95.0f < m && m < 265.0f ? l - 180.0f : l;
        }
        if (this.handSwingProgress > 0.0f) {
            g = this.yaw;
        }
        if (!this.onGround) {
            k = 0.0f;
        }
        this.stepBobbingAmount += (k - this.stepBobbingAmount) * 0.3f;
        this.world.getProfiler().push("headTurn");
        h = this.turnHead(g, h);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rangeChecks");
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        while (this.bodyYaw - this.prevBodyYaw < -180.0f) {
            this.prevBodyYaw -= 360.0f;
        }
        while (this.bodyYaw - this.prevBodyYaw >= 180.0f) {
            this.prevBodyYaw += 360.0f;
        }
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.headYaw - this.prevHeadYaw < -180.0f) {
            this.prevHeadYaw -= 360.0f;
        }
        while (this.headYaw - this.prevHeadYaw >= 180.0f) {
            this.prevHeadYaw += 360.0f;
        }
        this.world.getProfiler().pop();
        this.lookDirection += h;
        this.roll = this.isFallFlying() ? ++this.roll : 0;
        if (this.isSleeping()) {
            this.pitch = 0.0f;
        }
    }

    private void method_30128() {
        Map<EquipmentSlot, ItemStack> map = this.method_30129();
        if (map != null) {
            this.method_30121(map);
            if (!map.isEmpty()) {
                this.method_30123(map);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private Map<EquipmentSlot, ItemStack> method_30129() {
        EnumMap map = null;
        block4: for (EquipmentSlot lv : EquipmentSlot.values()) {
            void lv4;
            switch (lv.getType()) {
                case HAND: {
                    ItemStack lv2 = this.method_30126(lv);
                    break;
                }
                case ARMOR: {
                    ItemStack lv3 = this.method_30125(lv);
                    break;
                }
                default: {
                    continue block4;
                }
            }
            ItemStack lv5 = this.getEquippedStack(lv);
            if (ItemStack.areEqual(lv5, (ItemStack)lv4)) continue;
            if (map == null) {
                map = Maps.newEnumMap(EquipmentSlot.class);
            }
            map.put(lv, lv5);
            if (!lv4.isEmpty()) {
                this.getAttributes().removeModifiers(lv4.getAttributeModifiers(lv));
            }
            if (lv5.isEmpty()) continue;
            this.getAttributes().addTemporaryModifiers(lv5.getAttributeModifiers(lv));
        }
        return map;
    }

    private void method_30121(Map<EquipmentSlot, ItemStack> map) {
        ItemStack lv = map.get((Object)EquipmentSlot.MAINHAND);
        ItemStack lv2 = map.get((Object)EquipmentSlot.OFFHAND);
        if (lv != null && lv2 != null && ItemStack.areEqual(lv, this.method_30126(EquipmentSlot.OFFHAND)) && ItemStack.areEqual(lv2, this.method_30126(EquipmentSlot.MAINHAND))) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityStatusS2CPacket(this, 55));
            map.remove((Object)EquipmentSlot.MAINHAND);
            map.remove((Object)EquipmentSlot.OFFHAND);
            this.method_30124(EquipmentSlot.MAINHAND, lv.copy());
            this.method_30124(EquipmentSlot.OFFHAND, lv2.copy());
        }
    }

    private void method_30123(Map<EquipmentSlot, ItemStack> map) {
        ArrayList list = Lists.newArrayListWithCapacity((int)map.size());
        map.forEach((arg, arg2) -> {
            ItemStack lv = arg2.copy();
            list.add(Pair.of((Object)arg, (Object)lv));
            switch (arg.getType()) {
                case HAND: {
                    this.method_30124((EquipmentSlot)((Object)arg), lv);
                    break;
                }
                case ARMOR: {
                    this.method_30122((EquipmentSlot)((Object)arg), lv);
                }
            }
        });
        ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), list));
    }

    private ItemStack method_30125(EquipmentSlot arg) {
        return this.equippedArmor.get(arg.getEntitySlotId());
    }

    private void method_30122(EquipmentSlot arg, ItemStack arg2) {
        this.equippedArmor.set(arg.getEntitySlotId(), arg2);
    }

    private ItemStack method_30126(EquipmentSlot arg) {
        return this.equippedHand.get(arg.getEntitySlotId());
    }

    private void method_30124(EquipmentSlot arg, ItemStack arg2) {
        this.equippedHand.set(arg.getEntitySlotId(), arg2);
    }

    protected float turnHead(float f, float g) {
        boolean bl;
        float h = MathHelper.wrapDegrees(f - this.bodyYaw);
        this.bodyYaw += h * 0.3f;
        float i = MathHelper.wrapDegrees(this.yaw - this.bodyYaw);
        boolean bl2 = bl = i < -90.0f || i >= 90.0f;
        if (i < -75.0f) {
            i = -75.0f;
        }
        if (i >= 75.0f) {
            i = 75.0f;
        }
        this.bodyYaw = this.yaw - i;
        if (i * i > 2500.0f) {
            this.bodyYaw += i * 0.2f;
        }
        if (bl) {
            g *= -1.0f;
        }
        return g;
    }

    public void tickMovement() {
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            this.bodyTrackingIncrements = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.bodyTrackingIncrements > 0) {
            double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
            double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
            double f = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
            double g = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
            this.yaw = (float)((double)this.yaw + g / (double)this.bodyTrackingIncrements);
            this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
            --this.bodyTrackingIncrements;
            this.updatePosition(d, e, f);
            this.setRotation(this.yaw, this.pitch);
        } else if (!this.canMoveVoluntarily()) {
            this.setVelocity(this.getVelocity().multiply(0.98));
        }
        if (this.headTrackingIncrements > 0) {
            this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.headYaw) / (double)this.headTrackingIncrements);
            --this.headTrackingIncrements;
        }
        Vec3d lv = this.getVelocity();
        double h = lv.x;
        double i = lv.y;
        double j = lv.z;
        if (Math.abs(lv.x) < 0.003) {
            h = 0.0;
        }
        if (Math.abs(lv.y) < 0.003) {
            i = 0.0;
        }
        if (Math.abs(lv.z) < 0.003) {
            j = 0.0;
        }
        this.setVelocity(h, i, j);
        this.world.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0f;
            this.forwardSpeed = 0.0f;
        } else if (this.canMoveVoluntarily()) {
            this.world.getProfiler().push("newAi");
            this.tickNewAi();
            this.world.getProfiler().pop();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("jump");
        if (this.jumping && this.method_29920()) {
            double l;
            if (this.isInLava()) {
                double k = this.getFluidHeight(FluidTags.LAVA);
            } else {
                l = this.getFluidHeight(FluidTags.WATER);
            }
            boolean bl = this.isTouchingWater() && l > 0.0;
            double m = this.method_29241();
            if (bl && (!this.onGround || l > m)) {
                this.swimUpward(FluidTags.WATER);
            } else if (this.isInLava() && (!this.onGround || l > m)) {
                this.swimUpward(FluidTags.LAVA);
            } else if ((this.onGround || bl && l <= m) && this.jumpingCooldown == 0) {
                this.jump();
                this.jumpingCooldown = 10;
            }
        } else {
            this.jumpingCooldown = 0;
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("travel");
        this.sidewaysSpeed *= 0.98f;
        this.forwardSpeed *= 0.98f;
        this.initAi();
        Box lv2 = this.getBoundingBox();
        this.travel(new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed));
        this.world.getProfiler().pop();
        this.world.getProfiler().push("push");
        if (this.pushCooldown > 0) {
            --this.pushCooldown;
            this.push(lv2, this.getBoundingBox());
        }
        this.tickCramming();
        this.world.getProfiler().pop();
        if (!this.world.isClient && this.hurtByWater() && this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
    }

    public boolean hurtByWater() {
        return false;
    }

    private void initAi() {
        boolean bl = this.getFlag(7);
        if (bl && !this.onGround && !this.hasVehicle() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
            ItemStack lv = this.getEquippedStack(EquipmentSlot.CHEST);
            if (lv.getItem() == Items.ELYTRA && ElytraItem.isUsable(lv)) {
                bl = true;
                if (!this.world.isClient && (this.roll + 1) % 20 == 0) {
                    lv.damage(1, this, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
                }
            } else {
                bl = false;
            }
        } else {
            bl = false;
        }
        if (!this.world.isClient) {
            this.setFlag(7, bl);
        }
    }

    protected void tickNewAi() {
    }

    protected void tickCramming() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                int j = 0;
                for (int k = 0; k < list.size(); ++k) {
                    if (list.get(k).hasVehicle()) continue;
                    ++j;
                }
                if (j > i - 1) {
                    this.damage(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (int l = 0; l < list.size(); ++l) {
                Entity lv = list.get(l);
                this.pushAway(lv);
            }
        }
    }

    protected void push(Box arg, Box arg2) {
        Box lv = arg.union(arg2);
        List<Entity> list = this.world.getEntities(this, lv);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity lv2 = list.get(i);
                if (!(lv2 instanceof LivingEntity)) continue;
                this.attackLivingEntity((LivingEntity)lv2);
                this.pushCooldown = 0;
                this.setVelocity(this.getVelocity().multiply(-0.2));
                break;
            }
        } else if (this.horizontalCollision) {
            this.pushCooldown = 0;
        }
        if (!this.world.isClient && this.pushCooldown <= 0) {
            this.setLivingFlag(4, false);
        }
    }

    protected void pushAway(Entity arg) {
        arg.pushAwayFrom(this);
    }

    protected void attackLivingEntity(LivingEntity arg) {
    }

    public void setPushCooldown(int i) {
        this.pushCooldown = i;
        if (!this.world.isClient) {
            this.setLivingFlag(4, true);
        }
    }

    public boolean isUsingRiptide() {
        return (this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity lv = this.getVehicle();
        super.stopRiding();
        if (lv != null && lv != this.getVehicle() && !this.world.isClient) {
            this.onDismounted(lv);
        }
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        this.stepBobbingAmount = 0.0f;
        this.fallDistance = 0.0f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.serverX = d;
        this.serverY = e;
        this.serverZ = f;
        this.serverYaw = g;
        this.serverPitch = h;
        this.bodyTrackingIncrements = i;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float f, int i) {
        this.serverHeadYaw = f;
        this.headTrackingIncrements = i;
    }

    public void setJumping(boolean bl) {
        this.jumping = bl;
    }

    public void method_29499(ItemEntity arg) {
        PlayerEntity lv;
        PlayerEntity playerEntity = lv = arg.getThrower() != null ? this.world.getPlayerByUuid(arg.getThrower()) : null;
        if (lv instanceof ServerPlayerEntity) {
            Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayerEntity)lv, arg.getStack(), this);
        }
    }

    public void sendPickup(Entity arg, int i) {
        if (!arg.removed && !this.world.isClient && (arg instanceof ItemEntity || arg instanceof PersistentProjectileEntity || arg instanceof ExperienceOrbEntity)) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(arg, new ItemPickupAnimationS2CPacket(arg.getEntityId(), this.getEntityId(), i));
        }
    }

    public boolean canSee(Entity arg) {
        Vec3d lv2;
        Vec3d lv = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        return this.world.rayTrace(new RayTraceContext(lv, lv2 = new Vec3d(arg.getX(), arg.getEyeY(), arg.getZ()), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getYaw(float f) {
        if (f == 1.0f) {
            return this.headYaw;
        }
        return MathHelper.lerp(f, this.prevHeadYaw, this.headYaw);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHandSwingProgress(float f) {
        float g = this.handSwingProgress - this.lastHandSwingProgress;
        if (g < 0.0f) {
            g += 1.0f;
        }
        return this.lastHandSwingProgress + g * f;
    }

    public boolean canMoveVoluntarily() {
        return !this.world.isClient;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isClimbing();
    }

    @Override
    protected void scheduleVelocityUpdate() {
        this.velocityModified = this.random.nextDouble() >= this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
    }

    @Override
    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void setHeadYaw(float f) {
        this.headYaw = f;
    }

    @Override
    public void setYaw(float f) {
        this.bodyYaw = f;
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.absorptionAmount = f;
    }

    public void enterCombat() {
    }

    public void endCombat() {
    }

    protected void markEffectsDirty() {
        this.effectsChanged = true;
    }

    public abstract Arm getMainArm();

    public boolean isUsingItem() {
        return (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
    }

    public Hand getActiveHand() {
        return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private void tickActiveItemStack() {
        if (this.isUsingItem()) {
            if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
                this.activeItemStack = this.getStackInHand(this.getActiveHand());
                this.activeItemStack.usageTick(this.world, this, this.getItemUseTimeLeft());
                if (this.shouldSpawnConsumptionEffects()) {
                    this.spawnConsumptionEffects(this.activeItemStack, 5);
                }
                if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !this.activeItemStack.isUsedOnRelease()) {
                    this.consumeItem();
                }
            } else {
                this.clearActiveItem();
            }
        }
    }

    private boolean shouldSpawnConsumptionEffects() {
        int i = this.getItemUseTimeLeft();
        FoodComponent lv = this.activeItemStack.getItem().getFoodComponent();
        boolean bl = lv != null && lv.isSnack();
        return (bl |= i <= this.activeItemStack.getMaxUseTime() - 7) && i % 4 == 0;
    }

    private void updateLeaningPitch() {
        this.lastLeaningPitch = this.leaningPitch;
        this.leaningPitch = this.isInSwimmingPose() ? Math.min(1.0f, this.leaningPitch + 0.09f) : Math.max(0.0f, this.leaningPitch - 0.09f);
    }

    protected void setLivingFlag(int i, boolean bl) {
        int j = this.dataTracker.get(LIVING_FLAGS).byteValue();
        j = bl ? (j |= i) : (j &= ~i);
        this.dataTracker.set(LIVING_FLAGS, (byte)j);
    }

    public void setCurrentHand(Hand arg) {
        ItemStack lv = this.getStackInHand(arg);
        if (lv.isEmpty() || this.isUsingItem()) {
            return;
        }
        this.activeItemStack = lv;
        this.itemUseTimeLeft = lv.getMaxUseTime();
        if (!this.world.isClient) {
            this.setLivingFlag(1, true);
            this.setLivingFlag(2, arg == Hand.OFF_HAND);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (SLEEPING_POSITION.equals(arg)) {
            if (this.world.isClient) {
                this.getSleepingPosition().ifPresent(this::setPositionInBed);
            }
        } else if (LIVING_FLAGS.equals(arg) && this.world.isClient) {
            if (this.isUsingItem() && this.activeItemStack.isEmpty()) {
                this.activeItemStack = this.getStackInHand(this.getActiveHand());
                if (!this.activeItemStack.isEmpty()) {
                    this.itemUseTimeLeft = this.activeItemStack.getMaxUseTime();
                }
            } else if (!this.isUsingItem() && !this.activeItemStack.isEmpty()) {
                this.activeItemStack = ItemStack.EMPTY;
                this.itemUseTimeLeft = 0;
            }
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor arg, Vec3d arg2) {
        super.lookAt(arg, arg2);
        this.prevHeadYaw = this.headYaw;
        this.prevBodyYaw = this.bodyYaw = this.headYaw;
    }

    protected void spawnConsumptionEffects(ItemStack arg, int i) {
        if (arg.isEmpty() || !this.isUsingItem()) {
            return;
        }
        if (arg.getUseAction() == UseAction.DRINK) {
            this.playSound(this.getDrinkSound(arg), 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
        }
        if (arg.getUseAction() == UseAction.EAT) {
            this.spawnItemParticles(arg, i);
            this.playSound(this.getEatSound(arg), 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }

    private void spawnItemParticles(ItemStack arg, int i) {
        for (int j = 0; j < i; ++j) {
            Vec3d lv = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            lv = lv.rotateX(-this.pitch * ((float)Math.PI / 180));
            lv = lv.rotateY(-this.yaw * ((float)Math.PI / 180));
            double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3d lv2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
            lv2 = lv2.rotateX(-this.pitch * ((float)Math.PI / 180));
            lv2 = lv2.rotateY(-this.yaw * ((float)Math.PI / 180));
            lv2 = lv2.add(this.getX(), this.getEyeY(), this.getZ());
            this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, arg), lv2.x, lv2.y, lv2.z, lv.x, lv.y + 0.05, lv.z);
        }
    }

    protected void consumeItem() {
        if (!this.activeItemStack.equals(this.getStackInHand(this.getActiveHand()))) {
            this.stopUsingItem();
            return;
        }
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.spawnConsumptionEffects(this.activeItemStack, 16);
            this.setStackInHand(this.getActiveHand(), this.activeItemStack.finishUsing(this.world, this));
            this.clearActiveItem();
        }
    }

    public ItemStack getActiveItem() {
        return this.activeItemStack;
    }

    public int getItemUseTimeLeft() {
        return this.itemUseTimeLeft;
    }

    public int getItemUseTime() {
        if (this.isUsingItem()) {
            return this.activeItemStack.getMaxUseTime() - this.getItemUseTimeLeft();
        }
        return 0;
    }

    public void stopUsingItem() {
        if (!this.activeItemStack.isEmpty()) {
            this.activeItemStack.onStoppedUsing(this.world, this, this.getItemUseTimeLeft());
            if (this.activeItemStack.isUsedOnRelease()) {
                this.tickActiveItemStack();
            }
        }
        this.clearActiveItem();
    }

    public void clearActiveItem() {
        if (!this.world.isClient) {
            this.setLivingFlag(1, false);
        }
        this.activeItemStack = ItemStack.EMPTY;
        this.itemUseTimeLeft = 0;
    }

    public boolean isBlocking() {
        if (!this.isUsingItem() || this.activeItemStack.isEmpty()) {
            return false;
        }
        Item lv = this.activeItemStack.getItem();
        if (lv.getUseAction(this.activeItemStack) != UseAction.BLOCK) {
            return false;
        }
        return lv.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5;
    }

    public boolean isHoldingOntoLadder() {
        return this.isSneaking();
    }

    public boolean isFallFlying() {
        return this.getFlag(7);
    }

    @Override
    public boolean isInSwimmingPose() {
        return super.isInSwimmingPose() || !this.isFallFlying() && this.getPose() == EntityPose.FALL_FLYING;
    }

    @Environment(value=EnvType.CLIENT)
    public int getRoll() {
        return this.roll;
    }

    public boolean teleport(double d, double e, double f, boolean bl) {
        double g = this.getX();
        double h = this.getY();
        double i = this.getZ();
        double j = e;
        boolean bl2 = false;
        World lv2 = this.world;
        BlockPos lv = new BlockPos(d, j, f);
        if (lv2.isChunkLoaded(lv)) {
            boolean bl3 = false;
            while (!bl3 && lv.getY() > 0) {
                BlockPos lv3 = lv.down();
                BlockState lv4 = lv2.getBlockState(lv3);
                if (lv4.getMaterial().blocksMovement()) {
                    bl3 = true;
                    continue;
                }
                j -= 1.0;
                lv = lv3;
            }
            if (bl3) {
                this.requestTeleport(d, j, f);
                if (lv2.doesNotCollide(this) && !lv2.containsFluid(this.getBoundingBox())) {
                    bl2 = true;
                }
            }
        }
        if (!bl2) {
            this.requestTeleport(g, h, i);
            return false;
        }
        if (bl) {
            lv2.sendEntityStatus(this, (byte)46);
        }
        if (this instanceof MobEntityWithAi) {
            ((MobEntityWithAi)this).getNavigation().stop();
        }
        return true;
    }

    public boolean isAffectedBySplashPotions() {
        return true;
    }

    public boolean isMobOrPlayer() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void setNearbySongPlaying(BlockPos arg, boolean bl) {
    }

    public boolean canPickUp(ItemStack arg) {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new MobSpawnS2CPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose arg) {
        return arg == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(arg).scaled(this.getScaleFactor());
    }

    public ImmutableList<EntityPose> getPoses() {
        return ImmutableList.of((Object)((Object)EntityPose.STANDING));
    }

    public Box getBoundingBox(EntityPose arg) {
        EntityDimensions lv = this.getDimensions(arg);
        return new Box(-lv.width / 2.0f, 0.0, -lv.width / 2.0f, lv.width / 2.0f, lv.height, lv.width / 2.0f);
    }

    public Optional<BlockPos> getSleepingPosition() {
        return this.dataTracker.get(SLEEPING_POSITION);
    }

    public void setSleepingPosition(BlockPos arg) {
        this.dataTracker.set(SLEEPING_POSITION, Optional.of(arg));
    }

    public void clearSleepingPosition() {
        this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getSleepingPosition().isPresent();
    }

    public void sleep(BlockPos arg) {
        BlockState lv;
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        if ((lv = this.world.getBlockState(arg)).getBlock() instanceof BedBlock) {
            this.world.setBlockState(arg, (BlockState)lv.with(BedBlock.OCCUPIED, true), 3);
        }
        this.setPose(EntityPose.SLEEPING);
        this.setPositionInBed(arg);
        this.setSleepingPosition(arg);
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;
    }

    private void setPositionInBed(BlockPos arg) {
        this.updatePosition((double)arg.getX() + 0.5, (double)arg.getY() + 0.6875, (double)arg.getZ() + 0.5);
    }

    private boolean isSleepingInBed() {
        return this.getSleepingPosition().map(arg -> this.world.getBlockState((BlockPos)arg).getBlock() instanceof BedBlock).orElse(false);
    }

    public void wakeUp() {
        this.getSleepingPosition().filter(this.world::isChunkLoaded).ifPresent(arg -> {
            BlockState lv = this.world.getBlockState((BlockPos)arg);
            if (lv.getBlock() instanceof BedBlock) {
                this.world.setBlockState((BlockPos)arg, (BlockState)lv.with(BedBlock.OCCUPIED, false), 3);
                Vec3d lv2 = BedBlock.findWakeUpPosition(this.getType(), this.world, arg, 0).orElseGet(() -> {
                    BlockPos lv = arg.up();
                    return new Vec3d((double)lv.getX() + 0.5, (double)lv.getY() + 0.1, (double)lv.getZ() + 0.5);
                });
                this.updatePosition(lv2.x, lv2.y, lv2.z);
            }
        });
        Vec3d lv = this.getPos();
        this.setPose(EntityPose.STANDING);
        this.updatePosition(lv.x, lv.y, lv.z);
        this.clearSleepingPosition();
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Direction getSleepingDirection() {
        BlockPos lv = this.getSleepingPosition().orElse(null);
        return lv != null ? BedBlock.getDirection(this.world, lv) : null;
    }

    @Override
    public boolean isInsideWall() {
        return !this.isSleeping() && super.isInsideWall();
    }

    @Override
    protected final float getEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg == EntityPose.SLEEPING ? 0.2f : this.getActiveEyeHeight(arg, arg2);
    }

    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return super.getEyeHeight(arg, arg2);
    }

    public ItemStack getArrowType(ItemStack arg) {
        return ItemStack.EMPTY;
    }

    public ItemStack eatFood(World arg, ItemStack arg2) {
        if (arg2.isFood()) {
            arg.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatSound(arg2), SoundCategory.NEUTRAL, 1.0f, 1.0f + (arg.random.nextFloat() - arg.random.nextFloat()) * 0.4f);
            this.applyFoodEffects(arg2, arg, this);
            if (!(this instanceof PlayerEntity) || !((PlayerEntity)this).abilities.creativeMode) {
                arg2.decrement(1);
            }
        }
        return arg2;
    }

    private void applyFoodEffects(ItemStack arg, World arg2, LivingEntity arg3) {
        Item lv = arg.getItem();
        if (lv.isFood()) {
            List<Pair<StatusEffectInstance, Float>> list = lv.getFoodComponent().getStatusEffects();
            for (Pair<StatusEffectInstance, Float> pair : list) {
                if (arg2.isClient || pair.getFirst() == null || !(arg2.random.nextFloat() < ((Float)pair.getSecond()).floatValue())) continue;
                arg3.addStatusEffect(new StatusEffectInstance((StatusEffectInstance)pair.getFirst()));
            }
        }
    }

    private static byte getEquipmentBreakStatus(EquipmentSlot arg) {
        switch (arg) {
            case MAINHAND: {
                return 47;
            }
            case OFFHAND: {
                return 48;
            }
            case HEAD: {
                return 49;
            }
            case CHEST: {
                return 50;
            }
            case FEET: {
                return 52;
            }
            case LEGS: {
                return 51;
            }
        }
        return 47;
    }

    public void sendEquipmentBreakStatus(EquipmentSlot arg) {
        this.world.sendEntityStatus(this, LivingEntity.getEquipmentBreakStatus(arg));
    }

    public void sendToolBreakStatus(Hand arg) {
        this.sendEquipmentBreakStatus(arg == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }
}

