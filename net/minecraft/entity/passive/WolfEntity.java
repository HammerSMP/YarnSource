/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WolfEntity
extends TameableEntity
implements Angerable {
    private static final TrackedData<Boolean> BEGGING = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final Predicate<LivingEntity> FOLLOW_TAMED_PREDICATE = arg -> {
        EntityType<?> lv = arg.getType();
        return lv == EntityType.SHEEP || lv == EntityType.RABBIT || lv == EntityType.FOX;
    };
    private float begAnimationProgress;
    private float lastBegAnimationProgress;
    private boolean furWet;
    private boolean canShakeWaterOff;
    private float shakeProgress;
    private float lastShakeProgress;
    private static final IntRange ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    private UUID angryAt;

    public WolfEntity(EntityType<? extends WolfEntity> arg, World arg2) {
        super((EntityType<? extends TameableEntity>)arg, arg2);
        this.setTamed(false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new AvoidLlamaGoal<LlamaEntity>(this, LlamaEntity.class, 24.0f, 1.5, 1.5));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4f));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(9, new WolfBegGoal(this, 8.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(4, new FollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(5, new FollowTargetIfTamedGoal<AnimalEntity>(this, AnimalEntity.class, false, FOLLOW_TAMED_PREDICATE));
        this.targetSelector.add(6, new FollowTargetIfTamedGoal<TurtleEntity>(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
        this.targetSelector.add(7, new FollowTargetGoal<AbstractSkeletonEntity>((MobEntity)this, AbstractSkeletonEntity.class, false));
        this.targetSelector.add(8, new UniversalAngerGoal<WolfEntity>(this, true));
    }

    public static DefaultAttributeContainer.Builder createWolfAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BEGGING, false);
        this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
        this.dataTracker.startTracking(ANGER_TIME, 0);
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putByte("CollarColor", (byte)this.getCollarColor().getId());
        this.angerToTag(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(arg.getInt("CollarColor")));
        }
        this.angerFromTag((ServerWorld)this.world, arg);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasAngerTime()) {
            return SoundEvents.ENTITY_WOLF_GROWL;
        }
        if (this.random.nextInt(3) == 0) {
            if (this.isTamed() && this.getHealth() < 10.0f) {
                return SoundEvents.ENTITY_WOLF_WHINE;
            }
            return SoundEvents.ENTITY_WOLF_PANT;
        }
        return SoundEvents.ENTITY_WOLF_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient && this.furWet && !this.canShakeWaterOff && !this.isNavigating() && this.onGround) {
            this.canShakeWaterOff = true;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
            this.world.sendEntityStatus(this, (byte)8);
        }
        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) {
            return;
        }
        this.lastBegAnimationProgress = this.begAnimationProgress;
        this.begAnimationProgress = this.isBegging() ? (this.begAnimationProgress += (1.0f - this.begAnimationProgress) * 0.4f) : (this.begAnimationProgress += (0.0f - this.begAnimationProgress) * 0.4f);
        if (this.isWet()) {
            this.furWet = true;
            this.canShakeWaterOff = false;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
        } else if ((this.furWet || this.canShakeWaterOff) && this.canShakeWaterOff) {
            if (this.shakeProgress == 0.0f) {
                this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            }
            this.lastShakeProgress = this.shakeProgress;
            this.shakeProgress += 0.05f;
            if (this.lastShakeProgress >= 2.0f) {
                this.furWet = false;
                this.canShakeWaterOff = false;
                this.lastShakeProgress = 0.0f;
                this.shakeProgress = 0.0f;
            }
            if (this.shakeProgress > 0.4f) {
                float f = (float)this.getY();
                int i = (int)(MathHelper.sin((this.shakeProgress - 0.4f) * (float)Math.PI) * 7.0f);
                Vec3d lv = this.getVelocity();
                for (int j = 0; j < i; ++j) {
                    float g = (this.random.nextFloat() * 2.0f - 1.0f) * this.getWidth() * 0.5f;
                    float h = (this.random.nextFloat() * 2.0f - 1.0f) * this.getWidth() * 0.5f;
                    this.world.addParticle(ParticleTypes.SPLASH, this.getX() + (double)g, f + 0.8f, this.getZ() + (double)h, lv.x, lv.y, lv.z);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource arg) {
        this.furWet = false;
        this.canShakeWaterOff = false;
        this.lastShakeProgress = 0.0f;
        this.shakeProgress = 0.0f;
        super.onDeath(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurWet() {
        return this.furWet;
    }

    @Environment(value=EnvType.CLIENT)
    public float getFurWetBrightnessMultiplier(float f) {
        return 0.75f + MathHelper.lerp(f, this.lastShakeProgress, this.shakeProgress) / 2.0f * 0.25f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getShakeAnimationProgress(float f, float g) {
        float h = (MathHelper.lerp(f, this.lastShakeProgress, this.shakeProgress) + g) / 1.8f;
        if (h < 0.0f) {
            h = 0.0f;
        } else if (h > 1.0f) {
            h = 1.0f;
        }
        return MathHelper.sin(h * (float)Math.PI) * MathHelper.sin(h * (float)Math.PI * 11.0f) * 0.15f * (float)Math.PI;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBegAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastBegAnimationProgress, this.begAnimationProgress) * 0.15f * (float)Math.PI;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.8f;
    }

    @Override
    public int getLookPitchSpeed() {
        if (this.isInSittingPose()) {
            return 20;
        }
        return super.getLookPitchSpeed();
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        Entity lv = arg.getAttacker();
        this.setSitting(false);
        if (lv != null && !(lv instanceof PlayerEntity) && !(lv instanceof PersistentProjectileEntity)) {
            f = (f + 1.0f) / 2.0f;
        }
        return super.damage(arg, f);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        boolean bl = arg.damage(DamageSource.mob(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.dealDamage(this, arg);
        }
        return bl;
    }

    @Override
    public void setTamed(boolean bl) {
        super.setTamed(bl);
        if (bl) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            this.setHealth(20.0f);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
        }
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        Item lv2 = lv.getItem();
        if (this.world.isClient) {
            boolean bl = this.isOwner(arg) || this.isTamed() || lv2 == Items.BONE && !this.isTamed() && !this.hasAngerTime();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        }
        if (this.isTamed()) {
            if (this.isBreedingItem(lv) && this.getHealth() < this.getMaxHealth()) {
                if (!arg.abilities.creativeMode) {
                    lv.decrement(1);
                }
                this.heal(lv2.getFoodComponent().getHunger());
                return ActionResult.SUCCESS;
            }
            if (lv2 instanceof DyeItem) {
                DyeColor lv3 = ((DyeItem)lv2).getColor();
                if (lv3 == this.getCollarColor()) return super.interactMob(arg, arg2);
                this.setCollarColor(lv3);
                if (arg.abilities.creativeMode) return ActionResult.SUCCESS;
                lv.decrement(1);
                return ActionResult.SUCCESS;
            }
            ActionResult lv4 = super.interactMob(arg, arg2);
            if (lv4.isAccepted() && !this.isBaby() || !this.isOwner(arg)) return lv4;
            this.setSitting(!this.isSitting());
            this.jumping = false;
            this.navigation.stop();
            this.setTarget(null);
            return ActionResult.SUCCESS;
        }
        if (lv2 != Items.BONE || this.hasAngerTime()) return super.interactMob(arg, arg2);
        if (!arg.abilities.creativeMode) {
            lv.decrement(1);
        }
        if (this.random.nextInt(3) == 0) {
            this.setOwner(arg);
            this.navigation.stop();
            this.setTarget(null);
            this.setSitting(true);
            this.world.sendEntityStatus(this, (byte)7);
            return ActionResult.SUCCESS;
        } else {
            this.world.sendEntityStatus(this, (byte)6);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 8) {
            this.canShakeWaterOff = true;
            this.shakeProgress = 0.0f;
            this.lastShakeProgress = 0.0f;
        } else {
            super.handleStatus(b);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getTailAngle() {
        if (this.hasAngerTime()) {
            return 1.5393804f;
        }
        if (this.isTamed()) {
            return (0.55f - (this.getMaxHealth() - this.getHealth()) * 0.02f) * (float)Math.PI;
        }
        return 0.62831855f;
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        Item lv = arg.getItem();
        return lv.isFood() && lv.getFoodComponent().isMeat();
    }

    @Override
    public int getLimitPerChunk() {
        return 8;
    }

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int i) {
        this.dataTracker.set(ANGER_TIME, i);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.choose(this.random));
    }

    @Override
    @Nullable
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID uUID) {
        this.angryAt = uUID;
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor arg) {
        this.dataTracker.set(COLLAR_COLOR, arg.getId());
    }

    @Override
    public WolfEntity createChild(PassiveEntity arg) {
        WolfEntity lv = EntityType.WOLF.create(this.world);
        UUID uUID = this.getOwnerUuid();
        if (uUID != null) {
            lv.setOwnerUuid(uUID);
            lv.setTamed(true);
        }
        return lv;
    }

    public void setBegging(boolean bl) {
        this.dataTracker.set(BEGGING, bl);
    }

    @Override
    public boolean canBreedWith(AnimalEntity arg) {
        if (arg == this) {
            return false;
        }
        if (!this.isTamed()) {
            return false;
        }
        if (!(arg instanceof WolfEntity)) {
            return false;
        }
        WolfEntity lv = (WolfEntity)arg;
        if (!lv.isTamed()) {
            return false;
        }
        if (lv.isInSittingPose()) {
            return false;
        }
        return this.isInLove() && lv.isInLove();
    }

    public boolean isBegging() {
        return this.dataTracker.get(BEGGING);
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity arg, LivingEntity arg2) {
        if (arg instanceof CreeperEntity || arg instanceof GhastEntity) {
            return false;
        }
        if (arg instanceof WolfEntity) {
            WolfEntity lv = (WolfEntity)arg;
            return !lv.isTamed() || lv.getOwner() != arg2;
        }
        if (arg instanceof PlayerEntity && arg2 instanceof PlayerEntity && !((PlayerEntity)arg2).shouldDamagePlayer((PlayerEntity)arg)) {
            return false;
        }
        if (arg instanceof HorseBaseEntity && ((HorseBaseEntity)arg).isTame()) {
            return false;
        }
        return !(arg instanceof TameableEntity) || !((TameableEntity)arg).isTamed();
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return !this.hasAngerTime() && super.canBeLeashedBy(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.6f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    class AvoidLlamaGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final WolfEntity wolf;

        public AvoidLlamaGoal(WolfEntity arg2, Class<T> class_, float f, double d, double e) {
            super(arg2, class_, f, d, e);
            this.wolf = arg2;
        }

        @Override
        public boolean canStart() {
            if (super.canStart() && this.targetEntity instanceof LlamaEntity) {
                return !this.wolf.isTamed() && this.isScaredOf((LlamaEntity)this.targetEntity);
            }
            return false;
        }

        private boolean isScaredOf(LlamaEntity arg) {
            return arg.getStrength() >= WolfEntity.this.random.nextInt(5);
        }

        @Override
        public void start() {
            WolfEntity.this.setTarget(null);
            super.start();
        }

        @Override
        public void tick() {
            WolfEntity.this.setTarget(null);
            super.tick();
        }
    }
}

