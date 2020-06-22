/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class StriderEntity
extends AnimalEntity
implements ItemSteerable,
Saddleable {
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS);
    private static final Ingredient ATTRACTING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> COLD = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final SaddledComponent saddledComponent;
    private TemptGoal temptGoal;
    private EscapeDangerGoal escapeDangerGoal;

    public StriderEntity(EntityType<? extends StriderEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
        this.inanimate = true;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0f);
    }

    public static boolean canSpawn(EntityType<StriderEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        BlockPos.Mutable lv = arg4.mutableCopy();
        do {
            lv.move(Direction.UP);
        } while (arg2.getFluidState(lv).isIn(FluidTags.LAVA));
        return arg2.getBlockState(lv).isAir();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (BOOST_TIME.equals(arg) && this.world.isClient) {
            this.saddledComponent.boost();
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BOOST_TIME, 0);
        this.dataTracker.startTracking(COLD, false);
        this.dataTracker.startTracking(SADDLED, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        this.saddledComponent.toTag(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.saddledComponent.fromTag(arg);
    }

    @Override
    public boolean isSaddled() {
        return this.saddledComponent.isSaddled();
    }

    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void saddle(@Nullable SoundCategory arg) {
        this.saddledComponent.setSaddled(true);
        if (arg != null) {
            this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_STRIDER_SADDLE, arg, 0.5f, 1.0f);
        }
    }

    @Override
    protected void initGoals() {
        this.escapeDangerGoal = new EscapeDangerGoal(this, 1.65);
        this.goalSelector.add(1, this.escapeDangerGoal);
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.temptGoal = new TemptGoal((MobEntityWithAi)this, 1.4, false, ATTRACTING_INGREDIENT);
        this.goalSelector.add(4, this.temptGoal);
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 60));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(9, new LookAtEntityGoal(this, StriderEntity.class, 8.0f));
    }

    public void setCold(boolean bl) {
        this.dataTracker.set(COLD, bl);
    }

    public boolean isCold() {
        if (this.getVehicle() instanceof StriderEntity) {
            return ((StriderEntity)this.getVehicle()).isCold();
        }
        return this.dataTracker.get(COLD);
    }

    @Override
    public boolean canWalkOnFluid(Fluid arg) {
        return arg.isIn(FluidTags.LAVA);
    }

    @Override
    @Nullable
    public Box getHardCollisionBox(Entity arg) {
        if (arg.isPushable()) {
            return arg.getBoundingBox();
        }
        return null;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getMountedHeightOffset() {
        float f = Math.min(0.25f, this.limbDistance);
        float g = this.limbAngle;
        return (double)this.getHeight() - 0.2 + (double)(0.12f * MathHelper.cos(g * 1.5f) * 2.0f * f);
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity lv = this.getPrimaryPassenger();
        if (!(lv instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity lv2 = (PlayerEntity)lv;
        return lv2.getMainHandStack().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || lv2.getOffHandStack().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        return arg.intersectsEntities(this);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity arg) {
        Vec3d[] lvs = new Vec3d[]{StriderEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), arg.yaw), StriderEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), arg.yaw - 22.5f), StriderEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), arg.yaw + 22.5f), StriderEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), arg.yaw - 45.0f), StriderEntity.getPassengerDismountOffset(this.getWidth(), arg.getWidth(), arg.yaw + 45.0f)};
        LinkedHashSet set = Sets.newLinkedHashSet();
        double d = this.getBoundingBox().maxY;
        double e = this.getBoundingBox().minY - 0.5;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Vec3d lv2 : lvs) {
            lv.set(this.getX() + lv2.x, d, this.getZ() + lv2.z);
            for (double f = d; f > e; f -= 1.0) {
                set.add(lv.toImmutable());
                lv.move(Direction.DOWN);
            }
        }
        for (BlockPos lv3 : set) {
            if (this.world.getFluidState(lv3).isIn(FluidTags.LAVA)) continue;
            for (EntityPose lv4 : arg.getPoses()) {
                Vec3d lv6;
                Box lv5;
                double g = this.world.getCollisionHeightAt(lv3);
                if (!Dismounting.canDismountInBlock(g) || !Dismounting.canPlaceEntityAt(this.world, arg, (lv5 = arg.getBoundingBox(lv4)).offset(lv6 = Vec3d.ofCenter(lv3, g)))) continue;
                arg.setPose(lv4);
                return lv6;
            }
        }
        return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Override
    public void travel(Vec3d arg) {
        this.setMovementSpeed(this.getSpeed());
        this.travel(this, this.saddledComponent, arg);
    }

    public float getSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.66f : 1.0f);
    }

    @Override
    public float getSaddledSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.23f : 0.55f);
    }

    @Override
    public void setMovementInput(Vec3d arg) {
        super.travel(arg);
    }

    @Override
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.6f;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(this.isInLava() ? SoundEvents.ENTITY_STRIDER_STEP_LAVA : SoundEvents.ENTITY_STRIDER_STEP, 1.0f, 1.0f);
    }

    @Override
    public boolean consumeOnAStickItem() {
        return this.saddledComponent.boost(this.getRandom());
    }

    @Override
    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
        this.checkBlockCollision();
        if (this.isInLava()) {
            this.fallDistance = 0.0f;
            return;
        }
        super.fall(d, bl, arg, arg2);
    }

    @Override
    public void tick() {
        if (this.method_30079() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY, 1.0f, this.getSoundPitch());
        } else if (this.method_30078() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT, 1.0f, this.getSoundPitch());
        }
        BlockState lv = this.world.getBlockState(this.getBlockPos());
        BlockState lv2 = this.getLandingBlockState();
        boolean bl = lv.isIn(BlockTags.STRIDER_WARM_BLOCKS) || lv2.isIn(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
        this.setCold(!bl);
        super.tick();
        this.updateFloating();
        this.checkBlockCollision();
    }

    private boolean method_30078() {
        return this.escapeDangerGoal != null && this.escapeDangerGoal.isActive();
    }

    private boolean method_30079() {
        return this.temptGoal != null && this.temptGoal.isActive();
    }

    @Override
    protected boolean movesIndependently() {
        return true;
    }

    private void updateFloating() {
        if (this.isInLava()) {
            ShapeContext lv = ShapeContext.of(this);
            if (!lv.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) || this.world.getFluidState(this.getBlockPos().up()).isIn(FluidTags.LAVA)) {
                this.setVelocity(this.getVelocity().multiply(0.5).add(0.0, 0.05, 0.0));
            } else {
                this.onGround = true;
            }
        }
    }

    public static DefaultAttributeContainer.Builder createStriderAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175f).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.method_30078() || this.method_30079()) {
            return null;
        }
        return SoundEvents.ENTITY_STRIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_STRIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRIDER_DEATH;
    }

    @Override
    protected boolean canAddPassenger(Entity arg) {
        return this.getPassengerList().isEmpty() && !this.isSubmergedIn(FluidTags.LAVA);
    }

    @Override
    public boolean hurtByWater() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        return new Navigation(this, arg);
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (arg2.getBlockState(arg).getFluidState().isIn(FluidTags.LAVA)) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    public StriderEntity createChild(PassiveEntity arg) {
        return EntityType.STRIDER.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return BREEDING_INGREDIENT.test(arg);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isSaddled()) {
            this.dropItem(Items.SADDLE);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        boolean bl = this.isBreedingItem(arg.getStackInHand(arg2));
        if (!bl && this.isSaddled() && !this.hasPassengers()) {
            if (!this.world.isClient) {
                arg.startRiding(this);
            }
            return ActionResult.success(this.world.isClient);
        }
        ActionResult lv = super.interactMob(arg, arg2);
        if (!lv.isAccepted()) {
            ItemStack lv2 = arg.getStackInHand(arg2);
            if (lv2.getItem() == Items.SADDLE) {
                return lv2.useOnEntity(arg, this, arg2);
            }
            return ActionResult.PASS;
        }
        if (bl && !this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_STRIDER_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return lv;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.6f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        ZombifiedPiglinEntity lv9;
        StriderData.RiderType lv6;
        ZombieEntity.ZombieData lv = null;
        if (arg4 instanceof StriderData) {
            StriderData.RiderType lv2 = ((StriderData)arg4).type;
        } else if (!this.isBaby()) {
            StriderData.RiderType lv5;
            if (this.random.nextInt(30) == 0) {
                StriderData.RiderType lv3 = StriderData.RiderType.PIGLIN_RIDER;
                lv = new ZombieEntity.ZombieData(ZombieEntity.method_29936(this.random), false);
            } else if (this.random.nextInt(10) == 0) {
                StriderData.RiderType lv4 = StriderData.RiderType.BABY_RIDER;
            } else {
                lv5 = StriderData.RiderType.NO_RIDER;
            }
            arg4 = new StriderData(lv5);
            ((PassiveEntity.PassiveData)arg4).setBabyChance(lv5 == StriderData.RiderType.NO_RIDER ? 0.5f : 0.0f);
        } else {
            lv6 = StriderData.RiderType.NO_RIDER;
        }
        MobEntityWithAi lv7 = null;
        if (lv6 == StriderData.RiderType.BABY_RIDER) {
            StriderEntity lv8 = EntityType.STRIDER.create(arg.getWorld());
            if (lv8 != null) {
                lv7 = lv8;
                lv8.setBreedingAge(-24000);
            }
        } else if (lv6 == StriderData.RiderType.PIGLIN_RIDER && (lv9 = EntityType.ZOMBIFIED_PIGLIN.create(arg.getWorld())) != null) {
            lv7 = lv9;
            this.saddle(null);
        }
        if (lv7 != null) {
            lv7.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0f);
            lv7.initialize(arg, arg2, SpawnReason.JOCKEY, lv, null);
            lv7.startRiding(this, true);
            arg.spawnEntity(lv7);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    static class Navigation
    extends MobNavigation {
        Navigation(StriderEntity arg, World arg2) {
            super(arg, arg2);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int i) {
            this.nodeMaker = new LandPathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, i);
        }

        @Override
        protected boolean canWalkOnPath(PathNodeType arg) {
            if (arg == PathNodeType.LAVA || arg == PathNodeType.DAMAGE_FIRE || arg == PathNodeType.DANGER_FIRE) {
                return true;
            }
            return super.canWalkOnPath(arg);
        }

        @Override
        public boolean isValidPosition(BlockPos arg) {
            return this.world.getBlockState(arg).isOf(Blocks.LAVA) || super.isValidPosition(arg);
        }
    }

    public static class StriderData
    extends PassiveEntity.PassiveData {
        public final RiderType type;

        public StriderData(RiderType arg) {
            this.type = arg;
        }

        public static enum RiderType {
            NO_RIDER,
            BABY_RIDER,
            PIGLIN_RIDER;

        }
    }
}

