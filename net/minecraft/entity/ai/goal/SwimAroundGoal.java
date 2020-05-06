/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SwimAroundGoal
extends WanderAroundGoal {
    public SwimAroundGoal(MobEntityWithAi arg, double d, int i) {
        super(arg, d, i);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d lv = TargetFinder.findTarget(this.mob, 10, 7);
        int i = 0;
        while (lv != null && !this.mob.world.getBlockState(new BlockPos(lv)).canPathfindThrough(this.mob.world, new BlockPos(lv), NavigationType.WATER) && i++ < 10) {
            lv = TargetFinder.findTarget(this.mob, 10, 7);
        }
        return lv;
    }
}

