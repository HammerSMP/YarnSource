/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class WaterPathNodeMaker
extends PathNodeMaker {
    private final boolean canJumpOutOfWater;

    public WaterPathNodeMaker(boolean bl) {
        this.canJumpOutOfWater = bl;
    }

    @Override
    public PathNode getStart() {
        return super.getNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }

    @Override
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(super.getNode(MathHelper.floor(d - (double)(this.entity.getWidth() / 2.0f)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.entity.getWidth() / 2.0f))));
    }

    @Override
    public int getSuccessors(PathNode[] args, PathNode arg) {
        int i = 0;
        for (Direction lv : Direction.values()) {
            PathNode lv2 = this.getPathNodeInWater(arg.x + lv.getOffsetX(), arg.y + lv.getOffsetY(), arg.z + lv.getOffsetZ());
            if (lv2 == null || lv2.visited) continue;
            args[i++] = lv2;
        }
        return i;
    }

    @Override
    public PathNodeType getNodeType(BlockView arg, int i, int j, int k, MobEntity arg2, int l, int m, int n, boolean bl, boolean bl2) {
        return this.getDefaultNodeType(arg, i, j, k);
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView arg, int i, int j, int k) {
        BlockPos lv = new BlockPos(i, j, k);
        FluidState lv2 = arg.getFluidState(lv);
        BlockState lv3 = arg.getBlockState(lv);
        if (lv2.isEmpty() && lv3.canPathfindThrough(arg, lv.down(), NavigationType.WATER) && lv3.isAir()) {
            return PathNodeType.BREACH;
        }
        if (!lv2.isIn(FluidTags.WATER) || !lv3.canPathfindThrough(arg, lv, NavigationType.WATER)) {
            return PathNodeType.BLOCKED;
        }
        return PathNodeType.WATER;
    }

    @Nullable
    private PathNode getPathNodeInWater(int i, int j, int k) {
        PathNodeType lv = this.getNodeType(i, j, k);
        if (this.canJumpOutOfWater && lv == PathNodeType.BREACH || lv == PathNodeType.WATER) {
            return this.getNode(i, j, k);
        }
        return null;
    }

    @Override
    @Nullable
    protected PathNode getNode(int i, int j, int k) {
        PathNode lv = null;
        PathNodeType lv2 = this.getDefaultNodeType(this.entity.world, i, j, k);
        float f = this.entity.getPathfindingPenalty(lv2);
        if (f >= 0.0f) {
            lv = super.getNode(i, j, k);
            lv.type = lv2;
            lv.penalty = Math.max(lv.penalty, f);
            if (this.cachedWorld.getFluidState(new BlockPos(i, j, k)).isEmpty()) {
                lv.penalty += 8.0f;
            }
        }
        if (lv2 == PathNodeType.OPEN) {
            return lv;
        }
        return lv;
    }

    private PathNodeType getNodeType(int i, int j, int k) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = i; l < i + this.entityBlockXSize; ++l) {
            for (int m = j; m < j + this.entityBlockYSize; ++m) {
                for (int n = k; n < k + this.entityBlockZSize; ++n) {
                    FluidState lv2 = this.cachedWorld.getFluidState(lv.set(l, m, n));
                    BlockState lv3 = this.cachedWorld.getBlockState(lv.set(l, m, n));
                    if (lv2.isEmpty() && lv3.canPathfindThrough(this.cachedWorld, (BlockPos)lv.down(), NavigationType.WATER) && lv3.isAir()) {
                        return PathNodeType.BREACH;
                    }
                    if (lv2.isIn(FluidTags.WATER)) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        BlockState lv4 = this.cachedWorld.getBlockState(lv);
        if (lv4.canPathfindThrough(this.cachedWorld, lv, NavigationType.WATER)) {
            return PathNodeType.WATER;
        }
        return PathNodeType.BLOCKED;
    }
}

