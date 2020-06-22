/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TurtleEntity
extends AnimalEntity {
    private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> HAS_EGG = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DIGGING_SAND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> TRAVEL_POS = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> LAND_BOUND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ACTIVELY_TRAVELLING = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int sandDiggingCounter;
    public static final Predicate<LivingEntity> BABY_TURTLE_ON_LAND_FILTER = arg -> arg.isBaby() && !arg.isTouchingWater();

    public TurtleEntity(EntityType<? extends TurtleEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.moveControl = new TurtleMoveControl(this);
        this.stepHeight = 1.0f;
    }

    public void setHomePos(BlockPos arg) {
        this.dataTracker.set(HOME_POS, arg);
    }

    private BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }

    private void setTravelPos(BlockPos arg) {
        this.dataTracker.set(TRAVEL_POS, arg);
    }

    private BlockPos getTravelPos() {
        return this.dataTracker.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return this.dataTracker.get(HAS_EGG);
    }

    private void setHasEgg(boolean bl) {
        this.dataTracker.set(HAS_EGG, bl);
    }

    public boolean isDiggingSand() {
        return this.dataTracker.get(DIGGING_SAND);
    }

    private void setDiggingSand(boolean bl) {
        this.sandDiggingCounter = bl ? 1 : 0;
        this.dataTracker.set(DIGGING_SAND, bl);
    }

    private boolean isLandBound() {
        return this.dataTracker.get(LAND_BOUND);
    }

    private void setLandBound(boolean bl) {
        this.dataTracker.set(LAND_BOUND, bl);
    }

    private boolean isActivelyTravelling() {
        return this.dataTracker.get(ACTIVELY_TRAVELLING);
    }

    private void setActivelyTravelling(boolean bl) {
        this.dataTracker.set(ACTIVELY_TRAVELLING, bl);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HOME_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(HAS_EGG, false);
        this.dataTracker.startTracking(TRAVEL_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(LAND_BOUND, false);
        this.dataTracker.startTracking(ACTIVELY_TRAVELLING, false);
        this.dataTracker.startTracking(DIGGING_SAND, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("HomePosX", this.getHomePos().getX());
        arg.putInt("HomePosY", this.getHomePos().getY());
        arg.putInt("HomePosZ", this.getHomePos().getZ());
        arg.putBoolean("HasEgg", this.hasEgg());
        arg.putInt("TravelPosX", this.getTravelPos().getX());
        arg.putInt("TravelPosY", this.getTravelPos().getY());
        arg.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        int i = arg.getInt("HomePosX");
        int j = arg.getInt("HomePosY");
        int k = arg.getInt("HomePosZ");
        this.setHomePos(new BlockPos(i, j, k));
        super.readCustomDataFromTag(arg);
        this.setHasEgg(arg.getBoolean("HasEgg"));
        int l = arg.getInt("TravelPosX");
        int m = arg.getInt("TravelPosY");
        int n = arg.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(l, m, n));
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setHomePos(this.getBlockPos());
        this.setTravelPos(BlockPos.ORIGIN);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public static boolean canSpawn(EntityType<TurtleEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return arg4.getY() < arg2.getSeaLevel() + 4 && TurtleEggBlock.isSand(arg2, arg4) && arg2.getBaseLightLevel(arg4, 0) > 8;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new TurtleEscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new MateGoal(this, 1.0));
        this.goalSelector.add(1, new LayEggGoal(this, 1.0));
        this.goalSelector.add(2, new ApproachFoodHoldingPlayerGoal(this, 1.1, Blocks.SEAGRASS.asItem()));
        this.goalSelector.add(3, new WanderInWaterGoal(this, 1.0));
        this.goalSelector.add(4, new GoHomeGoal(this, 1.0));
        this.goalSelector.add(7, new TravelGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(9, new WanderOnLandGoal(this, 1.0, 100));
    }

    public static DefaultAttributeContainer.Builder createTurtleAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 200;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (!this.isTouchingWater() && this.onGround && !this.isBaby()) {
            return SoundEvents.ENTITY_TURTLE_AMBIENT_LAND;
        }
        return super.getAmbientSound();
    }

    @Override
    protected void playSwimSound(float f) {
        super.playSwimSound(f * 1.5f);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_TURTLE_SWIM;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource arg) {
        if (this.isBaby()) {
            return SoundEvents.ENTITY_TURTLE_HURT_BABY;
        }
        return SoundEvents.ENTITY_TURTLE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        if (this.isBaby()) {
            return SoundEvents.ENTITY_TURTLE_DEATH_BABY;
        }
        return SoundEvents.ENTITY_TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        SoundEvent lv = this.isBaby() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
        this.playSound(lv, 0.15f, 1.0f);
    }

    @Override
    public boolean canEat() {
        return super.canEat() && !this.hasEgg();
    }

    @Override
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.15f;
    }

    @Override
    public float getScaleFactor() {
        return this.isBaby() ? 0.3f : 1.0f;
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        return new TurtleSwimNavigation(this, arg);
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity arg) {
        return EntityType.TURTLE.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return arg.getItem() == Blocks.SEAGRASS.asItem();
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (!this.isLandBound() && arg2.getFluidState(arg).isIn(FluidTags.WATER)) {
            return 10.0f;
        }
        if (TurtleEggBlock.isSand(arg2, arg)) {
            return 10.0f;
        }
        return arg2.getBrightness(arg) - 0.5f;
    }

    @Override
    public void tickMovement() {
        BlockPos lv;
        super.tickMovement();
        if (this.isAlive() && this.isDiggingSand() && this.sandDiggingCounter >= 1 && this.sandDiggingCounter % 5 == 0 && TurtleEggBlock.isSand(this.world, lv = this.getBlockPos())) {
            this.world.syncWorldEvent(2001, lv, Block.getRawIdFromState(Blocks.SAND.getDefaultState()));
        }
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (!this.isBaby() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropItem(Items.SCUTE, 1);
        }
    }

    @Override
    public void travel(Vec3d arg) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1f, arg);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (!(this.getTarget() != null || this.isLandBound() && this.getHomePos().isWithinDistance(this.getPos(), 20.0))) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(arg);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return false;
    }

    @Override
    public void onStruckByLightning(LightningEntity arg) {
        this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    static class TurtleSwimNavigation
    extends SwimNavigation {
        TurtleSwimNavigation(TurtleEntity arg, World arg2) {
            super(arg, arg2);
        }

        @Override
        protected boolean isAtValidPosition() {
            return true;
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int i) {
            this.nodeMaker = new AmphibiousPathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, i);
        }

        @Override
        public boolean isValidPosition(BlockPos arg) {
            TurtleEntity lv;
            if (this.entity instanceof TurtleEntity && (lv = (TurtleEntity)this.entity).isActivelyTravelling()) {
                return this.world.getBlockState(arg).isOf(Blocks.WATER);
            }
            return !this.world.getBlockState(arg.down()).isAir();
        }
    }

    static class TurtleMoveControl
    extends MoveControl {
        private final TurtleEntity turtle;

        TurtleMoveControl(TurtleEntity arg) {
            super(arg);
            this.turtle = arg;
        }

        private void updateVelocity() {
            if (this.turtle.isTouchingWater()) {
                this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 16.0)) {
                    this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.08f));
                }
                if (this.turtle.isBaby()) {
                    this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0f, 0.06f));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.06f));
            }
        }

        @Override
        public void tick() {
            this.updateVelocity();
            if (this.state != MoveControl.State.MOVE_TO || this.turtle.getNavigation().isIdle()) {
                this.turtle.setMovementSpeed(0.0f);
                return;
            }
            double d = this.targetX - this.turtle.getX();
            double e = this.targetY - this.turtle.getY();
            double f = this.targetZ - this.turtle.getZ();
            double g = MathHelper.sqrt(d * d + e * e + f * f);
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
            this.turtle.bodyYaw = this.turtle.yaw = this.changeAngle(this.turtle.yaw, h, 90.0f);
            float i = (float)(this.speed * this.turtle.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            this.turtle.setMovementSpeed(MathHelper.lerp(0.125f, this.turtle.getMovementSpeed(), i));
            this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, (double)this.turtle.getMovementSpeed() * (e /= g) * 0.1, 0.0));
        }
    }

    static class WanderInWaterGoal
    extends MoveToTargetPosGoal {
        private final TurtleEntity turtle;

        private WanderInWaterGoal(TurtleEntity arg, double d) {
            super(arg, arg.isBaby() ? 2.0 : d, 24);
            this.turtle = arg;
            this.lowestY = -1;
        }

        @Override
        public boolean shouldContinue() {
            return !this.turtle.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.turtle.world, this.targetPos);
        }

        @Override
        public boolean canStart() {
            if (this.turtle.isBaby() && !this.turtle.isTouchingWater()) {
                return super.canStart();
            }
            if (!(this.turtle.isLandBound() || this.turtle.isTouchingWater() || this.turtle.hasEgg())) {
                return super.canStart();
            }
            return false;
        }

        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 160 == 0;
        }

        @Override
        protected boolean isTargetPos(WorldView arg, BlockPos arg2) {
            return arg.getBlockState(arg2).isOf(Blocks.WATER);
        }
    }

    static class WanderOnLandGoal
    extends WanderAroundGoal {
        private final TurtleEntity turtle;

        private WanderOnLandGoal(TurtleEntity arg, double d, int i) {
            super(arg, d, i);
            this.turtle = arg;
        }

        @Override
        public boolean canStart() {
            if (!(this.mob.isTouchingWater() || this.turtle.isLandBound() || this.turtle.hasEgg())) {
                return super.canStart();
            }
            return false;
        }
    }

    static class LayEggGoal
    extends MoveToTargetPosGoal {
        private final TurtleEntity turtle;

        LayEggGoal(TurtleEntity arg, double d) {
            super(arg, d, 16);
            this.turtle = arg;
        }

        @Override
        public boolean canStart() {
            if (this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0)) {
                return super.canStart();
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos lv = this.turtle.getBlockPos();
            if (!this.turtle.isTouchingWater() && this.hasReached()) {
                if (this.turtle.sandDiggingCounter < 1) {
                    this.turtle.setDiggingSand(true);
                } else if (this.turtle.sandDiggingCounter > 200) {
                    World lv2 = this.turtle.world;
                    lv2.playSound(null, lv, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3f, 0.9f + lv2.random.nextFloat() * 0.2f);
                    lv2.setBlockState(this.targetPos.up(), (BlockState)Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setDiggingSand(false);
                    this.turtle.setLoveTicks(600);
                }
                if (this.turtle.isDiggingSand()) {
                    this.turtle.sandDiggingCounter++;
                }
            }
        }

        @Override
        protected boolean isTargetPos(WorldView arg, BlockPos arg2) {
            if (!arg.isAir(arg2.up())) {
                return false;
            }
            return TurtleEggBlock.method_29952(arg, arg2);
        }
    }

    static class MateGoal
    extends AnimalMateGoal {
        private final TurtleEntity turtle;

        MateGoal(TurtleEntity arg, double d) {
            super(arg, d);
            this.turtle = arg;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.turtle.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayerEntity lv = this.animal.getLovingPlayer();
            if (lv == null && this.mate.getLovingPlayer() != null) {
                lv = this.mate.getLovingPlayer();
            }
            if (lv != null) {
                lv.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(lv, this.animal, this.mate, null);
            }
            this.turtle.setHasEgg(true);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();
            Random random = this.animal.getRandom();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }
        }
    }

    static class ApproachFoodHoldingPlayerGoal
    extends Goal {
        private static final TargetPredicate CLOSE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeTeammates().includeInvulnerable();
        private final TurtleEntity turtle;
        private final double speed;
        private PlayerEntity targetPlayer;
        private int cooldown;
        private final Set<Item> attractiveItems;

        ApproachFoodHoldingPlayerGoal(TurtleEntity arg, double d, Item arg2) {
            this.turtle = arg;
            this.speed = d;
            this.attractiveItems = Sets.newHashSet((Object[])new Item[]{arg2});
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            }
            this.targetPlayer = this.turtle.world.getClosestPlayer(CLOSE_ENTITY_PREDICATE, this.turtle);
            if (this.targetPlayer == null) {
                return false;
            }
            return this.isAttractive(this.targetPlayer.getMainHandStack()) || this.isAttractive(this.targetPlayer.getOffHandStack());
        }

        private boolean isAttractive(ItemStack arg) {
            return this.attractiveItems.contains(arg.getItem());
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart();
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.turtle.getNavigation().stop();
            this.cooldown = 100;
        }

        @Override
        public void tick() {
            this.turtle.getLookControl().lookAt(this.targetPlayer, this.turtle.getBodyYawSpeed() + 20, this.turtle.getLookPitchSpeed());
            if (this.turtle.squaredDistanceTo(this.targetPlayer) < 6.25) {
                this.turtle.getNavigation().stop();
            } else {
                this.turtle.getNavigation().startMovingTo(this.targetPlayer, this.speed);
            }
        }
    }

    static class GoHomeGoal
    extends Goal {
        private final TurtleEntity turtle;
        private final double speed;
        private boolean noPath;
        private int homeReachingTryTicks;

        GoHomeGoal(TurtleEntity arg, double d) {
            this.turtle = arg;
            this.speed = d;
        }

        @Override
        public boolean canStart() {
            if (this.turtle.isBaby()) {
                return false;
            }
            if (this.turtle.hasEgg()) {
                return true;
            }
            if (this.turtle.getRandom().nextInt(700) != 0) {
                return false;
            }
            return !this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 64.0);
        }

        @Override
        public void start() {
            this.turtle.setLandBound(true);
            this.noPath = false;
            this.homeReachingTryTicks = 0;
        }

        @Override
        public void stop() {
            this.turtle.setLandBound(false);
        }

        @Override
        public boolean shouldContinue() {
            return !this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= 600;
        }

        @Override
        public void tick() {
            BlockPos lv = this.turtle.getHomePos();
            boolean bl = lv.isWithinDistance(this.turtle.getPos(), 16.0);
            if (bl) {
                ++this.homeReachingTryTicks;
            }
            if (this.turtle.getNavigation().isIdle()) {
                Vec3d lv2 = Vec3d.ofBottomCenter(lv);
                Vec3d lv3 = TargetFinder.findTargetTowards(this.turtle, 16, 3, lv2, 0.3141592741012573);
                if (lv3 == null) {
                    lv3 = TargetFinder.findTargetTowards(this.turtle, 8, 7, lv2);
                }
                if (lv3 != null && !bl && !this.turtle.world.getBlockState(new BlockPos(lv3)).isOf(Blocks.WATER)) {
                    lv3 = TargetFinder.findTargetTowards(this.turtle, 16, 5, lv2);
                }
                if (lv3 == null) {
                    this.noPath = true;
                    return;
                }
                this.turtle.getNavigation().startMovingTo(lv3.x, lv3.y, lv3.z, this.speed);
            }
        }
    }

    static class TravelGoal
    extends Goal {
        private final TurtleEntity turtle;
        private final double speed;
        private boolean noPath;

        TravelGoal(TurtleEntity arg, double d) {
            this.turtle = arg;
            this.speed = d;
        }

        @Override
        public boolean canStart() {
            return !this.turtle.isLandBound() && !this.turtle.hasEgg() && this.turtle.isTouchingWater();
        }

        @Override
        public void start() {
            int i = 512;
            int j = 4;
            Random random = this.turtle.random;
            int k = random.nextInt(1025) - 512;
            int l = random.nextInt(9) - 4;
            int m = random.nextInt(1025) - 512;
            if ((double)l + this.turtle.getY() > (double)(this.turtle.world.getSeaLevel() - 1)) {
                l = 0;
            }
            BlockPos lv = new BlockPos((double)k + this.turtle.getX(), (double)l + this.turtle.getY(), (double)m + this.turtle.getZ());
            this.turtle.setTravelPos(lv);
            this.turtle.setActivelyTravelling(true);
            this.noPath = false;
        }

        @Override
        public void tick() {
            if (this.turtle.getNavigation().isIdle()) {
                Vec3d lv = Vec3d.ofBottomCenter(this.turtle.getTravelPos());
                Vec3d lv2 = TargetFinder.findTargetTowards(this.turtle, 16, 3, lv, 0.3141592741012573);
                if (lv2 == null) {
                    lv2 = TargetFinder.findTargetTowards(this.turtle, 8, 7, lv);
                }
                if (lv2 != null) {
                    int i = MathHelper.floor(lv2.x);
                    int j = MathHelper.floor(lv2.z);
                    int k = 34;
                    if (!this.turtle.world.isRegionLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                        lv2 = null;
                    }
                }
                if (lv2 == null) {
                    this.noPath = true;
                    return;
                }
                this.turtle.getNavigation().startMovingTo(lv2.x, lv2.y, lv2.z, this.speed);
            }
        }

        @Override
        public boolean shouldContinue() {
            return !this.turtle.getNavigation().isIdle() && !this.noPath && !this.turtle.isLandBound() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void stop() {
            this.turtle.setActivelyTravelling(false);
            super.stop();
        }
    }

    static class TurtleEscapeDangerGoal
    extends EscapeDangerGoal {
        TurtleEscapeDangerGoal(TurtleEntity arg, double d) {
            super(arg, d);
        }

        @Override
        public boolean canStart() {
            if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
                return false;
            }
            BlockPos lv = this.locateClosestWater(this.mob.world, this.mob, 7, 4);
            if (lv != null) {
                this.targetX = lv.getX();
                this.targetY = lv.getY();
                this.targetZ = lv.getZ();
                return true;
            }
            return this.findTarget();
        }
    }
}

