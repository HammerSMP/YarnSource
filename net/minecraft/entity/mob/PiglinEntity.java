/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PiglinEntity
extends HostileEntity
implements CrossbowUser {
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IMMUNE_TO_ZOMBIFICATION = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> dancing = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
    private static final EntityAttributeModifier BABY_SPEED_BOOST = new EntityAttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", (double)0.2f, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private int conversionTicks = 0;
    private final SimpleInventory inventory = new SimpleInventory(8);
    private boolean cannotHunt = false;
    protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.INTERACTABLE_DOORS, SensorType.PIGLIN_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEAREST_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, (Object[])new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_BABY_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT});

    public PiglinEntity(EntityType<? extends HostileEntity> arg, World arg2) {
        super(arg, arg2);
        this.setCanPickUpLoot(true);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.experiencePoints = 5;
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.isBaby()) {
            arg.putBoolean("IsBaby", true);
        }
        if (this.isImmuneToZombification()) {
            arg.putBoolean("IsImmuneToZombification", true);
        }
        if (this.cannotHunt) {
            arg.putBoolean("CannotHunt", true);
        }
        arg.putInt("TimeInOverworld", this.conversionTicks);
        arg.put("Inventory", this.inventory.getTags());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setBaby(arg.getBoolean("IsBaby"));
        this.setImmuneToZombification(arg.getBoolean("IsImmuneToZombification"));
        this.setCannotHunt(arg.getBoolean("CannotHunt"));
        this.conversionTicks = arg.getInt("TimeInOverworld");
        this.inventory.readTags(arg.getList("Inventory", 10));
    }

    @Override
    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
        super.dropEquipment(arg, i, bl);
        this.inventory.clearToList().forEach(this::dropStack);
    }

    protected ItemStack addItem(ItemStack arg) {
        return this.inventory.addStack(arg);
    }

    protected boolean canInsertIntoInventory(ItemStack arg) {
        return this.inventory.canInsert(arg);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
        this.dataTracker.startTracking(CHARGING, false);
        this.dataTracker.startTracking(IMMUNE_TO_ZOMBIFICATION, false);
        this.dataTracker.startTracking(dancing, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (BABY.equals(arg)) {
            this.calculateDimensions();
        }
    }

    public static DefaultAttributeContainer.Builder createPiglinAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }

    public static boolean canSpawn(EntityType<PiglinEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return !arg2.getBlockState(arg4.down()).isOf(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg3 != SpawnReason.STRUCTURE) {
            if (arg.getRandom().nextFloat() < 0.2f) {
                this.setBaby(true);
            } else if (this.isAdult()) {
                this.equipStack(EquipmentSlot.MAINHAND, this.makeInitialWeapon());
            }
        }
        PiglinBrain.setHuntedRecently(this);
        this.initEquipment(arg2);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.isPersistent();
    }

    @Override
    protected void initEquipment(LocalDifficulty arg) {
        if (this.isAdult()) {
            this.equipAtChance(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.equipAtChance(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.equipAtChance(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.equipAtChance(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }
    }

    private void equipAtChance(EquipmentSlot arg, ItemStack arg2) {
        if (this.world.random.nextFloat() < 0.1f) {
            this.equipStack(arg, arg2);
        }
    }

    protected Brain.Profile<PiglinEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return PiglinBrain.create(this, this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<PiglinEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ActionResult lv = super.interactMob(arg, arg2);
        if (lv.isAccepted()) {
            return lv;
        }
        if (this.world.isClient) {
            boolean bl = PiglinBrain.method_27086(this, arg.getStackInHand(arg2)) && this.getActivity() != Activity.ADMIRING_ITEM;
            return bl ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        return PiglinBrain.playerInteract(this, arg, arg2);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return this.isBaby() ? 0.93f : 1.74f;
    }

    @Override
    public double getHeightOffset() {
        return this.isBaby() ? -0.1 : -0.45;
    }

    @Override
    public double getMountedHeightOffset() {
        return (double)this.getHeight() * 0.92;
    }

    @Override
    public void setBaby(boolean bl) {
        this.getDataTracker().set(BABY, bl);
        if (!this.world.isClient) {
            EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            lv.removeModifier(BABY_SPEED_BOOST);
            if (bl) {
                lv.addTemporaryModifier(BABY_SPEED_BOOST);
            }
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataTracker().get(BABY);
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    public void setImmuneToZombification(boolean bl) {
        this.getDataTracker().set(IMMUNE_TO_ZOMBIFICATION, bl);
    }

    private boolean isImmuneToZombification() {
        return this.getDataTracker().get(IMMUNE_TO_ZOMBIFICATION);
    }

    private void setCannotHunt(boolean bl) {
        this.cannotHunt = bl;
    }

    protected boolean canHunt() {
        return !this.cannotHunt;
    }

    public boolean canConvert() {
        return !this.world.getDimension().isNether() && !this.isImmuneToZombification() && !this.isAiDisabled();
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("piglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        PiglinBrain.tickActivities(this);
        PiglinBrain.playSoundAtChance(this);
        this.conversionTicks = this.canConvert() ? ++this.conversionTicks : 0;
        if (this.conversionTicks > 300) {
            this.playZombifySound();
            this.zombify((ServerWorld)this.world);
        }
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        return this.experiencePoints;
    }

    private void zombify(ServerWorld arg) {
        PiglinBrain.method_25948(this);
        this.inventory.clearToList().forEach(this::dropStack);
        ZombifiedPiglinEntity lv = this.method_29243(EntityType.ZOMBIFIED_PIGLIN);
        lv.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    private ItemStack makeInitialWeapon() {
        if ((double)this.random.nextFloat() < 0.5) {
            return new ItemStack(Items.CROSSBOW);
        }
        return new ItemStack(Items.GOLDEN_SWORD);
    }

    private boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }

    @Override
    public void setCharging(boolean bl) {
        this.dataTracker.set(CHARGING, bl);
    }

    @Override
    public void postShoot() {
        this.despawnCounter = 0;
    }

    public Activity getActivity() {
        if (this.isDancing()) {
            return Activity.DANCING;
        }
        if (this.handSwinging) {
            return Activity.DEFAULT;
        }
        if (PiglinBrain.isGoldenItem(this.getOffHandStack().getItem())) {
            return Activity.ADMIRING_ITEM;
        }
        if (this.isCharging()) {
            return Activity.CROSSBOW_CHARGE;
        }
        if (this.isAttacking() && this.isHolding(Items.CROSSBOW)) {
            return Activity.CROSSBOW_HOLD;
        }
        if (this.isAttacking() && this.isHoldingTool()) {
            return Activity.ATTACKING_WITH_MELEE_WEAPON;
        }
        return Activity.DEFAULT;
    }

    public boolean isDancing() {
        return this.dataTracker.get(dancing);
    }

    public void setDancing(boolean bl) {
        this.dataTracker.set(dancing, bl);
    }

    private boolean isHoldingTool() {
        return this.getMainHandStack().getItem() instanceof ToolItem;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        boolean bl = super.damage(arg, f);
        if (this.world.isClient) {
            return false;
        }
        if (bl && arg.getAttacker() instanceof LivingEntity) {
            PiglinBrain.onAttacked(this, (LivingEntity)arg.getAttacker());
        }
        return bl;
    }

    @Override
    public void attack(LivingEntity arg, float f) {
        this.shoot(this, 1.6f);
    }

    @Override
    public void shoot(LivingEntity arg, ItemStack arg2, ProjectileEntity arg3, float f) {
        this.shoot(this, arg, arg3, f, 1.6f);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem arg) {
        return arg == Items.CROSSBOW;
    }

    protected void equipToMainHand(ItemStack arg) {
        this.equipLootStack(EquipmentSlot.MAINHAND, arg);
    }

    protected void equipToOffHand(ItemStack arg) {
        if (arg.getItem() == PiglinBrain.BARTERING_ITEM) {
            this.equipStack(EquipmentSlot.OFFHAND, arg);
            this.updateDropChances(EquipmentSlot.OFFHAND);
        } else {
            this.equipLootStack(EquipmentSlot.OFFHAND, arg);
        }
    }

    @Override
    public boolean canGather(ItemStack arg) {
        return this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && PiglinBrain.canGather(this, arg);
    }

    protected boolean method_24846(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        ItemStack lv2 = this.getEquippedStack(lv);
        return this.prefersNewEquipment(arg, lv2);
    }

    @Override
    protected boolean prefersNewEquipment(ItemStack arg, ItemStack arg2) {
        boolean bl2;
        boolean bl = PiglinBrain.isGoldenItem(arg.getItem()) || arg.getItem() == Items.CROSSBOW;
        boolean bl3 = bl2 = PiglinBrain.isGoldenItem(arg2.getItem()) || arg2.getItem() == Items.CROSSBOW;
        if (bl && !bl2) {
            return true;
        }
        if (!bl && bl2) {
            return false;
        }
        if (this.isAdult() && arg.getItem() != Items.CROSSBOW && arg2.getItem() == Items.CROSSBOW) {
            return false;
        }
        return super.prefersNewEquipment(arg, arg2);
    }

    @Override
    protected void loot(ItemEntity arg) {
        this.method_29499(arg);
        PiglinBrain.loot(this, arg);
    }

    @Override
    public boolean startRiding(Entity arg, boolean bl) {
        if (this.isBaby() && arg.getType() == EntityType.HOGLIN) {
            arg = this.method_26089(arg, 3);
        }
        return super.startRiding(arg, bl);
    }

    private Entity method_26089(Entity arg, int i) {
        List<Entity> list = arg.getPassengerList();
        if (i == 1 || list.isEmpty()) {
            return arg;
        }
        return this.method_26089(list.get(0), i - 1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15f, 1.0f);
    }

    protected void playAdmireItemSound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, 1.0f, this.getSoundPitch());
    }

    @Override
    public void playAmbientSound() {
        if (PiglinBrain.hasIdleActivity(this)) {
            super.playAmbientSound();
        }
    }

    protected void playAngrySound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_ANGRY, 1.0f, this.getSoundPitch());
    }

    protected void playCelebrateSound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_CELEBRATE, 1.0f, this.getSoundPitch());
    }

    protected void playRetreatSound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_RETREAT, 1.0f, this.getSoundPitch());
    }

    protected void playJealousSound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_JEALOUS, 1.0f, this.getSoundPitch());
    }

    private void playZombifySound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED, 1.0f, this.getSoundPitch());
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public static enum Activity {
        ATTACKING_WITH_MELEE_WEAPON,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        ADMIRING_ITEM,
        DANCING,
        DEFAULT;

    }
}

