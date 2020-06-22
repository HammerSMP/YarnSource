/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.DolphinLookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class DolphinEntity
extends WaterCreatureEntity {
    private static final TrackedData<BlockPos> TREASURE_POS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> HAS_FISH = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MOISTNESS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeTeammates().includeInvulnerable().includeHidden();
    public static final Predicate<ItemEntity> CAN_TAKE = arg -> !arg.cannotPickup() && arg.isAlive() && arg.isTouchingWater();

    public DolphinEntity(EntityType<? extends DolphinEntity> arg, World arg2) {
        super((EntityType<? extends WaterCreatureEntity>)arg, arg2);
        this.moveControl = new DolphinMoveControl(this);
        this.lookControl = new DolphinLookControl(this, 10);
        this.setCanPickUpLoot(true);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setAir(this.getMaxAir());
        this.pitch = 0.0f;
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public boolean canBreatheInWater() {
        return false;
    }

    @Override
    protected void tickWaterBreathingAir(int i) {
    }

    public void setTreasurePos(BlockPos arg) {
        this.dataTracker.set(TREASURE_POS, arg);
    }

    public BlockPos getTreasurePos() {
        return this.dataTracker.get(TREASURE_POS);
    }

    public boolean hasFish() {
        return this.dataTracker.get(HAS_FISH);
    }

    public void setHasFish(boolean bl) {
        this.dataTracker.set(HAS_FISH, bl);
    }

    public int getMoistness() {
        return this.dataTracker.get(MOISTNESS);
    }

    public void setMoistness(int i) {
        this.dataTracker.set(MOISTNESS, i);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TREASURE_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(HAS_FISH, false);
        this.dataTracker.startTracking(MOISTNESS, 2400);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("TreasurePosX", this.getTreasurePos().getX());
        arg.putInt("TreasurePosY", this.getTreasurePos().getY());
        arg.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        arg.putBoolean("GotFish", this.hasFish());
        arg.putInt("Moistness", this.getMoistness());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        int i = arg.getInt("TreasurePosX");
        int j = arg.getInt("TreasurePosY");
        int k = arg.getInt("TreasurePosZ");
        this.setTreasurePos(new BlockPos(i, j, k));
        super.readCustomDataFromTag(arg);
        this.setHasFish(arg.getBoolean("GotFish"));
        this.setMoistness(arg.getInt("Moistness"));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BreatheAirGoal(this));
        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(1, new LeadToNearbyTreasureGoal(this));
        this.goalSelector.add(2, new SwimWithPlayerGoal(this, 4.0));
        this.goalSelector.add(4, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.add(6, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.add(8, new PlayWithItemsGoal());
        this.goalSelector.add(8, new ChaseBoatGoal(this));
        this.goalSelector.add(9, new FleeEntityGoal<GuardianEntity>(this, GuardianEntity.class, 8.0f, 1.0, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, GuardianEntity.class).setGroupRevenge(new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createDolphinAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        return new SwimNavigation(this, arg);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        boolean bl = arg.damage(DamageSource.mob(this), (int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.dealDamage(this, arg);
            this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public int getMaxAir() {
        return 4800;
    }

    @Override
    protected int getNextAirOnLand(int i) {
        return this.getMaxAir();
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 0.3f;
    }

    @Override
    public int getLookPitchSpeed() {
        return 1;
    }

    @Override
    public int getBodyYawSpeed() {
        return 1;
    }

    @Override
    protected boolean canStartRiding(Entity arg) {
        return true;
    }

    @Override
    public boolean canPickUp(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        if (!this.getEquippedStack(lv).isEmpty()) {
            return false;
        }
        return lv == EquipmentSlot.MAINHAND && super.canPickUp(arg);
    }

    @Override
    protected void loot(ItemEntity arg) {
        ItemStack lv;
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && this.canPickupItem(lv = arg.getStack())) {
            this.method_29499(arg);
            this.equipStack(EquipmentSlot.MAINHAND, lv);
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(arg, lv.getCount());
            arg.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAiDisabled()) {
            this.setAir(this.getMaxAir());
            return;
        }
        if (this.isWet()) {
            this.setMoistness(2400);
        } else {
            this.setMoistness(this.getMoistness() - 1);
            if (this.getMoistness() <= 0) {
                this.damage(DamageSource.DRYOUT, 1.0f);
            }
            if (this.onGround) {
                this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.2f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f));
                this.yaw = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.velocityDirty = true;
            }
        }
        if (this.world.isClient && this.isTouchingWater() && this.getVelocity().lengthSquared() > 0.03) {
            Vec3d lv = this.getRotationVec(0.0f);
            float f = MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * 0.3f;
            float g = MathHelper.sin(this.yaw * ((float)Math.PI / 180)) * 0.3f;
            float h = 1.2f - this.random.nextFloat() * 0.7f;
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.DOLPHIN, this.getX() - lv.x * (double)h + (double)f, this.getY() - lv.y, this.getZ() - lv.z * (double)h + (double)g, 0.0, 0.0, 0.0);
                this.world.addParticle(ParticleTypes.DOLPHIN, this.getX() - lv.x * (double)h - (double)f, this.getY() - lv.y, this.getZ() - lv.z * (double)h - (double)g, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 38) {
            this.spawnParticlesAround(ParticleTypes.HAPPY_VILLAGER);
        } else {
            super.handleStatus(b);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void spawnParticlesAround(ParticleEffect arg) {
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.01;
            double e = this.random.nextGaussian() * 0.01;
            double f = this.random.nextGaussian() * 0.01;
            this.world.addParticle(arg, this.getParticleX(1.0), this.getRandomBodyY() + 0.2, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (!lv.isEmpty() && lv.getItem().isIn(ItemTags.FISHES)) {
            if (!this.world.isClient) {
                this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0f, 1.0f);
            }
            this.setHasFish(true);
            if (!arg.abilities.creativeMode) {
                lv.decrement(1);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(arg, arg2);
    }

    public static boolean canSpawn(EntityType<DolphinEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return arg4.getY() > 45 && arg4.getY() < arg2.getSeaLevel() && (arg2.getBiome(arg4) != Biomes.OCEAN || arg2.getBiome(arg4) != Biomes.DEEP_OCEAN) && arg2.getFluidState(arg4).isIn(FluidTags.WATER);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_DOLPHIN_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isTouchingWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DOLPHIN_SWIM;
    }

    protected boolean isNearTarget() {
        BlockPos lv = this.getNavigation().getTargetPos();
        if (lv != null) {
            return lv.isWithinDistance(this.getPos(), 12.0);
        }
        return false;
    }

    @Override
    public void travel(Vec3d arg) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), arg);
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
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return true;
    }

    static class LeadToNearbyTreasureGoal
    extends Goal {
        private final DolphinEntity dolphin;
        private boolean noPathToStructure;

        LeadToNearbyTreasureGoal(DolphinEntity arg) {
            this.dolphin = arg;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public boolean canStart() {
            return this.dolphin.hasFish() && this.dolphin.getAir() >= 100;
        }

        @Override
        public boolean shouldContinue() {
            BlockPos lv = this.dolphin.getTreasurePos();
            return !new BlockPos((double)lv.getX(), this.dolphin.getY(), (double)lv.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0) && !this.noPathToStructure && this.dolphin.getAir() >= 100;
        }

        /*
         * Enabled aggressive block sorting
         */
        @Override
        public void start() {
            if (!(this.dolphin.world instanceof ServerWorld)) {
                return;
            }
            ServerWorld lv = (ServerWorld)this.dolphin.world;
            this.noPathToStructure = false;
            this.dolphin.getNavigation().stop();
            BlockPos lv2 = this.dolphin.getBlockPos();
            StructureFeature<FeatureConfig> lv3 = (double)lv.random.nextFloat() >= 0.5 ? StructureFeature.OCEAN_RUIN : StructureFeature.SHIPWRECK;
            BlockPos lv4 = lv.locateStructure(lv3, lv2, 50, false);
            if (lv4 == null) {
                StructureFeature<FeatureConfig> structureFeature = lv3.equals(StructureFeature.OCEAN_RUIN) ? StructureFeature.SHIPWRECK : StructureFeature.OCEAN_RUIN;
                BlockPos lv6 = lv.locateStructure(structureFeature, lv2, 50, false);
                if (lv6 == null) {
                    this.noPathToStructure = true;
                    return;
                }
                this.dolphin.setTreasurePos(lv6);
            } else {
                this.dolphin.setTreasurePos(lv4);
            }
            lv.sendEntityStatus(this.dolphin, (byte)38);
        }

        @Override
        public void stop() {
            BlockPos lv = this.dolphin.getTreasurePos();
            if (new BlockPos((double)lv.getX(), this.dolphin.getY(), (double)lv.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0) || this.noPathToStructure) {
                this.dolphin.setHasFish(false);
            }
        }

        @Override
        public void tick() {
            World lv = this.dolphin.world;
            if (this.dolphin.isNearTarget() || this.dolphin.getNavigation().isIdle()) {
                BlockPos lv4;
                Vec3d lv2 = Vec3d.ofCenter(this.dolphin.getTreasurePos());
                Vec3d lv3 = TargetFinder.findTargetTowards(this.dolphin, 16, 1, lv2, 0.3926991f);
                if (lv3 == null) {
                    lv3 = TargetFinder.findTargetTowards(this.dolphin, 8, 4, lv2);
                }
                if (!(lv3 == null || lv.getFluidState(lv4 = new BlockPos(lv3)).isIn(FluidTags.WATER) && lv.getBlockState(lv4).canPathfindThrough(lv, lv4, NavigationType.WATER))) {
                    lv3 = TargetFinder.findTargetTowards(this.dolphin, 8, 5, lv2);
                }
                if (lv3 == null) {
                    this.noPathToStructure = true;
                    return;
                }
                this.dolphin.getLookControl().lookAt(lv3.x, lv3.y, lv3.z, this.dolphin.getBodyYawSpeed() + 20, this.dolphin.getLookPitchSpeed());
                this.dolphin.getNavigation().startMovingTo(lv3.x, lv3.y, lv3.z, 1.3);
                if (lv.random.nextInt(80) == 0) {
                    lv.sendEntityStatus(this.dolphin, (byte)38);
                }
            }
        }
    }

    static class SwimWithPlayerGoal
    extends Goal {
        private final DolphinEntity dolphin;
        private final double speed;
        private PlayerEntity closestPlayer;

        SwimWithPlayerGoal(DolphinEntity arg, double d) {
            this.dolphin = arg;
            this.speed = d;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.closestPlayer = this.dolphin.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this.dolphin);
            if (this.closestPlayer == null) {
                return false;
            }
            return this.closestPlayer.isSwimming() && this.dolphin.getTarget() != this.closestPlayer;
        }

        @Override
        public boolean shouldContinue() {
            return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.dolphin.squaredDistanceTo(this.closestPlayer) < 256.0;
        }

        @Override
        public void start() {
            this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100));
        }

        @Override
        public void stop() {
            this.closestPlayer = null;
            this.dolphin.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.dolphin.getLookControl().lookAt(this.closestPlayer, this.dolphin.getBodyYawSpeed() + 20, this.dolphin.getLookPitchSpeed());
            if (this.dolphin.squaredDistanceTo(this.closestPlayer) < 6.25) {
                this.dolphin.getNavigation().stop();
            } else {
                this.dolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
            }
            if (this.closestPlayer.isSwimming() && this.closestPlayer.world.random.nextInt(6) == 0) {
                this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100));
            }
        }
    }

    class PlayWithItemsGoal
    extends Goal {
        private int field_6758;

        private PlayWithItemsGoal() {
        }

        @Override
        public boolean canStart() {
            if (this.field_6758 > DolphinEntity.this.age) {
                return false;
            }
            List<ItemEntity> list = DolphinEntity.this.world.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
            return !list.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public void start() {
            List<ItemEntity> list = DolphinEntity.this.world.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
            if (!list.isEmpty()) {
                DolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
                DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0f, 1.0f);
            }
            this.field_6758 = 0;
        }

        @Override
        public void stop() {
            ItemStack lv = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!lv.isEmpty()) {
                this.spitOutItem(lv);
                DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.field_6758 = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = DolphinEntity.this.world.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
            ItemStack lv = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!lv.isEmpty()) {
                this.spitOutItem(lv);
                DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                DolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            }
        }

        private void spitOutItem(ItemStack arg) {
            if (arg.isEmpty()) {
                return;
            }
            double d = DolphinEntity.this.getEyeY() - (double)0.3f;
            ItemEntity lv = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.getX(), d, DolphinEntity.this.getZ(), arg);
            lv.setPickupDelay(40);
            lv.setThrower(DolphinEntity.this.getUuid());
            float f = 0.3f;
            float g = DolphinEntity.this.random.nextFloat() * ((float)Math.PI * 2);
            float h = 0.02f * DolphinEntity.this.random.nextFloat();
            lv.setVelocity(0.3f * -MathHelper.sin(DolphinEntity.this.yaw * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.pitch * ((float)Math.PI / 180)) + MathHelper.cos(g) * h, 0.3f * MathHelper.sin(DolphinEntity.this.pitch * ((float)Math.PI / 180)) * 1.5f, 0.3f * MathHelper.cos(DolphinEntity.this.yaw * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.pitch * ((float)Math.PI / 180)) + MathHelper.sin(g) * h);
            DolphinEntity.this.world.spawnEntity(lv);
        }
    }

    static class DolphinMoveControl
    extends MoveControl {
        private final DolphinEntity dolphin;

        public DolphinMoveControl(DolphinEntity arg) {
            super(arg);
            this.dolphin = arg;
        }

        @Override
        public void tick() {
            double f;
            double e;
            if (this.dolphin.isTouchingWater()) {
                this.dolphin.setVelocity(this.dolphin.getVelocity().add(0.0, 0.005, 0.0));
            }
            if (this.state != MoveControl.State.MOVE_TO || this.dolphin.getNavigation().isIdle()) {
                this.dolphin.setMovementSpeed(0.0f);
                this.dolphin.setSidewaysSpeed(0.0f);
                this.dolphin.setUpwardSpeed(0.0f);
                this.dolphin.setForwardSpeed(0.0f);
                return;
            }
            double d = this.targetX - this.dolphin.getX();
            double g = d * d + (e = this.targetY - this.dolphin.getY()) * e + (f = this.targetZ - this.dolphin.getZ()) * f;
            if (g < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
            this.dolphin.bodyYaw = this.dolphin.yaw = this.changeAngle(this.dolphin.yaw, h, 10.0f);
            this.dolphin.headYaw = this.dolphin.yaw;
            float i = (float)(this.speed * this.dolphin.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            if (this.dolphin.isTouchingWater()) {
                this.dolphin.setMovementSpeed(i * 0.02f);
                float j = -((float)(MathHelper.atan2(e, MathHelper.sqrt(d * d + f * f)) * 57.2957763671875));
                j = MathHelper.clamp(MathHelper.wrapDegrees(j), -85.0f, 85.0f);
                this.dolphin.pitch = this.changeAngle(this.dolphin.pitch, j, 5.0f);
                float k = MathHelper.cos(this.dolphin.pitch * ((float)Math.PI / 180));
                float l = MathHelper.sin(this.dolphin.pitch * ((float)Math.PI / 180));
                this.dolphin.forwardSpeed = k * i;
                this.dolphin.upwardSpeed = -l * i;
            } else {
                this.dolphin.setMovementSpeed(i * 0.1f);
            }
        }
    }
}

