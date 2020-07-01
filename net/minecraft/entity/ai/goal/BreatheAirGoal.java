/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;

public class BreatheAirGoal
extends Goal {
    private final PathAwareEntity mob;

    public BreatheAirGoal(PathAwareEntity arg) {
        this.mob = arg;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.mob.getAir() < 140;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        this.moveToAir();
    }

    private void moveToAir() {
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 1.0), MathHelper.floor(this.mob.getY()), MathHelper.floor(this.mob.getZ() - 1.0), MathHelper.floor(this.mob.getX() + 1.0), MathHelper.floor(this.mob.getY() + 8.0), MathHelper.floor(this.mob.getZ() + 1.0));
        Vec3i lv = null;
        for (BlockPos lv2 : iterable) {
            if (!this.isAirPos(this.mob.world, lv2)) continue;
            lv = lv2;
            break;
        }
        if (lv == null) {
            lv = new BlockPos(this.mob.getX(), this.mob.getY() + 8.0, this.mob.getZ());
        }
        this.mob.getNavigation().startMovingTo(lv.getX(), lv.getY() + 1, lv.getZ(), 1.0);
    }

    @Override
    public void tick() {
        this.moveToAir();
        this.mob.updateVelocity(0.02f, new Vec3d(this.mob.sidewaysSpeed, this.mob.upwardSpeed, this.mob.forwardSpeed));
        this.mob.move(MovementType.SELF, this.mob.getVelocity());
    }

    private boolean isAirPos(WorldView arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        return (arg.getFluidState(arg2).isEmpty() || lv.isOf(Blocks.BUBBLE_COLUMN)) && lv.canPathfindThrough(arg, arg2, NavigationType.LAND);
    }
}

