/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class MoveToTargetPosGoal
extends Goal {
    protected final PathAwareEntity mob;
    public final double speed;
    protected int cooldown;
    protected int tryingTime;
    private int safeWaitingTime;
    protected BlockPos targetPos = BlockPos.ORIGIN;
    private boolean reached;
    private final int range;
    private final int maxYDifference;
    protected int lowestY;

    public MoveToTargetPosGoal(PathAwareEntity arg, double d, int i) {
        this(arg, d, i, 1);
    }

    public MoveToTargetPosGoal(PathAwareEntity arg, double d, int i, int j) {
        this.mob = arg;
        this.speed = d;
        this.range = i;
        this.lowestY = 0;
        this.maxYDifference = j;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = this.getInterval(this.mob);
        return this.findTargetPos();
    }

    protected int getInterval(PathAwareEntity arg) {
        return 200 + arg.getRandom().nextInt(200);
    }

    @Override
    public boolean shouldContinue() {
        return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.mob.world, this.targetPos);
    }

    @Override
    public void start() {
        this.startMovingToTarget();
        this.tryingTime = 0;
        this.safeWaitingTime = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
    }

    protected void startMovingToTarget() {
        this.mob.getNavigation().startMovingTo((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, this.speed);
    }

    public double getDesiredSquaredDistanceToTarget() {
        return 1.0;
    }

    @Override
    public void tick() {
        if (!this.targetPos.up().isWithinDistance(this.mob.getPos(), this.getDesiredSquaredDistanceToTarget())) {
            this.reached = false;
            ++this.tryingTime;
            if (this.shouldResetPath()) {
                this.mob.getNavigation().startMovingTo((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, this.speed);
            }
        } else {
            this.reached = true;
            --this.tryingTime;
        }
    }

    public boolean shouldResetPath() {
        return this.tryingTime % 40 == 0;
    }

    protected boolean hasReached() {
        return this.reached;
    }

    protected boolean findTargetPos() {
        int i = this.range;
        int j = this.maxYDifference;
        BlockPos lv = this.mob.getBlockPos();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        int k = this.lowestY;
        while (k <= j) {
            for (int l = 0; l < i; ++l) {
                int m = 0;
                while (m <= l) {
                    int n;
                    int n2 = n = m < l && m > -l ? l : 0;
                    while (n <= l) {
                        lv2.set(lv, m, k - 1, n);
                        if (this.mob.isInWalkTargetRange(lv2) && this.isTargetPos(this.mob.world, lv2)) {
                            this.targetPos = lv2;
                            return true;
                        }
                        n = n > 0 ? -n : 1 - n;
                    }
                    m = m > 0 ? -m : 1 - m;
                }
            }
            k = k > 0 ? -k : 1 - k;
        }
        return false;
    }

    protected abstract boolean isTargetPos(WorldView var1, BlockPos var2);
}

