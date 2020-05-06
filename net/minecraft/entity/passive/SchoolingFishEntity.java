/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowGroupLeaderGoal;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class SchoolingFishEntity
extends FishEntity {
    private SchoolingFishEntity leader;
    private int groupSize = 1;

    public SchoolingFishEntity(EntityType<? extends SchoolingFishEntity> arg, World arg2) {
        super((EntityType<? extends FishEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(5, new FollowGroupLeaderGoal(this));
    }

    @Override
    public int getLimitPerChunk() {
        return this.getMaxGroupSize();
    }

    public int getMaxGroupSize() {
        return super.getLimitPerChunk();
    }

    @Override
    protected boolean hasSelfControl() {
        return !this.hasLeader();
    }

    public boolean hasLeader() {
        return this.leader != null && this.leader.isAlive();
    }

    public SchoolingFishEntity joinGroupOf(SchoolingFishEntity arg) {
        this.leader = arg;
        arg.increaseGroupSize();
        return arg;
    }

    public void leaveGroup() {
        this.leader.decreaseGroupSize();
        this.leader = null;
    }

    private void increaseGroupSize() {
        ++this.groupSize;
    }

    private void decreaseGroupSize() {
        --this.groupSize;
    }

    public boolean canHaveMoreFishInGroup() {
        return this.hasOtherFishInGroup() && this.groupSize < this.getMaxGroupSize();
    }

    @Override
    public void tick() {
        List<?> list;
        super.tick();
        if (this.hasOtherFishInGroup() && this.world.random.nextInt(200) == 1 && (list = this.world.getNonSpectatingEntities(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0))).size() <= 1) {
            this.groupSize = 1;
        }
    }

    public boolean hasOtherFishInGroup() {
        return this.groupSize > 1;
    }

    public boolean isCloseEnoughToLeader() {
        return this.squaredDistanceTo(this.leader) <= 121.0;
    }

    public void moveTowardLeader() {
        if (this.hasLeader()) {
            this.getNavigation().startMovingTo(this.leader, 1.0);
        }
    }

    public void pullInOtherFish(Stream<SchoolingFishEntity> stream) {
        stream.limit(this.getMaxGroupSize() - this.groupSize).filter(arg -> arg != this).forEach(arg -> arg.joinGroupOf(this));
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld arg, LocalDifficulty arg2, SpawnType arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        super.initialize(arg, arg2, arg3, arg4, arg5);
        if (arg4 == null) {
            arg4 = new FishData(this);
        } else {
            this.joinGroupOf(((FishData)arg4).leader);
        }
        return arg4;
    }

    public static class FishData
    implements EntityData {
        public final SchoolingFishEntity leader;

        public FishData(SchoolingFishEntity arg) {
            this.leader = arg;
        }
    }
}

