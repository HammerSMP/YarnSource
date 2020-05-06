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
import net.minecraft.entity.SpawnType;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

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
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg3 != SpawnType.PATROL && arg3 != SpawnType.EVENT && arg3 != SpawnType.STRUCTURE && this.random.nextFloat() < 0.06f && this.canLead()) {
            this.patrolLeader = true;
        }
        if (this.isPatrolLeader()) {
            this.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
            this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
        }
        if (arg3 == SpawnType.PATROL) {
            this.patrolling = true;
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public static boolean canSpawn(EntityType<? extends PatrolEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
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
        private final T actor;
        private final double leaderSpeed;
        private final double fellowSpeed;
        private long field_20701;

        public PatrolGoal(T arg, double d, double e) {
            this.actor = arg;
            this.leaderSpeed = d;
            this.fellowSpeed = e;
            this.field_20701 = -1L;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            boolean bl = ((PatrolEntity)this.actor).world.getTime() < this.field_20701;
            return ((PatrolEntity)this.actor).isRaidCenterSet() && ((MobEntity)this.actor).getTarget() == null && !((Entity)this.actor).hasPassengers() && ((PatrolEntity)this.actor).hasPatrolTarget() && !bl;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            boolean bl = ((PatrolEntity)this.actor).isPatrolLeader();
            EntityNavigation lv = ((MobEntity)this.actor).getNavigation();
            if (lv.isIdle()) {
                List<PatrolEntity> list = this.method_22333();
                if (((PatrolEntity)this.actor).isRaidCenterSet() && list.isEmpty()) {
                    ((PatrolEntity)this.actor).setPatrolling(false);
                } else if (!bl || !((PatrolEntity)this.actor).getPatrolTarget().isWithinDistance(((Entity)this.actor).getPos(), 10.0)) {
                    Vec3d lv2 = Vec3d.method_24955(((PatrolEntity)this.actor).getPatrolTarget());
                    Vec3d lv3 = ((Entity)this.actor).getPos();
                    Vec3d lv4 = lv3.subtract(lv2);
                    lv2 = lv4.rotateY(90.0f).multiply(0.4).add(lv2);
                    Vec3d lv5 = lv2.subtract(lv3).normalize().multiply(10.0).add(lv3);
                    BlockPos lv6 = new BlockPos(lv5);
                    if (!lv.startMovingTo((lv6 = ((PatrolEntity)this.actor).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv6)).getX(), lv6.getY(), lv6.getZ(), bl ? this.fellowSpeed : this.leaderSpeed)) {
                        this.wander();
                        this.field_20701 = ((PatrolEntity)this.actor).world.getTime() + 200L;
                    } else if (bl) {
                        for (PatrolEntity lv7 : list) {
                            lv7.setPatrolTarget(lv6);
                        }
                    }
                } else {
                    ((PatrolEntity)this.actor).setRandomPatrolTarget();
                }
            }
        }

        private List<PatrolEntity> method_22333() {
            return ((PatrolEntity)this.actor).world.getEntities(PatrolEntity.class, ((Entity)this.actor).getBoundingBox().expand(16.0), arg -> arg.hasNoRaid() && !arg.isPartOf((Entity)this.actor));
        }

        private boolean wander() {
            Random random = ((LivingEntity)this.actor).getRandom();
            BlockPos lv = ((PatrolEntity)this.actor).world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ((Entity)this.actor).getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
            return ((MobEntity)this.actor).getNavigation().startMovingTo(lv.getX(), lv.getY(), lv.getZ(), this.leaderSpeed);
        }
    }
}

