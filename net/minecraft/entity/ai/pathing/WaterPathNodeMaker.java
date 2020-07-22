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

    public WaterPathNodeMaker(boolean canJumpOutOfWater) {
        this.canJumpOutOfWater = canJumpOutOfWater;
    }

    @Override
    public PathNode getStart() {
        return super.getNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }

    @Override
    public TargetPathNode getNode(double x, double y, double z) {
        return new TargetPathNode(super.getNode(MathHelper.floor(x - (double)(this.entity.getWidth() / 2.0f)), MathHelper.floor(y + 0.5), MathHelper.floor(z - (double)(this.entity.getWidth() / 2.0f))));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        int i = 0;
        for (Direction lv : Direction.values()) {
            PathNode lv2 = this.getPathNodeInWater(node.x + lv.getOffsetX(), node.y + lv.getOffsetY(), node.z + lv.getOffsetZ());
            if (lv2 == null || lv2.visited) continue;
            successors[i++] = lv2;
        }
        return i;
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
        return this.getDefaultNodeType(world, x, y, z);
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        BlockPos lv = new BlockPos(x, y, z);
        FluidState lv2 = world.getFluidState(lv);
        BlockState lv3 = world.getBlockState(lv);
        if (lv2.isEmpty() && lv3.canPathfindThrough(world, lv.down(), NavigationType.WATER) && lv3.isAir()) {
            return PathNodeType.BREACH;
        }
        if (!lv2.isIn(FluidTags.WATER) || !lv3.canPathfindThrough(world, lv, NavigationType.WATER)) {
            return PathNodeType.BLOCKED;
        }
        return PathNodeType.WATER;
    }

    @Nullable
    private PathNode getPathNodeInWater(int x, int y, int z) {
        PathNodeType lv = this.getNodeType(x, y, z);
        if (this.canJumpOutOfWater && lv == PathNodeType.BREACH || lv == PathNodeType.WATER) {
            return this.getNode(x, y, z);
        }
        return null;
    }

    @Override
    @Nullable
    protected PathNode getNode(int x, int y, int z) {
        PathNode lv = null;
        PathNodeType lv2 = this.getDefaultNodeType(this.entity.world, x, y, z);
        float f = this.entity.getPathfindingPenalty(lv2);
        if (f >= 0.0f) {
            lv = super.getNode(x, y, z);
            lv.type = lv2;
            lv.penalty = Math.max(lv.penalty, f);
            if (this.cachedWorld.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
                lv.penalty += 8.0f;
            }
        }
        if (lv2 == PathNodeType.OPEN) {
            return lv;
        }
        return lv;
    }

    private PathNodeType getNodeType(int x, int y, int z) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = x; l < x + this.entityBlockXSize; ++l) {
            for (int m = y; m < y + this.entityBlockYSize; ++m) {
                for (int n = z; n < z + this.entityBlockZSize; ++n) {
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

