/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class BirdPathNodeMaker
extends LandPathNodeMaker {
    @Override
    public void init(ChunkCache arg, MobEntity arg2) {
        super.init(arg, arg2);
        this.waterPathNodeTypeWeight = arg2.getPathfindingPenalty(PathNodeType.WATER);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
        super.clear();
    }

    @Override
    public PathNode getStart() {
        BlockPos lv3;
        PathNodeType lv4;
        int j;
        if (this.canSwim() && this.entity.isTouchingWater()) {
            int i = MathHelper.floor(this.entity.getY());
            BlockPos.Mutable lv = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());
            Block lv2 = this.cachedWorld.getBlockState(lv).getBlock();
            while (lv2 == Blocks.WATER) {
                lv.set(this.entity.getX(), (double)(++i), this.entity.getZ());
                lv2 = this.cachedWorld.getBlockState(lv).getBlock();
            }
        } else {
            j = MathHelper.floor(this.entity.getY() + 0.5);
        }
        if (this.entity.getPathfindingPenalty(lv4 = this.getNodeType(this.entity, (lv3 = this.entity.getBlockPos()).getX(), j, lv3.getZ())) < 0.0f) {
            HashSet set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)j, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)j, this.entity.getBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)j, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)j, this.entity.getBoundingBox().maxZ));
            for (BlockPos lv5 : set) {
                PathNodeType lv6 = this.getNodeType(this.entity, lv5);
                if (!(this.entity.getPathfindingPenalty(lv6) >= 0.0f)) continue;
                return super.getNode(lv5.getX(), lv5.getY(), lv5.getZ());
            }
        }
        return super.getNode(lv3.getX(), j, lv3.getZ());
    }

    @Override
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(super.getNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
    }

    @Override
    public int getSuccessors(PathNode[] args, PathNode arg) {
        PathNode lv26;
        PathNode lv25;
        PathNode lv24;
        PathNode lv23;
        PathNode lv22;
        PathNode lv21;
        PathNode lv20;
        PathNode lv19;
        PathNode lv18;
        PathNode lv17;
        PathNode lv16;
        PathNode lv15;
        PathNode lv14;
        PathNode lv13;
        PathNode lv12;
        PathNode lv11;
        PathNode lv10;
        PathNode lv9;
        PathNode lv8;
        PathNode lv7;
        PathNode lv6;
        PathNode lv5;
        PathNode lv4;
        PathNode lv3;
        PathNode lv2;
        int i = 0;
        PathNode lv = this.getNode(arg.x, arg.y, arg.z + 1);
        if (this.unvisited(lv)) {
            args[i++] = lv;
        }
        if (this.unvisited(lv2 = this.getNode(arg.x - 1, arg.y, arg.z))) {
            args[i++] = lv2;
        }
        if (this.unvisited(lv3 = this.getNode(arg.x + 1, arg.y, arg.z))) {
            args[i++] = lv3;
        }
        if (this.unvisited(lv4 = this.getNode(arg.x, arg.y, arg.z - 1))) {
            args[i++] = lv4;
        }
        if (this.unvisited(lv5 = this.getNode(arg.x, arg.y + 1, arg.z))) {
            args[i++] = lv5;
        }
        if (this.unvisited(lv6 = this.getNode(arg.x, arg.y - 1, arg.z))) {
            args[i++] = lv6;
        }
        if (this.unvisited(lv7 = this.getNode(arg.x, arg.y + 1, arg.z + 1)) && this.isPassable(lv) && this.isPassable(lv5)) {
            args[i++] = lv7;
        }
        if (this.unvisited(lv8 = this.getNode(arg.x - 1, arg.y + 1, arg.z)) && this.isPassable(lv2) && this.isPassable(lv5)) {
            args[i++] = lv8;
        }
        if (this.unvisited(lv9 = this.getNode(arg.x + 1, arg.y + 1, arg.z)) && this.isPassable(lv3) && this.isPassable(lv5)) {
            args[i++] = lv9;
        }
        if (this.unvisited(lv10 = this.getNode(arg.x, arg.y + 1, arg.z - 1)) && this.isPassable(lv4) && this.isPassable(lv5)) {
            args[i++] = lv10;
        }
        if (this.unvisited(lv11 = this.getNode(arg.x, arg.y - 1, arg.z + 1)) && this.isPassable(lv) && this.isPassable(lv6)) {
            args[i++] = lv11;
        }
        if (this.unvisited(lv12 = this.getNode(arg.x - 1, arg.y - 1, arg.z)) && this.isPassable(lv2) && this.isPassable(lv6)) {
            args[i++] = lv12;
        }
        if (this.unvisited(lv13 = this.getNode(arg.x + 1, arg.y - 1, arg.z)) && this.isPassable(lv3) && this.isPassable(lv6)) {
            args[i++] = lv13;
        }
        if (this.unvisited(lv14 = this.getNode(arg.x, arg.y - 1, arg.z - 1)) && this.isPassable(lv4) && this.isPassable(lv6)) {
            args[i++] = lv14;
        }
        if (this.unvisited(lv15 = this.getNode(arg.x + 1, arg.y, arg.z - 1)) && this.isPassable(lv4) && this.isPassable(lv3)) {
            args[i++] = lv15;
        }
        if (this.unvisited(lv16 = this.getNode(arg.x + 1, arg.y, arg.z + 1)) && this.isPassable(lv) && this.isPassable(lv3)) {
            args[i++] = lv16;
        }
        if (this.unvisited(lv17 = this.getNode(arg.x - 1, arg.y, arg.z - 1)) && this.isPassable(lv4) && this.isPassable(lv2)) {
            args[i++] = lv17;
        }
        if (this.unvisited(lv18 = this.getNode(arg.x - 1, arg.y, arg.z + 1)) && this.isPassable(lv) && this.isPassable(lv2)) {
            args[i++] = lv18;
        }
        if (this.unvisited(lv19 = this.getNode(arg.x + 1, arg.y + 1, arg.z - 1)) && this.isPassable(lv15) && this.isPassable(lv4) && this.isPassable(lv3) && this.isPassable(lv5) && this.isPassable(lv10) && this.isPassable(lv9)) {
            args[i++] = lv19;
        }
        if (this.unvisited(lv20 = this.getNode(arg.x + 1, arg.y + 1, arg.z + 1)) && this.isPassable(lv16) && this.isPassable(lv) && this.isPassable(lv3) && this.isPassable(lv5) && this.isPassable(lv7) && this.isPassable(lv9)) {
            args[i++] = lv20;
        }
        if (this.unvisited(lv21 = this.getNode(arg.x - 1, arg.y + 1, arg.z - 1)) && this.isPassable(lv17) && this.isPassable(lv4) && this.isPassable(lv2) & this.isPassable(lv5) && this.isPassable(lv10) && this.isPassable(lv8)) {
            args[i++] = lv21;
        }
        if (this.unvisited(lv22 = this.getNode(arg.x - 1, arg.y + 1, arg.z + 1)) && this.isPassable(lv18) && this.isPassable(lv) && this.isPassable(lv2) & this.isPassable(lv5) && this.isPassable(lv7) && this.isPassable(lv8)) {
            args[i++] = lv22;
        }
        if (this.unvisited(lv23 = this.getNode(arg.x + 1, arg.y - 1, arg.z - 1)) && this.isPassable(lv15) && this.isPassable(lv4) && this.isPassable(lv3) && this.isPassable(lv6) && this.isPassable(lv14) && this.isPassable(lv13)) {
            args[i++] = lv23;
        }
        if (this.unvisited(lv24 = this.getNode(arg.x + 1, arg.y - 1, arg.z + 1)) && this.isPassable(lv16) && this.isPassable(lv) && this.isPassable(lv3) && this.isPassable(lv6) && this.isPassable(lv11) && this.isPassable(lv13)) {
            args[i++] = lv24;
        }
        if (this.unvisited(lv25 = this.getNode(arg.x - 1, arg.y - 1, arg.z - 1)) && this.isPassable(lv17) && this.isPassable(lv4) && this.isPassable(lv2) && this.isPassable(lv6) && this.isPassable(lv14) && this.isPassable(lv12)) {
            args[i++] = lv25;
        }
        if (this.unvisited(lv26 = this.getNode(arg.x - 1, arg.y - 1, arg.z + 1)) && this.isPassable(lv18) && this.isPassable(lv) && this.isPassable(lv2) && this.isPassable(lv6) && this.isPassable(lv11) && this.isPassable(lv12)) {
            args[i++] = lv26;
        }
        return i;
    }

    private boolean isPassable(@Nullable PathNode arg) {
        return arg != null && arg.penalty >= 0.0f;
    }

    private boolean unvisited(@Nullable PathNode arg) {
        return arg != null && !arg.visited;
    }

    @Override
    @Nullable
    protected PathNode getNode(int i, int j, int k) {
        PathNode lv = null;
        PathNodeType lv2 = this.getNodeType(this.entity, i, j, k);
        float f = this.entity.getPathfindingPenalty(lv2);
        if (f >= 0.0f) {
            lv = super.getNode(i, j, k);
            lv.type = lv2;
            lv.penalty = Math.max(lv.penalty, f);
            if (lv2 == PathNodeType.WALKABLE) {
                lv.penalty += 1.0f;
            }
        }
        if (lv2 == PathNodeType.OPEN || lv2 == PathNodeType.WALKABLE) {
            return lv;
        }
        return lv;
    }

    @Override
    public PathNodeType getNodeType(BlockView arg, int i, int j, int k, MobEntity arg2, int l, int m, int n, boolean bl, boolean bl2) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType lv = PathNodeType.BLOCKED;
        BlockPos lv2 = arg2.getBlockPos();
        lv = this.findNearbyNodeTypes(arg, i, j, k, l, m, n, bl, bl2, enumSet, lv, lv2);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType lv3 = PathNodeType.BLOCKED;
        for (PathNodeType lv4 : enumSet) {
            if (arg2.getPathfindingPenalty(lv4) < 0.0f) {
                return lv4;
            }
            if (!(arg2.getPathfindingPenalty(lv4) >= arg2.getPathfindingPenalty(lv3))) continue;
            lv3 = lv4;
        }
        if (lv == PathNodeType.OPEN && arg2.getPathfindingPenalty(lv3) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return lv3;
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView arg, int i, int j, int k) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        PathNodeType lv2 = BirdPathNodeMaker.getCommonNodeType(arg, lv.set(i, j, k));
        if (lv2 == PathNodeType.OPEN && j >= 1) {
            BlockState lv3 = arg.getBlockState(lv.set(i, j - 1, k));
            PathNodeType lv4 = BirdPathNodeMaker.getCommonNodeType(arg, lv.set(i, j - 1, k));
            if (lv4 == PathNodeType.DAMAGE_FIRE || lv3.isOf(Blocks.MAGMA_BLOCK) || lv4 == PathNodeType.LAVA || lv3.isIn(BlockTags.CAMPFIRES)) {
                lv2 = PathNodeType.DAMAGE_FIRE;
            } else if (lv4 == PathNodeType.DAMAGE_CACTUS) {
                lv2 = PathNodeType.DAMAGE_CACTUS;
            } else if (lv4 == PathNodeType.DAMAGE_OTHER) {
                lv2 = PathNodeType.DAMAGE_OTHER;
            } else if (lv4 == PathNodeType.COCOA) {
                lv2 = PathNodeType.COCOA;
            } else if (lv4 == PathNodeType.FENCE) {
                lv2 = PathNodeType.FENCE;
            } else {
                PathNodeType pathNodeType = lv2 = lv4 == PathNodeType.WALKABLE || lv4 == PathNodeType.OPEN || lv4 == PathNodeType.WATER ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            }
        }
        if (lv2 == PathNodeType.WALKABLE || lv2 == PathNodeType.OPEN) {
            lv2 = BirdPathNodeMaker.getNodeTypeFromNeighbors(arg, lv.set(i, j, k), lv2);
        }
        return lv2;
    }

    private PathNodeType getNodeType(MobEntity arg, BlockPos arg2) {
        return this.getNodeType(arg, arg2.getX(), arg2.getY(), arg2.getZ());
    }

    private PathNodeType getNodeType(MobEntity arg, int i, int j, int k) {
        return this.getNodeType(this.cachedWorld, i, j, k, arg, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors());
    }
}

