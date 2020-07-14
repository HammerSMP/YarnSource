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
import net.minecraft.class_5425;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.CompoundTag;
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
extends AbstractPiglinEntity
implements CrossbowUser {
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DANCING = DataTracker.registerData(PiglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
    private static final EntityAttributeModifier BABY_SPEED_BOOST = new EntityAttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", (double)0.2f, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private final SimpleInventory inventory = new SimpleInventory(8);
    private boolean cannotHunt = false;
    protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.INTERACTABLE_DOORS, SensorType.PIGLIN_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, (Object[])new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT});

    public PiglinEntity(EntityType<? extends AbstractPiglinEntity> arg, World arg2) {
        super(arg, arg2);
        this.experiencePoints = 5;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.isBaby()) {
            tag.putBoolean("IsBaby", true);
        }
        if (this.cannotHunt) {
            tag.putBoolean("CannotHunt", true);
        }
        tag.put("Inventory", this.inventory.getTags());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setBaby(tag.getBoolean("IsBaby"));
        this.setCannotHunt(tag.getBoolean("CannotHunt"));
        this.inventory.readTags(tag.getList("Inventory", 10));
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        this.inventory.clearToList().forEach(this::dropStack);
    }

    protected ItemStack addItem(ItemStack stack) {
        return this.inventory.addStack(stack);
    }

    protected boolean canInsertIntoInventory(ItemStack stack) {
        return this.inventory.canInsert(stack);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
        this.dataTracker.startTracking(CHARGING, false);
        this.dataTracker.startTracking(DANCING, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (BABY.equals(data)) {
            this.calculateDimensions();
        }
    }

    public static DefaultAttributeContainer.Builder createPiglinAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }

    public static boolean canSpawn(EntityType<PiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        if (spawnReason != SpawnReason.STRUCTURE) {
            if (arg.getRandom().nextFloat() < 0.2f) {
                this.setBaby(true);
            } else if (this.isAdult()) {
                this.equipStack(EquipmentSlot.MAINHAND, this.makeInitialWeapon());
            }
        }
        PiglinBrain.setHuntedRecently(this);
        this.initEquipment(difficulty);
        this.updateEnchantments(difficulty);
        return super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isPersistent();
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        if (this.isAdult()) {
            this.equipAtChance(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.equipAtChance(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.equipAtChance(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.equipAtChance(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }
    }

    private void equipAtChance(EquipmentSlot slot, ItemStack stack) {
        if (this.world.random.nextFloat() < 0.1f) {
            this.equipStack(slot, stack);
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ActionResult lv = super.interactMob(player, hand);
        if (lv.isAccepted()) {
            return lv;
        }
        if (this.world.isClient) {
            boolean bl = PiglinBrain.isWillingToTrade(this, player.getStackInHand(hand)) && this.getActivity() != PiglinActivity.ADMIRING_ITEM;
            return bl ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        return PiglinBrain.playerInteract(this, player, hand);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
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
    public void setBaby(boolean baby) {
        this.getDataTracker().set(BABY, baby);
        if (!this.world.isClient) {
            EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            lv.removeModifier(BABY_SPEED_BOOST);
            if (baby) {
                lv.addTemporaryModifier(BABY_SPEED_BOOST);
            }
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataTracker().get(BABY);
    }

    private void setCannotHunt(boolean cannotHunt) {
        this.cannotHunt = cannotHunt;
    }

    @Override
    protected boolean canHunt() {
        return !this.cannotHunt;
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("piglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        PiglinBrain.tickActivities(this);
        super.mobTick();
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        return this.experiencePoints;
    }

    @Override
    protected void zombify(ServerWorld world) {
        PiglinBrain.pickupItemWithOffHand(this);
        this.inventory.clearToList().forEach(this::dropStack);
        super.zombify(world);
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
    public void setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }

    @Override
    public void postShoot() {
        this.despawnCounter = 0;
    }

    @Override
    public PiglinActivity getActivity() {
        if (this.isDancing()) {
            return PiglinActivity.DANCING;
        }
        if (PiglinBrain.isGoldenItem(this.getOffHandStack().getItem())) {
            return PiglinActivity.ADMIRING_ITEM;
        }
        if (this.isAttacking() && this.isHoldingTool()) {
            return PiglinActivity.ATTACKING_WITH_MELEE_WEAPON;
        }
        if (this.isCharging()) {
            return PiglinActivity.CROSSBOW_CHARGE;
        }
        if (this.isAttacking() && this.isHolding(Items.CROSSBOW)) {
            return PiglinActivity.CROSSBOW_HOLD;
        }
        return PiglinActivity.DEFAULT;
    }

    public boolean isDancing() {
        return this.dataTracker.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        this.dataTracker.set(DANCING, dancing);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (this.world.isClient) {
            return false;
        }
        if (bl && source.getAttacker() instanceof LivingEntity) {
            PiglinBrain.onAttacked(this, (LivingEntity)source.getAttacker());
        }
        return bl;
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        this.shoot(this, 1.6f);
    }

    @Override
    public void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
        this.shoot(this, target, projectile, multiShotSpray, 1.6f);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == Items.CROSSBOW;
    }

    protected void equipToMainHand(ItemStack stack) {
        this.equipLootStack(EquipmentSlot.MAINHAND, stack);
    }

    protected void equipToOffHand(ItemStack stack) {
        if (stack.getItem() == PiglinBrain.BARTERING_ITEM) {
            this.equipStack(EquipmentSlot.OFFHAND, stack);
            this.updateDropChances(EquipmentSlot.OFFHAND);
        } else {
            this.equipLootStack(EquipmentSlot.OFFHAND, stack);
        }
    }

    @Override
    public boolean canGather(ItemStack stack) {
        return this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.canPickUpLoot() && PiglinBrain.canGather(this, stack);
    }

    protected boolean method_24846(ItemStack stack) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(stack);
        ItemStack lv2 = this.getEquippedStack(lv);
        return this.prefersNewEquipment(stack, lv2);
    }

    @Override
    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        boolean bl2;
        if (EnchantmentHelper.hasBindingCurse(oldStack)) {
            return false;
        }
        boolean bl = PiglinBrain.isGoldenItem(newStack.getItem()) || newStack.getItem() == Items.CROSSBOW;
        boolean bl3 = bl2 = PiglinBrain.isGoldenItem(oldStack.getItem()) || oldStack.getItem() == Items.CROSSBOW;
        if (bl && !bl2) {
            return true;
        }
        if (!bl && bl2) {
            return false;
        }
        if (this.isAdult() && newStack.getItem() != Items.CROSSBOW && oldStack.getItem() == Items.CROSSBOW) {
            return false;
        }
        return super.prefersNewEquipment(newStack, oldStack);
    }

    @Override
    protected void loot(ItemEntity item) {
        this.method_29499(item);
        PiglinBrain.loot(this, item);
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (this.isBaby() && entity.getType() == EntityType.HOGLIN) {
            entity = this.method_26089(entity, 3);
        }
        return super.startRiding(entity, force);
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
        if (this.world.isClient) {
            return null;
        }
        return PiglinBrain.method_30091(this).orElse(null);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15f, 1.0f);
    }

    protected void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    protected void playZombificationSound() {
        this.playSound(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }
}

