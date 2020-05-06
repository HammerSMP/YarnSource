/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VexEntity
extends HostileEntity {
    protected static final TrackedData<Byte> VEX_FLAGS = DataTracker.registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
    private MobEntity owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;

    public VexEntity(EntityType<? extends VexEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.moveControl = new VexMoveControl(this);
        this.experiencePoints = 3;
    }

    @Override
    public void move(MovementType arg, Vec3d arg2) {
        super.move(arg, arg2);
        this.checkBlockCollision();
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.damage(DamageSource.STARVE, 1.0f);
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(8, new LookAtTargetGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TrackOwnerTargetGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createVexAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VEX_FLAGS, (byte)0);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("BoundX")) {
            this.bounds = new BlockPos(arg.getInt("BoundX"), arg.getInt("BoundY"), arg.getInt("BoundZ"));
        }
        if (arg.contains("LifeTicks")) {
            this.setLifeTicks(arg.getInt("LifeTicks"));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.bounds != null) {
            arg.putInt("BoundX", this.bounds.getX());
            arg.putInt("BoundY", this.bounds.getY());
            arg.putInt("BoundZ", this.bounds.getZ());
        }
        if (this.alive) {
            arg.putInt("LifeTicks", this.lifeTicks);
        }
    }

    public MobEntity getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }

    public void setBounds(@Nullable BlockPos arg) {
        this.bounds = arg;
    }

    private boolean areFlagsSet(int i) {
        byte j = this.dataTracker.get(VEX_FLAGS);
        return (j & i) != 0;
    }

    private void setVexFlag(int i, boolean bl) {
        int j = this.dataTracker.get(VEX_FLAGS).byteValue();
        j = bl ? (j |= i) : (j &= ~i);
        this.dataTracker.set(VEX_FLAGS, (byte)(j & 0xFF));
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging(boolean bl) {
        this.setVexFlag(1, bl);
    }

    public void setOwner(MobEntity arg) {
        this.owner = arg;
    }

    public void setLifeTicks(int i) {
        this.alive = true;
        this.lifeTicks = i;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.initEquipment(arg2);
        this.updateEnchantments(arg2);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    protected void initEquipment(LocalDifficulty arg) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    class TrackOwnerTargetGoal
    extends TrackTargetGoal {
        private final TargetPredicate TRACK_OWNER_PREDICATE;

        public TrackOwnerTargetGoal(MobEntityWithAi arg2) {
            super(arg2, false);
            this.TRACK_OWNER_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
        }

        @Override
        public boolean canStart() {
            return VexEntity.this.owner != null && VexEntity.this.owner.getTarget() != null && this.canTrack(VexEntity.this.owner.getTarget(), this.TRACK_OWNER_PREDICATE);
        }

        @Override
        public void start() {
            VexEntity.this.setTarget(VexEntity.this.owner.getTarget());
            super.start();
        }
    }

    class LookAtTargetGoal
    extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(7) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos lv = VexEntity.this.getBounds();
            if (lv == null) {
                lv = VexEntity.this.getBlockPos();
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos lv2 = lv.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
                if (!VexEntity.this.world.isAir(lv2)) continue;
                VexEntity.this.moveControl.moveTo((double)lv2.getX() + 0.5, (double)lv2.getY() + 0.5, (double)lv2.getZ() + 0.5, 0.25);
                if (VexEntity.this.getTarget() != null) break;
                VexEntity.this.getLookControl().lookAt((double)lv2.getX() + 0.5, (double)lv2.getY() + 0.5, (double)lv2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class ChargeTargetGoal
    extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (VexEntity.this.getTarget() != null && !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(7) == 0) {
                return VexEntity.this.squaredDistanceTo(VexEntity.this.getTarget()) > 4.0;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return VexEntity.this.getMoveControl().isMoving() && VexEntity.this.isCharging() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity lv = VexEntity.this.getTarget();
            Vec3d lv2 = lv.getCameraPosVec(1.0f);
            VexEntity.this.moveControl.moveTo(lv2.x, lv2.y, lv2.z, 1.0);
            VexEntity.this.setCharging(true);
            VexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            VexEntity.this.setCharging(false);
        }

        @Override
        public void tick() {
            LivingEntity lv = VexEntity.this.getTarget();
            if (VexEntity.this.getBoundingBox().intersects(lv.getBoundingBox())) {
                VexEntity.this.tryAttack(lv);
                VexEntity.this.setCharging(false);
            } else {
                double d = VexEntity.this.squaredDistanceTo(lv);
                if (d < 9.0) {
                    Vec3d lv2 = lv.getCameraPosVec(1.0f);
                    VexEntity.this.moveControl.moveTo(lv2.x, lv2.y, lv2.z, 1.0);
                }
            }
        }
    }

    class VexMoveControl
    extends MoveControl {
        public VexMoveControl(VexEntity arg2) {
            super(arg2);
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            Vec3d lv = new Vec3d(this.targetX - VexEntity.this.getX(), this.targetY - VexEntity.this.getY(), this.targetZ - VexEntity.this.getZ());
            double d = lv.length();
            if (d < VexEntity.this.getBoundingBox().getAverageSideLength()) {
                this.state = MoveControl.State.WAIT;
                VexEntity.this.setVelocity(VexEntity.this.getVelocity().multiply(0.5));
            } else {
                VexEntity.this.setVelocity(VexEntity.this.getVelocity().add(lv.multiply(this.speed * 0.05 / d)));
                if (VexEntity.this.getTarget() == null) {
                    Vec3d lv2 = VexEntity.this.getVelocity();
                    VexEntity.this.bodyYaw = VexEntity.this.yaw = -((float)MathHelper.atan2(lv2.x, lv2.z)) * 57.295776f;
                } else {
                    double e = VexEntity.this.getTarget().getX() - VexEntity.this.getX();
                    double f = VexEntity.this.getTarget().getZ() - VexEntity.this.getZ();
                    VexEntity.this.bodyYaw = VexEntity.this.yaw = -((float)MathHelper.atan2(e, f)) * 57.295776f;
                }
            }
        }
    }
}

