/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HoglinEntity
extends AnimalEntity
implements Monster,
Hoglin {
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(HoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int movementCooldownTicks;
    private int timeInOverworld = 0;
    private boolean cannotBeHunted = false;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super HoglinEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HOGLIN_SPECIFIC_SENSOR);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, (Object[])new MemoryModuleType[]{MemoryModuleType.AVOID_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.PACIFIED});

    public HoglinEntity(EntityType<? extends HoglinEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.experiencePoints = 5;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return !this.isLeashed();
    }

    public static DefaultAttributeContainer.Builder createHoglinAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        if (!(arg instanceof LivingEntity)) {
            return false;
        }
        this.movementCooldownTicks = 10;
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0f, this.getSoundPitch());
        HoglinBrain.onAttacking(this, (LivingEntity)arg);
        return Hoglin.tryAttack(this, (LivingEntity)arg);
    }

    @Override
    protected void knockback(LivingEntity arg) {
        if (this.isAdult()) {
            Hoglin.knockback(this, arg);
        }
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        boolean bl = super.damage(arg, f);
        if (this.world.isClient) {
            return false;
        }
        if (bl && arg.getAttacker() instanceof LivingEntity) {
            HoglinBrain.onAttacked(this, (LivingEntity)arg.getAttacker());
        }
        return bl;
    }

    protected Brain.Profile<HoglinEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return HoglinBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<HoglinEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("hoglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        HoglinBrain.refreshActivities(this);
        HoglinBrain.playSoundAtChance(this);
        if (this.canConvert()) {
            ++this.timeInOverworld;
            if (this.timeInOverworld > 300) {
                this.playZombifySound();
                this.zombify((ServerWorld)this.world);
            }
        } else {
            this.timeInOverworld = 0;
        }
    }

    @Override
    public void tickMovement() {
        if (this.movementCooldownTicks > 0) {
            --this.movementCooldownTicks;
        }
        super.tickMovement();
    }

    @Override
    protected void onGrowUp() {
        if (this.isBaby()) {
            this.experiencePoints = 3;
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
        } else {
            this.experiencePoints = 5;
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6.0);
        }
    }

    public static boolean canSpawn(EntityType<HoglinEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return !arg2.getBlockState(arg4.down()).isOf(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg.getRandom().nextFloat() < 0.2f) {
            this.setBaby(true);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.isPersistent();
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (HoglinBrain.isWarpedFungusAround(this, arg)) {
            return -1.0f;
        }
        if (arg2.getBlockState(arg.down()).isOf(Blocks.CRIMSON_NYLIUM)) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    public double getMountedHeightOffset() {
        return (double)this.getHeight() - (this.isBaby() ? 0.2 : 0.15);
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        boolean bl = super.interactMob(arg, arg2);
        if (bl) {
            this.setPersistent();
        }
        return bl;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 4) {
            this.movementCooldownTicks = 10;
            this.playSound(SoundEvents.ENTITY_HOGLIN_ATTACK, 1.0f, this.getSoundPitch());
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getMovementCooldownTicks() {
        return this.movementCooldownTicks;
    }

    @Override
    protected boolean canDropLootAndXp() {
        return true;
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        return this.experiencePoints;
    }

    private void zombify(ServerWorld arg) {
        ZoglinEntity lv = this.method_29243(EntityType.ZOGLIN);
        lv.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return arg.getItem() == Items.CRIMSON_FUNGUS;
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.isImmuneToZombification()) {
            arg.putBoolean("IsImmuneToZombification", true);
        }
        arg.putInt("TimeInOverworld", this.timeInOverworld);
        if (this.cannotBeHunted) {
            arg.putBoolean("CannotBeHunted", true);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setImmuneToZombification(arg.getBoolean("IsImmuneToZombification"));
        this.timeInOverworld = arg.getInt("TimeInOverworld");
        this.setCannotBeHunted(arg.getBoolean("CannotBeHunted"));
    }

    public void setImmuneToZombification(boolean bl) {
        this.getDataTracker().set(BABY, bl);
    }

    private boolean isImmuneToZombification() {
        return this.getDataTracker().get(BABY);
    }

    public boolean canConvert() {
        return !this.world.getDimension().isNether() && !this.isImmuneToZombification() && !this.isAiDisabled();
    }

    private void setCannotBeHunted(boolean bl) {
        this.cannotBeHunted = bl;
    }

    public boolean canBeHunted() {
        return this.isAdult() && !this.cannotBeHunted;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity arg) {
        HoglinEntity lv = EntityType.HOGLIN.create(this.world);
        if (lv != null) {
            lv.setPersistent();
        }
        return lv;
    }

    @Override
    public boolean canEat() {
        return !HoglinBrain.isNearPlayer(this) && super.canEat();
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HOGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_HOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HOGLIN_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    public void playAmbientSound() {
        if (HoglinBrain.hasIdleActivity(this)) {
            super.playAmbientSound();
        }
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_HOGLIN_STEP, 0.15f, 1.0f);
    }

    protected void playFightSound() {
        this.playSound(SoundEvents.ENTITY_HOGLIN_ANGRY, 1.0f, this.getSoundPitch());
    }

    protected void playRetreatSound() {
        this.playSound(SoundEvents.ENTITY_HOGLIN_RETREAT, 1.0f, this.getSoundPitch());
    }

    private void playZombifySound() {
        this.playSound(SoundEvents.ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED, 1.0f, this.getSoundPitch());
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }
}

