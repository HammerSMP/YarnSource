/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class EndermanEntity
extends HostileEntity {
    private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", (double)0.15f, EntityAttributeModifier.Operation.ADDITION);
    private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
    private static final TrackedData<Boolean> ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> field_20618 = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE = arg -> arg instanceof EndermiteEntity && ((EndermiteEntity)arg).isPlayerSpawned();
    private int lastAngrySoundAge = Integer.MIN_VALUE;
    private int ageWhenTargetSet;

    public EndermanEntity(EntityType<? extends EndermanEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.stepHeight = 1.0f;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChasePlayerGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal((MobEntityWithAi)this, 1.0, 0.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(10, new PlaceBlockGoal(this));
        this.goalSelector.add(11, new PickUpBlockGoal(this));
        this.targetSelector.add(1, new TeleportTowardsPlayerGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<EndermiteEntity>(this, EndermiteEntity.class, 10, true, false, PLAYER_ENDERMITE_PREDICATE));
    }

    public static DefaultAttributeContainer.Builder createEndermanAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0);
    }

    @Override
    public void setTarget(@Nullable LivingEntity arg) {
        super.setTarget(arg);
        EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (arg == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, false);
            this.dataTracker.set(field_20618, false);
            lv.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
            if (!lv.hasModifier(ATTACKING_SPEED_BOOST)) {
                lv.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CARRIED_BLOCK, Optional.empty());
        this.dataTracker.startTracking(ANGRY, false);
        this.dataTracker.startTracking(field_20618, false);
    }

    public void playAngrySound() {
        if (this.age >= this.lastAngrySoundAge + 400) {
            this.lastAngrySoundAge = this.age;
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5f, 1.0f, false);
            }
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (ANGRY.equals(arg) && this.method_22330() && this.world.isClient) {
            this.playAngrySound();
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        BlockState lv = this.getCarriedBlock();
        if (lv != null) {
            arg.put("carriedBlockState", NbtHelper.fromBlockState(lv));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        BlockState lv = null;
        if (arg.contains("carriedBlockState", 10) && (lv = NbtHelper.toBlockState(arg.getCompound("carriedBlockState"))).isAir()) {
            lv = null;
        }
        this.setCarriedBlock(lv);
    }

    private boolean isPlayerStaring(PlayerEntity arg) {
        ItemStack lv = arg.inventory.armor.get(3);
        if (lv.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            return false;
        }
        Vec3d lv2 = arg.getRotationVec(1.0f).normalize();
        Vec3d lv3 = new Vec3d(this.getX() - arg.getX(), this.getEyeY() - arg.getEyeY(), this.getZ() - arg.getZ());
        double d = lv3.length();
        double e = lv2.dotProduct(lv3 = lv3.normalize());
        if (e > 1.0 - 0.025 / d) {
            return arg.canSee(this);
        }
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 2.55f;
    }

    @Override
    public void tickMovement() {
        if (this.world.isClient) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getParticleX(0.5), this.getRandomBodyY() - 0.25, this.getParticleZ(0.5), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        this.jumping = false;
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        float f;
        if (this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        if (this.world.isDay() && this.age >= this.ageWhenTargetSet + 600 && (f = this.getBrightnessAtEyes()) > 0.5f && this.world.isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f) {
            this.setTarget(null);
            this.teleportRandomly();
        }
        super.mobTick();
    }

    protected boolean teleportRandomly() {
        if (this.world.isClient() || !this.isAlive()) {
            return false;
        }
        double d = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
        double e = this.getY() + (double)(this.random.nextInt(64) - 32);
        double f = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
        return this.teleportTo(d, e, f);
    }

    private boolean teleportTo(Entity arg) {
        Vec3d lv = new Vec3d(this.getX() - arg.getX(), this.getBodyY(0.5) - arg.getEyeY(), this.getZ() - arg.getZ());
        lv = lv.normalize();
        double d = 16.0;
        double e = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - lv.x * 16.0;
        double f = this.getY() + (double)(this.random.nextInt(16) - 8) - lv.y * 16.0;
        double g = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - lv.z * 16.0;
        return this.teleportTo(e, f, g);
    }

    private boolean teleportTo(double d, double e, double f) {
        BlockPos.Mutable lv = new BlockPos.Mutable(d, e, f);
        while (lv.getY() > 0 && !this.world.getBlockState(lv).getMaterial().blocksMovement()) {
            lv.move(Direction.DOWN);
        }
        BlockState lv2 = this.world.getBlockState(lv);
        boolean bl = lv2.getMaterial().blocksMovement();
        boolean bl2 = lv2.getFluidState().matches(FluidTags.WATER);
        if (!bl || bl2) {
            return false;
        }
        boolean bl3 = this.teleport(d, e, f, true);
        if (bl3 && !this.isSilent()) {
            this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0f, 1.0f);
            this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        return bl3;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isAngry() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    @Override
    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
        super.dropEquipment(arg, i, bl);
        BlockState lv = this.getCarriedBlock();
        if (lv != null) {
            this.dropItem(lv.getBlock());
        }
    }

    public void setCarriedBlock(@Nullable BlockState arg) {
        this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(arg));
    }

    @Nullable
    public BlockState getCarriedBlock() {
        return this.dataTracker.get(CARRIED_BLOCK).orElse(null);
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        if (arg instanceof ProjectileDamageSource) {
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean bl = super.damage(arg, f);
        if (!this.world.isClient() && this.random.nextInt(10) != 0) {
            this.teleportRandomly();
        }
        return bl;
    }

    public boolean isAngry() {
        return this.dataTracker.get(ANGRY);
    }

    public boolean method_22330() {
        return this.dataTracker.get(field_20618);
    }

    public void method_22331() {
        this.dataTracker.set(field_20618, true);
    }

    static class PickUpBlockGoal
    extends Goal {
        private final EndermanEntity enderman;

        public PickUpBlockGoal(EndermanEntity arg) {
            this.enderman = arg;
        }

        @Override
        public boolean canStart() {
            if (this.enderman.getCarriedBlock() != null) {
                return false;
            }
            if (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }
            return this.enderman.getRandom().nextInt(20) == 0;
        }

        @Override
        public void tick() {
            Random random = this.enderman.getRandom();
            World lv = this.enderman.world;
            int i = MathHelper.floor(this.enderman.getX() - 2.0 + random.nextDouble() * 4.0);
            int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0);
            int k = MathHelper.floor(this.enderman.getZ() - 2.0 + random.nextDouble() * 4.0);
            BlockPos lv2 = new BlockPos(i, j, k);
            BlockState lv3 = lv.getBlockState(lv2);
            Block lv4 = lv3.getBlock();
            Vec3d lv5 = new Vec3d((double)MathHelper.floor(this.enderman.getX()) + 0.5, (double)j + 0.5, (double)MathHelper.floor(this.enderman.getZ()) + 0.5);
            Vec3d lv6 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5);
            BlockHitResult lv7 = lv.rayTrace(new RayTraceContext(lv5, lv6, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, this.enderman));
            boolean bl = lv7.getBlockPos().equals(lv2);
            if (lv4.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl) {
                this.enderman.setCarriedBlock(lv3);
                lv.removeBlock(lv2, false);
            }
        }
    }

    static class PlaceBlockGoal
    extends Goal {
        private final EndermanEntity enderman;

        public PlaceBlockGoal(EndermanEntity arg) {
            this.enderman = arg;
        }

        @Override
        public boolean canStart() {
            if (this.enderman.getCarriedBlock() == null) {
                return false;
            }
            if (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }
            return this.enderman.getRandom().nextInt(2000) == 0;
        }

        @Override
        public void tick() {
            Random random = this.enderman.getRandom();
            World lv = this.enderman.world;
            int i = MathHelper.floor(this.enderman.getX() - 1.0 + random.nextDouble() * 2.0);
            int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0);
            int k = MathHelper.floor(this.enderman.getZ() - 1.0 + random.nextDouble() * 2.0);
            BlockPos lv2 = new BlockPos(i, j, k);
            BlockState lv3 = lv.getBlockState(lv2);
            BlockPos lv4 = lv2.down();
            BlockState lv5 = lv.getBlockState(lv4);
            BlockState lv6 = this.enderman.getCarriedBlock();
            if (lv6 != null && this.method_7033(lv, lv2, lv6, lv3, lv5, lv4)) {
                lv.setBlockState(lv2, lv6, 3);
                this.enderman.setCarriedBlock(null);
            }
        }

        private boolean method_7033(WorldView arg, BlockPos arg2, BlockState arg3, BlockState arg4, BlockState arg5, BlockPos arg6) {
            return arg4.isAir() && !arg5.isAir() && arg5.isFullCube(arg, arg6) && arg3.canPlaceAt(arg, arg2);
        }
    }

    static class ChasePlayerGoal
    extends Goal {
        private final EndermanEntity enderman;
        private LivingEntity field_21513;

        public ChasePlayerGoal(EndermanEntity arg) {
            this.enderman = arg;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            this.field_21513 = this.enderman.getTarget();
            if (!(this.field_21513 instanceof PlayerEntity)) {
                return false;
            }
            double d = this.field_21513.squaredDistanceTo(this.enderman);
            if (d > 256.0) {
                return false;
            }
            return this.enderman.isPlayerStaring((PlayerEntity)this.field_21513);
        }

        @Override
        public void start() {
            this.enderman.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.enderman.getLookControl().lookAt(this.field_21513.getX(), this.field_21513.getEyeY(), this.field_21513.getZ());
        }
    }

    static class TeleportTowardsPlayerGoal
    extends FollowTargetGoal<PlayerEntity> {
        private final EndermanEntity enderman;
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate = new TargetPredicate().includeHidden();

        public TeleportTowardsPlayerGoal(EndermanEntity arg) {
            super((MobEntity)arg, PlayerEntity.class, false);
            this.enderman = arg;
            this.staringPlayerPredicate = new TargetPredicate().setBaseMaxDistance(this.getFollowRange()).setPredicate(arg2 -> arg.isPlayerStaring((PlayerEntity)arg2));
        }

        @Override
        public boolean canStart() {
            this.targetPlayer = this.enderman.world.getClosestPlayer(this.staringPlayerPredicate, this.enderman);
            return this.targetPlayer != null;
        }

        @Override
        public void start() {
            this.lookAtPlayerWarmup = 5;
            this.ticksSinceUnseenTeleport = 0;
            this.enderman.method_22331();
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        @Override
        public boolean shouldContinue() {
            if (this.targetPlayer != null) {
                if (!this.enderman.isPlayerStaring(this.targetPlayer)) {
                    return false;
                }
                this.enderman.lookAtEntity(this.targetPlayer, 10.0f, 10.0f);
                return true;
            }
            if (this.targetEntity != null && this.validTargetPredicate.test(this.enderman, this.targetEntity)) {
                return true;
            }
            return super.shouldContinue();
        }

        @Override
        public void tick() {
            if (this.enderman.getTarget() == null) {
                super.method_24632(null);
            }
            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.targetEntity != null && !this.enderman.hasVehicle()) {
                    if (this.enderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                        if (this.targetEntity.squaredDistanceTo(this.enderman) < 16.0) {
                            this.enderman.teleportRandomly();
                        }
                        this.ticksSinceUnseenTeleport = 0;
                    } else if (this.targetEntity.squaredDistanceTo(this.enderman) > 256.0 && this.ticksSinceUnseenTeleport++ >= 30 && this.enderman.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }
                super.tick();
            }
        }
    }
}

