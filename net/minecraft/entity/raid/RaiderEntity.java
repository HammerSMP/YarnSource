/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaidManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public abstract class RaiderEntity
extends PatrolEntity {
    protected static final TrackedData<Boolean> CELEBRATING = DataTracker.registerData(RaiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<ItemEntity> OBTAINABLE_OMINOUS_BANNER_PREDICATE = arg -> !arg.cannotPickup() && arg.isAlive() && ItemStack.areEqual(arg.getStack(), Raid.getOminousBanner());
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean ableToJoinRaid;
    private int outOfRaidCounter;

    protected RaiderEntity(EntityType<? extends RaiderEntity> arg, World arg2) {
        super((EntityType<? extends PatrolEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new PickupBannerAsLeaderGoal(this, this));
        this.goalSelector.add(3, new MoveToRaidCenterGoal<RaiderEntity>(this));
        this.goalSelector.add(4, new AttackHomeGoal(this, 1.05f, 1));
        this.goalSelector.add(5, new CelebrateGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CELEBRATING, false);
    }

    public abstract void addBonusForWave(int var1, boolean var2);

    public boolean canJoinRaid() {
        return this.ableToJoinRaid;
    }

    public void setAbleToJoinRaid(boolean bl) {
        this.ableToJoinRaid = bl;
    }

    @Override
    public void tickMovement() {
        if (this.world instanceof ServerWorld && this.isAlive()) {
            Raid lv = this.getRaid();
            if (this.canJoinRaid()) {
                if (lv == null) {
                    Raid lv2;
                    if (this.world.getTime() % 20L == 0L && (lv2 = ((ServerWorld)this.world).getRaidAt(this.getBlockPos())) != null && RaidManager.isValidRaiderFor(this, lv2)) {
                        lv2.addRaider(lv2.getGroupsSpawned(), this, null, true);
                    }
                } else {
                    LivingEntity lv3 = this.getTarget();
                    if (lv3 != null && (lv3.getType() == EntityType.PLAYER || lv3.getType() == EntityType.IRON_GOLEM)) {
                        this.despawnCounter = 0;
                    }
                }
            }
        }
        super.tickMovement();
    }

    @Override
    protected void updateDespawnCounter() {
        this.despawnCounter += 2;
    }

    @Override
    public void onDeath(DamageSource arg) {
        if (this.world instanceof ServerWorld) {
            Entity lv = arg.getAttacker();
            Raid lv2 = this.getRaid();
            if (lv2 != null) {
                if (this.isPatrolLeader()) {
                    lv2.removeLeader(this.getWave());
                }
                if (lv != null && lv.getType() == EntityType.PLAYER) {
                    lv2.addHero(lv);
                }
                lv2.removeFromWave(this, false);
            }
            if (this.isPatrolLeader() && lv2 == null && ((ServerWorld)this.world).getRaidAt(this.getBlockPos()) == null) {
                ItemStack lv3 = this.getEquippedStack(EquipmentSlot.HEAD);
                PlayerEntity lv4 = null;
                Entity lv5 = lv;
                if (lv5 instanceof PlayerEntity) {
                    lv4 = (PlayerEntity)lv5;
                } else if (lv5 instanceof WolfEntity) {
                    WolfEntity lv6 = (WolfEntity)lv5;
                    LivingEntity lv7 = lv6.getOwner();
                    if (lv6.isTamed() && lv7 instanceof PlayerEntity) {
                        lv4 = (PlayerEntity)lv7;
                    }
                }
                if (!lv3.isEmpty() && ItemStack.areEqual(lv3, Raid.getOminousBanner()) && lv4 != null) {
                    StatusEffectInstance lv8 = lv4.getStatusEffect(StatusEffects.BAD_OMEN);
                    int i = 1;
                    if (lv8 != null) {
                        i += lv8.getAmplifier();
                        lv4.removeStatusEffectInternal(StatusEffects.BAD_OMEN);
                    } else {
                        --i;
                    }
                    i = MathHelper.clamp(i, 0, 4);
                    StatusEffectInstance lv9 = new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, i, false, false, true);
                    if (!this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                        lv4.addStatusEffect(lv9);
                    }
                }
            }
        }
        super.onDeath(arg);
    }

    @Override
    public boolean hasNoRaid() {
        return !this.hasActiveRaid();
    }

    public void setRaid(@Nullable Raid arg) {
        this.raid = arg;
    }

    @Nullable
    public Raid getRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getRaid() != null && this.getRaid().isActive();
    }

    public void setWave(int i) {
        this.wave = i;
    }

    public int getWave() {
        return this.wave;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCelebrating() {
        return this.dataTracker.get(CELEBRATING);
    }

    public void setCelebrating(boolean bl) {
        this.dataTracker.set(CELEBRATING, bl);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Wave", this.wave);
        arg.putBoolean("CanJoinRaid", this.ableToJoinRaid);
        if (this.raid != null) {
            arg.putInt("RaidId", this.raid.getRaidId());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.wave = arg.getInt("Wave");
        this.ableToJoinRaid = arg.getBoolean("CanJoinRaid");
        if (arg.contains("RaidId", 3)) {
            if (this.world instanceof ServerWorld) {
                this.raid = ((ServerWorld)this.world).getRaidManager().getRaid(arg.getInt("RaidId"));
            }
            if (this.raid != null) {
                this.raid.addToWave(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setWaveCaptain(this.wave, this);
                }
            }
        }
    }

    @Override
    protected void loot(ItemEntity arg) {
        boolean bl;
        ItemStack lv = arg.getStack();
        boolean bl2 = bl = this.hasActiveRaid() && this.getRaid().getCaptain(this.getWave()) != null;
        if (this.hasActiveRaid() && !bl && ItemStack.areEqual(lv, Raid.getOminousBanner())) {
            EquipmentSlot lv2 = EquipmentSlot.HEAD;
            ItemStack lv3 = this.getEquippedStack(lv2);
            double d = this.getDropChance(lv2);
            if (!lv3.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.dropStack(lv3);
            }
            this.equipStack(lv2, lv);
            this.sendPickup(arg, lv.getCount());
            arg.remove();
            this.getRaid().setWaveCaptain(this.getWave(), this);
            this.setPatrolLeader(true);
        } else {
            super.loot(arg);
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        if (this.getRaid() == null) {
            return super.canImmediatelyDespawn(d);
        }
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return this.getRaid() != null;
    }

    public int getOutOfRaidCounter() {
        return this.outOfRaidCounter;
    }

    public void setOutOfRaidCounter(int i) {
        this.outOfRaidCounter = i;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.hasActiveRaid()) {
            this.getRaid().updateBar();
        }
        return super.damage(arg, f);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setAbleToJoinRaid(this.getType() != EntityType.WITCH || arg3 != SpawnType.NATURAL);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public abstract SoundEvent getCelebratingSound();

    static class AttackHomeGoal
    extends Goal {
        private final RaiderEntity raider;
        private final double speed;
        private BlockPos home;
        private final List<BlockPos> lastHomes = Lists.newArrayList();
        private final int distance;
        private boolean finished;

        public AttackHomeGoal(RaiderEntity arg, double d, int i) {
            this.raider = arg;
            this.speed = d;
            this.distance = i;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            this.purgeMemory();
            return this.isRaiding() && this.tryFindHome() && this.raider.getTarget() == null;
        }

        private boolean isRaiding() {
            return this.raider.hasActiveRaid() && !this.raider.getRaid().isFinished();
        }

        private boolean tryFindHome() {
            ServerWorld lv = (ServerWorld)this.raider.world;
            BlockPos lv2 = this.raider.getBlockPos();
            Optional<BlockPos> optional = lv.getPointOfInterestStorage().getPosition(arg -> arg == PointOfInterestType.HOME, this::canLootHome, PointOfInterestStorage.OccupationStatus.ANY, lv2, 48, this.raider.random);
            if (!optional.isPresent()) {
                return false;
            }
            this.home = optional.get().toImmutable();
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (this.raider.getNavigation().isIdle()) {
                return false;
            }
            return this.raider.getTarget() == null && !this.home.isWithinDistance(this.raider.getPos(), (double)(this.raider.getWidth() + (float)this.distance)) && !this.finished;
        }

        @Override
        public void stop() {
            if (this.home.isWithinDistance(this.raider.getPos(), (double)this.distance)) {
                this.lastHomes.add(this.home);
            }
        }

        @Override
        public void start() {
            super.start();
            this.raider.setDespawnCounter(0);
            this.raider.getNavigation().startMovingTo(this.home.getX(), this.home.getY(), this.home.getZ(), this.speed);
            this.finished = false;
        }

        @Override
        public void tick() {
            if (this.raider.getNavigation().isIdle()) {
                Vec3d lv = Vec3d.method_24955(this.home);
                Vec3d lv2 = TargetFinder.findTargetTowards(this.raider, 16, 7, lv, 0.3141592741012573);
                if (lv2 == null) {
                    lv2 = TargetFinder.findTargetTowards(this.raider, 8, 7, lv);
                }
                if (lv2 == null) {
                    this.finished = true;
                    return;
                }
                this.raider.getNavigation().startMovingTo(lv2.x, lv2.y, lv2.z, this.speed);
            }
        }

        private boolean canLootHome(BlockPos arg) {
            for (BlockPos lv : this.lastHomes) {
                if (!Objects.equals(arg, lv)) continue;
                return false;
            }
            return true;
        }

        private void purgeMemory() {
            if (this.lastHomes.size() > 2) {
                this.lastHomes.remove(0);
            }
        }
    }

    public class PatrolApproachGoal
    extends Goal {
        private final RaiderEntity raider;
        private final float squaredDistance;
        public final TargetPredicate closeRaiderPredicate = new TargetPredicate().setBaseMaxDistance(8.0).ignoreEntityTargetRules().includeInvulnerable().includeTeammates().includeHidden().ignoreDistanceScalingFactor();

        public PatrolApproachGoal(IllagerEntity arg2, float f) {
            this.raider = arg2;
            this.squaredDistance = f * f;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity lv = this.raider.getAttacker();
            return this.raider.getRaid() == null && this.raider.isRaidCenterSet() && this.raider.getTarget() != null && !this.raider.isAttacking() && (lv == null || lv.getType() != EntityType.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.raider.getNavigation().stop();
            List<RaiderEntity> list = this.raider.world.getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
            for (RaiderEntity lv : list) {
                lv.setTarget(this.raider.getTarget());
            }
        }

        @Override
        public void stop() {
            super.stop();
            LivingEntity lv = this.raider.getTarget();
            if (lv != null) {
                List<RaiderEntity> list = this.raider.world.getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
                for (RaiderEntity lv2 : list) {
                    lv2.setTarget(lv);
                    lv2.setAttacking(true);
                }
                this.raider.setAttacking(true);
            }
        }

        @Override
        public void tick() {
            LivingEntity lv = this.raider.getTarget();
            if (lv == null) {
                return;
            }
            if (this.raider.squaredDistanceTo(lv) > (double)this.squaredDistance) {
                this.raider.getLookControl().lookAt(lv, 30.0f, 30.0f);
                if (this.raider.random.nextInt(50) == 0) {
                    this.raider.playAmbientSound();
                }
            } else {
                this.raider.setAttacking(true);
            }
            super.tick();
        }
    }

    public class CelebrateGoal
    extends Goal {
        private final RaiderEntity raider;

        CelebrateGoal(RaiderEntity arg2) {
            this.raider = arg2;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            Raid lv = this.raider.getRaid();
            return this.raider.isAlive() && this.raider.getTarget() == null && lv != null && lv.hasLost();
        }

        @Override
        public void start() {
            this.raider.setCelebrating(true);
            super.start();
        }

        @Override
        public void stop() {
            this.raider.setCelebrating(false);
            super.stop();
        }

        @Override
        public void tick() {
            if (!this.raider.isSilent() && this.raider.random.nextInt(100) == 0) {
                RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound(), RaiderEntity.this.getSoundVolume(), RaiderEntity.this.getSoundPitch());
            }
            if (!this.raider.hasVehicle() && this.raider.random.nextInt(50) == 0) {
                this.raider.getJumpControl().setActive();
            }
            super.tick();
        }
    }

    public static class PickupBannerAsLeaderGoal<T extends RaiderEntity>
    extends Goal {
        private final T actor;
        final /* synthetic */ RaiderEntity field_16604;

        public PickupBannerAsLeaderGoal(T arg2) {
            this.field_16604 = arg;
            this.actor = arg2;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            List<ItemEntity> list;
            Raid lv = ((RaiderEntity)this.actor).getRaid();
            if (!((RaiderEntity)this.actor).hasActiveRaid() || ((RaiderEntity)this.actor).getRaid().isFinished() || !((PatrolEntity)this.actor).canLead() || ItemStack.areEqual(((MobEntity)this.actor).getEquippedStack(EquipmentSlot.HEAD), Raid.getOminousBanner())) {
                return false;
            }
            RaiderEntity lv2 = lv.getCaptain(((RaiderEntity)this.actor).getWave());
            if (!(lv2 != null && lv2.isAlive() || (list = ((RaiderEntity)this.actor).world.getEntities(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0, 8.0, 16.0), OBTAINABLE_OMINOUS_BANNER_PREDICATE)).isEmpty())) {
                return ((MobEntity)this.actor).getNavigation().startMovingTo(list.get(0), 1.15f);
            }
            return false;
        }

        @Override
        public void tick() {
            List<ItemEntity> list;
            if (((MobEntity)this.actor).getNavigation().getTargetPos().isWithinDistance(((Entity)this.actor).getPos(), 1.414) && !(list = ((RaiderEntity)this.actor).world.getEntities(ItemEntity.class, ((Entity)this.actor).getBoundingBox().expand(4.0, 4.0, 4.0), OBTAINABLE_OMINOUS_BANNER_PREDICATE)).isEmpty()) {
                ((RaiderEntity)this.actor).loot(list.get(0));
            }
        }
    }
}

