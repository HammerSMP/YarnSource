/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.world.World;

public class ZoglinEntity
extends HostileEntity
implements Monster,
Hoglin {
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZoglinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int movementCooldownTicks;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super ZoglinEntity>>> USED_SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> USED_MEMORY_MODULES = ImmutableList.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    public ZoglinEntity(EntityType<? extends ZoglinEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.experiencePoints = 5;
    }

    protected Brain.Profile<ZoglinEntity> createBrainProfile() {
        return Brain.createProfile(USED_MEMORY_MODULES, USED_SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<ZoglinEntity> lv = this.createBrainProfile().deserialize(dynamic);
        ZoglinEntity.method_26928(lv);
        ZoglinEntity.method_26929(lv);
        ZoglinEntity.method_26930(lv);
        lv.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        lv.setDefaultActivity(Activity.IDLE);
        lv.resetPossibleActivities();
        return lv;
    }

    private static void method_26928(Brain<ZoglinEntity> arg) {
        arg.setTaskList(Activity.CORE, 0, (ImmutableList<Task<ZoglinEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(200)));
    }

    private static void method_26929(Brain<ZoglinEntity> arg) {
        arg.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<ZoglinEntity>>)ImmutableList.of(new UpdateAttackTargetTask<ZoglinEntity>(ZoglinEntity::method_26934), new TimeLimitedTask<LivingEntity>(new FollowMobTask(8.0f), IntRange.between(30, 60)), new RandomTask(ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.4f), (Object)2), (Object)Pair.of((Object)new GoTowardsLookTarget(0.4f, 3), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)))));
    }

    private static void method_26930(Brain<ZoglinEntity> arg) {
        arg.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<ZoglinEntity>>)ImmutableList.of((Object)new RangedApproachTask(1.0f), new ConditionalTask<MobEntity>(ZoglinEntity::isAdult, new MeleeAttackTask(40)), new ConditionalTask<MobEntity>(ZoglinEntity::isBaby, new MeleeAttackTask(15)), new ForgetAttackTargetTask()), MemoryModuleType.ATTACK_TARGET);
    }

    private Optional<? extends LivingEntity> method_26934() {
        return this.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse((List<LivingEntity>)ImmutableList.of()).stream().filter(ZoglinEntity::method_26936).findFirst();
    }

    private static boolean method_26936(LivingEntity arg) {
        EntityType<?> lv = arg.getType();
        return lv != EntityType.ZOGLIN && lv != EntityType.CREEPER && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(arg);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (BABY.equals(arg)) {
            this.calculateDimensions();
        }
    }

    public static DefaultAttributeContainer.Builder createZoglinAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6f).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    @Override
    public boolean tryAttack(Entity arg) {
        if (!(arg instanceof LivingEntity)) {
            return false;
        }
        this.movementCooldownTicks = 10;
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0f, this.getSoundPitch());
        return Hoglin.tryAttack(this, (LivingEntity)arg);
    }

    @Override
    protected void knockback(LivingEntity arg) {
        if (!this.isBaby()) {
            Hoglin.knockback(this, arg);
        }
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        boolean bl = super.damage(arg, f);
        if (this.world.isClient) {
            return false;
        }
        if (!bl || !(arg.getAttacker() instanceof LivingEntity)) {
            return bl;
        }
        LivingEntity lv = (LivingEntity)arg.getAttacker();
        if (EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(lv) && !LookTargetUtil.isNewTargetTooFar(this, lv, 4.0)) {
            this.method_26938(lv);
        }
        return bl;
    }

    private void method_26938(LivingEntity arg) {
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.brain.remember(MemoryModuleType.ATTACK_TARGET, arg, 200L);
    }

    public Brain<ZoglinEntity> getBrain() {
        return super.getBrain();
    }

    protected void method_26931() {
        Activity lv = this.brain.getFirstPossibleNonCoreActivity().orElse(null);
        this.brain.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity lv2 = this.brain.getFirstPossibleNonCoreActivity().orElse(null);
        if (lv2 == Activity.FIGHT && lv != Activity.FIGHT) {
            this.playAngrySound();
        }
        this.setAttacking(this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("zoglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.method_26931();
    }

    @Override
    public void setBaby(boolean bl) {
        this.getDataTracker().set(BABY, bl);
        if (!this.world.isClient && bl) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5);
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataTracker().get(BABY);
    }

    @Override
    public void tickMovement() {
        if (this.movementCooldownTicks > 0) {
            --this.movementCooldownTicks;
        }
        super.tickMovement();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 4) {
            this.movementCooldownTicks = 10;
            this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0f, this.getSoundPitch());
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
    protected SoundEvent getAmbientSound() {
        if (this.world.isClient) {
            return null;
        }
        if (this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            return SoundEvents.ENTITY_ZOGLIN_ANGRY;
        }
        return SoundEvents.ENTITY_ZOGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ZOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_ZOGLIN_STEP, 0.15f, 1.0f);
    }

    protected void playAngrySound() {
        this.playSound(SoundEvents.ENTITY_ZOGLIN_ANGRY, 1.0f, this.getSoundPitch());
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.isBaby()) {
            arg.putBoolean("IsBaby", true);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.getBoolean("IsBaby")) {
            this.setBaby(true);
        }
    }
}

