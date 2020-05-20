/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class AmphibiousPathNodeMaker
extends LandPathNodeMaker {
    private float oldWalkablePenalty;
    private float oldWaterBorderPenalty;

    @Override
    public void init(ChunkCache arg, MobEntity arg2) {
        super.init(arg, arg2);
        arg2.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.oldWalkablePenalty = arg2.getPathfindingPenalty(PathNodeType.WALKABLE);
        arg2.setPathfindingPenalty(PathNodeType.WALKABLE, 6.0f);
        this.oldWaterBorderPenalty = arg2.getPathfindingPenalty(PathNodeType.WATER_BORDER);
        arg2.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0f);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WALKABLE, this.oldWalkablePenalty);
        this.entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, this.oldWaterBorderPenalty);
        super.clear();
    }

    @Override
    public PathNode getStart() {
        return this.getNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }

    @Override
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(this.getNode(MathHelper.floor(d), MathHelper.floor(e + 0.5), MathHelper.floor(f)));
    }

    @Override
    public int getSuccessors(PathNode[] args, PathNode arg) {
        PathNode lv11;
        PathNode lv10;
        PathNode lv9;
        PathNode lv8;
        boolean bl4;
        int i = 0;
        boolean j = true;
        BlockPos lv = new BlockPos(arg.x, arg.y, arg.z);
        double d = this.getFeetY(lv);
        PathNode lv2 = this.getPathNode(arg.x, arg.y, arg.z + 1, 1, d);
        PathNode lv3 = this.getPathNode(arg.x - 1, arg.y, arg.z, 1, d);
        PathNode lv4 = this.getPathNode(arg.x + 1, arg.y, arg.z, 1, d);
        PathNode lv5 = this.getPathNode(arg.x, arg.y, arg.z - 1, 1, d);
        PathNode lv6 = this.getPathNode(arg.x, arg.y + 1, arg.z, 0, d);
        PathNode lv7 = this.getPathNode(arg.x, arg.y - 1, arg.z, 1, d);
        if (lv2 != null && !lv2.visited) {
            args[i++] = lv2;
        }
        if (lv3 != null && !lv3.visited) {
            args[i++] = lv3;
        }
        if (lv4 != null && !lv4.visited) {
            args[i++] = lv4;
        }
        if (lv5 != null && !lv5.visited) {
            args[i++] = lv5;
        }
        if (lv6 != null && !lv6.visited) {
            args[i++] = lv6;
        }
        if (lv7 != null && !lv7.visited) {
            args[i++] = lv7;
        }
        boolean bl = lv5 == null || lv5.type == PathNodeType.OPEN || lv5.penalty != 0.0f;
        boolean bl2 = lv2 == null || lv2.type == PathNodeType.OPEN || lv2.penalty != 0.0f;
        boolean bl3 = lv4 == null || lv4.type == PathNodeType.OPEN || lv4.penalty != 0.0f;
        boolean bl5 = bl4 = lv3 == null || lv3.type == PathNodeType.OPEN || lv3.penalty != 0.0f;
        if (bl && bl4 && (lv8 = this.getPathNode(arg.x - 1, arg.y, arg.z - 1, 1, d)) != null && !lv8.visited) {
            args[i++] = lv8;
        }
        if (bl && bl3 && (lv9 = this.getPathNode(arg.x + 1, arg.y, arg.z - 1, 1, d)) != null && !lv9.visited) {
            args[i++] = lv9;
        }
        if (bl2 && bl4 && (lv10 = this.getPathNode(arg.x - 1, arg.y, arg.z + 1, 1, d)) != null && !lv10.visited) {
            args[i++] = lv10;
        }
        if (bl2 && bl3 && (lv11 = this.getPathNode(arg.x + 1, arg.y, arg.z + 1, 1, d)) != null && !lv11.visited) {
            args[i++] = lv11;
        }
        return i;
    }

    private double getFeetY(BlockPos arg) {
        if (!this.entity.isTouchingWater()) {
            VoxelShape lv2;
            BlockPos lv;
            return (double)lv.getY() + ((lv2 = this.cachedWorld.getBlockState(lv = arg.down()).getCollisionShape(this.cachedWorld, lv)).isEmpty() ? 0.0 : lv2.getMaximum(Direction.Axis.Y));
        }
        return (double)arg.getY() + 0.5;
    }

    @Nullable
    private PathNode getPathNode(int i, int j, int k, int l, double d) {
        PathNode lv = null;
        BlockPos lv2 = new BlockPos(i, j, k);
        double e = this.getFeetY(lv2);
        if (e - d > 1.125) {
            return null;
        }
        PathNodeType lv3 = this.getNodeType(this.cachedWorld, i, j, k, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
        float f = this.entity.getPathfindingPenalty(lv3);
        double g = (double)this.entity.getWidth() / 2.0;
        if (f >= 0.0f) {
            lv = this.getNode(i, j, k);
            lv.type = lv3;
            lv.penalty = Math.max(lv.penalty, f);
        }
        if (lv3 == PathNodeType.WATER || lv3 == PathNodeType.WALKABLE) {
            if (j < this.entity.world.getSeaLevel() - 10 && lv != null) {
                lv.penalty += 1.0f;
            }
            return lv;
        }
        if (lv == null && l > 0 && lv3 != PathNodeType.FENCE && lv3 != PathNodeType.TRAPDOOR) {
            lv = this.getPathNode(i, j + 1, k, l - 1, d);
        }
        if (lv3 == PathNodeType.OPEN) {
            Box lv4 = new Box((double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (float)j + this.entity.getHeight(), (double)k + g + 0.5);
            if (!this.entity.world.doesNotCollide(this.entity, lv4)) {
                return null;
            }
            PathNodeType lv5 = this.getNodeType(this.cachedWorld, i, j - 1, k, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
            if (lv5 == PathNodeType.BLOCKED) {
                lv = this.getNode(i, j, k);
                lv.type = PathNodeType.WALKABLE;
                lv.penalty = Math.max(lv.penalty, f);
                return lv;
            }
            if (lv5 == PathNodeType.WATER) {
                lv = this.getNode(i, j, k);
                lv.type = PathNodeType.WATER;
                lv.penalty = Math.max(lv.penalty, f);
                return lv;
            }
            int m = 0;
            while (j > 0 && lv3 == PathNodeType.OPEN) {
                --j;
                if (m++ >= this.entity.getSafeFallDistance()) {
                    return null;
                }
                lv3 = this.getNodeType(this.cachedWorld, i, j, k, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
                f = this.entity.getPathfindingPenalty(lv3);
                if (lv3 != PathNodeType.OPEN && f >= 0.0f) {
                    lv = this.getNode(i, j, k);
                    lv.type = lv3;
                    lv.penalty = Math.max(lv.penalty, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                return null;
            }
        }
        return lv;
    }

    @Override
    protected PathNodeType adjustNodeType(BlockView arg, boolean bl, boolean bl2, BlockPos arg2, PathNodeType arg3) {
        if (arg3 == PathNodeType.RAIL && !(arg.getBlockState(arg2).getBlock() instanceof AbstractRailBlock) && !(arg.getBlockState(arg2.down()).getBlock() instanceof AbstractRailBlock)) {
            arg3 = PathNodeType.FENCE;
        }
        if (arg3 == PathNodeType.DOOR_OPEN || arg3 == PathNodeType.DOOR_WOOD_CLOSED || arg3 == PathNodeType.DOOR_IRON_CLOSED) {
            arg3 = PathNodeType.BLOCKED;
        }
        if (arg3 == PathNodeType.LEAVES) {
            arg3 = PathNodeType.BLOCKED;
        }
        return arg3;
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView arg, int i, int j, int k) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        PathNodeType lv2 = AmphibiousPathNodeMaker.getCommonNodeType(arg, lv.set(i, j, k));
        if (lv2 == PathNodeType.WATER) {
            for (Direction lv3 : Direction.values()) {
                PathNodeType lv4 = AmphibiousPathNodeMaker.getCommonNodeType(arg, lv.set(i, j, k).move(lv3));
                if (lv4 != PathNodeType.BLOCKED) continue;
                return PathNodeType.WATER_BORDER;
            }
            return PathNodeType.WATER;
        }
        if (lv2 == PathNodeType.OPEN && j >= 1) {
            BlockState lv5 = arg.getBlockState(new BlockPos(i, j - 1, k));
            PathNodeType lv6 = AmphibiousPathNodeMaker.getCommonNodeType(arg, lv.set(i, j - 1, k));
            lv2 = lv6 == PathNodeType.WALKABLE || lv6 == PathNodeType.OPEN || lv6 == PathNodeType.LAVA ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            if (lv6 == PathNodeType.DAMAGE_FIRE || lv5.isOf(Blocks.MAGMA_BLOCK) || lv5.isIn(BlockTags.CAMPFIRES)) {
                lv2 = PathNodeType.DAMAGE_FIRE;
            }
            if (lv6 == PathNodeType.DAMAGE_CACTUS) {
                lv2 = PathNodeType.DAMAGE_CACTUS;
            }
            if (lv6 == PathNodeType.DAMAGE_OTHER) {
                lv2 = PathNodeType.DAMAGE_OTHER;
            }
        }
        if (lv2 == PathNodeType.WALKABLE) {
            lv2 = AmphibiousPathNodeMaker.getNodeTypeFromNeighbors(arg, lv.set(i, j, k), lv2);
        }
        return lv2;
    }
}

