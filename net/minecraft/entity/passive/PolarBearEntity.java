/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class PolarBearEntity
extends AnimalEntity {
    private static final TrackedData<Boolean> WARNING = DataTracker.registerData(PolarBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;

    public PolarBearEntity(EntityType<? extends PolarBearEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
    }

    @Override
    public PassiveEntity createChild(PassiveEntity arg) {
        return EntityType.POLAR_BEAR.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return false;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AttackGoal());
        this.goalSelector.add(1, new PolarBearEscapeDangerGoal());
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new PolarBearRevengeGoal());
        this.targetSelector.add(2, new FollowPlayersGoal());
        this.targetSelector.add(3, new FollowTargetGoal<FoxEntity>(this, FoxEntity.class, 10, true, true, null));
    }

    public static DefaultAttributeContainer.Builder createPolarBearAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    public static boolean canSpawn(EntityType<PolarBearEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        Biome lv = arg2.getBiome(arg4);
        if (lv == Biomes.FROZEN_OCEAN || lv == Biomes.DEEP_FROZEN_OCEAN) {
            return arg2.getBaseLightLevel(arg4, 0) > 8 && arg2.getBlockState(arg4.down()).isOf(Blocks.ICE);
        }
        return PolarBearEntity.isValidNaturalSpawn(arg, arg2, arg3, arg4, random);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isBaby()) {
            return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY;
        }
        return SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15f, 1.0f);
    }

    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0f, this.getSoundPitch());
            this.warningSoundCooldown = 40;
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WARNING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.warningAnimationProgress != this.lastWarningAnimationProgress) {
                this.calculateDimensions();
            }
            this.lastWarningAnimationProgress = this.warningAnimationProgress;
            this.warningAnimationProgress = this.isWarning() ? MathHelper.clamp(this.warningAnimationProgress + 1.0f, 0.0f, 6.0f) : MathHelper.clamp(this.warningAnimationProgress - 1.0f, 0.0f, 6.0f);
        }
        if (this.warningSoundCooldown > 0) {
            --this.warningSoundCooldown;
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose arg) {
        if (this.warningAnimationProgress > 0.0f) {
            float f = this.warningAnimationProgress / 6.0f;
            float g = 1.0f + f;
            return super.getDimensions(arg).scaled(1.0f, g);
        }
        return super.getDimensions(arg);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        boolean bl = arg.damage(DamageSource.mob(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.dealDamage(this, arg);
        }
        return bl;
    }

    public boolean isWarning() {
        return this.dataTracker.get(WARNING);
    }

    public void setWarning(boolean bl) {
        this.dataTracker.set(WARNING, bl);
    }

    @Environment(value=EnvType.CLIENT)
    public float getWarningAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0f;
    }

    @Override
    protected float getBaseMovementSpeedMultiplier() {
        return 0.98f;
    }

    @Override
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData();
            ((PassiveEntity.PassiveData)arg4).setBabyChance(1.0f);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    class PolarBearEscapeDangerGoal
    extends EscapeDangerGoal {
        public PolarBearEscapeDangerGoal() {
            super(PolarBearEntity.this, 2.0);
        }

        @Override
        public boolean canStart() {
            if (!PolarBearEntity.this.isBaby() && !PolarBearEntity.this.isOnFire()) {
                return false;
            }
            return super.canStart();
        }
    }

    class AttackGoal
    extends MeleeAttackGoal {
        public AttackGoal() {
            super(PolarBearEntity.this, 1.25, true);
        }

        @Override
        protected void attack(LivingEntity arg, double d) {
            double e = this.getSquaredMaxAttackDistance(arg);
            if (d <= e && this.ticksUntilAttack <= 0) {
                this.ticksUntilAttack = 20;
                this.mob.tryAttack(arg);
                PolarBearEntity.this.setWarning(false);
            } else if (d <= e * 2.0) {
                if (this.ticksUntilAttack <= 0) {
                    PolarBearEntity.this.setWarning(false);
                    this.ticksUntilAttack = 20;
                }
                if (this.ticksUntilAttack <= 10) {
                    PolarBearEntity.this.setWarning(true);
                    PolarBearEntity.this.playWarningSound();
                }
            } else {
                this.ticksUntilAttack = 20;
                PolarBearEntity.this.setWarning(false);
            }
        }

        @Override
        public void stop() {
            PolarBearEntity.this.setWarning(false);
            super.stop();
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity arg) {
            return 4.0f + arg.getWidth();
        }
    }

    class FollowPlayersGoal
    extends FollowTargetGoal<PlayerEntity> {
        public FollowPlayersGoal() {
            super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
        }

        @Override
        public boolean canStart() {
            if (PolarBearEntity.this.isBaby()) {
                return false;
            }
            if (super.canStart()) {
                List<PolarBearEntity> list = PolarBearEntity.this.world.getNonSpectatingEntities(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().expand(8.0, 4.0, 8.0));
                for (PolarBearEntity lv : list) {
                    if (!lv.isBaby()) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.5;
        }
    }

    class PolarBearRevengeGoal
    extends RevengeGoal {
        public PolarBearRevengeGoal() {
            super(PolarBearEntity.this, new Class[0]);
        }

        @Override
        public void start() {
            super.start();
            if (PolarBearEntity.this.isBaby()) {
                this.callSameTypeForRevenge();
                this.stop();
            }
        }

        @Override
        protected void setMobEntityTarget(MobEntity arg, LivingEntity arg2) {
            if (arg instanceof PolarBearEntity && !arg.isBaby()) {
                super.setMobEntityTarget(arg, arg2);
            }
        }
    }
}

