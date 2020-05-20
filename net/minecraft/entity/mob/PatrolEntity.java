/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class PatrolEntity
extends HostileEntity {
    private BlockPos patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;

    protected PatrolEntity(EntityType<? extends PatrolEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(4, new PatrolGoal<PatrolEntity>(this, 0.7, 0.595));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (this.patrolTarget != null) {
            arg.put("PatrolTarget", NbtHelper.fromBlockPos(this.patrolTarget));
        }
        arg.putBoolean("PatrolLeader", this.patrolLeader);
        arg.putBoolean("Patrolling", this.patrolling);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("PatrolTarget")) {
            this.patrolTarget = NbtHelper.toBlockPos(arg.getCompound("PatrolTarget"));
        }
        this.patrolLeader = arg.getBoolean("PatrolLeader");
        this.patrolling = arg.getBoolean("Patrolling");
    }

    @Override
    public double getHeightOffset() {
        return -0.45;
    }

    public boolean canLead() {
        return true;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg3 != SpawnReason.PATROL && arg3 != SpawnReason.EVENT && arg3 != SpawnReason.STRUCTURE && this.random.nextFloat() < 0.06f && this.canLead()) {
            this.patrolLeader = true;
        }
        if (this.isPatrolLeader()) {
            this.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
            this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
        }
        if (arg3 == SpawnReason.PATROL) {
            this.patrolling = true;
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public static boolean canSpawn(EntityType<? extends PatrolEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        if (arg2.getLightLevel(LightType.BLOCK, arg4) > 8) {
            return false;
        }
        return PatrolEntity.canSpawnIgnoreLightLevel(arg, arg2, arg3, arg4, random);
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.patrolling || d > 16384.0;
    }

    public void setPatrolTarget(BlockPos arg) {
        this.patrolTarget = arg;
        this.patrolling = true;
    }

    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }

    public boolean hasPatrolTarget() {
        return this.patrolTarget != null;
    }

    public void setPatrolLeader(boolean bl) {
        this.patrolLeader = bl;
        this.patrolling = true;
    }

    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }

    public boolean hasNoRaid() {
        return true;
    }

    public void setRandomPatrolTarget() {
        this.patrolTarget = this.getBlockPos().add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }

    protected boolean isRaidCenterSet() {
        return this.patrolling;
    }

    protected void setPatrolling(boolean bl) {
        this.patrolling = bl;
    }

    public static class PatrolGoal<T extends PatrolEntity>
    extends Goal {
        private final T entity;
        private final double leaderSpeed;
        private final double followSpeed;
        private long nextPatrolSearchTime;

        public PatrolGoal(T arg, double d, double e) {
            this.entity = arg;
            this.leaderSpeed = d;
            this.followSpeed = e;
            this.nextPatrolSearchTime = -1L;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            boolean bl = ((PatrolEntity)this.entity).world.getTime() < this.nextPatrolSearchTime;
            return ((PatrolEntity)this.entity).isRaidCenterSet() && ((MobEntity)this.entity).getTarget() == null && !((Entity)this.entity).hasPassengers() && ((PatrolEntity)this.entity).hasPatrolTarget() && !bl;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            boolean bl = ((PatrolEntity)this.entity).isPatrolLeader();
            EntityNavigation lv = ((MobEntity)this.entity).getNavigation();
            if (lv.isIdle()) {
                List<PatrolEntity> list = this.findPatrolTargets();
                if (((PatrolEntity)this.entity).isRaidCenterSet() && list.isEmpty()) {
                    ((PatrolEntity)this.entity).setPatrolling(false);
                } else if (!bl || !((PatrolEntity)this.entity).getPatrolTarget().isWithinDistance(((Entity)this.entity).getPos(), 10.0)) {
                    Vec3d lv2 = Vec3d.ofBottomCenter(((PatrolEntity)this.entity).getPatrolTarget());
                    Vec3d lv3 = ((Entity)this.entity).getPos();
                    Vec3d lv4 = lv3.subtract(lv2);
                    lv2 = lv4.rotateY(90.0f).multiply(0.4).add(lv2);
                    Vec3d lv5 = lv2.subtract(lv3).normalize().multiply(10.0).add(lv3);
                    BlockPos lv6 = new BlockPos(lv5);
                    if (!lv.startMovingTo((lv6 = ((PatrolEntity)this.entity).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv6)).getX(), lv6.getY(), lv6.getZ(), bl ? this.followSpeed : this.leaderSpeed)) {
                        this.wander();
                        this.nextPatrolSearchTime = ((PatrolEntity)this.entity).world.getTime() + 200L;
                    } else if (bl) {
                        for (PatrolEntity lv7 : list) {
                            lv7.setPatrolTarget(lv6);
                        }
                    }
                } else {
                    ((PatrolEntity)this.entity).setRandomPatrolTarget();
                }
            }
        }

        private List<PatrolEntity> findPatrolTargets() {
            return ((PatrolEntity)this.entity).world.getEntities(PatrolEntity.class, ((Entity)this.entity).getBoundingBox().expand(16.0), arg -> arg.hasNoRaid() && !arg.isPartOf((Entity)this.entity));
        }

        private boolean wander() {
            Random random = ((LivingEntity)this.entity).getRandom();
            BlockPos lv = ((PatrolEntity)this.entity).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ((Entity)this.entity).getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
            return ((MobEntity)this.entity).getNavigation().startMovingTo(lv.getX(), lv.getY(), lv.getZ(), this.leaderSpeed);
        }
    }
}

