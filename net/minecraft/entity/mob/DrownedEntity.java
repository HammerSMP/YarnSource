/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.class_5425;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity
extends ZombieEntity
implements RangedAttackMob {
    private boolean targetingUnderwater;
    protected final SwimNavigation waterNavigation;
    protected final MobNavigation landNavigation;

    public DrownedEntity(EntityType<? extends DrownedEntity> arg, World arg2) {
        super((EntityType<? extends ZombieEntity>)arg, arg2);
        this.stepHeight = 1.0f;
        this.moveControl = new DrownedMoveControl(this);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.waterNavigation = new SwimNavigation(this, arg2);
        this.landNavigation = new MobNavigation(this, arg2);
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(1, new WanderAroundOnSurfaceGoal(this, 1.0));
        this.goalSelector.add(2, new TridentAttackGoal(this, 1.0, 40, 10.0f));
        this.goalSelector.add(2, new DrownedAttackGoal(this, 1.0, false));
        this.goalSelector.add(5, new LeaveWaterGoal(this, 1.0));
        this.goalSelector.add(6, new TargetAboveWaterGoal(this, 1.0, this.world.getSeaLevel()));
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, DrownedEntity.class).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::canDrownedAttackTarget));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new FollowTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    @Override
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        entityData = super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
        if (this.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03f) {
            this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.handDropChances[EquipmentSlot.OFFHAND.getEntitySlotId()] = 2.0f;
        }
        return entityData;
    }

    public static boolean canSpawn(EntityType<DrownedEntity> type, class_5425 arg2, SpawnReason spawnReason, BlockPos pos, Random random) {
        boolean bl;
        Biome lv = arg2.getBiome(pos);
        boolean bl2 = bl = arg2.getDifficulty() != Difficulty.PEACEFUL && DrownedEntity.isSpawnDark(arg2, pos, random) && (spawnReason == SpawnReason.SPAWNER || arg2.getFluidState(pos).isIn(FluidTags.WATER));
        if (lv == Biomes.RIVER || lv == Biomes.FROZEN_RIVER) {
            return random.nextInt(15) == 0 && bl;
        }
        return random.nextInt(40) == 0 && DrownedEntity.isValidSpawnDepth(arg2, pos) && bl;
    }

    private static boolean isValidSpawnDepth(WorldAccess world, BlockPos pos) {
        return pos.getY() < world.getSeaLevel() - 5;
    }

    @Override
    protected boolean shouldBreakDoors() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isTouchingWater()) {
            return SoundEvents.ENTITY_DROWNED_AMBIENT_WATER;
        }
        return SoundEvents.ENTITY_DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.isTouchingWater()) {
            return SoundEvents.ENTITY_DROWNED_HURT_WATER;
        }
        return SoundEvents.ENTITY_DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isTouchingWater()) {
            return SoundEvents.ENTITY_DROWNED_DEATH_WATER;
        }
        return SoundEvents.ENTITY_DROWNED_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_DROWNED_STEP;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DROWNED_SWIM;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        if ((double)this.random.nextFloat() > 0.9) {
            int i = this.random.nextInt(16);
            if (i < 10) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }
    }

    @Override
    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        if (oldStack.getItem() == Items.NAUTILUS_SHELL) {
            return false;
        }
        if (oldStack.getItem() == Items.TRIDENT) {
            if (newStack.getItem() == Items.TRIDENT) {
                return newStack.getDamage() < oldStack.getDamage();
            }
            return false;
        }
        if (newStack.getItem() == Items.TRIDENT) {
            return true;
        }
        return super.prefersNewEquipment(newStack, oldStack);
    }

    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return world.intersectsEntities(this);
    }

    public boolean canDrownedAttackTarget(@Nullable LivingEntity target) {
        if (target != null) {
            return !this.world.isDay() || target.isTouchingWater();
        }
        return false;
    }

    @Override
    public boolean canFly() {
        return !this.isSwimming();
    }

    private boolean isTargetingUnderwater() {
        if (this.targetingUnderwater) {
            return true;
        }
        LivingEntity lv = this.getTarget();
        return lv != null && lv.isTouchingWater();
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater() && this.isTargetingUnderwater()) {
            this.updateVelocity(0.01f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public void updateSwimming() {
        if (!this.world.isClient) {
            if (this.canMoveVoluntarily() && this.isTouchingWater() && this.isTargetingUnderwater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.landNavigation;
                this.setSwimming(false);
            }
        }
    }

    protected boolean hasFinishedCurrentPath() {
        double d;
        BlockPos lv2;
        Path lv = this.getNavigation().getCurrentPath();
        return lv != null && (lv2 = lv.getTarget()) != null && (d = this.squaredDistanceTo(lv2.getX(), lv2.getY(), lv2.getZ())) < 4.0;
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        TridentEntity lv = new TridentEntity(this.world, (LivingEntity)this, new ItemStack(Items.TRIDENT));
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - lv.getY();
        double g = target.getZ() - this.getZ();
        double h = MathHelper.sqrt(d * d + g * g);
        lv.setVelocity(d, e + h * (double)0.2f, g, 1.6f, 14 - this.world.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(lv);
    }

    public void setTargetingUnderwater(boolean targetingUnderwater) {
        this.targetingUnderwater = targetingUnderwater;
    }

    static class DrownedMoveControl
    extends MoveControl {
        private final DrownedEntity drowned;

        public DrownedMoveControl(DrownedEntity drowned) {
            super(drowned);
            this.drowned = drowned;
        }

        @Override
        public void tick() {
            LivingEntity lv = this.drowned.getTarget();
            if (this.drowned.isTargetingUnderwater() && this.drowned.isTouchingWater()) {
                if (lv != null && lv.getY() > this.drowned.getY() || this.drowned.targetingUnderwater) {
                    this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, 0.002, 0.0));
                }
                if (this.state != MoveControl.State.MOVE_TO || this.drowned.getNavigation().isIdle()) {
                    this.drowned.setMovementSpeed(0.0f);
                    return;
                }
                double d = this.targetX - this.drowned.getX();
                double e = this.targetY - this.drowned.getY();
                double f = this.targetZ - this.drowned.getZ();
                double g = MathHelper.sqrt(d * d + e * e + f * f);
                float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
                this.drowned.bodyYaw = this.drowned.yaw = this.changeAngle(this.drowned.yaw, h, 90.0f);
                float i = (float)(this.speed * this.drowned.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                float j = MathHelper.lerp(0.125f, this.drowned.getMovementSpeed(), i);
                this.drowned.setMovementSpeed(j);
                this.drowned.setVelocity(this.drowned.getVelocity().add((double)j * d * 0.005, (double)j * (e /= g) * 0.1, (double)j * f * 0.005));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, -0.008, 0.0));
                }
                super.tick();
            }
        }
    }

    static class DrownedAttackGoal
    extends ZombieAttackGoal {
        private final DrownedEntity drowned;

        public DrownedAttackGoal(DrownedEntity drowned, double speed, boolean pauseWhenMobIdle) {
            super(drowned, speed, pauseWhenMobIdle);
            this.drowned = drowned;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
        }
    }

    static class WanderAroundOnSurfaceGoal
    extends Goal {
        private final PathAwareEntity mob;
        private double x;
        private double y;
        private double z;
        private final double speed;
        private final World world;

        public WanderAroundOnSurfaceGoal(PathAwareEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.world = mob.world;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!this.world.isDay()) {
                return false;
            }
            if (this.mob.isTouchingWater()) {
                return false;
            }
            Vec3d lv = this.getWanderTarget();
            if (lv == null) {
                return false;
            }
            this.x = lv.x;
            this.y = lv.y;
            this.z = lv.z;
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return !this.mob.getNavigation().isIdle();
        }

        @Override
        public void start() {
            this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
        }

        @Nullable
        private Vec3d getWanderTarget() {
            Random random = this.mob.getRandom();
            BlockPos lv = this.mob.getBlockPos();
            for (int i = 0; i < 10; ++i) {
                BlockPos lv2 = lv.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (!this.world.getBlockState(lv2).isOf(Blocks.WATER)) continue;
                return Vec3d.ofBottomCenter(lv2);
            }
            return null;
        }
    }

    static class LeaveWaterGoal
    extends MoveToTargetPosGoal {
        private final DrownedEntity drowned;

        public LeaveWaterGoal(DrownedEntity drowned, double speed) {
            super(drowned, speed, 8, 2);
            this.drowned = drowned;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.drowned.world.isDay() && this.drowned.isTouchingWater() && this.drowned.getY() >= (double)(this.drowned.world.getSeaLevel() - 3);
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue();
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            BlockPos lv = pos.up();
            if (!world.isAir(lv) || !world.isAir(lv.up())) {
                return false;
            }
            return world.getBlockState(pos).hasSolidTopSurface(world, pos, this.drowned);
        }

        @Override
        public void start() {
            this.drowned.setTargetingUnderwater(false);
            this.drowned.navigation = this.drowned.landNavigation;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    static class TargetAboveWaterGoal
    extends Goal {
        private final DrownedEntity drowned;
        private final double speed;
        private final int minY;
        private boolean foundTarget;

        public TargetAboveWaterGoal(DrownedEntity drowned, double speed, int minY) {
            this.drowned = drowned;
            this.speed = speed;
            this.minY = minY;
        }

        @Override
        public boolean canStart() {
            return !this.drowned.world.isDay() && this.drowned.isTouchingWater() && this.drowned.getY() < (double)(this.minY - 2);
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart() && !this.foundTarget;
        }

        @Override
        public void tick() {
            if (this.drowned.getY() < (double)(this.minY - 1) && (this.drowned.getNavigation().isIdle() || this.drowned.hasFinishedCurrentPath())) {
                Vec3d lv = TargetFinder.findTargetTowards(this.drowned, 4, 8, new Vec3d(this.drowned.getX(), this.minY - 1, this.drowned.getZ()));
                if (lv == null) {
                    this.foundTarget = true;
                    return;
                }
                this.drowned.getNavigation().startMovingTo(lv.x, lv.y, lv.z, this.speed);
            }
        }

        @Override
        public void start() {
            this.drowned.setTargetingUnderwater(true);
            this.foundTarget = false;
        }

        @Override
        public void stop() {
            this.drowned.setTargetingUnderwater(false);
        }
    }

    static class TridentAttackGoal
    extends ProjectileAttackGoal {
        private final DrownedEntity drowned;

        public TridentAttackGoal(RangedAttackMob arg, double d, int i, float f) {
            super(arg, d, i, f);
            this.drowned = (DrownedEntity)arg;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.drowned.getMainHandStack().getItem() == Items.TRIDENT;
        }

        @Override
        public void start() {
            super.start();
            this.drowned.setAttacking(true);
            this.drowned.setCurrentHand(Hand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            this.drowned.clearActiveItem();
            this.drowned.setAttacking(false);
        }
    }
}

