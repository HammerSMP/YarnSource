/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class EscapeDangerGoal
extends Goal {
    protected final MobEntityWithAi mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;

    public EscapeDangerGoal(MobEntityWithAi arg, double d) {
        this.mob = arg;
        this.speed = d;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        BlockPos lv;
        if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
            return false;
        }
        if (this.mob.isOnFire() && (lv = this.locateClosestWater(this.mob.world, this.mob, 5, 4)) != null) {
            this.targetX = lv.getX();
            this.targetY = lv.getY();
            this.targetZ = lv.getZ();
            return true;
        }
        return this.findTarget();
    }

    protected boolean findTarget() {
        Vec3d lv = TargetFinder.findTarget(this.mob, 5, 4);
        if (lv == null) {
            return false;
        }
        this.targetX = lv.x;
        this.targetY = lv.y;
        this.targetZ = lv.z;
        return true;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
    }

    @Override
    public void stop() {
        this.active = false;
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Nullable
    protected BlockPos locateClosestWater(BlockView arg, Entity arg2, int i, int j) {
        BlockPos lv = arg2.getBlockPos();
        int k = lv.getX();
        int l = lv.getY();
        int m = lv.getZ();
        float f = i * i * j * 2;
        BlockPos lv2 = null;
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int n = k - i; n <= k + i; ++n) {
            for (int o = l - j; o <= l + j; ++o) {
                for (int p = m - i; p <= m + i; ++p) {
                    float g;
                    lv3.set(n, o, p);
                    if (!arg.getFluidState(lv3).matches(FluidTags.WATER) || !((g = (float)((n - k) * (n - k) + (o - l) * (o - l) + (p - m) * (p - m))) < f)) continue;
                    f = g;
                    lv2 = new BlockPos(lv3);
                }
            }
        }
        return lv2;
    }
}

