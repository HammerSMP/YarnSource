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
    public void init(ChunkCache cachedWorld, MobEntity entity) {
        super.init(cachedWorld, entity);
        entity.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        this.oldWalkablePenalty = entity.getPathfindingPenalty(PathNodeType.WALKABLE);
        entity.setPathfindingPenalty(PathNodeType.WALKABLE, 6.0f);
        this.oldWaterBorderPenalty = entity.getPathfindingPenalty(PathNodeType.WATER_BORDER);
        entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0f);
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
    public TargetPathNode getNode(double x, double y, double z) {
        return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y + 0.5), MathHelper.floor(z)));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        PathNode lv11;
        PathNode lv10;
        PathNode lv9;
        PathNode lv8;
        boolean bl4;
        int i = 0;
        boolean j = true;
        BlockPos lv = new BlockPos(node.x, node.y, node.z);
        double d = this.getFeetY(lv);
        PathNode lv2 = this.getPathNode(node.x, node.y, node.z + 1, 1, d);
        PathNode lv3 = this.getPathNode(node.x - 1, node.y, node.z, 1, d);
        PathNode lv4 = this.getPathNode(node.x + 1, node.y, node.z, 1, d);
        PathNode lv5 = this.getPathNode(node.x, node.y, node.z - 1, 1, d);
        PathNode lv6 = this.getPathNode(node.x, node.y + 1, node.z, 0, d);
        PathNode lv7 = this.getPathNode(node.x, node.y - 1, node.z, 1, d);
        if (lv2 != null && !lv2.visited) {
            successors[i++] = lv2;
        }
        if (lv3 != null && !lv3.visited) {
            successors[i++] = lv3;
        }
        if (lv4 != null && !lv4.visited) {
            successors[i++] = lv4;
        }
        if (lv5 != null && !lv5.visited) {
            successors[i++] = lv5;
        }
        if (lv6 != null && !lv6.visited) {
            successors[i++] = lv6;
        }
        if (lv7 != null && !lv7.visited) {
            successors[i++] = lv7;
        }
        boolean bl = lv5 == null || lv5.type == PathNodeType.OPEN || lv5.penalty != 0.0f;
        boolean bl2 = lv2 == null || lv2.type == PathNodeType.OPEN || lv2.penalty != 0.0f;
        boolean bl3 = lv4 == null || lv4.type == PathNodeType.OPEN || lv4.penalty != 0.0f;
        boolean bl5 = bl4 = lv3 == null || lv3.type == PathNodeType.OPEN || lv3.penalty != 0.0f;
        if (bl && bl4 && (lv8 = this.getPathNode(node.x - 1, node.y, node.z - 1, 1, d)) != null && !lv8.visited) {
            successors[i++] = lv8;
        }
        if (bl && bl3 && (lv9 = this.getPathNode(node.x + 1, node.y, node.z - 1, 1, d)) != null && !lv9.visited) {
            successors[i++] = lv9;
        }
        if (bl2 && bl4 && (lv10 = this.getPathNode(node.x - 1, node.y, node.z + 1, 1, d)) != null && !lv10.visited) {
            successors[i++] = lv10;
        }
        if (bl2 && bl3 && (lv11 = this.getPathNode(node.x + 1, node.y, node.z + 1, 1, d)) != null && !lv11.visited) {
            successors[i++] = lv11;
        }
        return i;
    }

    private double getFeetY(BlockPos pos) {
        if (!this.entity.isTouchingWater()) {
            VoxelShape lv2;
            BlockPos lv;
            return (double)lv.getY() + ((lv2 = this.cachedWorld.getBlockState(lv = pos.down()).getCollisionShape(this.cachedWorld, lv)).isEmpty() ? 0.0 : lv2.getMax(Direction.Axis.Y));
        }
        return (double)pos.getY() + 0.5;
    }

    @Nullable
    private PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY) {
        PathNode lv = null;
        BlockPos lv2 = new BlockPos(x, y, z);
        double e = this.getFeetY(lv2);
        if (e - prevFeetY > 1.125) {
            return null;
        }
        PathNodeType lv3 = this.getNodeType(this.cachedWorld, x, y, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
        float f = this.entity.getPathfindingPenalty(lv3);
        double g = (double)this.entity.getWidth() / 2.0;
        if (f >= 0.0f) {
            lv = this.getNode(x, y, z);
            lv.type = lv3;
            lv.penalty = Math.max(lv.penalty, f);
        }
        if (lv3 == PathNodeType.WATER || lv3 == PathNodeType.WALKABLE) {
            if (y < this.entity.world.getSeaLevel() - 10 && lv != null) {
                lv.penalty += 1.0f;
            }
            return lv;
        }
        if (lv == null && maxYStep > 0 && lv3 != PathNodeType.FENCE && lv3 != PathNodeType.UNPASSABLE_RAIL && lv3 != PathNodeType.TRAPDOOR) {
            lv = this.getPathNode(x, y + 1, z, maxYStep - 1, prevFeetY);
        }
        if (lv3 == PathNodeType.OPEN) {
            Box lv4 = new Box((double)x - g + 0.5, (double)y + 0.001, (double)z - g + 0.5, (double)x + g + 0.5, (float)y + this.entity.getHeight(), (double)z + g + 0.5);
            if (!this.entity.world.doesNotCollide(this.entity, lv4)) {
                return null;
            }
            PathNodeType lv5 = this.getNodeType(this.cachedWorld, x, y - 1, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
            if (lv5 == PathNodeType.BLOCKED) {
                lv = this.getNode(x, y, z);
                lv.type = PathNodeType.WALKABLE;
                lv.penalty = Math.max(lv.penalty, f);
                return lv;
            }
            if (lv5 == PathNodeType.WATER) {
                lv = this.getNode(x, y, z);
                lv.type = PathNodeType.WATER;
                lv.penalty = Math.max(lv.penalty, f);
                return lv;
            }
            int m = 0;
            while (y > 0 && lv3 == PathNodeType.OPEN) {
                --y;
                if (m++ >= this.entity.getSafeFallDistance()) {
                    return null;
                }
                lv3 = this.getNodeType(this.cachedWorld, x, y, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
                f = this.entity.getPathfindingPenalty(lv3);
                if (lv3 != PathNodeType.OPEN && f >= 0.0f) {
                    lv = this.getNode(x, y, z);
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
    protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType type) {
        if (type == PathNodeType.RAIL && !(world.getBlockState(pos).getBlock() instanceof AbstractRailBlock) && !(world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
            type = PathNodeType.UNPASSABLE_RAIL;
        }
        if (type == PathNodeType.DOOR_OPEN || type == PathNodeType.DOOR_WOOD_CLOSED || type == PathNodeType.DOOR_IRON_CLOSED) {
            type = PathNodeType.BLOCKED;
        }
        if (type == PathNodeType.LEAVES) {
            type = PathNodeType.BLOCKED;
        }
        return type;
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        PathNodeType lv2 = AmphibiousPathNodeMaker.getCommonNodeType(world, lv.set(x, y, z));
        if (lv2 == PathNodeType.WATER) {
            for (Direction lv3 : Direction.values()) {
                PathNodeType lv4 = AmphibiousPathNodeMaker.getCommonNodeType(world, lv.set(x, y, z).move(lv3));
                if (lv4 != PathNodeType.BLOCKED) continue;
                return PathNodeType.WATER_BORDER;
            }
            return PathNodeType.WATER;
        }
        if (lv2 == PathNodeType.OPEN && y >= 1) {
            BlockState lv5 = world.getBlockState(new BlockPos(x, y - 1, z));
            PathNodeType lv6 = AmphibiousPathNodeMaker.getCommonNodeType(world, lv.set(x, y - 1, z));
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
            lv2 = AmphibiousPathNodeMaker.getNodeTypeFromNeighbors(world, lv.set(x, y, z), lv2);
        }
        return lv2;
    }
}

