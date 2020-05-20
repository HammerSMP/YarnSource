/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class BeeEntity
extends AnimalEntity
implements Flutterer {
    private static final TrackedData<Byte> multipleByteTracker = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> anger = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private UUID targetPlayer;
    private float currentPitch;
    private float lastPitch;
    private int ticksSinceSting;
    private int ticksSincePollination;
    private int cannotEnterHiveTicks;
    private int cropsGrownSincePollination;
    private int ticksLeftToFindHive = 0;
    private int ticksUntilCanPollinate = 0;
    @Nullable
    private BlockPos flowerPos = null;
    @Nullable
    private BlockPos hivePos = null;
    private PollinateGoal pollinateGoal;
    private MoveToHiveGoal moveToHiveGoal;
    private MoveToFlowerGoal moveToFlowerGoal;
    private int ticksInsideWater;

    public BeeEntity(EntityType<? extends BeeEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.lookControl = new BeeLookControl(this);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0f);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(multipleByteTracker, (byte)0);
        this.dataTracker.startTracking(anger, 0);
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (arg2.getBlockState(arg).isAir()) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new StingGoal(this, 1.4f, true));
        this.goalSelector.add(1, new EnterHiveGoal());
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal((MobEntityWithAi)this, 1.25, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.pollinateGoal = new PollinateGoal();
        this.goalSelector.add(4, this.pollinateGoal);
        this.goalSelector.add(5, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new FindHiveGoal());
        this.moveToHiveGoal = new MoveToHiveGoal();
        this.goalSelector.add(5, this.moveToHiveGoal);
        this.moveToFlowerGoal = new MoveToFlowerGoal();
        this.goalSelector.add(6, this.moveToFlowerGoal);
        this.goalSelector.add(7, new GrowCropsGoal());
        this.goalSelector.add(8, new BeeWanderAroundGoal());
        this.goalSelector.add(9, new SwimGoal(this));
        this.targetSelector.add(1, new BeeRevengeGoal(this).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new BeeFollowTargetGoal(this));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.hasHive()) {
            arg.put("HivePos", NbtHelper.fromBlockPos(this.getHivePos()));
        }
        if (this.hasFlower()) {
            arg.put("FlowerPos", NbtHelper.fromBlockPos(this.getFlowerPos()));
        }
        arg.putBoolean("HasNectar", this.hasNectar());
        arg.putBoolean("HasStung", this.hasStung());
        arg.putInt("TicksSincePollination", this.ticksSincePollination);
        arg.putInt("CannotEnterHiveTicks", this.cannotEnterHiveTicks);
        arg.putInt("CropsGrownSincePollination", this.cropsGrownSincePollination);
        arg.putInt("Anger", this.getAnger());
        if (this.targetPlayer != null) {
            arg.putUuidNew("HurtBy", this.targetPlayer);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        this.hivePos = null;
        if (arg.contains("HivePos")) {
            this.hivePos = NbtHelper.toBlockPos(arg.getCompound("HivePos"));
        }
        this.flowerPos = null;
        if (arg.contains("FlowerPos")) {
            this.flowerPos = NbtHelper.toBlockPos(arg.getCompound("FlowerPos"));
        }
        super.readCustomDataFromTag(arg);
        this.setHasNectar(arg.getBoolean("HasNectar"));
        this.setHasStung(arg.getBoolean("HasStung"));
        this.setAnger(arg.getInt("Anger"));
        this.ticksSincePollination = arg.getInt("TicksSincePollination");
        this.cannotEnterHiveTicks = arg.getInt("CannotEnterHiveTicks");
        this.cropsGrownSincePollination = arg.getInt("CropsGrownSincePollination");
        if (arg.containsUuidNew("HurtBy")) {
            this.targetPlayer = arg.getUuidNew("HurtBy");
            PlayerEntity lv = this.world.getPlayerByUuid(this.targetPlayer);
            this.setAttacker(lv);
            if (lv != null) {
                this.attackingPlayer = lv;
                this.playerHitTimer = this.getLastAttackedTime();
            }
        }
    }

    @Override
    public boolean tryAttack(Entity arg) {
        boolean bl = arg.damage(DamageSource.sting(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.dealDamage(this, arg);
            if (arg instanceof LivingEntity) {
                ((LivingEntity)arg).setStingerCount(((LivingEntity)arg).getStingerCount() + 1);
                int i = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }
                if (i > 0) {
                    ((LivingEntity)arg).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, i * 20, 0));
                }
            }
            this.setHasStung(true);
            this.setTarget(null);
            this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05f) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.addParticle(this.world, this.getX() - (double)0.3f, this.getX() + (double)0.3f, this.getZ() - (double)0.3f, this.getZ() + (double)0.3f, this.getBodyY(0.5), ParticleTypes.FALLING_NECTAR);
            }
        }
        this.updateBodyPitch();
    }

    private void addParticle(World arg, double d, double e, double f, double g, double h, ParticleEffect arg2) {
        arg.addParticle(arg2, MathHelper.lerp(arg.random.nextDouble(), d, e), h, MathHelper.lerp(arg.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
    }

    private void startMovingTo(BlockPos arg) {
        Vec3d lv3;
        Vec3d lv = Vec3d.ofBottomCenter(arg);
        int i = 0;
        BlockPos lv2 = this.getBlockPos();
        int j = (int)lv.y - lv2.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }
        int k = 6;
        int l = 8;
        int m = lv2.getManhattanDistance(arg);
        if (m < 15) {
            k = m / 2;
            l = m / 2;
        }
        if ((lv3 = TargetFinder.findGroundTargetTowards(this, k, l, i, lv, 0.3141592741012573)) == null) {
            return;
        }
        this.navigation.setRangeMultiplier(0.5f);
        this.navigation.startMovingTo(lv3.x, lv3.y, lv3.z, 1.0);
    }

    @Nullable
    public BlockPos getFlowerPos() {
        return this.flowerPos;
    }

    public boolean hasFlower() {
        return this.flowerPos != null;
    }

    public void setFlowerPos(BlockPos arg) {
        this.flowerPos = arg;
    }

    private boolean failedPollinatingTooLong() {
        return this.ticksSincePollination > 3600;
    }

    private boolean canEnterHive() {
        if (this.cannotEnterHiveTicks > 0 || this.pollinateGoal.isRunning() || this.hasStung()) {
            return false;
        }
        boolean bl = this.failedPollinatingTooLong() || this.world.isRaining() || this.world.isNight() || this.hasNectar();
        return bl && !this.isHiveNearFire();
    }

    public void setCannotEnterHiveTicks(int i) {
        this.cannotEnterHiveTicks = i;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBodyPitch(float f) {
        return MathHelper.lerp(f, this.lastPitch, this.currentPitch);
    }

    private void updateBodyPitch() {
        this.lastPitch = this.currentPitch;
        this.currentPitch = this.isNearTarget() ? Math.min(1.0f, this.currentPitch + 0.2f) : Math.max(0.0f, this.currentPitch - 0.24f);
    }

    @Override
    public void setAttacker(@Nullable LivingEntity arg) {
        super.setAttacker(arg);
        if (arg != null) {
            this.targetPlayer = arg.getUuid();
        }
    }

    @Override
    protected void mobTick() {
        boolean bl = this.hasStung();
        this.ticksInsideWater = this.isInsideWaterOrBubbleColumn() ? ++this.ticksInsideWater : 0;
        if (this.ticksInsideWater > 20) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        if (bl) {
            ++this.ticksSinceSting;
            if (this.ticksSinceSting % 5 == 0 && this.random.nextInt(MathHelper.clamp(1200 - this.ticksSinceSting, 1, 1200)) == 0) {
                this.damage(DamageSource.GENERIC, this.getHealth());
            }
        }
        if (this.isAngry()) {
            int i = this.getAnger();
            this.setAnger(i - 1);
            LivingEntity lv = this.getTarget();
            if (i == 0 && lv != null) {
                this.setBeeAttacker(lv);
            }
        }
        if (!this.hasNectar()) {
            ++this.ticksSincePollination;
        }
    }

    public void resetPollinationTicks() {
        this.ticksSincePollination = 0;
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        }
        BlockEntity lv = this.world.getBlockEntity(this.hivePos);
        return lv instanceof BeehiveBlockEntity && ((BeehiveBlockEntity)lv).isNearFire();
    }

    public boolean isAngry() {
        return this.getAnger() > 0;
    }

    private int getAnger() {
        return this.dataTracker.get(anger);
    }

    private void setAnger(int i) {
        this.dataTracker.set(anger, i);
    }

    private boolean doesHiveHaveSpace(BlockPos arg) {
        BlockEntity lv = this.world.getBlockEntity(arg);
        if (lv instanceof BeehiveBlockEntity) {
            return !((BeehiveBlockEntity)lv).isFullOfBees();
        }
        return false;
    }

    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Nullable
    public BlockPos getHivePos() {
        return this.hivePos;
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBeeDebugData(this);
    }

    private int getCropsGrownSincePollination() {
        return this.cropsGrownSincePollination;
    }

    private void resetCropCounter() {
        this.cropsGrownSincePollination = 0;
    }

    private void addCropCounter() {
        ++this.cropsGrownSincePollination;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            if (this.cannotEnterHiveTicks > 0) {
                --this.cannotEnterHiveTicks;
            }
            if (this.ticksLeftToFindHive > 0) {
                --this.ticksLeftToFindHive;
            }
            if (this.ticksUntilCanPollinate > 0) {
                --this.ticksUntilCanPollinate;
            }
            boolean bl = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().squaredDistanceTo(this) < 4.0;
            this.setNearTarget(bl);
            if (this.age % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
        }
    }

    private boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        }
        BlockEntity lv = this.world.getBlockEntity(this.hivePos);
        return lv != null && lv.getType() == BlockEntityType.BEEHIVE;
    }

    public boolean hasNectar() {
        return this.getBeeFlag(8);
    }

    private void setHasNectar(boolean bl) {
        if (bl) {
            this.resetPollinationTicks();
        }
        this.setBeeFlag(8, bl);
    }

    public boolean hasStung() {
        return this.getBeeFlag(4);
    }

    private void setHasStung(boolean bl) {
        this.setBeeFlag(4, bl);
    }

    private boolean isNearTarget() {
        return this.getBeeFlag(2);
    }

    private void setNearTarget(boolean bl) {
        this.setBeeFlag(2, bl);
    }

    private boolean isTooFar(BlockPos arg) {
        return !this.isWithinDistance(arg, 32);
    }

    private void setBeeFlag(int i, boolean bl) {
        if (bl) {
            this.dataTracker.set(multipleByteTracker, (byte)(this.dataTracker.get(multipleByteTracker) | i));
        } else {
            this.dataTracker.set(multipleByteTracker, (byte)(this.dataTracker.get(multipleByteTracker) & ~i));
        }
    }

    private boolean getBeeFlag(int i) {
        return (this.dataTracker.get(multipleByteTracker) & i) != 0;
    }

    public static DefaultAttributeContainer.Builder createBeeAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        BirdNavigation lv = new BirdNavigation(this, arg){

            @Override
            public boolean isValidPosition(BlockPos arg) {
                return !this.world.getBlockState(arg.down()).isAir();
            }

            @Override
            public void tick() {
                if (BeeEntity.this.pollinateGoal.isRunning()) {
                    return;
                }
                super.tick();
            }
        };
        lv.setCanPathThroughDoors(false);
        lv.setCanSwim(false);
        lv.setCanEnterOpenDoors(true);
        return lv;
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return arg.getItem().isIn(ItemTags.FLOWERS);
    }

    private boolean isFlowers(BlockPos arg) {
        return this.world.canSetBlock(arg) && this.world.getBlockState(arg).getBlock().isIn(BlockTags.FLOWERS);
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public BeeEntity createChild(PassiveEntity arg) {
        return EntityType.BEE.create(this.world);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        if (this.isBaby()) {
            return arg2.height * 0.5f;
        }
        return arg2.height * 0.5f;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
    }

    @Override
    protected boolean hasWings() {
        return true;
    }

    public void onHoneyDelivered() {
        this.setHasNectar(false);
        this.resetCropCounter();
    }

    public boolean setBeeAttacker(Entity arg) {
        this.setAnger(400 + this.random.nextInt(400));
        if (arg instanceof LivingEntity) {
            this.setAttacker((LivingEntity)arg);
        }
        return true;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        Entity lv = arg.getAttacker();
        if (!this.world.isClient && lv instanceof PlayerEntity && !((PlayerEntity)lv).isCreative() && this.canSee(lv) && !this.isAiDisabled()) {
            this.pollinateGoal.cancel();
            this.setBeeAttacker(lv);
        }
        return super.damage(arg, f);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    protected void swimUpward(Tag<Fluid> arg) {
        this.setVelocity(this.getVelocity().add(0.0, 0.01, 0.0));
    }

    private boolean isWithinDistance(BlockPos arg, int i) {
        return arg.isWithinDistance(this.getBlockPos(), (double)i);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    class EnterHiveGoal
    extends NotAngryGoal {
        private EnterHiveGoal() {
        }

        @Override
        public boolean canBeeStart() {
            BlockEntity lv;
            if (BeeEntity.this.hasHive() && BeeEntity.this.canEnterHive() && BeeEntity.this.hivePos.isWithinDistance(BeeEntity.this.getPos(), 2.0) && (lv = BeeEntity.this.world.getBlockEntity(BeeEntity.this.hivePos)) instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity lv2 = (BeehiveBlockEntity)lv;
                if (lv2.isFullOfBees()) {
                    BeeEntity.this.hivePos = null;
                } else {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void start() {
            BlockEntity lv = BeeEntity.this.world.getBlockEntity(BeeEntity.this.hivePos);
            if (lv instanceof BeehiveBlockEntity) {
                BeehiveBlockEntity lv2 = (BeehiveBlockEntity)lv;
                lv2.tryEnterHive(BeeEntity.this, BeeEntity.this.hasNectar());
            }
        }
    }

    class StingGoal
    extends MeleeAttackGoal {
        StingGoal(MobEntityWithAi arg2, double d, boolean bl) {
            super(arg2, d, bl);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && BeeEntity.this.isAngry() && !BeeEntity.this.hasStung();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && BeeEntity.this.isAngry() && !BeeEntity.this.hasStung();
        }
    }

    class GrowCropsGoal
    extends NotAngryGoal {
        private GrowCropsGoal() {
        }

        @Override
        public boolean canBeeStart() {
            if (BeeEntity.this.getCropsGrownSincePollination() >= 10) {
                return false;
            }
            if (BeeEntity.this.random.nextFloat() < 0.3f) {
                return false;
            }
            return BeeEntity.this.hasNectar() && BeeEntity.this.isHiveValid();
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void tick() {
            if (BeeEntity.this.random.nextInt(30) != 0) {
                return;
            }
            for (int i = 1; i <= 2; ++i) {
                int k;
                BlockPos lv = BeeEntity.this.getBlockPos().down(i);
                BlockState lv2 = BeeEntity.this.world.getBlockState(lv);
                Block lv3 = lv2.getBlock();
                boolean bl = false;
                IntProperty lv4 = null;
                if (!lv3.isIn(BlockTags.BEE_GROWABLES)) continue;
                if (lv3 instanceof CropBlock) {
                    CropBlock lv5 = (CropBlock)lv3;
                    if (!lv5.isMature(lv2)) {
                        bl = true;
                        lv4 = lv5.getAgeProperty();
                    }
                } else if (lv3 instanceof StemBlock) {
                    int j = lv2.get(StemBlock.AGE);
                    if (j < 7) {
                        bl = true;
                        lv4 = StemBlock.AGE;
                    }
                } else if (lv3 == Blocks.SWEET_BERRY_BUSH && (k = lv2.get(SweetBerryBushBlock.AGE).intValue()) < 3) {
                    bl = true;
                    lv4 = SweetBerryBushBlock.AGE;
                }
                if (!bl) continue;
                BeeEntity.this.world.syncWorldEvent(2005, lv, 0);
                BeeEntity.this.world.setBlockState(lv, (BlockState)lv2.with(lv4, lv2.get(lv4) + 1));
                BeeEntity.this.addCropCounter();
            }
        }
    }

    class FindHiveGoal
    extends NotAngryGoal {
        private FindHiveGoal() {
        }

        @Override
        public boolean canBeeStart() {
            return BeeEntity.this.ticksLeftToFindHive == 0 && !BeeEntity.this.hasHive() && BeeEntity.this.canEnterHive();
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void start() {
            BeeEntity.this.ticksLeftToFindHive = 200;
            List<BlockPos> list = this.getNearbyFreeHives();
            if (list.isEmpty()) {
                return;
            }
            for (BlockPos lv : list) {
                if (BeeEntity.this.moveToHiveGoal.isPossibleHive(lv)) continue;
                BeeEntity.this.hivePos = lv;
                return;
            }
            BeeEntity.this.moveToHiveGoal.clearPossibleHives();
            BeeEntity.this.hivePos = list.get(0);
        }

        private List<BlockPos> getNearbyFreeHives() {
            BlockPos lv = BeeEntity.this.getBlockPos();
            PointOfInterestStorage lv2 = ((ServerWorld)BeeEntity.this.world).getPointOfInterestStorage();
            Stream<PointOfInterest> stream = lv2.getInCircle(arg -> arg == PointOfInterestType.BEEHIVE || arg == PointOfInterestType.BEE_NEST, lv, 20, PointOfInterestStorage.OccupationStatus.ANY);
            return stream.map(PointOfInterest::getPos).filter(arg2 -> BeeEntity.this.doesHiveHaveSpace(arg2)).sorted(Comparator.comparingDouble(arg2 -> arg2.getSquaredDistance(lv))).collect(Collectors.toList());
        }
    }

    class PollinateGoal
    extends NotAngryGoal {
        private final Predicate<BlockState> flowerPredicate;
        private int pollinationTicks;
        private int lastPollinationTick;
        private boolean running;
        private Vec3d nextTarget;
        private int ticks;

        PollinateGoal() {
            this.flowerPredicate = arg -> {
                if (arg.isIn(BlockTags.TALL_FLOWERS)) {
                    if (arg.isOf(Blocks.SUNFLOWER)) {
                        return arg.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER;
                    }
                    return true;
                }
                return arg.isIn(BlockTags.SMALL_FLOWERS);
            };
            this.pollinationTicks = 0;
            this.lastPollinationTick = 0;
            this.ticks = 0;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            if (BeeEntity.this.ticksUntilCanPollinate > 0) {
                return false;
            }
            if (BeeEntity.this.hasNectar()) {
                return false;
            }
            if (BeeEntity.this.world.isRaining()) {
                return false;
            }
            if (BeeEntity.this.random.nextFloat() < 0.7f) {
                return false;
            }
            Optional<BlockPos> optional = this.getFlower();
            if (optional.isPresent()) {
                BeeEntity.this.flowerPos = optional.get();
                BeeEntity.this.navigation.startMovingTo((double)BeeEntity.this.flowerPos.getX() + 0.5, (double)BeeEntity.this.flowerPos.getY() + 0.5, (double)BeeEntity.this.flowerPos.getZ() + 0.5, 1.2f);
                return true;
            }
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            if (!this.running) {
                return false;
            }
            if (!BeeEntity.this.hasFlower()) {
                return false;
            }
            if (BeeEntity.this.world.isRaining()) {
                return false;
            }
            if (this.completedPollination()) {
                return BeeEntity.this.random.nextFloat() < 0.2f;
            }
            if (BeeEntity.this.age % 20 == 0 && !BeeEntity.this.isFlowers(BeeEntity.this.flowerPos)) {
                BeeEntity.this.flowerPos = null;
                return false;
            }
            return true;
        }

        private boolean completedPollination() {
            return this.pollinationTicks > 400;
        }

        private boolean isRunning() {
            return this.running;
        }

        private void cancel() {
            this.running = false;
        }

        @Override
        public void start() {
            this.pollinationTicks = 0;
            this.ticks = 0;
            this.lastPollinationTick = 0;
            this.running = true;
            BeeEntity.this.resetPollinationTicks();
        }

        @Override
        public void stop() {
            if (this.completedPollination()) {
                BeeEntity.this.setHasNectar(true);
            }
            this.running = false;
            BeeEntity.this.navigation.stop();
            BeeEntity.this.ticksUntilCanPollinate = 200;
        }

        @Override
        public void tick() {
            ++this.ticks;
            if (this.ticks > 600) {
                BeeEntity.this.flowerPos = null;
                return;
            }
            Vec3d lv = Vec3d.ofBottomCenter(BeeEntity.this.flowerPos).add(0.0, 0.6f, 0.0);
            if (lv.distanceTo(BeeEntity.this.getPos()) > 1.0) {
                this.nextTarget = lv;
                this.moveToNextTarget();
                return;
            }
            if (this.nextTarget == null) {
                this.nextTarget = lv;
            }
            boolean bl = BeeEntity.this.getPos().distanceTo(this.nextTarget) <= 0.1;
            boolean bl2 = true;
            if (!bl && this.ticks > 600) {
                BeeEntity.this.flowerPos = null;
                return;
            }
            if (bl) {
                boolean bl3;
                boolean bl4 = bl3 = BeeEntity.this.random.nextInt(25) == 0;
                if (bl3) {
                    this.nextTarget = new Vec3d(lv.getX() + (double)this.getRandomOffset(), lv.getY(), lv.getZ() + (double)this.getRandomOffset());
                    BeeEntity.this.navigation.stop();
                } else {
                    bl2 = false;
                }
                BeeEntity.this.getLookControl().lookAt(lv.getX(), lv.getY(), lv.getZ());
            }
            if (bl2) {
                this.moveToNextTarget();
            }
            ++this.pollinationTicks;
            if (BeeEntity.this.random.nextFloat() < 0.05f && this.pollinationTicks > this.lastPollinationTick + 60) {
                this.lastPollinationTick = this.pollinationTicks;
                BeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0f, 1.0f);
            }
        }

        private void moveToNextTarget() {
            BeeEntity.this.getMoveControl().moveTo(this.nextTarget.getX(), this.nextTarget.getY(), this.nextTarget.getZ(), 0.35f);
        }

        private float getRandomOffset() {
            return (BeeEntity.this.random.nextFloat() * 2.0f - 1.0f) * 0.33333334f;
        }

        private Optional<BlockPos> getFlower() {
            return this.findFlower(this.flowerPredicate, 5.0);
        }

        private Optional<BlockPos> findFlower(Predicate<BlockState> predicate, double d) {
            BlockPos lv = BeeEntity.this.getBlockPos();
            BlockPos.Mutable lv2 = new BlockPos.Mutable();
            int i = 0;
            while ((double)i <= d) {
                int j = 0;
                while ((double)j < d) {
                    int k = 0;
                    while (k <= j) {
                        int l;
                        int n = l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            lv2.set(lv, k, i - 1, l);
                            if (lv.isWithinDistance(lv2, d) && predicate.test(BeeEntity.this.world.getBlockState(lv2))) {
                                return Optional.of(lv2);
                            }
                            l = l > 0 ? -l : 1 - l;
                        }
                        k = k > 0 ? -k : 1 - k;
                    }
                    ++j;
                }
                i = i > 0 ? -i : 1 - i;
            }
            return Optional.empty();
        }
    }

    class BeeLookControl
    extends LookControl {
        BeeLookControl(MobEntity arg2) {
            super(arg2);
        }

        @Override
        public void tick() {
            if (BeeEntity.this.isAngry()) {
                return;
            }
            super.tick();
        }

        @Override
        protected boolean shouldStayHorizontal() {
            return !BeeEntity.this.pollinateGoal.isRunning();
        }
    }

    public class MoveToFlowerGoal
    extends NotAngryGoal {
        private int ticks;

        MoveToFlowerGoal() {
            this.ticks = BeeEntity.this.world.random.nextInt(10);
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            return BeeEntity.this.flowerPos != null && !BeeEntity.this.hasPositionTarget() && this.shouldMoveToFlower() && BeeEntity.this.isFlowers(BeeEntity.this.flowerPos) && !BeeEntity.this.isWithinDistance(BeeEntity.this.flowerPos, 2);
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void start() {
            this.ticks = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.ticks = 0;
            BeeEntity.this.navigation.stop();
            BeeEntity.this.navigation.resetRangeMultiplier();
        }

        @Override
        public void tick() {
            if (BeeEntity.this.flowerPos == null) {
                return;
            }
            ++this.ticks;
            if (this.ticks > 600) {
                BeeEntity.this.flowerPos = null;
                return;
            }
            if (BeeEntity.this.navigation.isFollowingPath()) {
                return;
            }
            if (BeeEntity.this.isTooFar(BeeEntity.this.flowerPos)) {
                BeeEntity.this.flowerPos = null;
                return;
            }
            BeeEntity.this.startMovingTo(BeeEntity.this.flowerPos);
        }

        private boolean shouldMoveToFlower() {
            return BeeEntity.this.ticksSincePollination > 2400;
        }
    }

    public class MoveToHiveGoal
    extends NotAngryGoal {
        private int ticks;
        private List<BlockPos> possibleHives;
        @Nullable
        private Path path;
        private int ticksUntilLost;

        MoveToHiveGoal() {
            this.ticks = BeeEntity.this.world.random.nextInt(10);
            this.possibleHives = Lists.newArrayList();
            this.path = null;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            return BeeEntity.this.hivePos != null && !BeeEntity.this.hasPositionTarget() && BeeEntity.this.canEnterHive() && !this.isCloseEnough(BeeEntity.this.hivePos) && BeeEntity.this.world.getBlockState(BeeEntity.this.hivePos).isIn(BlockTags.BEEHIVES);
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void start() {
            this.ticks = 0;
            this.ticksUntilLost = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.ticks = 0;
            this.ticksUntilLost = 0;
            BeeEntity.this.navigation.stop();
            BeeEntity.this.navigation.resetRangeMultiplier();
        }

        @Override
        public void tick() {
            if (BeeEntity.this.hivePos == null) {
                return;
            }
            ++this.ticks;
            if (this.ticks > 600) {
                this.makeChosenHivePossibleHive();
                return;
            }
            if (BeeEntity.this.navigation.isFollowingPath()) {
                return;
            }
            if (BeeEntity.this.isWithinDistance(BeeEntity.this.hivePos, 16)) {
                boolean bl = this.startMovingToFar(BeeEntity.this.hivePos);
                if (!bl) {
                    this.makeChosenHivePossibleHive();
                } else if (this.path != null && BeeEntity.this.navigation.getCurrentPath().equalsPath(this.path)) {
                    ++this.ticksUntilLost;
                    if (this.ticksUntilLost > 60) {
                        this.setLost();
                        this.ticksUntilLost = 0;
                    }
                } else {
                    this.path = BeeEntity.this.navigation.getCurrentPath();
                }
                return;
            }
            if (BeeEntity.this.isTooFar(BeeEntity.this.hivePos)) {
                this.setLost();
                return;
            }
            BeeEntity.this.startMovingTo(BeeEntity.this.hivePos);
        }

        private boolean startMovingToFar(BlockPos arg) {
            BeeEntity.this.navigation.setRangeMultiplier(10.0f);
            BeeEntity.this.navigation.startMovingTo(arg.getX(), arg.getY(), arg.getZ(), 1.0);
            return BeeEntity.this.navigation.getCurrentPath() != null && BeeEntity.this.navigation.getCurrentPath().reachesTarget();
        }

        private boolean isPossibleHive(BlockPos arg) {
            return this.possibleHives.contains(arg);
        }

        private void addPossibleHive(BlockPos arg) {
            this.possibleHives.add(arg);
            while (this.possibleHives.size() > 3) {
                this.possibleHives.remove(0);
            }
        }

        private void clearPossibleHives() {
            this.possibleHives.clear();
        }

        private void makeChosenHivePossibleHive() {
            if (BeeEntity.this.hivePos != null) {
                this.addPossibleHive(BeeEntity.this.hivePos);
            }
            this.setLost();
        }

        private void setLost() {
            BeeEntity.this.hivePos = null;
            BeeEntity.this.ticksLeftToFindHive = 200;
        }

        private boolean isCloseEnough(BlockPos arg) {
            if (BeeEntity.this.isWithinDistance(arg, 2)) {
                return true;
            }
            Path lv = BeeEntity.this.navigation.getCurrentPath();
            return lv != null && lv.getTarget().equals(arg) && lv.reachesTarget() && lv.isFinished();
        }
    }

    class BeeWanderAroundGoal
    extends Goal {
        BeeWanderAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return BeeEntity.this.navigation.isIdle() && BeeEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return BeeEntity.this.navigation.isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d lv = this.getRandomLocation();
            if (lv != null) {
                BeeEntity.this.navigation.startMovingAlong(BeeEntity.this.navigation.findPathTo(new BlockPos(lv), 1), 1.0);
            }
        }

        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d lv3;
            if (BeeEntity.this.isHiveValid() && !BeeEntity.this.isWithinDistance(BeeEntity.this.hivePos, 22)) {
                Vec3d lv = Vec3d.ofCenter(BeeEntity.this.hivePos);
                Vec3d lv2 = lv.subtract(BeeEntity.this.getPos()).normalize();
            } else {
                lv3 = BeeEntity.this.getRotationVec(0.0f);
            }
            int i = 8;
            Vec3d lv4 = TargetFinder.findAirTarget(BeeEntity.this, 8, 7, lv3, 1.5707964f, 2, 1);
            if (lv4 != null) {
                return lv4;
            }
            return TargetFinder.findGroundTarget(BeeEntity.this, 8, 4, -2, lv3, 1.5707963705062866);
        }
    }

    abstract class NotAngryGoal
    extends Goal {
        private NotAngryGoal() {
        }

        public abstract boolean canBeeStart();

        public abstract boolean canBeeContinue();

        @Override
        public boolean canStart() {
            return this.canBeeStart() && !BeeEntity.this.isAngry();
        }

        @Override
        public boolean shouldContinue() {
            return this.canBeeContinue() && !BeeEntity.this.isAngry();
        }
    }

    static class BeeFollowTargetGoal
    extends FollowTargetGoal<PlayerEntity> {
        BeeFollowTargetGoal(BeeEntity arg) {
            super((MobEntity)arg, PlayerEntity.class, true);
        }

        @Override
        public boolean canStart() {
            return this.canSting() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            boolean bl = this.canSting();
            if (!bl || this.mob.getTarget() == null) {
                this.target = null;
                return false;
            }
            return super.shouldContinue();
        }

        private boolean canSting() {
            BeeEntity lv = (BeeEntity)this.mob;
            return lv.isAngry() && !lv.hasStung();
        }
    }

    class BeeRevengeGoal
    extends RevengeGoal {
        BeeRevengeGoal(BeeEntity arg2) {
            super(arg2, new Class[0]);
        }

        @Override
        protected void setMobEntityTarget(MobEntity arg, LivingEntity arg2) {
            if (arg instanceof BeeEntity && this.mob.canSee(arg2) && ((BeeEntity)arg).setBeeAttacker(arg2)) {
                arg.setTarget(arg2);
            }
        }
    }
}

