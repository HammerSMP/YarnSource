/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class MoveIntoWaterGoal
extends Goal {
    private final MobEntityWithAi mob;

    public MoveIntoWaterGoal(MobEntityWithAi arg) {
        this.mob = arg;
    }

    @Override
    public boolean canStart() {
        return this.mob.isOnGround() && !this.mob.world.getFluidState(this.mob.getBlockPos()).isIn(FluidTags.WATER);
    }

    @Override
    public void start() {
        Vec3i lv = null;
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 2.0), MathHelper.floor(this.mob.getY() - 2.0), MathHelper.floor(this.mob.getZ() - 2.0), MathHelper.floor(this.mob.getX() + 2.0), MathHelper.floor(this.mob.getY()), MathHelper.floor(this.mob.getZ() + 2.0));
        for (BlockPos lv2 : iterable) {
            if (!this.mob.world.getFluidState(lv2).isIn(FluidTags.WATER)) continue;
            lv = lv2;
            break;
        }
        if (lv != null) {
            this.mob.getMoveControl().moveTo(lv.getX(), lv.getY(), lv.getZ(), 1.0);
        }
    }
}

