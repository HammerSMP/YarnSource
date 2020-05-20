/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class FishEntity
extends WaterCreatureEntity {
    private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public FishEntity(EntityType<? extends FishEntity> arg, World arg2) {
        super((EntityType<? extends WaterCreatureEntity>)arg, arg2);
        this.moveControl = new FishMoveControl(this);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.65f;
    }

    public static DefaultAttributeContainer.Builder createFishAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0);
    }

    @Override
    public boolean cannotDespawn() {
        return this.isFromBucket();
    }

    public static boolean canSpawn(EntityType<? extends FishEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return arg2.getBlockState(arg4).isOf(Blocks.WATER) && arg2.getBlockState(arg4.up()).isOf(Blocks.WATER);
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    @Override
    public int getLimitPerChunk() {
        return 8;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FROM_BUCKET, false);
    }

    private boolean isFromBucket() {
        return this.dataTracker.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean bl) {
        this.dataTracker.set(FROM_BUCKET, bl);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putBoolean("FromBucket", this.isFromBucket());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setFromBucket(arg.getBoolean("FromBucket"));
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(2, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 1.6, 1.4, EntityPredicates.EXCEPT_SPECTATOR::test));
        this.goalSelector.add(4, new SwimToRandomPlaceGoal(this));
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        return new SwimNavigation(this, arg);
    }

    @Override
    public void travel(Vec3d arg) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.01f, arg);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(arg);
        }
    }

    @Override
    public void tickMovement() {
        if (!this.isTouchingWater() && this.onGround && this.verticalCollision) {
            this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f, 0.4f, (this.random.nextFloat() * 2.0f - 1.0f) * 0.05f));
            this.onGround = false;
            this.velocityDirty = true;
            this.playSound(this.getFlopSound(), this.getSoundVolume(), this.getSoundPitch());
        }
        super.tickMovement();
    }

    @Override
    protected boolean interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0f, 1.0f);
            lv.decrement(1);
            ItemStack lv2 = this.getFishBucketItem();
            this.copyDataToStack(lv2);
            if (!this.world.isClient) {
                Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)arg, lv2);
            }
            if (lv.isEmpty()) {
                arg.setStackInHand(arg2, lv2);
            } else if (!arg.inventory.insertStack(lv2)) {
                arg.dropItem(lv2, false);
            }
            this.remove();
            return true;
        }
        return super.interactMob(arg, arg2);
    }

    protected void copyDataToStack(ItemStack arg) {
        if (this.hasCustomName()) {
            arg.setCustomName(this.getCustomName());
        }
    }

    protected abstract ItemStack getFishBucketItem();

    protected boolean hasSelfControl() {
        return true;
    }

    protected abstract SoundEvent getFlopSound();

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_FISH_SWIM;
    }

    static class FishMoveControl
    extends MoveControl {
        private final FishEntity fish;

        FishMoveControl(FishEntity arg) {
            super(arg);
            this.fish = arg;
        }

        @Override
        public void tick() {
            if (this.fish.isSubmergedIn(FluidTags.WATER)) {
                this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
            }
            if (this.state != MoveControl.State.MOVE_TO || this.fish.getNavigation().isIdle()) {
                this.fish.setMovementSpeed(0.0f);
                return;
            }
            float f = (float)(this.speed * this.fish.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            this.fish.setMovementSpeed(MathHelper.lerp(0.125f, this.fish.getMovementSpeed(), f));
            double d = this.targetX - this.fish.getX();
            double e = this.targetY - this.fish.getY();
            double g = this.targetZ - this.fish.getZ();
            if (e != 0.0) {
                double h = MathHelper.sqrt(d * d + e * e + g * g);
                this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * (e / h) * 0.1, 0.0));
            }
            if (d != 0.0 || g != 0.0) {
                float i = (float)(MathHelper.atan2(g, d) * 57.2957763671875) - 90.0f;
                this.fish.bodyYaw = this.fish.yaw = this.changeAngle(this.fish.yaw, i, 90.0f);
            }
        }
    }

    static class SwimToRandomPlaceGoal
    extends SwimAroundGoal {
        private final FishEntity fish;

        public SwimToRandomPlaceGoal(FishEntity arg) {
            super(arg, 1.0, 40);
            this.fish = arg;
        }

        @Override
        public boolean canStart() {
            return this.fish.hasSelfControl() && super.canStart();
        }
    }
}

