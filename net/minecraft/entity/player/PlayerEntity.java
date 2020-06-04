/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class PlayerEntity
extends LivingEntity {
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.changing(0.6f, 1.8f);
    private static final Map<EntityPose, EntityDimensions> POSE_DIMENSIONS = ImmutableMap.builder().put((Object)EntityPose.STANDING, (Object)STANDING_DIMENSIONS).put((Object)EntityPose.SLEEPING, (Object)SLEEPING_DIMENSIONS).put((Object)EntityPose.FALL_FLYING, (Object)EntityDimensions.changing(0.6f, 0.6f)).put((Object)EntityPose.SWIMMING, (Object)EntityDimensions.changing(0.6f, 0.6f)).put((Object)EntityPose.SPIN_ATTACK, (Object)EntityDimensions.changing(0.6f, 0.6f)).put((Object)EntityPose.CROUCHING, (Object)EntityDimensions.changing(0.6f, 1.5f)).put((Object)EntityPose.DYING, (Object)EntityDimensions.fixed(0.2f, 0.2f)).build();
    private static final TrackedData<Float> ABSORPTION_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> SCORE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Byte> PLAYER_MODEL_PARTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> MAIN_ARM = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<CompoundTag> LEFT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    protected static final TrackedData<CompoundTag> RIGHT_SHOULDER_ENTITY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    private long shoulderEntityAddedTime;
    public final PlayerInventory inventory = new PlayerInventory(this);
    protected EnderChestInventory enderChestInventory = new EnderChestInventory();
    public final PlayerScreenHandler playerScreenHandler;
    public ScreenHandler currentScreenHandler;
    protected HungerManager hungerManager = new HungerManager();
    protected int abilityResyncCountdown;
    public float prevStrideDistance;
    public float strideDistance;
    public int experiencePickUpDelay;
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public double capeX;
    public double capeY;
    public double capeZ;
    private int sleepTimer;
    protected boolean isSubmergedInWater;
    public final PlayerAbilities abilities = new PlayerAbilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentTableSeed;
    protected final float field_7509 = 0.02f;
    private int lastPlayedLevelUpSoundTime;
    private final GameProfile gameProfile;
    @Environment(value=EnvType.CLIENT)
    private boolean reducedDebugInfo;
    private ItemStack selectedItem = ItemStack.EMPTY;
    private final ItemCooldownManager itemCooldownManager = this.createCooldownManager();
    @Nullable
    public FishingBobberEntity fishHook;

    public PlayerEntity(World arg, BlockPos arg2, GameProfile gameProfile) {
        super((EntityType<? extends LivingEntity>)EntityType.PLAYER, arg);
        this.setUuid(PlayerEntity.getUuidFromProfile(gameProfile));
        this.gameProfile = gameProfile;
        this.playerScreenHandler = new PlayerScreenHandler(this.inventory, !arg.isClient, this);
        this.currentScreenHandler = this.playerScreenHandler;
        this.refreshPositionAndAngles((double)arg2.getX() + 0.5, arg2.getY() + 1, (double)arg2.getZ() + 0.5, 0.0f, 0.0f);
        this.field_6215 = 180.0f;
    }

    public boolean isBlockBreakingRestricted(World arg, BlockPos arg2, GameMode arg3) {
        if (!arg3.isBlockBreakingRestricted()) {
            return false;
        }
        if (arg3 == GameMode.SPECTATOR) {
            return true;
        }
        if (this.canModifyBlocks()) {
            return false;
        }
        ItemStack lv = this.getMainHandStack();
        return lv.isEmpty() || !lv.canDestroy(arg.getTagManager(), new CachedBlockPosition(arg, arg2, false));
    }

    public static DefaultAttributeContainer.Builder createPlayerAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1f).add(EntityAttributes.GENERIC_ATTACK_SPEED).add(EntityAttributes.GENERIC_LUCK);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ABSORPTION_AMOUNT, Float.valueOf(0.0f));
        this.dataTracker.startTracking(SCORE, 0);
        this.dataTracker.startTracking(PLAYER_MODEL_PARTS, (byte)0);
        this.dataTracker.startTracking(MAIN_ARM, (byte)1);
        this.dataTracker.startTracking(LEFT_SHOULDER_ENTITY, new CompoundTag());
        this.dataTracker.startTracking(RIGHT_SHOULDER_ENTITY, new CompoundTag());
    }

    @Override
    public void tick() {
        this.noClip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.experiencePickUpDelay > 0) {
            --this.experiencePickUpDelay;
        }
        if (this.isSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }
            if (!this.world.isClient && this.world.isDay()) {
                this.wakeUp(false, true);
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }
        this.updateWaterSubmersionState();
        super.tick();
        if (!this.world.isClient && this.currentScreenHandler != null && !this.currentScreenHandler.canUse(this)) {
            this.closeHandledScreen();
            this.currentScreenHandler = this.playerScreenHandler;
        }
        if (this.isOnFire() && this.abilities.invulnerable) {
            this.extinguish();
        }
        this.updateCapeAngles();
        if (!this.world.isClient) {
            this.hungerManager.update(this);
            this.incrementStat(Stats.PLAY_ONE_MINUTE);
            if (this.isAlive()) {
                this.incrementStat(Stats.TIME_SINCE_DEATH);
            }
            if (this.isSneaky()) {
                this.incrementStat(Stats.SNEAK_TIME);
            }
            if (!this.isSleeping()) {
                this.incrementStat(Stats.TIME_SINCE_REST);
            }
        }
        int i = 29999999;
        double d = MathHelper.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double e = MathHelper.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if (d != this.getX() || e != this.getZ()) {
            this.updatePosition(d, this.getY(), e);
        }
        ++this.lastAttackedTicks;
        ItemStack lv = this.getMainHandStack();
        if (!ItemStack.areEqual(this.selectedItem, lv)) {
            if (!ItemStack.areItemsEqual(this.selectedItem, lv)) {
                this.resetLastAttackedTicks();
            }
            this.selectedItem = lv.copy();
        }
        this.updateTurtleHelmet();
        this.itemCooldownManager.update();
        this.updateSize();
    }

    public boolean shouldCancelInteraction() {
        return this.isSneaking();
    }

    protected boolean shouldDismount() {
        return this.isSneaking();
    }

    protected boolean clipAtLedge() {
        return this.isSneaking();
    }

    protected boolean updateWaterSubmersionState() {
        this.isSubmergedInWater = this.isSubmergedIn(FluidTags.WATER);
        return this.isSubmergedInWater;
    }

    private void updateTurtleHelmet() {
        ItemStack lv = this.getEquippedStack(EquipmentSlot.HEAD);
        if (lv.getItem() == Items.TURTLE_HELMET && !this.isSubmergedIn(FluidTags.WATER)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, true));
        }
    }

    protected ItemCooldownManager createCooldownManager() {
        return new ItemCooldownManager();
    }

    private void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        double d = this.getX() - this.capeX;
        double e = this.getY() - this.capeY;
        double f = this.getZ() - this.capeZ;
        double g = 10.0;
        if (d > 10.0) {
            this.prevCapeX = this.capeX = this.getX();
        }
        if (f > 10.0) {
            this.prevCapeZ = this.capeZ = this.getZ();
        }
        if (e > 10.0) {
            this.prevCapeY = this.capeY = this.getY();
        }
        if (d < -10.0) {
            this.prevCapeX = this.capeX = this.getX();
        }
        if (f < -10.0) {
            this.prevCapeZ = this.capeZ = this.getZ();
        }
        if (e < -10.0) {
            this.prevCapeY = this.capeY = this.getY();
        }
        this.capeX += d * 0.25;
        this.capeZ += f * 0.25;
        this.capeY += e * 0.25;
    }

    protected void updateSize() {
        EntityPose lv9;
        EntityPose lv6;
        if (!this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
            return;
        }
        if (this.isFallFlying()) {
            EntityPose lv = EntityPose.FALL_FLYING;
        } else if (this.isSleeping()) {
            EntityPose lv2 = EntityPose.SLEEPING;
        } else if (this.isSwimming()) {
            EntityPose lv3 = EntityPose.SWIMMING;
        } else if (this.isUsingRiptide()) {
            EntityPose lv4 = EntityPose.SPIN_ATTACK;
        } else if (this.isSneaking() && !this.abilities.flying) {
            EntityPose lv5 = EntityPose.CROUCHING;
        } else {
            lv6 = EntityPose.STANDING;
        }
        if (this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(lv6)) {
            void lv7 = lv6;
        } else if (this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
            EntityPose lv8 = EntityPose.CROUCHING;
        } else {
            lv9 = EntityPose.SWIMMING;
        }
        this.setPose(lv9);
    }

    @Override
    public int getMaxNetherPortalTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDefaultNetherPortalCooldown() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent arg, float f, float g) {
        this.world.playSound(this, this.getX(), this.getY(), this.getZ(), arg, this.getSoundCategory(), f, g);
    }

    public void playSound(SoundEvent arg, SoundCategory arg2, float f, float g) {
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected int getBurningDuration() {
        return 20;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 9) {
            this.consumeItem();
        } else if (b == 23) {
            this.reducedDebugInfo = false;
        } else if (b == 22) {
            this.reducedDebugInfo = true;
        } else if (b == 43) {
            this.spawnParticles(ParticleTypes.CLOUD);
        } else {
            super.handleStatus(b);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void spawnParticles(ParticleEffect arg) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(arg, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
        }
    }

    protected void closeHandledScreen() {
        this.currentScreenHandler = this.playerScreenHandler;
    }

    @Override
    public void tickRiding() {
        if (this.shouldDismount() && this.hasVehicle()) {
            this.stopRiding();
            this.setSneaking(false);
            return;
        }
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.tickRiding();
        this.prevStrideDistance = this.strideDistance;
        this.strideDistance = 0.0f;
        this.increaseRidingMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void afterSpawn() {
        this.setPose(EntityPose.STANDING);
        super.afterSpawn();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    protected void tickNewAi() {
        super.tickNewAi();
        this.tickHandSwing();
        this.headYaw = this.yaw;
    }

    @Override
    public void tickMovement() {
        float g;
        if (this.abilityResyncCountdown > 0) {
            --this.abilityResyncCountdown;
        }
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.age % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.hungerManager.isNotFull() && this.age % 10 == 0) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }
        this.inventory.updateItems();
        this.prevStrideDistance = this.strideDistance;
        super.tickMovement();
        EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (!this.world.isClient) {
            lv.setBaseValue(this.abilities.getWalkSpeed());
        }
        this.flyingSpeed = 0.02f;
        if (this.isSprinting()) {
            this.flyingSpeed = (float)((double)this.flyingSpeed + 0.005999999865889549);
        }
        this.setMovementSpeed((float)lv.getValue());
        if (!this.onGround || this.method_29504() || this.isSwimming()) {
            float f = 0.0f;
        } else {
            g = Math.min(0.1f, MathHelper.sqrt(PlayerEntity.squaredHorizontalLength(this.getVelocity())));
        }
        this.strideDistance += (g - this.strideDistance) * 0.4f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            Box lv3;
            if (this.hasVehicle() && !this.getVehicle().removed) {
                Box lv2 = this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0);
            } else {
                lv3 = this.getBoundingBox().expand(1.0, 0.5, 1.0);
            }
            List<Entity> list = this.world.getEntities(this, lv3);
            for (int i = 0; i < list.size(); ++i) {
                Entity lv4 = list.get(i);
                if (lv4.removed) continue;
                this.collideWithEntity(lv4);
            }
        }
        this.updateShoulderEntity(this.getShoulderEntityLeft());
        this.updateShoulderEntity(this.getShoulderEntityRight());
        if (!this.world.isClient && (this.fallDistance > 0.5f || this.isTouchingWater()) || this.abilities.flying || this.isSleeping()) {
            this.dropShoulderEntities();
        }
    }

    private void updateShoulderEntity(@Nullable CompoundTag arg2) {
        if (!(arg2 == null || arg2.contains("Silent") && arg2.getBoolean("Silent") || this.world.random.nextInt(200) != 0)) {
            String string = arg2.getString("id");
            EntityType.get(string).filter(arg -> arg == EntityType.PARROT).ifPresent(arg -> {
                if (!ParrotEntity.imitateNearbyMob(this.world, this)) {
                    this.world.playSound(null, this.getX(), this.getY(), this.getZ(), ParrotEntity.getRandomSound(this.world, this.world.random), this.getSoundCategory(), 1.0f, ParrotEntity.getSoundPitch(this.world.random));
                }
            });
        }
    }

    private void collideWithEntity(Entity arg) {
        arg.onPlayerCollision(this);
    }

    public int getScore() {
        return this.dataTracker.get(SCORE);
    }

    public void setScore(int i) {
        this.dataTracker.set(SCORE, i);
    }

    public void addScore(int i) {
        int j = this.getScore();
        this.dataTracker.set(SCORE, j + i);
    }

    @Override
    public void onDeath(DamageSource arg) {
        super.onDeath(arg);
        this.refreshPosition();
        if (!this.isSpectator()) {
            this.drop(arg);
        }
        if (arg != null) {
            this.setVelocity(-MathHelper.cos((this.knockbackVelocity + this.yaw) * ((float)Math.PI / 180)) * 0.1f, 0.1f, -MathHelper.sin((this.knockbackVelocity + this.yaw) * ((float)Math.PI / 180)) * 0.1f);
        } else {
            this.setVelocity(0.0, 0.1, 0.0);
        }
        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.vanishCursedItems();
            this.inventory.dropAll();
        }
    }

    protected void vanishCursedItems() {
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack lv = this.inventory.getStack(i);
            if (lv.isEmpty() || !EnchantmentHelper.hasVanishingCurse(lv)) continue;
            this.inventory.removeStack(i);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        if (arg == DamageSource.ON_FIRE) {
            return SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
        }
        if (arg == DamageSource.DROWN) {
            return SoundEvents.ENTITY_PLAYER_HURT_DROWN;
        }
        if (arg == DamageSource.SWEET_BERRY_BUSH) {
            return SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH;
        }
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    public boolean dropSelectedItem(boolean bl) {
        return this.dropItem(this.inventory.removeStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1), false, true) != null;
    }

    @Nullable
    public ItemEntity dropItem(ItemStack arg, boolean bl) {
        return this.dropItem(arg, false, bl);
    }

    @Nullable
    public ItemEntity dropItem(ItemStack arg, boolean bl, boolean bl2) {
        if (arg.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            this.swingHand(Hand.MAIN_HAND);
        }
        double d = this.getEyeY() - (double)0.3f;
        ItemEntity lv = new ItemEntity(this.world, this.getX(), d, this.getZ(), arg);
        lv.setPickupDelay(40);
        if (bl2) {
            lv.setThrower(this.getUuid());
        }
        if (bl) {
            float f = this.random.nextFloat() * 0.5f;
            float g = this.random.nextFloat() * ((float)Math.PI * 2);
            lv.setVelocity(-MathHelper.sin(g) * f, 0.2f, MathHelper.cos(g) * f);
        } else {
            float h = 0.3f;
            float i = MathHelper.sin(this.pitch * ((float)Math.PI / 180));
            float j = MathHelper.cos(this.pitch * ((float)Math.PI / 180));
            float k = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
            float l = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
            float m = this.random.nextFloat() * ((float)Math.PI * 2);
            float n = 0.02f * this.random.nextFloat();
            lv.setVelocity((double)(-k * j * 0.3f) + Math.cos(m) * (double)n, -i * 0.3f + 0.1f + (this.random.nextFloat() - this.random.nextFloat()) * 0.1f, (double)(l * j * 0.3f) + Math.sin(m) * (double)n);
        }
        return lv;
    }

    public float getBlockBreakingSpeed(BlockState arg) {
        float f = this.inventory.getBlockBreakingSpeed(arg);
        if (f > 1.0f) {
            int i = EnchantmentHelper.getEfficiency(this);
            ItemStack lv = this.getMainHandStack();
            if (i > 0 && !lv.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }
        if (StatusEffectUtil.hasHaste(this)) {
            f *= 1.0f + (float)(StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2f;
        }
        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float k;
            switch (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    float g = 0.3f;
                    break;
                }
                case 1: {
                    float h = 0.09f;
                    break;
                }
                case 2: {
                    float j = 0.0027f;
                    break;
                }
                default: {
                    k = 8.1E-4f;
                }
            }
            f *= k;
        }
        if (this.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= 5.0f;
        }
        if (!this.onGround) {
            f /= 5.0f;
        }
        return f;
    }

    public boolean isUsingEffectiveTool(BlockState arg) {
        return !arg.isToolRequired() || this.inventory.getMainHandStack().isEffectiveOn(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setUuid(PlayerEntity.getUuidFromProfile(this.gameProfile));
        ListTag lv = arg.getList("Inventory", 10);
        this.inventory.deserialize(lv);
        this.inventory.selectedSlot = arg.getInt("SelectedItemSlot");
        this.sleepTimer = arg.getShort("SleepTimer");
        this.experienceProgress = arg.getFloat("XpP");
        this.experienceLevel = arg.getInt("XpLevel");
        this.totalExperience = arg.getInt("XpTotal");
        this.enchantmentTableSeed = arg.getInt("XpSeed");
        if (this.enchantmentTableSeed == 0) {
            this.enchantmentTableSeed = this.random.nextInt();
        }
        this.setScore(arg.getInt("Score"));
        this.hungerManager.fromTag(arg);
        this.abilities.deserialize(arg);
        if (arg.contains("EnderItems", 9)) {
            this.enderChestInventory.readTags(arg.getList("EnderItems", 10));
        }
        if (arg.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(arg.getCompound("ShoulderEntityLeft"));
        }
        if (arg.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(arg.getCompound("ShoulderEntityRight"));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        arg.put("Inventory", this.inventory.serialize(new ListTag()));
        arg.putInt("SelectedItemSlot", this.inventory.selectedSlot);
        arg.putShort("SleepTimer", (short)this.sleepTimer);
        arg.putFloat("XpP", this.experienceProgress);
        arg.putInt("XpLevel", this.experienceLevel);
        arg.putInt("XpTotal", this.totalExperience);
        arg.putInt("XpSeed", this.enchantmentTableSeed);
        arg.putInt("Score", this.getScore());
        this.hungerManager.toTag(arg);
        this.abilities.serialize(arg);
        arg.put("EnderItems", this.enderChestInventory.getTags());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            arg.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }
        if (!this.getShoulderEntityRight().isEmpty()) {
            arg.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource arg) {
        if (super.isInvulnerableTo(arg)) {
            return true;
        }
        if (arg == DamageSource.DROWN) {
            return !this.world.getGameRules().getBoolean(GameRules.DROWNING_DAMAGE);
        }
        if (arg == DamageSource.FALL) {
            return !this.world.getGameRules().getBoolean(GameRules.FALL_DAMAGE);
        }
        if (arg.isFire()) {
            return !this.world.getGameRules().getBoolean(GameRules.FIRE_DAMAGE);
        }
        return false;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        if (this.abilities.invulnerable && !arg.isOutOfWorld()) {
            return false;
        }
        this.despawnCounter = 0;
        if (this.method_29504()) {
            return false;
        }
        this.dropShoulderEntities();
        if (arg.isScaledWithDifficulty()) {
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                f = 0.0f;
            }
            if (this.world.getDifficulty() == Difficulty.EASY) {
                f = Math.min(f / 2.0f + 1.0f, f);
            }
            if (this.world.getDifficulty() == Difficulty.HARD) {
                f = f * 3.0f / 2.0f;
            }
        }
        if (f == 0.0f) {
            return false;
        }
        return super.damage(arg, f);
    }

    @Override
    protected void takeShieldHit(LivingEntity arg) {
        super.takeShieldHit(arg);
        if (arg.getMainHandStack().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    public boolean shouldDamagePlayer(PlayerEntity arg) {
        AbstractTeam lv = this.getScoreboardTeam();
        AbstractTeam lv2 = arg.getScoreboardTeam();
        if (lv == null) {
            return true;
        }
        if (!lv.isEqual(lv2)) {
            return true;
        }
        return lv.isFriendlyFireAllowed();
    }

    @Override
    protected void damageArmor(DamageSource arg, float f) {
        this.inventory.damageArmor(arg, f);
    }

    @Override
    protected void damageShield(float f) {
        if (this.activeItemStack.getItem() != Items.SHIELD) {
            return;
        }
        if (!this.world.isClient) {
            this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
        }
        if (f >= 3.0f) {
            int i = 1 + MathHelper.floor(f);
            Hand lv = this.getActiveHand();
            this.activeItemStack.damage(i, this, arg2 -> arg2.sendToolBreakStatus(lv));
            if (this.activeItemStack.isEmpty()) {
                if (lv == Hand.MAIN_HAND) {
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.activeItemStack = ItemStack.EMPTY;
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f);
            }
        }
    }

    @Override
    protected void applyDamage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return;
        }
        f = this.applyArmorToDamage(arg, f);
        float g = f = this.applyEnchantmentsToDamage(arg, f);
        f = Math.max(f - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (g - f));
        float h = g - f;
        if (h > 0.0f && h < 3.4028235E37f) {
            this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(h * 10.0f));
        }
        if (f == 0.0f) {
            return;
        }
        this.addExhaustion(arg.getExhaustion());
        float i = this.getHealth();
        this.setHealth(this.getHealth() - f);
        this.getDamageTracker().onDamage(arg, i, f);
        if (f < 3.4028235E37f) {
            this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(f * 10.0f));
        }
    }

    @Override
    protected boolean isOnSoulSpeedBlock() {
        return !this.abilities.flying && super.isOnSoulSpeedBlock();
    }

    public void openEditSignScreen(SignBlockEntity arg) {
    }

    public void openCommandBlockMinecartScreen(CommandBlockExecutor arg) {
    }

    public void openCommandBlockScreen(CommandBlockBlockEntity arg) {
    }

    public void openStructureBlockScreen(StructureBlockBlockEntity arg) {
    }

    public void openJigsawScreen(JigsawBlockEntity arg) {
    }

    public void openHorseInventory(HorseBaseEntity arg, Inventory arg2) {
    }

    public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory arg) {
        return OptionalInt.empty();
    }

    public void sendTradeOffers(int i, TraderOfferList arg, int j, int k, boolean bl, boolean bl2) {
    }

    public void openEditBookScreen(ItemStack arg, Hand arg2) {
    }

    public ActionResult interact(Entity arg, Hand arg2) {
        if (this.isSpectator()) {
            if (arg instanceof NamedScreenHandlerFactory) {
                this.openHandledScreen((NamedScreenHandlerFactory)((Object)arg));
            }
            return ActionResult.PASS;
        }
        ItemStack lv = this.getStackInHand(arg2);
        ItemStack lv2 = lv.copy();
        ActionResult lv3 = arg.interact(this, arg2);
        if (lv3.isAccepted()) {
            if (this.abilities.creativeMode && lv == this.getStackInHand(arg2) && lv.getCount() < lv2.getCount()) {
                lv.setCount(lv2.getCount());
            }
            return lv3;
        }
        if (!lv.isEmpty() && arg instanceof LivingEntity) {
            ActionResult lv4;
            if (this.abilities.creativeMode) {
                lv = lv2;
            }
            if ((lv4 = lv.useOnEntity(this, (LivingEntity)arg, arg2)).isAccepted()) {
                if (lv.isEmpty() && !this.abilities.creativeMode) {
                    this.setStackInHand(arg2, ItemStack.EMPTY);
                }
                return lv4;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public double getHeightOffset() {
        return -0.35;
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        this.ridingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    @Override
    protected Vec3d adjustMovementForSneaking(Vec3d arg, MovementType arg2) {
        if ((arg2 == MovementType.SELF || arg2 == MovementType.PLAYER) && this.onGround && this.clipAtLedge()) {
            double d = arg.x;
            double e = arg.z;
            double f = 0.05;
            while (d != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(d, -this.stepHeight, 0.0))) {
                if (d < 0.05 && d >= -0.05) {
                    d = 0.0;
                    continue;
                }
                if (d > 0.0) {
                    d -= 0.05;
                    continue;
                }
                d += 0.05;
            }
            while (e != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(0.0, -this.stepHeight, e))) {
                if (e < 0.05 && e >= -0.05) {
                    e = 0.0;
                    continue;
                }
                if (e > 0.0) {
                    e -= 0.05;
                    continue;
                }
                e += 0.05;
            }
            while (d != 0.0 && e != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(d, -this.stepHeight, e))) {
                d = d < 0.05 && d >= -0.05 ? 0.0 : (d > 0.0 ? (d -= 0.05) : (d += 0.05));
                if (e < 0.05 && e >= -0.05) {
                    e = 0.0;
                    continue;
                }
                if (e > 0.0) {
                    e -= 0.05;
                    continue;
                }
                e += 0.05;
            }
            arg = new Vec3d(d, arg.y, e);
        }
        return arg;
    }

    public void attack(Entity arg) {
        float h;
        if (!arg.isAttackable()) {
            return;
        }
        if (arg.handleAttack(this)) {
            return;
        }
        float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (arg instanceof LivingEntity) {
            float g = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)arg).getGroup());
        } else {
            h = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
        }
        float i = this.getAttackCooldownProgress(0.5f);
        h *= i;
        this.resetLastAttackedTicks();
        if ((f *= 0.2f + i * i * 0.8f) > 0.0f || h > 0.0f) {
            ItemStack lv;
            boolean bl = i > 0.9f;
            boolean bl2 = false;
            int j = 0;
            j += EnchantmentHelper.getKnockback(this);
            if (this.isSprinting() && bl) {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0f, 1.0f);
                ++j;
                bl2 = true;
            }
            boolean bl3 = bl && this.fallDistance > 0.0f && !this.onGround && !this.isClimbing() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && !this.hasVehicle() && arg instanceof LivingEntity;
            boolean bl4 = bl3 = bl3 && !this.isSprinting();
            if (bl3) {
                f *= 1.5f;
            }
            f += h;
            boolean bl42 = false;
            double d = this.horizontalSpeed - this.prevHorizontalSpeed;
            if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed() && (lv = this.getStackInHand(Hand.MAIN_HAND)).getItem() instanceof SwordItem) {
                bl42 = true;
            }
            float k = 0.0f;
            boolean bl5 = false;
            int l = EnchantmentHelper.getFireAspect(this);
            if (arg instanceof LivingEntity) {
                k = ((LivingEntity)arg).getHealth();
                if (l > 0 && !arg.isOnFire()) {
                    bl5 = true;
                    arg.setOnFireFor(1);
                }
            }
            Vec3d lv2 = arg.getVelocity();
            boolean bl6 = arg.damage(DamageSource.player(this), f);
            if (bl6) {
                if (j > 0) {
                    if (arg instanceof LivingEntity) {
                        ((LivingEntity)arg).takeKnockback((float)j * 0.5f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                    } else {
                        arg.addVelocity(-MathHelper.sin(this.yaw * ((float)Math.PI / 180)) * (float)j * 0.5f, 0.1, MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * (float)j * 0.5f);
                    }
                    this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
                    this.setSprinting(false);
                }
                if (bl42) {
                    float m = 1.0f + EnchantmentHelper.getSweepingMultiplier(this) * f;
                    List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, arg.getBoundingBox().expand(1.0, 0.25, 1.0));
                    for (LivingEntity lv3 : list) {
                        if (lv3 == this || lv3 == arg || this.isTeammate(lv3) || lv3 instanceof ArmorStandEntity && ((ArmorStandEntity)lv3).isMarker() || !(this.squaredDistanceTo(lv3) < 9.0)) continue;
                        lv3.takeKnockback(0.4f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                        lv3.damage(DamageSource.player(this), m);
                    }
                    this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0f, 1.0f);
                    this.spawnSweepAttackParticles();
                }
                if (arg instanceof ServerPlayerEntity && arg.velocityModified) {
                    ((ServerPlayerEntity)arg).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(arg));
                    arg.velocityModified = false;
                    arg.setVelocity(lv2);
                }
                if (bl3) {
                    this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0f, 1.0f);
                    this.addCritParticles(arg);
                }
                if (!bl3 && !bl42) {
                    if (bl) {
                        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0f, 1.0f);
                    } else {
                        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0f, 1.0f);
                    }
                }
                if (h > 0.0f) {
                    this.addEnchantedHitParticles(arg);
                }
                this.onAttacking(arg);
                if (arg instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged((LivingEntity)arg, this);
                }
                EnchantmentHelper.onTargetDamaged(this, arg);
                ItemStack lv4 = this.getMainHandStack();
                Entity lv5 = arg;
                if (arg instanceof EnderDragonPart) {
                    lv5 = ((EnderDragonPart)arg).owner;
                }
                if (!this.world.isClient && !lv4.isEmpty() && lv5 instanceof LivingEntity) {
                    lv4.postHit((LivingEntity)lv5, this);
                    if (lv4.isEmpty()) {
                        this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (arg instanceof LivingEntity) {
                    float n = k - ((LivingEntity)arg).getHealth();
                    this.increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0f));
                    if (l > 0) {
                        arg.setOnFireFor(l * 4);
                    }
                    if (this.world instanceof ServerWorld && n > 2.0f) {
                        int o = (int)((double)n * 0.5);
                        ((ServerWorld)this.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, arg.getX(), arg.getBodyY(0.5), arg.getZ(), o, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                this.addExhaustion(0.1f);
            } else {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0f, 1.0f);
                if (bl5) {
                    arg.extinguish();
                }
            }
        }
    }

    @Override
    protected void attackLivingEntity(LivingEntity arg) {
        this.attack(arg);
    }

    public void disableShield(boolean bl) {
        float f = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
        if (bl) {
            f += 0.75f;
        }
        if (this.random.nextFloat() < f) {
            this.getItemCooldownManager().set(Items.SHIELD, 100);
            this.clearActiveItem();
            this.world.sendEntityStatus(this, (byte)30);
        }
    }

    public void addCritParticles(Entity arg) {
    }

    public void addEnchantedHitParticles(Entity arg) {
    }

    public void spawnSweepAttackParticles() {
        double d = -MathHelper.sin(this.yaw * ((float)Math.PI / 180));
        double e = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d, this.getBodyY(0.5), this.getZ() + e, 0, d, 0.0, e, 0.0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void requestRespawn() {
    }

    @Override
    public void remove() {
        super.remove();
        this.playerScreenHandler.close(this);
        if (this.currentScreenHandler != null) {
            this.currentScreenHandler.close(this);
        }
    }

    public boolean isMainPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Either<SleepFailureReason, Unit> trySleep(BlockPos arg) {
        this.sleep(arg);
        this.sleepTimer = 0;
        return Either.right((Object)((Object)Unit.INSTANCE));
    }

    public void wakeUp(boolean bl, boolean bl2) {
        super.wakeUp();
        if (this.world instanceof ServerWorld && bl2) {
            ((ServerWorld)this.world).updateSleepingPlayers();
        }
        this.sleepTimer = bl ? 0 : 100;
    }

    @Override
    public void wakeUp() {
        this.wakeUp(true, true);
    }

    public static Optional<Vec3d> findRespawnPosition(ServerWorld arg, BlockPos arg2, boolean bl, boolean bl2) {
        BlockState lv = arg.getBlockState(arg2);
        Block lv2 = lv.getBlock();
        if (lv2 instanceof RespawnAnchorBlock && lv.get(RespawnAnchorBlock.CHARGES) > 0 && RespawnAnchorBlock.isNether(arg)) {
            Optional<Vec3d> optional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, arg, arg2);
            if (!bl2 && optional.isPresent()) {
                arg.setBlockState(arg2, (BlockState)lv.with(RespawnAnchorBlock.CHARGES, lv.get(RespawnAnchorBlock.CHARGES) - 1), 3);
            }
            return optional;
        }
        if (lv2 instanceof BedBlock && BedBlock.isOverworld(arg, arg2)) {
            return BedBlock.findWakeUpPosition(EntityType.PLAYER, arg, arg2, 0);
        }
        if (!bl) {
            return Optional.empty();
        }
        boolean bl3 = lv2.canMobSpawnInside();
        boolean bl4 = arg.getBlockState(arg2.up()).getBlock().canMobSpawnInside();
        if (bl3 && bl4) {
            return Optional.of(new Vec3d((double)arg2.getX() + 0.5, (double)arg2.getY() + 0.1, (double)arg2.getZ() + 0.5));
        }
        return Optional.empty();
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepTimer >= 100;
    }

    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public void sendMessage(Text arg, boolean bl) {
    }

    public void incrementStat(Identifier arg) {
        this.incrementStat(Stats.CUSTOM.getOrCreateStat(arg));
    }

    public void increaseStat(Identifier arg, int i) {
        this.increaseStat(Stats.CUSTOM.getOrCreateStat(arg), i);
    }

    public void incrementStat(Stat<?> arg) {
        this.increaseStat(arg, 1);
    }

    public void increaseStat(Stat<?> arg, int i) {
    }

    public void resetStat(Stat<?> arg) {
    }

    public int unlockRecipes(Collection<Recipe<?>> collection) {
        return 0;
    }

    public void unlockRecipes(Identifier[] args) {
    }

    public int lockRecipes(Collection<Recipe<?>> collection) {
        return 0;
    }

    @Override
    public void jump() {
        super.jump();
        this.incrementStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.addExhaustion(0.2f);
        } else {
            this.addExhaustion(0.05f);
        }
    }

    @Override
    public void travel(Vec3d arg) {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        if (this.isSwimming() && !this.hasVehicle()) {
            double h;
            double g = this.getRotationVector().y;
            double d2 = h = g < -0.2 ? 0.085 : 0.06;
            if (g <= 0.0 || this.jumping || !this.world.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
                Vec3d lv = this.getVelocity();
                this.setVelocity(lv.add(0.0, (g - lv.y) * h, 0.0));
            }
        }
        if (this.abilities.flying && !this.hasVehicle()) {
            double i = this.getVelocity().y;
            float j = this.flyingSpeed;
            this.flyingSpeed = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
            super.travel(arg);
            Vec3d lv2 = this.getVelocity();
            this.setVelocity(lv2.x, i * 0.6, lv2.z);
            this.flyingSpeed = j;
            this.fallDistance = 0.0f;
            this.setFlag(7, false);
        } else {
            super.travel(arg);
        }
        this.increaseTravelMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean doesNotSuffocate(BlockPos arg) {
        return !this.world.getBlockState(arg).shouldSuffocate(this.world, arg);
    }

    @Override
    public float getMovementSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    public void increaseTravelMotionStats(double d, double e, double f) {
        if (this.hasVehicle()) {
            return;
        }
        if (this.isSwimming()) {
            int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.SWIM_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isSubmergedIn(FluidTags.WATER)) {
            int j = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f);
            if (j > 0) {
                this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
                this.addExhaustion(0.01f * (float)j * 0.01f);
            }
        } else if (this.isTouchingWater()) {
            int k = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0f);
            if (k > 0) {
                this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, k);
                this.addExhaustion(0.01f * (float)k * 0.01f);
            }
        } else if (this.isClimbing()) {
            if (e > 0.0) {
                this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(e * 100.0));
            }
        } else if (this.onGround) {
            int l = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0f);
            if (l > 0) {
                if (this.isSprinting()) {
                    this.increaseStat(Stats.SPRINT_ONE_CM, l);
                    this.addExhaustion(0.1f * (float)l * 0.01f);
                } else if (this.isInSneakingPose()) {
                    this.increaseStat(Stats.CROUCH_ONE_CM, l);
                    this.addExhaustion(0.0f * (float)l * 0.01f);
                } else {
                    this.increaseStat(Stats.WALK_ONE_CM, l);
                    this.addExhaustion(0.0f * (float)l * 0.01f);
                }
            }
        } else if (this.isFallFlying()) {
            int m = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f);
            this.increaseStat(Stats.AVIATE_ONE_CM, m);
        } else {
            int n = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0f);
            if (n > 25) {
                this.increaseStat(Stats.FLY_ONE_CM, n);
            }
        }
    }

    private void increaseRidingMotionStats(double d, double e, double f) {
        int i;
        if (this.hasVehicle() && (i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0f)) > 0) {
            Entity lv = this.getVehicle();
            if (lv instanceof AbstractMinecartEntity) {
                this.increaseStat(Stats.MINECART_ONE_CM, i);
            } else if (lv instanceof BoatEntity) {
                this.increaseStat(Stats.BOAT_ONE_CM, i);
            } else if (lv instanceof PigEntity) {
                this.increaseStat(Stats.PIG_ONE_CM, i);
            } else if (lv instanceof HorseBaseEntity) {
                this.increaseStat(Stats.HORSE_ONE_CM, i);
            } else if (lv instanceof StriderEntity) {
                this.increaseStat(Stats.STRIDER_ONE_CM, i);
            }
        }
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        if (this.abilities.allowFlying) {
            return false;
        }
        if (f >= 2.0f) {
            this.increaseStat(Stats.FALL_ONE_CM, (int)Math.round((double)f * 100.0));
        }
        return super.handleFallDamage(f, g);
    }

    public boolean checkFallFlying() {
        ItemStack lv;
        if (!(this.onGround || this.isFallFlying() || this.isTouchingWater() || this.hasStatusEffect(StatusEffects.LEVITATION) || (lv = this.getEquippedStack(EquipmentSlot.CHEST)).getItem() != Items.ELYTRA || !ElytraItem.isUsable(lv))) {
            this.startFallFlying();
            return true;
        }
        return false;
    }

    public void startFallFlying() {
        this.setFlag(7, true);
    }

    public void stopFallFlying() {
        this.setFlag(7, true);
        this.setFlag(7, false);
    }

    @Override
    protected void onSwimmingStart() {
        if (!this.isSpectator()) {
            super.onSwimmingStart();
        }
    }

    @Override
    protected SoundEvent getFallSound(int i) {
        if (i > 4) {
            return SoundEvents.ENTITY_PLAYER_BIG_FALL;
        }
        return SoundEvents.ENTITY_PLAYER_SMALL_FALL;
    }

    @Override
    public void onKilledOther(LivingEntity arg) {
        this.incrementStat(Stats.KILLED.getOrCreateStat(arg.getType()));
    }

    @Override
    public void slowMovement(BlockState arg, Vec3d arg2) {
        if (!this.abilities.flying) {
            super.slowMovement(arg, arg2);
        }
    }

    public void addExperience(int i) {
        this.addScore(i);
        this.experienceProgress += (float)i / (float)this.getNextLevelExperience();
        this.totalExperience = MathHelper.clamp(this.totalExperience + i, 0, Integer.MAX_VALUE);
        while (this.experienceProgress < 0.0f) {
            float f = this.experienceProgress * (float)this.getNextLevelExperience();
            if (this.experienceLevel > 0) {
                this.addExperienceLevels(-1);
                this.experienceProgress = 1.0f + f / (float)this.getNextLevelExperience();
                continue;
            }
            this.addExperienceLevels(-1);
            this.experienceProgress = 0.0f;
        }
        while (this.experienceProgress >= 1.0f) {
            this.experienceProgress = (this.experienceProgress - 1.0f) * (float)this.getNextLevelExperience();
            this.addExperienceLevels(1);
            this.experienceProgress /= (float)this.getNextLevelExperience();
        }
    }

    public int getEnchantmentTableSeed() {
        return this.enchantmentTableSeed;
    }

    public void applyEnchantmentCosts(ItemStack arg, int i) {
        this.experienceLevel -= i;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        this.enchantmentTableSeed = this.random.nextInt();
    }

    public void addExperienceLevels(int i) {
        this.experienceLevel += i;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0f;
            this.totalExperience = 0;
        }
        if (i > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.age - 100.0f) {
            float f = this.experienceLevel > 30 ? 1.0f : (float)this.experienceLevel / 30.0f;
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75f, 1.0f);
            this.lastPlayedLevelUpSoundTime = this.age;
        }
    }

    public int getNextLevelExperience() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        }
        if (this.experienceLevel >= 15) {
            return 37 + (this.experienceLevel - 15) * 5;
        }
        return 7 + this.experienceLevel * 2;
    }

    public void addExhaustion(float f) {
        if (this.abilities.invulnerable) {
            return;
        }
        if (!this.world.isClient) {
            this.hungerManager.addExhaustion(f);
        }
    }

    public HungerManager getHungerManager() {
        return this.hungerManager;
    }

    public boolean canConsume(boolean bl) {
        return this.abilities.invulnerable || bl || this.hungerManager.isNotFull();
    }

    public boolean canFoodHeal() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }

    public boolean canModifyBlocks() {
        return this.abilities.allowModifyWorld;
    }

    public boolean canPlaceOn(BlockPos arg, Direction arg2, ItemStack arg3) {
        if (this.abilities.allowModifyWorld) {
            return true;
        }
        BlockPos lv = arg.offset(arg2.getOpposite());
        CachedBlockPosition lv2 = new CachedBlockPosition(this.world, lv, false);
        return arg3.canPlaceOn(this.world.getTagManager(), lv2);
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || this.isSpectator()) {
            return 0;
        }
        int i = this.experienceLevel * 7;
        if (i > 100) {
            return 100;
        }
        return i;
    }

    @Override
    protected boolean shouldAlwaysDropXp() {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return true;
    }

    @Override
    protected boolean canClimb() {
        return !this.abilities.flying && (!this.onGround || !this.isSneaky());
    }

    public void sendAbilitiesUpdate() {
    }

    public void setGameMode(GameMode arg) {
    }

    @Override
    public Text getName() {
        return new LiteralText(this.gameProfile.getName());
    }

    public EnderChestInventory getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot arg) {
        if (arg == EquipmentSlot.MAINHAND) {
            return this.inventory.getMainHandStack();
        }
        if (arg == EquipmentSlot.OFFHAND) {
            return this.inventory.offHand.get(0);
        }
        if (arg.getType() == EquipmentSlot.Type.ARMOR) {
            return this.inventory.armor.get(arg.getEntitySlotId());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot arg, ItemStack arg2) {
        if (arg == EquipmentSlot.MAINHAND) {
            this.onEquipStack(arg2);
            this.inventory.main.set(this.inventory.selectedSlot, arg2);
        } else if (arg == EquipmentSlot.OFFHAND) {
            this.onEquipStack(arg2);
            this.inventory.offHand.set(0, arg2);
        } else if (arg.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipStack(arg2);
            this.inventory.armor.set(arg.getEntitySlotId(), arg2);
        }
    }

    public boolean giveItemStack(ItemStack arg) {
        this.onEquipStack(arg);
        return this.inventory.insertStack(arg);
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return Lists.newArrayList((Object[])new ItemStack[]{this.getMainHandStack(), this.getOffHandStack()});
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    public boolean addShoulderEntity(CompoundTag arg) {
        if (this.hasVehicle() || !this.onGround || this.isTouchingWater()) {
            return false;
        }
        if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(arg);
            this.shoulderEntityAddedTime = this.world.getTime();
            return true;
        }
        if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(arg);
            this.shoulderEntityAddedTime = this.world.getTime();
            return true;
        }
        return false;
    }

    protected void dropShoulderEntities() {
        if (this.shoulderEntityAddedTime + 20L < this.world.getTime()) {
            this.dropShoulderEntity(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.dropShoulderEntity(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }

    private void dropShoulderEntity(CompoundTag arg2) {
        if (!this.world.isClient && !arg2.isEmpty()) {
            EntityType.getEntityFromTag(arg2, this.world).ifPresent(arg -> {
                if (arg instanceof TameableEntity) {
                    ((TameableEntity)arg).setOwnerUuid(this.uuid);
                }
                arg.updatePosition(this.getX(), this.getY() + (double)0.7f, this.getZ());
                ((ServerWorld)this.world).tryLoadEntity((Entity)arg);
            });
        }
    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean canFly() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public Text getDisplayName() {
        MutableText lv = Team.modifyText(this.getScoreboardTeam(), this.getName());
        return this.addTellClickEvent(lv);
    }

    public Text getNameAndUuid() {
        return new LiteralText("").append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
    }

    private MutableText addTellClickEvent(MutableText arg2) {
        String string = this.getGameProfile().getName();
        return arg2.styled(arg -> arg.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + string + " ")).setHoverEvent(this.getHoverEvent()).withInsertion(string));
    }

    @Override
    public String getEntityName() {
        return this.getGameProfile().getName();
    }

    @Override
    public float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        switch (arg) {
            case SWIMMING: 
            case FALL_FLYING: 
            case SPIN_ATTACK: {
                return 0.4f;
            }
            case CROUCHING: {
                return 1.27f;
            }
        }
        return 1.62f;
    }

    @Override
    public void setAbsorptionAmount(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.getDataTracker().set(ABSORPTION_AMOUNT, Float.valueOf(f));
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getDataTracker().get(ABSORPTION_AMOUNT).floatValue();
    }

    public static UUID getUuidFromProfile(GameProfile gameProfile) {
        UUID uUID = gameProfile.getId();
        if (uUID == null) {
            uUID = PlayerEntity.getOfflinePlayerUuid(gameProfile.getName());
        }
        return uUID;
    }

    public static UUID getOfflinePlayerUuid(String string) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isPartVisible(PlayerModelPart arg) {
        return (this.getDataTracker().get(PLAYER_MODEL_PARTS) & arg.getBitFlag()) == arg.getBitFlag();
    }

    @Override
    public boolean equip(int i, ItemStack arg) {
        Object lv5;
        if (i >= 0 && i < this.inventory.main.size()) {
            this.inventory.setStack(i, arg);
            return true;
        }
        if (i == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            EquipmentSlot lv = EquipmentSlot.HEAD;
        } else if (i == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            EquipmentSlot lv2 = EquipmentSlot.CHEST;
        } else if (i == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            EquipmentSlot lv3 = EquipmentSlot.LEGS;
        } else if (i == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            EquipmentSlot lv4 = EquipmentSlot.FEET;
        } else {
            lv5 = null;
        }
        if (i == 98) {
            this.equipStack(EquipmentSlot.MAINHAND, arg);
            return true;
        }
        if (i == 99) {
            this.equipStack(EquipmentSlot.OFFHAND, arg);
            return true;
        }
        if (lv5 != null) {
            if (!arg.isEmpty() && (arg.getItem() instanceof ArmorItem || arg.getItem() instanceof ElytraItem ? MobEntity.getPreferredEquipmentSlot(arg) != lv5 : lv5 != EquipmentSlot.HEAD)) {
                return false;
            }
            this.inventory.setStack(lv5.getEntitySlotId() + this.inventory.main.size(), arg);
            return true;
        }
        int j = i - 200;
        if (j >= 0 && j < this.enderChestInventory.size()) {
            this.enderChestInventory.setStack(j, arg);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean getReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public void setReducedDebugInfo(boolean bl) {
        this.reducedDebugInfo = bl;
    }

    @Override
    public Arm getMainArm() {
        return this.dataTracker.get(MAIN_ARM) == 0 ? Arm.LEFT : Arm.RIGHT;
    }

    public void setMainArm(Arm arg) {
        this.dataTracker.set(MAIN_ARM, (byte)(arg != Arm.LEFT ? 1 : 0));
    }

    public CompoundTag getShoulderEntityLeft() {
        return this.dataTracker.get(LEFT_SHOULDER_ENTITY);
    }

    protected void setShoulderEntityLeft(CompoundTag arg) {
        this.dataTracker.set(LEFT_SHOULDER_ENTITY, arg);
    }

    public CompoundTag getShoulderEntityRight() {
        return this.dataTracker.get(RIGHT_SHOULDER_ENTITY);
    }

    protected void setShoulderEntityRight(CompoundTag arg) {
        this.dataTracker.set(RIGHT_SHOULDER_ENTITY, arg);
    }

    public float getAttackCooldownProgressPerTick() {
        return (float)(1.0 / this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0);
    }

    public float getAttackCooldownProgress(float f) {
        return MathHelper.clamp(((float)this.lastAttackedTicks + f) / this.getAttackCooldownProgressPerTick(), 0.0f, 1.0f);
    }

    public void resetLastAttackedTicks() {
        this.lastAttackedTicks = 0;
    }

    public ItemCooldownManager getItemCooldownManager() {
        return this.itemCooldownManager;
    }

    @Override
    protected float getVelocityMultiplier() {
        return this.abilities.flying || this.isFallFlying() ? 1.0f : super.getVelocityMultiplier();
    }

    public float getLuck() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_LUCK);
    }

    public boolean isCreativeLevelTwoOp() {
        return this.abilities.creativeMode && this.getPermissionLevel() >= 2;
    }

    @Override
    public boolean canPickUp(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        return this.getEquippedStack(lv).isEmpty();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose arg) {
        return POSE_DIMENSIONS.getOrDefault((Object)arg, STANDING_DIMENSIONS);
    }

    @Override
    public ImmutableList<EntityPose> getPoses() {
        return ImmutableList.of((Object)((Object)EntityPose.STANDING), (Object)((Object)EntityPose.CROUCHING), (Object)((Object)EntityPose.SWIMMING));
    }

    @Override
    public ItemStack getArrowType(ItemStack arg) {
        if (!(arg.getItem() instanceof RangedWeaponItem)) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> predicate = ((RangedWeaponItem)arg.getItem()).getHeldProjectiles();
        ItemStack lv = RangedWeaponItem.getHeldProjectile(this, predicate);
        if (!lv.isEmpty()) {
            return lv;
        }
        predicate = ((RangedWeaponItem)arg.getItem()).getProjectiles();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack lv2 = this.inventory.getStack(i);
            if (!predicate.test(lv2)) continue;
            return lv2;
        }
        return this.abilities.creativeMode ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack eatFood(World arg, ItemStack arg2) {
        this.getHungerManager().eat(arg2.getItem(), arg2);
        this.incrementStat(Stats.USED.getOrCreateStat(arg2.getItem()));
        arg.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, arg.random.nextFloat() * 0.1f + 0.9f);
        if (this instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)this, arg2);
        }
        return super.eatFood(arg, arg2);
    }

    @Override
    protected boolean method_29500(BlockState arg) {
        return this.abilities.flying || super.method_29500(arg);
    }

    public static enum SleepFailureReason {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(new TranslatableText("block.minecraft.bed.no_sleep")),
        TOO_FAR_AWAY(new TranslatableText("block.minecraft.bed.too_far_away")),
        OBSTRUCTED(new TranslatableText("block.minecraft.bed.obstructed")),
        OTHER_PROBLEM,
        NOT_SAFE(new TranslatableText("block.minecraft.bed.not_safe"));

        @Nullable
        private final Text text;

        private SleepFailureReason() {
            this.text = null;
        }

        private SleepFailureReason(Text arg) {
            this.text = arg;
        }

        @Nullable
        public Text toText() {
            return this.text;
        }
    }
}

