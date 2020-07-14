/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5425;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.StepAndDestroyBlockGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ZombieEntity
extends HostileEntity {
    private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final EntityAttributeModifier BABY_SPEED_BONUS = new EntityAttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ZOMBIE_TYPE = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CONVERTING_IN_WATER = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = difficulty -> difficulty == Difficulty.HARD;
    private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER);
    private boolean canBreakDoors;
    private int inWaterTime;
    private int ticksUntilWaterConversion;

    public ZombieEntity(EntityType<? extends ZombieEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
    }

    public ZombieEntity(World world) {
        this((EntityType<? extends ZombieEntity>)EntityType.ZOMBIE, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new DestroyEggGoal((PathAwareEntity)this, 1.0, 3));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new FollowTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    public static DefaultAttributeContainer.Builder createZombieAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0).add(EntityAttributes.GENERIC_ARMOR, 2.0).add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(BABY, false);
        this.getDataTracker().startTracking(ZOMBIE_TYPE, 0);
        this.getDataTracker().startTracking(CONVERTING_IN_WATER, false);
    }

    public boolean isConvertingInWater() {
        return this.getDataTracker().get(CONVERTING_IN_WATER);
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        if (this.shouldBreakDoors()) {
            if (this.canBreakDoors != canBreakDoors) {
                this.canBreakDoors = canBreakDoors;
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
                if (canBreakDoors) {
                    this.goalSelector.add(1, this.breakDoorsGoal);
                } else {
                    this.goalSelector.remove(this.breakDoorsGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.remove(this.breakDoorsGoal);
            this.canBreakDoors = false;
        }
    }

    protected boolean shouldBreakDoors() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return this.getDataTracker().get(BABY);
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        if (this.isBaby()) {
            this.experiencePoints = (int)((float)this.experiencePoints * 2.5f);
        }
        return super.getCurrentExperience(player);
    }

    @Override
    public void setBaby(boolean baby) {
        this.getDataTracker().set(BABY, baby);
        if (this.world != null && !this.world.isClient) {
            EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            lv.removeModifier(BABY_SPEED_BONUS);
            if (baby) {
                lv.addTemporaryModifier(BABY_SPEED_BONUS);
            }
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (BABY.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    protected boolean canConvertInWater() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive() && !this.isAiDisabled()) {
            if (this.isConvertingInWater()) {
                --this.ticksUntilWaterConversion;
                if (this.ticksUntilWaterConversion < 0) {
                    this.convertInWater();
                }
            } else if (this.canConvertInWater()) {
                if (this.isSubmergedIn(FluidTags.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.setTicksUntilWaterConversion(300);
                    }
                } else {
                    this.inWaterTime = -1;
                }
            }
        }
        super.tick();
    }

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl;
            boolean bl2 = bl = this.burnsInDaylight() && this.isInDaylight();
            if (bl) {
                ItemStack lv = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!lv.isEmpty()) {
                    if (lv.isDamageable()) {
                        lv.setDamage(lv.getDamage() + this.random.nextInt(2));
                        if (lv.getDamage() >= lv.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    bl = false;
                }
                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }
        super.tickMovement();
    }

    private void setTicksUntilWaterConversion(int ticksUntilWaterConversion) {
        this.ticksUntilWaterConversion = ticksUntilWaterConversion;
        this.getDataTracker().set(CONVERTING_IN_WATER, true);
    }

    protected void convertInWater() {
        this.convertTo(EntityType.DROWNED);
        if (!this.isSilent()) {
            this.world.syncWorldEvent(null, 1040, this.getBlockPos(), 0);
        }
    }

    protected void convertTo(EntityType<? extends ZombieEntity> entityType) {
        ZombieEntity lv = this.method_29243(entityType);
        if (lv != null) {
            lv.applyAttributeModifiers(lv.world.getLocalDifficulty(lv.getBlockPos()).getClampedLocalDifficulty());
            lv.setCanBreakDoors(lv.shouldBreakDoors() && this.canBreakDoors());
        }
    }

    protected boolean burnsInDaylight() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!super.damage(source, amount)) {
            return false;
        }
        if (!(this.world instanceof ServerWorld)) {
            return false;
        }
        ServerWorld lv = (ServerWorld)this.world;
        LivingEntity lv2 = this.getTarget();
        if (lv2 == null && source.getAttacker() instanceof LivingEntity) {
            lv2 = (LivingEntity)source.getAttacker();
        }
        if (lv2 != null && this.world.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS) && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            ZombieEntity lv3 = new ZombieEntity(this.world);
            for (int l = 0; l < 50; ++l) {
                int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                BlockPos lv4 = new BlockPos(m, n, o);
                EntityType<?> lv5 = lv3.getType();
                SpawnRestriction.Location lv6 = SpawnRestriction.getLocation(lv5);
                if (!SpawnHelper.canSpawn(lv6, this.world, lv4, lv5) || !SpawnRestriction.canSpawn(lv5, lv, SpawnReason.REINFORCEMENT, lv4, this.world.random)) continue;
                lv3.updatePosition(m, n, o);
                if (this.world.isPlayerInRange(m, n, o, 7.0) || !this.world.intersectsEntities(lv3) || !this.world.doesNotCollide(lv3) || this.world.containsFluid(lv3.getBoundingBox())) continue;
                this.world.spawnEntity(lv3);
                lv3.setTarget(lv2);
                lv3.initialize(lv, this.world.getLocalDifficulty(lv3.getBlockPos()), SpawnReason.REINFORCEMENT, null, null);
                this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05f, EntityAttributeModifier.Operation.ADDITION));
                lv3.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05f, EntityAttributeModifier.Operation.ADDITION));
                break;
            }
        }
        return true;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl) {
            float f = this.world.getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
            if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3f) {
                target.setOnFireFor(2 * (int)f);
            }
        }
        return bl;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        super.initEquipment(difficulty);
        float f = this.world.getDifficulty() == Difficulty.HARD ? 0.05f : 0.01f;
        if (this.random.nextFloat() < f) {
            int i = this.random.nextInt(3);
            if (i == 0) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("IsBaby", this.isBaby());
        tag.putBoolean("CanBreakDoors", this.canBreakDoors());
        tag.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
        tag.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setBaby(tag.getBoolean("IsBaby"));
        this.setCanBreakDoors(tag.getBoolean("CanBreakDoors"));
        this.inWaterTime = tag.getInt("InWaterTime");
        if (tag.contains("DrownedConversionTime", 99) && tag.getInt("DrownedConversionTime") > -1) {
            this.setTicksUntilWaterConversion(tag.getInt("DrownedConversionTime"));
        }
    }

    @Override
    public void onKilledOther(ServerWorld arg, LivingEntity arg2) {
        super.onKilledOther(arg, arg2);
        if ((arg.getDifficulty() == Difficulty.NORMAL || arg.getDifficulty() == Difficulty.HARD) && arg2 instanceof VillagerEntity) {
            if (arg.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return;
            }
            VillagerEntity lv = (VillagerEntity)arg2;
            ZombieVillagerEntity lv2 = EntityType.ZOMBIE_VILLAGER.create(arg);
            lv2.copyPositionAndRotation(lv);
            lv.remove();
            lv2.initialize(arg, arg.getLocalDifficulty(lv2.getBlockPos()), SpawnReason.CONVERSION, new ZombieData(false, true), null);
            lv2.setVillagerData(lv.getVillagerData());
            lv2.setGossipData((Tag)lv.getGossip().serialize(NbtOps.INSTANCE).getValue());
            lv2.setOfferData(lv.getOffers().toTag());
            lv2.setXp(lv.getExperience());
            lv2.setBaby(lv.isBaby());
            lv2.setAiDisabled(lv.isAiDisabled());
            if (lv.hasCustomName()) {
                lv2.setCustomName(lv.getCustomName());
                lv2.setCustomNameVisible(lv.isCustomNameVisible());
            }
            if (lv.isPersistent()) {
                lv2.setPersistent();
            }
            lv2.setInvulnerable(this.isInvulnerable());
            arg.spawnEntity(lv2);
            if (!this.isSilent()) {
                arg.syncWorldEvent(null, 1026, this.getBlockPos(), 0);
            }
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.isBaby() ? 0.93f : 1.74f;
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        if (stack.getItem() == Items.EGG && this.isBaby() && this.hasVehicle()) {
            return false;
        }
        return super.canPickupItem(stack);
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        entityData = super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
        float f = difficulty.getClampedLocalDifficulty();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55f * f);
        if (entityData == null) {
            entityData = new ZombieData(ZombieEntity.method_29936(arg.getRandom()), true);
        }
        if (entityData instanceof ZombieData) {
            ZombieData lv = (ZombieData)entityData;
            if (lv.baby) {
                this.setBaby(true);
                if (lv.field_25607) {
                    if ((double)arg.getRandom().nextFloat() < 0.05) {
                        List<Entity> list = arg.getEntities(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
                        if (!list.isEmpty()) {
                            ChickenEntity lv2 = (ChickenEntity)list.get(0);
                            lv2.setHasJockey(true);
                            this.startRiding(lv2);
                        }
                    } else if ((double)arg.getRandom().nextFloat() < 0.05) {
                        ChickenEntity lv3 = EntityType.CHICKEN.create(this.world);
                        lv3.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0f);
                        lv3.initialize(arg, difficulty, SpawnReason.JOCKEY, null, null);
                        lv3.setHasJockey(true);
                        this.startRiding(lv3);
                        arg.spawnEntity(lv3);
                    }
                }
            }
            this.setCanBreakDoors(this.shouldBreakDoors() && this.random.nextFloat() < f * 0.1f);
            this.initEquipment(difficulty);
            this.updateEnchantments(difficulty);
        }
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25f) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
            }
        }
        this.applyAttributeModifiers(f);
        return entityData;
    }

    public static boolean method_29936(Random random) {
        return random.nextFloat() < 0.05f;
    }

    protected void applyAttributeModifiers(float chanceMultiplier) {
        this.initAttributes();
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * (double)0.05f, EntityAttributeModifier.Operation.ADDITION));
        double d = this.random.nextDouble() * 1.5 * (double)chanceMultiplier;
        if (d > 1.0) {
            this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random zombie-spawn bonus", d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (this.random.nextFloat() < chanceMultiplier * 0.05f) {
            this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADDITION));
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setCanBreakDoors(this.shouldBreakDoors());
        }
    }

    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * (double)0.1f);
    }

    @Override
    public double getHeightOffset() {
        return this.isBaby() ? 0.0 : -0.45;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        CreeperEntity lv2;
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        Entity lv = source.getAttacker();
        if (lv instanceof CreeperEntity && (lv2 = (CreeperEntity)lv).shouldDropHead()) {
            lv2.onHeadDropped();
            ItemStack lv3 = this.getSkull();
            if (!lv3.isEmpty()) {
                this.dropStack(lv3);
            }
        }
    }

    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class DestroyEggGoal
    extends StepAndDestroyBlockGoal {
        DestroyEggGoal(PathAwareEntity mob, double speed, int maxYDifference) {
            super(Blocks.TURTLE_EGG, mob, speed, maxYDifference);
        }

        @Override
        public void tickStepping(WorldAccess world, BlockPos pos) {
            world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5f, 0.9f + ZombieEntity.this.random.nextFloat() * 0.2f);
        }

        @Override
        public void onDestroyBlock(World world, BlockPos pos) {
            world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        }

        @Override
        public double getDesiredSquaredDistanceToTarget() {
            return 1.14;
        }
    }

    public static class ZombieData
    implements EntityData {
        public final boolean baby;
        public final boolean field_25607;

        public ZombieData(boolean baby, boolean bl2) {
            this.baby = baby;
            this.field_25607 = bl2;
        }
    }
}

