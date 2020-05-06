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
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PhantomEntity
extends FlyingEntity
implements Monster {
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Vec3d field_7314 = Vec3d.ZERO;
    private BlockPos field_7312 = BlockPos.ORIGIN;
    private PhantomMovementType movementType = PhantomMovementType.CIRCLE;

    public PhantomEntity(EntityType<? extends PhantomEntity> arg, World arg2) {
        super((EntityType<? extends FlyingEntity>)arg, arg2);
        this.experiencePoints = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }

    @Override
    protected BodyControl createBodyControl() {
        return new PhantomBodyControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new StartAttackGoal());
        this.goalSelector.add(2, new SwoopMovementGoal());
        this.goalSelector.add(3, new CircleMovementGoal());
        this.targetSelector.add(1, new FindTargetGoal());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SIZE, 0);
    }

    public void setPhantomSize(int i) {
        this.dataTracker.set(SIZE, MathHelper.clamp(i, 0, 64));
    }

    private void onSizeChanged() {
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.dataTracker.get(SIZE);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.35f;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (SIZE.equals(arg)) {
            this.onSizeChanged();
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.age) * 0.13f + (float)Math.PI);
            float g = MathHelper.cos((float)(this.getEntityId() * 3 + this.age + 1) * 0.13f + (float)Math.PI);
            if (f > 0.0f && g <= 0.0f) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            int i = this.getPhantomSize();
            float h = MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float j = MathHelper.sin(this.yaw * ((float)Math.PI / 180)) * (1.3f + 0.21f * (float)i);
            float k = (0.3f + f * 0.45f) * ((float)i * 0.2f + 1.0f);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)h, this.getY() + (double)k, this.getZ() + (double)j, 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)h, this.getY() + (double)k, this.getZ() - (double)j, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void tickMovement() {
        if (this.isAlive() && this.isInDaylight()) {
            this.setOnFireFor(8);
        }
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }

    @Override
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.field_7312 = this.getBlockPos().up(5);
        this.setPhantomSize(0);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("AX")) {
            this.field_7312 = new BlockPos(arg.getInt("AX"), arg.getInt("AY"), arg.getInt("AZ"));
        }
        this.setPhantomSize(arg.getInt("Size"));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("AX", this.field_7312.getX());
        arg.putInt("AY", this.field_7312.getY());
        arg.putInt("AZ", this.field_7312.getZ());
        arg.putInt("Size", this.getPhantomSize());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        return true;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0f;
    }

    @Override
    public boolean canTarget(EntityType<?> arg) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose arg) {
        int i = this.getPhantomSize();
        EntityDimensions lv = super.getDimensions(arg);
        float f = (lv.width + 0.2f * (float)i) / lv.width;
        return lv.scaled(f);
    }

    class FindTargetGoal
    extends Goal {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
        private int delay = 20;

        private FindTargetGoal() {
        }

        @Override
        public boolean canStart() {
            if (this.delay > 0) {
                --this.delay;
                return false;
            }
            this.delay = 60;
            List<PlayerEntity> list = PhantomEntity.this.world.getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
            if (!list.isEmpty()) {
                list.sort((arg, arg2) -> arg.getY() > arg2.getY() ? -1 : 1);
                for (PlayerEntity lv : list) {
                    if (!PhantomEntity.this.isTarget(lv, TargetPredicate.DEFAULT)) continue;
                    PhantomEntity.this.setTarget(lv);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity lv = PhantomEntity.this.getTarget();
            if (lv != null) {
                return PhantomEntity.this.isTarget(lv, TargetPredicate.DEFAULT);
            }
            return false;
        }
    }

    class StartAttackGoal
    extends Goal {
        private int field_7322;

        private StartAttackGoal() {
        }

        @Override
        public boolean canStart() {
            LivingEntity lv = PhantomEntity.this.getTarget();
            if (lv != null) {
                return PhantomEntity.this.isTarget(PhantomEntity.this.getTarget(), TargetPredicate.DEFAULT);
            }
            return false;
        }

        @Override
        public void start() {
            this.field_7322 = 10;
            PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            this.method_7102();
        }

        @Override
        public void stop() {
            PhantomEntity.this.field_7312 = PhantomEntity.this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.field_7312).up(10 + PhantomEntity.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.movementType == PhantomMovementType.CIRCLE) {
                --this.field_7322;
                if (this.field_7322 <= 0) {
                    PhantomEntity.this.movementType = PhantomMovementType.SWOOP;
                    this.method_7102();
                    this.field_7322 = (8 + PhantomEntity.this.random.nextInt(4)) * 20;
                    PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + PhantomEntity.this.random.nextFloat() * 0.1f);
                }
            }
        }

        private void method_7102() {
            PhantomEntity.this.field_7312 = PhantomEntity.this.getTarget().getBlockPos().up(20 + PhantomEntity.this.random.nextInt(20));
            if (PhantomEntity.this.field_7312.getY() < PhantomEntity.this.world.getSeaLevel()) {
                PhantomEntity.this.field_7312 = new BlockPos(PhantomEntity.this.field_7312.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.field_7312.getZ());
            }
        }
    }

    class SwoopMovementGoal
    extends MovementGoal {
        private SwoopMovementGoal() {
        }

        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() != null && PhantomEntity.this.movementType == PhantomMovementType.SWOOP;
        }

        @Override
        public boolean shouldContinue() {
            List<Entity> list;
            LivingEntity lv = PhantomEntity.this.getTarget();
            if (lv == null) {
                return false;
            }
            if (!lv.isAlive()) {
                return false;
            }
            if (lv instanceof PlayerEntity && (((PlayerEntity)lv).isSpectator() || ((PlayerEntity)lv).isCreative())) {
                return false;
            }
            if (!this.canStart()) {
                return false;
            }
            if (PhantomEntity.this.age % 20 == 0 && !(list = PhantomEntity.this.world.getEntities(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY)).isEmpty()) {
                for (CatEntity catEntity : list) {
                    catEntity.hiss();
                }
                return false;
            }
            return true;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            PhantomEntity.this.setTarget(null);
            PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
        }

        @Override
        public void tick() {
            LivingEntity lv = PhantomEntity.this.getTarget();
            PhantomEntity.this.field_7314 = new Vec3d(lv.getX(), lv.getBodyY(0.5), lv.getZ());
            if (PhantomEntity.this.getBoundingBox().expand(0.2f).intersects(lv.getBoundingBox())) {
                PhantomEntity.this.tryAttack(lv);
                PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
                if (!PhantomEntity.this.isSilent()) {
                    PhantomEntity.this.world.syncWorldEvent(1039, PhantomEntity.this.getBlockPos(), 0);
                }
            } else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
                PhantomEntity.this.movementType = PhantomMovementType.CIRCLE;
            }
        }
    }

    class CircleMovementGoal
    extends MovementGoal {
        private float field_7328;
        private float field_7327;
        private float field_7326;
        private float field_7324;

        private CircleMovementGoal() {
        }

        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() == null || PhantomEntity.this.movementType == PhantomMovementType.CIRCLE;
        }

        @Override
        public void start() {
            this.field_7327 = 5.0f + PhantomEntity.this.random.nextFloat() * 10.0f;
            this.field_7326 = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            this.field_7324 = PhantomEntity.this.random.nextBoolean() ? 1.0f : -1.0f;
            this.method_7103();
        }

        @Override
        public void tick() {
            if (PhantomEntity.this.random.nextInt(350) == 0) {
                this.field_7326 = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            }
            if (PhantomEntity.this.random.nextInt(250) == 0) {
                this.field_7327 += 1.0f;
                if (this.field_7327 > 15.0f) {
                    this.field_7327 = 5.0f;
                    this.field_7324 = -this.field_7324;
                }
            }
            if (PhantomEntity.this.random.nextInt(450) == 0) {
                this.field_7328 = PhantomEntity.this.random.nextFloat() * 2.0f * (float)Math.PI;
                this.method_7103();
            }
            if (this.method_7104()) {
                this.method_7103();
            }
            if (((PhantomEntity)PhantomEntity.this).field_7314.y < PhantomEntity.this.getY() && !PhantomEntity.this.world.isAir(PhantomEntity.this.getBlockPos().down(1))) {
                this.field_7326 = Math.max(1.0f, this.field_7326);
                this.method_7103();
            }
            if (((PhantomEntity)PhantomEntity.this).field_7314.y > PhantomEntity.this.getY() && !PhantomEntity.this.world.isAir(PhantomEntity.this.getBlockPos().up(1))) {
                this.field_7326 = Math.min(-1.0f, this.field_7326);
                this.method_7103();
            }
        }

        private void method_7103() {
            if (BlockPos.ORIGIN.equals(PhantomEntity.this.field_7312)) {
                PhantomEntity.this.field_7312 = PhantomEntity.this.getBlockPos();
            }
            this.field_7328 += this.field_7324 * 15.0f * ((float)Math.PI / 180);
            PhantomEntity.this.field_7314 = Vec3d.method_24954(PhantomEntity.this.field_7312).add(this.field_7327 * MathHelper.cos(this.field_7328), -4.0f + this.field_7326, this.field_7327 * MathHelper.sin(this.field_7328));
        }
    }

    abstract class MovementGoal
    extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected boolean method_7104() {
            return PhantomEntity.this.field_7314.squaredDistanceTo(PhantomEntity.this.getX(), PhantomEntity.this.getY(), PhantomEntity.this.getZ()) < 4.0;
        }
    }

    class PhantomLookControl
    extends LookControl {
        public PhantomLookControl(MobEntity arg2) {
            super(arg2);
        }

        @Override
        public void tick() {
        }
    }

    class PhantomBodyControl
    extends BodyControl {
        public PhantomBodyControl(MobEntity arg2) {
            super(arg2);
        }

        @Override
        public void tick() {
            PhantomEntity.this.headYaw = PhantomEntity.this.bodyYaw;
            PhantomEntity.this.bodyYaw = PhantomEntity.this.yaw;
        }
    }

    class PhantomMoveControl
    extends MoveControl {
        private float field_7331;

        public PhantomMoveControl(MobEntity arg2) {
            super(arg2);
            this.field_7331 = 0.1f;
        }

        @Override
        public void tick() {
            float n;
            if (PhantomEntity.this.horizontalCollision) {
                PhantomEntity.this.yaw += 180.0f;
                this.field_7331 = 0.1f;
            }
            float f = (float)(((PhantomEntity)PhantomEntity.this).field_7314.x - PhantomEntity.this.getX());
            float g = (float)(((PhantomEntity)PhantomEntity.this).field_7314.y - PhantomEntity.this.getY());
            float h = (float)(((PhantomEntity)PhantomEntity.this).field_7314.z - PhantomEntity.this.getZ());
            double d = MathHelper.sqrt(f * f + h * h);
            double e = 1.0 - (double)MathHelper.abs(g * 0.7f) / d;
            f = (float)((double)f * e);
            h = (float)((double)h * e);
            d = MathHelper.sqrt(f * f + h * h);
            double i = MathHelper.sqrt(f * f + h * h + g * g);
            float j = PhantomEntity.this.yaw;
            float k = (float)MathHelper.atan2(h, f);
            float l = MathHelper.wrapDegrees(PhantomEntity.this.yaw + 90.0f);
            float m = MathHelper.wrapDegrees(k * 57.295776f);
            PhantomEntity.this.bodyYaw = PhantomEntity.this.yaw = MathHelper.method_15388(l, m, 4.0f) - 90.0f;
            this.field_7331 = MathHelper.angleBetween(j, PhantomEntity.this.yaw) < 3.0f ? MathHelper.method_15348(this.field_7331, 1.8f, 0.005f * (1.8f / this.field_7331)) : MathHelper.method_15348(this.field_7331, 0.2f, 0.025f);
            PhantomEntity.this.pitch = n = (float)(-(MathHelper.atan2(-g, d) * 57.2957763671875));
            float o = PhantomEntity.this.yaw + 90.0f;
            double p = (double)(this.field_7331 * MathHelper.cos(o * ((float)Math.PI / 180))) * Math.abs((double)f / i);
            double q = (double)(this.field_7331 * MathHelper.sin(o * ((float)Math.PI / 180))) * Math.abs((double)h / i);
            double r = (double)(this.field_7331 * MathHelper.sin(n * ((float)Math.PI / 180))) * Math.abs((double)g / i);
            Vec3d lv = PhantomEntity.this.getVelocity();
            PhantomEntity.this.setVelocity(lv.add(new Vec3d(p, r, q).subtract(lv).multiply(0.2)));
        }
    }

    static enum PhantomMovementType {
        CIRCLE,
        SWOOP;

    }
}

