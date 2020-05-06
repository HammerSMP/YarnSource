/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal
extends Goal {
    private final SchoolingFishEntity fish;
    private int moveDelay;
    private int checkSurroundingDelay;

    public FollowGroupLeaderGoal(SchoolingFishEntity arg) {
        this.fish = arg;
        this.checkSurroundingDelay = this.getSurroundingSearchDelay(arg);
    }

    protected int getSurroundingSearchDelay(SchoolingFishEntity arg) {
        return 200 + arg.getRandom().nextInt(200) % 20;
    }

    @Override
    public boolean canStart() {
        if (this.fish.hasOtherFishInGroup()) {
            return false;
        }
        if (this.fish.hasLeader()) {
            return true;
        }
        if (this.checkSurroundingDelay > 0) {
            --this.checkSurroundingDelay;
            return false;
        }
        this.checkSurroundingDelay = this.getSurroundingSearchDelay(this.fish);
        Predicate<SchoolingFishEntity> predicate = arg -> arg.canHaveMoreFishInGroup() || !arg.hasLeader();
        List<SchoolingFishEntity> list = this.fish.world.getEntities(this.fish.getClass(), this.fish.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
        SchoolingFishEntity lv = list.stream().filter(SchoolingFishEntity::canHaveMoreFishInGroup).findAny().orElse(this.fish);
        lv.pullInOtherFish(list.stream().filter(arg -> !arg.hasLeader()));
        return this.fish.hasLeader();
    }

    @Override
    public boolean shouldContinue() {
        return this.fish.hasLeader() && this.fish.isCloseEnoughToLeader();
    }

    @Override
    public void start() {
        this.moveDelay = 0;
    }

    @Override
    public void stop() {
        this.fish.leaveGroup();
    }

    @Override
    public void tick() {
        if (--this.moveDelay > 0) {
            return;
        }
        this.moveDelay = 10;
        this.fish.moveTowardLeader();
    }
}

