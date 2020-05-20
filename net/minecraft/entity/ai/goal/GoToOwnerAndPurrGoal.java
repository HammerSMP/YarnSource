/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class GoToOwnerAndPurrGoal
extends MoveToTargetPosGoal {
    private final CatEntity cat;

    public GoToOwnerAndPurrGoal(CatEntity arg, double d, int i) {
        super(arg, d, i, 6);
        this.cat = arg;
        this.lowestY = -2;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.isSleepingWithOwner() && super.canStart();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    protected int getInterval(MobEntityWithAi arg) {
        return 40;
    }

    @Override
    public void stop() {
        super.stop();
        this.cat.setSleepingWithOwner(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.setInSittingPose(false);
        if (!this.hasReached()) {
            this.cat.setSleepingWithOwner(false);
        } else if (!this.cat.isSleepingWithOwner()) {
            this.cat.setSleepingWithOwner(true);
        }
    }

    @Override
    protected boolean isTargetPos(WorldView arg, BlockPos arg2) {
        return arg.isAir(arg2.up()) && arg.getBlockState(arg2).getBlock().isIn(BlockTags.BEDS);
    }
}

