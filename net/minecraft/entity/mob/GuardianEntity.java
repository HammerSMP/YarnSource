/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GuardianEntity
extends HostileEntity {
    private static final TrackedData<Boolean> SPIKES_RETRACTED = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BEAM_TARGET_ID = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float spikesExtension;
    private float prevSpikesExtension;
    private float spikesExtensionRate;
    private float tailAngle;
    private float prevTailAngle;
    private LivingEntity cachedBeamTarget;
    private int beamTicks;
    private boolean flopping;
    protected WanderAroundGoal wanderGoal;

    public GuardianEntity(EntityType<? extends GuardianEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.experiencePoints = 10;
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.moveControl = new GuardianMoveControl(this);
        this.prevSpikesExtension = this.spikesExtension = this.random.nextFloat();
    }

    @Override
    protected void initGoals() {
        GoToWalkTargetGoal lv = new GoToWalkTargetGoal(this, 1.0);
        this.wanderGoal = new WanderAroundGoal(this, 1.0, 80);
        this.goalSelector.add(4, new FireBeamGoal(this));
        this.goalSelector.add(5, lv);
        this.goalSelector.add(7, this.wanderGoal);
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0f, 0.01f));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.wanderGoal.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        lv.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.targetSelector.add(1, new FollowTargetGoal<LivingEntity>(this, LivingEntity.class, 10, true, false, new GuardianTargetPredicate(this)));
    }

    public static DefaultAttributeContainer.Builder createGuardianAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        return new SwimNavigation(this, arg);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPIKES_RETRACTED, false);
        this.dataTracker.startTracking(BEAM_TARGET_ID, 0);
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    public boolean areSpikesRetracted() {
        return this.dataTracker.get(SPIKES_RETRACTED);
    }

    private void setSpikesRetracted(boolean bl) {
        this.dataTracker.set(SPIKES_RETRACTED, bl);
    }

    public int getWarmupTime() {
        return 80;
    }

    private void setBeamTarget(int i) {
        this.dataTracker.set(BEAM_TARGET_ID, i);
    }

    public boolean hasBeamTarget() {
        return this.dataTracker.get(BEAM_TARGET_ID) != 0;
    }

    @Nullable
    public LivingEntity getBeamTarget() {
        if (!this.hasBeamTarget()) {
            return null;
        }
        if (this.world.isClient) {
            if (this.cachedBeamTarget != null) {
                return this.cachedBeamTarget;
            }
            Entity lv = this.world.getEntityById(this.dataTracker.get(BEAM_TARGET_ID));
            if (lv instanceof LivingEntity) {
                this.cachedBeamTarget = (LivingEntity)lv;
                return this.cachedBeamTarget;
            }
            return null;
        }
        return this.getTarget();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (BEAM_TARGET_ID.equals(arg)) {
            this.beamTicks = 0;
            this.cachedBeamTarget = null;
        }
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 160;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT : SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_HURT : SoundEvents.ENTITY_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_DEATH : SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.5f;
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (arg2.getFluidState(arg).matches(FluidTags.WATER)) {
            return 10.0f + arg2.getBrightness(arg) - 0.5f;
        }
        return super.getPathfindingFavor(arg, arg2);
    }

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            if (this.world.isClient) {
                this.prevSpikesExtension = this.spikesExtension;
                if (!this.isTouchingWater()) {
                    this.spikesExtensionRate = 2.0f;
                    Vec3d lv = this.getVelocity();
                    if (lv.y > 0.0 && this.flopping && !this.isSilent()) {
                        this.world.playSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(), this.getSoundCategory(), 1.0f, 1.0f, false);
                    }
                    this.flopping = lv.y < 0.0 && this.world.isTopSolid(this.getBlockPos().down(), this);
                } else {
                    this.spikesExtensionRate = this.areSpikesRetracted() ? (this.spikesExtensionRate < 0.5f ? 4.0f : (this.spikesExtensionRate += (0.5f - this.spikesExtensionRate) * 0.1f)) : (this.spikesExtensionRate += (0.125f - this.spikesExtensionRate) * 0.2f);
                }
                this.spikesExtension += this.spikesExtensionRate;
                this.prevTailAngle = this.tailAngle;
                this.tailAngle = !this.isInsideWaterOrBubbleColumn() ? this.random.nextFloat() : (this.areSpikesRetracted() ? (this.tailAngle += (0.0f - this.tailAngle) * 0.25f) : (this.tailAngle += (1.0f - this.tailAngle) * 0.06f));
                if (this.areSpikesRetracted() && this.isTouchingWater()) {
                    Vec3d lv2 = this.getRotationVec(0.0f);
                    for (int i = 0; i < 2; ++i) {
                        this.world.addParticle(ParticleTypes.BUBBLE, this.getParticleX(0.5) - lv2.x * 1.5, this.getRandomBodyY() - lv2.y * 1.5, this.getParticleZ(0.5) - lv2.z * 1.5, 0.0, 0.0, 0.0);
                    }
                }
                if (this.hasBeamTarget()) {
                    LivingEntity lv3;
                    if (this.beamTicks < this.getWarmupTime()) {
                        ++this.beamTicks;
                    }
                    if ((lv3 = this.getBeamTarget()) != null) {
                        this.getLookControl().lookAt(lv3, 90.0f, 90.0f);
                        this.getLookControl().tick();
                        double d = this.getBeamProgress(0.0f);
                        double e = lv3.getX() - this.getX();
                        double f = lv3.getBodyY(0.5) - this.getEyeY();
                        double g = lv3.getZ() - this.getZ();
                        double h = Math.sqrt(e * e + f * f + g * g);
                        e /= h;
                        f /= h;
                        g /= h;
                        double j = this.random.nextDouble();
                        while (j < h) {
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + e * (j += 1.8 - d + this.random.nextDouble() * (1.7 - d)), this.getEyeY() + f * j, this.getZ() + g * j, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
            if (this.isInsideWaterOrBubbleColumn()) {
                this.setAir(300);
            } else if (this.onGround) {
                this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.4f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.4f));
                this.yaw = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.velocityDirty = true;
            }
            if (this.hasBeamTarget()) {
                this.yaw = this.headYaw;
            }
        }
        super.tickMovement();
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_GUARDIAN_FLOP;
    }

    @Environment(value=EnvType.CLIENT)
    public float getSpikesExtension(float f) {
        return MathHelper.lerp(f, this.prevSpikesExtension, this.spikesExtension);
    }

    @Environment(value=EnvType.CLIENT)
    public float getTailAngle(float f) {
        return MathHelper.lerp(f, this.prevTailAngle, this.tailAngle);
    }

    public float getBeamProgress(float f) {
        return ((float)this.beamTicks + f) / (float)this.getWarmupTime();
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        return arg.intersectsEntities(this);
    }

    public static boolean canSpawn(EntityType<? extends GuardianEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        return !(random.nextInt(20) != 0 && arg2.isSkyVisibleAllowingSea(arg4) || arg2.getDifficulty() == Difficulty.PEACEFUL || arg3 != SpawnType.SPAWNER && !arg2.getFluidState(arg4).matches(FluidTags.WATER));
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (!this.areSpikesRetracted() && !arg.getMagic() && arg.getSource() instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)arg.getSource();
            if (!arg.isExplosive()) {
                lv.damage(DamageSource.thorns(this), 2.0f);
            }
        }
        if (this.wanderGoal != null) {
            this.wanderGoal.ignoreChanceOnce();
        }
        return super.damage(arg, f);
    }

    @Override
    public int getLookPitchSpeed() {
        return 180;
    }

    @Override
    public void travel(Vec3d arg) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1f, arg);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (!this.areSpikesRetracted() && this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(arg);
        }
    }

    static class GuardianMoveControl
    extends MoveControl {
        private final GuardianEntity guardian;

        public GuardianMoveControl(GuardianEntity arg) {
            super(arg);
            this.guardian = arg;
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO || this.guardian.getNavigation().isIdle()) {
                this.guardian.setMovementSpeed(0.0f);
                this.guardian.setSpikesRetracted(false);
                return;
            }
            Vec3d lv = new Vec3d(this.targetX - this.guardian.getX(), this.targetY - this.guardian.getY(), this.targetZ - this.guardian.getZ());
            double d = lv.length();
            double e = lv.x / d;
            double f = lv.y / d;
            double g = lv.z / d;
            float h = (float)(MathHelper.atan2(lv.z, lv.x) * 57.2957763671875) - 90.0f;
            this.guardian.bodyYaw = this.guardian.yaw = this.changeAngle(this.guardian.yaw, h, 90.0f);
            float i = (float)(this.speed * this.guardian.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            float j = MathHelper.lerp(0.125f, this.guardian.getMovementSpeed(), i);
            this.guardian.setMovementSpeed(j);
            double k = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.5) * 0.05;
            double l = Math.cos(this.guardian.yaw * ((float)Math.PI / 180));
            double m = Math.sin(this.guardian.yaw * ((float)Math.PI / 180));
            double n = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.75) * 0.05;
            this.guardian.setVelocity(this.guardian.getVelocity().add(k * l, n * (m + l) * 0.25 + (double)j * f * 0.1, k * m));
            LookControl lv2 = this.guardian.getLookControl();
            double o = this.guardian.getX() + e * 2.0;
            double p = this.guardian.getEyeY() + f / d;
            double q = this.guardian.getZ() + g * 2.0;
            double r = lv2.getLookX();
            double s = lv2.getLookY();
            double t = lv2.getLookZ();
            if (!lv2.isActive()) {
                r = o;
                s = p;
                t = q;
            }
            this.guardian.getLookControl().lookAt(MathHelper.lerp(0.125, r, o), MathHelper.lerp(0.125, s, p), MathHelper.lerp(0.125, t, q), 10.0f, 40.0f);
            this.guardian.setSpikesRetracted(true);
        }
    }

    static class FireBeamGoal
    extends Goal {
        private final GuardianEntity guardian;
        private int beamTicks;
        private final boolean elder;

        public FireBeamGoal(GuardianEntity arg) {
            this.guardian = arg;
            this.elder = arg instanceof ElderGuardianEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity lv = this.guardian.getTarget();
            return lv != null && lv.isAlive();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && (this.elder || this.guardian.squaredDistanceTo(this.guardian.getTarget()) > 9.0);
        }

        @Override
        public void start() {
            this.beamTicks = -10;
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().lookAt(this.guardian.getTarget(), 90.0f, 90.0f);
            this.guardian.velocityDirty = true;
        }

        @Override
        public void stop() {
            this.guardian.setBeamTarget(0);
            this.guardian.setTarget(null);
            this.guardian.wanderGoal.ignoreChanceOnce();
        }

        @Override
        public void tick() {
            LivingEntity lv = this.guardian.getTarget();
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().lookAt(lv, 90.0f, 90.0f);
            if (!this.guardian.canSee(lv)) {
                this.guardian.setTarget(null);
                return;
            }
            ++this.beamTicks;
            if (this.beamTicks == 0) {
                this.guardian.setBeamTarget(this.guardian.getTarget().getEntityId());
                if (!this.guardian.isSilent()) {
                    this.guardian.world.sendEntityStatus(this.guardian, (byte)21);
                }
            } else if (this.beamTicks >= this.guardian.getWarmupTime()) {
                float f = 1.0f;
                if (this.guardian.world.getDifficulty() == Difficulty.HARD) {
                    f += 2.0f;
                }
                if (this.elder) {
                    f += 2.0f;
                }
                lv.damage(DamageSource.magic(this.guardian, this.guardian), f);
                lv.damage(DamageSource.mob(this.guardian), (float)this.guardian.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
                this.guardian.setTarget(null);
            }
            super.tick();
        }
    }

    static class GuardianTargetPredicate
    implements Predicate<LivingEntity> {
        private final GuardianEntity owner;

        public GuardianTargetPredicate(GuardianEntity arg) {
            this.owner = arg;
        }

        @Override
        public boolean test(@Nullable LivingEntity arg) {
            return (arg instanceof PlayerEntity || arg instanceof SquidEntity) && arg.squaredDistanceTo(this.owner) > 9.0;
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((LivingEntity)object);
        }
    }
}

