/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlyOntoTreeGoal
extends WanderAroundFarGoal {
    public FlyOntoTreeGoal(PathAwareEntity arg, double d) {
        super(arg, d);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d lv = null;
        if (this.mob.isTouchingWater()) {
            lv = TargetFinder.findGroundTarget(this.mob, 15, 15);
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            lv = this.getTreeTarget();
        }
        return lv == null ? super.getWanderTarget() : lv;
    }

    @Nullable
    private Vec3d getTreeTarget() {
        BlockPos lv = this.mob.getBlockPos();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 3.0), MathHelper.floor(this.mob.getY() - 6.0), MathHelper.floor(this.mob.getZ() - 3.0), MathHelper.floor(this.mob.getX() + 3.0), MathHelper.floor(this.mob.getY() + 6.0), MathHelper.floor(this.mob.getZ() + 3.0));
        for (BlockPos lv4 : iterable) {
            Block lv5;
            boolean bl;
            if (lv.equals(lv4) || !(bl = (lv5 = this.mob.world.getBlockState(lv3.set(lv4, Direction.DOWN)).getBlock()) instanceof LeavesBlock || lv5.isIn(BlockTags.LOGS)) || !this.mob.world.isAir(lv4) || !this.mob.world.isAir(lv2.set(lv4, Direction.UP))) continue;
            return Vec3d.ofBottomCenter(lv4);
        }
        return null;
    }
}

