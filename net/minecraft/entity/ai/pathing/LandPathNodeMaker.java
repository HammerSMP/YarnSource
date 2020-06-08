/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class LandPathNodeMaker
extends PathNodeMaker {
    protected float waterPathNodeTypeWeight;
    private final Long2ObjectMap<PathNodeType> field_25190 = new Long2ObjectOpenHashMap();
    private final Object2BooleanMap<Box> field_25191 = new Object2BooleanOpenHashMap();

    @Override
    public void init(ChunkCache arg, MobEntity arg2) {
        super.init(arg, arg2);
        this.waterPathNodeTypeWeight = arg2.getPathfindingPenalty(PathNodeType.WATER);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
        this.field_25190.clear();
        this.field_25191.clear();
        super.clear();
    }

    @Override
    public PathNode getStart() {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int i = MathHelper.floor(this.entity.getY());
        BlockState lv2 = this.cachedWorld.getBlockState(lv.set(this.entity.getX(), (double)i, this.entity.getZ()));
        if (this.entity.canWalkOnFluid(lv2.getFluidState().getFluid())) {
            while (this.entity.canWalkOnFluid(lv2.getFluidState().getFluid())) {
                lv2 = this.cachedWorld.getBlockState(lv.set(this.entity.getX(), (double)(++i), this.entity.getZ()));
            }
            --i;
        } else if (this.canSwim() && this.entity.isTouchingWater()) {
            while (lv2.getBlock() == Blocks.WATER || lv2.getFluidState() == Fluids.WATER.getStill(false)) {
                lv2 = this.cachedWorld.getBlockState(lv.set(this.entity.getX(), (double)(++i), this.entity.getZ()));
            }
            --i;
        } else if (this.entity.isOnGround()) {
            i = MathHelper.floor(this.entity.getY() + 0.5);
        } else {
            BlockPos lv3 = this.entity.getBlockPos();
            while ((this.cachedWorld.getBlockState(lv3).isAir() || this.cachedWorld.getBlockState(lv3).canPathfindThrough(this.cachedWorld, lv3, NavigationType.LAND)) && lv3.getY() > 0) {
                lv3 = lv3.down();
            }
            i = lv3.up().getY();
        }
        BlockPos lv4 = this.entity.getBlockPos();
        PathNodeType lv5 = this.method_29303(this.entity, lv4.getX(), i, lv4.getZ());
        if (this.entity.getPathfindingPenalty(lv5) < 0.0f) {
            Box lv6 = this.entity.getBoundingBox();
            if (this.method_27139(lv.set(lv6.minX, (double)i, lv6.minZ)) || this.method_27139(lv.set(lv6.minX, (double)i, lv6.maxZ)) || this.method_27139(lv.set(lv6.maxX, (double)i, lv6.minZ)) || this.method_27139(lv.set(lv6.maxX, (double)i, lv6.maxZ))) {
                PathNode lv7 = this.method_27137(lv);
                lv7.type = this.getNodeType(this.entity, lv7.getPos());
                lv7.penalty = this.entity.getPathfindingPenalty(lv7.type);
                return lv7;
            }
        }
        PathNode lv8 = this.getNode(lv4.getX(), i, lv4.getZ());
        lv8.type = this.getNodeType(this.entity, lv8.getPos());
        lv8.penalty = this.entity.getPathfindingPenalty(lv8.type);
        return lv8;
    }

    private boolean method_27139(BlockPos arg) {
        PathNodeType lv = this.getNodeType(this.entity, arg);
        return this.entity.getPathfindingPenalty(lv) >= 0.0f;
    }

    @Override
    public TargetPathNode getNode(double d, double e, double f) {
        return new TargetPathNode(this.getNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
    }

    @Override
    public int getSuccessors(PathNode[] args, PathNode arg) {
        PathNode lv10;
        PathNode lv9;
        PathNode lv8;
        PathNode lv7;
        PathNode lv6;
        PathNode lv5;
        PathNode lv4;
        double d;
        PathNode lv3;
        int i = 0;
        int j = 0;
        PathNodeType lv = this.method_29303(this.entity, arg.x, arg.y + 1, arg.z);
        PathNodeType lv2 = this.method_29303(this.entity, arg.x, arg.y, arg.z);
        if (this.entity.getPathfindingPenalty(lv) >= 0.0f && lv2 != PathNodeType.STICKY_HONEY) {
            j = MathHelper.floor(Math.max(1.0f, this.entity.stepHeight));
        }
        if (this.isValidDiagonalSuccessor(lv3 = this.getPathNode(arg.x, arg.y, arg.z + 1, j, d = LandPathNodeMaker.getFeetY(this.cachedWorld, new BlockPos(arg.x, arg.y, arg.z)), Direction.SOUTH, lv2), arg)) {
            args[i++] = lv3;
        }
        if (this.isValidDiagonalSuccessor(lv4 = this.getPathNode(arg.x - 1, arg.y, arg.z, j, d, Direction.WEST, lv2), arg)) {
            args[i++] = lv4;
        }
        if (this.isValidDiagonalSuccessor(lv5 = this.getPathNode(arg.x + 1, arg.y, arg.z, j, d, Direction.EAST, lv2), arg)) {
            args[i++] = lv5;
        }
        if (this.isValidDiagonalSuccessor(lv6 = this.getPathNode(arg.x, arg.y, arg.z - 1, j, d, Direction.NORTH, lv2), arg)) {
            args[i++] = lv6;
        }
        if (this.method_29579(arg, lv4, lv6, lv7 = this.getPathNode(arg.x - 1, arg.y, arg.z - 1, j, d, Direction.NORTH, lv2))) {
            args[i++] = lv7;
        }
        if (this.method_29579(arg, lv5, lv6, lv8 = this.getPathNode(arg.x + 1, arg.y, arg.z - 1, j, d, Direction.NORTH, lv2))) {
            args[i++] = lv8;
        }
        if (this.method_29579(arg, lv4, lv3, lv9 = this.getPathNode(arg.x - 1, arg.y, arg.z + 1, j, d, Direction.SOUTH, lv2))) {
            args[i++] = lv9;
        }
        if (this.method_29579(arg, lv5, lv3, lv10 = this.getPathNode(arg.x + 1, arg.y, arg.z + 1, j, d, Direction.SOUTH, lv2))) {
            args[i++] = lv10;
        }
        return i;
    }

    private boolean isValidDiagonalSuccessor(PathNode arg, PathNode arg2) {
        return arg != null && !arg.visited && (arg.penalty >= 0.0f || arg2.penalty < 0.0f);
    }

    private boolean method_29579(PathNode arg, @Nullable PathNode arg2, @Nullable PathNode arg3, @Nullable PathNode arg4) {
        if (arg4 == null || arg3 == null || arg2 == null) {
            return false;
        }
        if (arg4.visited) {
            return false;
        }
        if (arg3.y > arg.y || arg2.y > arg.y) {
            return false;
        }
        boolean bl = arg3.type == PathNodeType.FENCE && arg2.type == PathNodeType.FENCE && (double)this.entity.getWidth() < 0.5;
        return arg4.penalty >= 0.0f && (arg3.y < arg.y || arg3.penalty >= 0.0f || bl) && (arg2.y < arg.y || arg2.penalty >= 0.0f || bl);
    }

    private boolean method_29578(PathNode arg) {
        Vec3d lv = new Vec3d((double)arg.x - this.entity.getX(), (double)arg.y - this.entity.getY(), (double)arg.z - this.entity.getZ());
        Box lv2 = this.entity.getBoundingBox();
        int i = MathHelper.ceil(lv.length() / lv2.getAverageSideLength());
        lv = lv.multiply(1.0f / (float)i);
        for (int j = 1; j <= i; ++j) {
            if (!this.method_29304(lv2 = lv2.offset(lv))) continue;
            return false;
        }
        return true;
    }

    public static double getFeetY(BlockView arg, BlockPos arg2) {
        VoxelShape lv2;
        BlockPos lv;
        return (double)lv.getY() + ((lv2 = arg.getBlockState(lv = arg2.down()).getCollisionShape(arg, lv)).isEmpty() ? 0.0 : lv2.getMax(Direction.Axis.Y));
    }

    @Nullable
    private PathNode getPathNode(int i, int j, int k, int l, double d, Direction arg, PathNodeType arg2) {
        double m;
        double h;
        Box lv4;
        PathNode lv = null;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        double e = LandPathNodeMaker.getFeetY(this.cachedWorld, lv2.set(i, j, k));
        if (e - d > 1.125) {
            return null;
        }
        PathNodeType lv3 = this.method_29303(this.entity, i, j, k);
        float f = this.entity.getPathfindingPenalty(lv3);
        double g = (double)this.entity.getWidth() / 2.0;
        if (f >= 0.0f) {
            lv = this.getNode(i, j, k);
            lv.type = lv3;
            lv.penalty = Math.max(lv.penalty, f);
        }
        if (arg2 == PathNodeType.FENCE && lv != null && lv.penalty >= 0.0f && !this.method_29578(lv)) {
            lv = null;
        }
        if (lv3 == PathNodeType.WALKABLE) {
            return lv;
        }
        if ((lv == null || lv.penalty < 0.0f) && l > 0 && lv3 != PathNodeType.FENCE && lv3 != PathNodeType.UNPASSABLE_RAIL && lv3 != PathNodeType.TRAPDOOR && (lv = this.getPathNode(i, j + 1, k, l - 1, d, arg, arg2)) != null && (lv.type == PathNodeType.OPEN || lv.type == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0f && this.method_29304(lv4 = new Box((h = (double)(i - arg.getOffsetX()) + 0.5) - g, LandPathNodeMaker.getFeetY(this.cachedWorld, lv2.set(h, (double)(j + 1), m = (double)(k - arg.getOffsetZ()) + 0.5)) + 0.001, m - g, h + g, (double)this.entity.getHeight() + LandPathNodeMaker.getFeetY(this.cachedWorld, lv2.set((double)lv.x, (double)lv.y, (double)lv.z)) - 0.002, m + g))) {
            lv = null;
        }
        if (lv3 == PathNodeType.WATER && !this.canSwim()) {
            if (this.method_29303(this.entity, i, j - 1, k) != PathNodeType.WATER) {
                return lv;
            }
            while (j > 0) {
                if ((lv3 = this.method_29303(this.entity, i, --j, k)) == PathNodeType.WATER) {
                    lv = this.getNode(i, j, k);
                    lv.type = lv3;
                    lv.penalty = Math.max(lv.penalty, this.entity.getPathfindingPenalty(lv3));
                    continue;
                }
                return lv;
            }
        }
        if (lv3 == PathNodeType.OPEN) {
            PathNodeType lv6;
            Box lv5 = new Box((double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (float)j + this.entity.getHeight(), (double)k + g + 0.5);
            if (this.method_29304(lv5)) {
                return null;
            }
            if (this.entity.getWidth() >= 1.0f && (lv6 = this.method_29303(this.entity, i, j - 1, k)) == PathNodeType.BLOCKED) {
                lv = this.getNode(i, j, k);
                lv.type = PathNodeType.WALKABLE;
                lv.penalty = Math.max(lv.penalty, f);
                return lv;
            }
            int n = 0;
            int o = j;
            while (lv3 == PathNodeType.OPEN) {
                if (--j < 0) {
                    PathNode lv7 = this.getNode(i, o, k);
                    lv7.type = PathNodeType.BLOCKED;
                    lv7.penalty = -1.0f;
                    return lv7;
                }
                PathNode lv8 = this.getNode(i, j, k);
                if (n++ >= this.entity.getSafeFallDistance()) {
                    lv8.type = PathNodeType.BLOCKED;
                    lv8.penalty = -1.0f;
                    return lv8;
                }
                lv3 = this.method_29303(this.entity, i, j, k);
                f = this.entity.getPathfindingPenalty(lv3);
                if (lv3 != PathNodeType.OPEN && f >= 0.0f) {
                    lv = lv8;
                    lv.type = lv3;
                    lv.penalty = Math.max(lv.penalty, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                lv8.type = PathNodeType.BLOCKED;
                lv8.penalty = -1.0f;
                return lv8;
            }
        }
        if (lv3 == PathNodeType.FENCE) {
            lv = this.getNode(i, j, k);
            lv.visited = true;
            lv.type = lv3;
            lv.penalty = lv3.getDefaultPenalty();
        }
        return lv;
    }

    private boolean method_29304(Box arg) {
        return (Boolean)this.field_25191.computeIfAbsent((Object)arg, arg2 -> !this.cachedWorld.doesNotCollide(this.entity, arg));
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
        if (enumSet.contains((Object)PathNodeType.UNPASSABLE_RAIL)) {
            return PathNodeType.UNPASSABLE_RAIL;
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

    public PathNodeType findNearbyNodeTypes(BlockView arg, int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2, EnumSet<PathNodeType> enumSet, PathNodeType arg2, BlockPos arg3) {
        for (int o = 0; o < l; ++o) {
            for (int p = 0; p < m; ++p) {
                for (int q = 0; q < n; ++q) {
                    int r = o + i;
                    int s = p + j;
                    int t = q + k;
                    PathNodeType lv = this.getDefaultNodeType(arg, r, s, t);
                    lv = this.adjustNodeType(arg, bl, bl2, arg3, lv);
                    if (o == 0 && p == 0 && q == 0) {
                        arg2 = lv;
                    }
                    enumSet.add(lv);
                }
            }
        }
        return arg2;
    }

    protected PathNodeType adjustNodeType(BlockView arg, boolean bl, boolean bl2, BlockPos arg2, PathNodeType arg3) {
        if (arg3 == PathNodeType.DOOR_WOOD_CLOSED && bl && bl2) {
            arg3 = PathNodeType.WALKABLE;
        }
        if (arg3 == PathNodeType.DOOR_OPEN && !bl2) {
            arg3 = PathNodeType.BLOCKED;
        }
        if (arg3 == PathNodeType.RAIL && !(arg.getBlockState(arg2).getBlock() instanceof AbstractRailBlock) && !(arg.getBlockState(arg2.down()).getBlock() instanceof AbstractRailBlock)) {
            arg3 = PathNodeType.UNPASSABLE_RAIL;
        }
        if (arg3 == PathNodeType.LEAVES) {
            arg3 = PathNodeType.BLOCKED;
        }
        return arg3;
    }

    private PathNodeType getNodeType(MobEntity arg, BlockPos arg2) {
        return this.method_29303(arg, arg2.getX(), arg2.getY(), arg2.getZ());
    }

    private PathNodeType method_29303(MobEntity arg, int i, int j, int k) {
        return (PathNodeType)((Object)this.field_25190.computeIfAbsent(BlockPos.asLong(i, j, k), l -> this.getNodeType(this.cachedWorld, i, j, k, arg, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors())));
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView arg, int i, int j, int k) {
        return LandPathNodeMaker.getLandNodeType(arg, new BlockPos.Mutable(i, j, k));
    }

    public static PathNodeType getLandNodeType(BlockView arg, BlockPos.Mutable arg2) {
        int i = arg2.getX();
        int j = arg2.getY();
        int k = arg2.getZ();
        PathNodeType lv = LandPathNodeMaker.getCommonNodeType(arg, arg2);
        if (lv == PathNodeType.OPEN && j >= 1) {
            PathNodeType lv2 = LandPathNodeMaker.getCommonNodeType(arg, arg2.set(i, j - 1, k));
            PathNodeType pathNodeType = lv = lv2 == PathNodeType.WALKABLE || lv2 == PathNodeType.OPEN || lv2 == PathNodeType.WATER || lv2 == PathNodeType.LAVA ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            if (lv2 == PathNodeType.DAMAGE_FIRE) {
                lv = PathNodeType.DAMAGE_FIRE;
            }
            if (lv2 == PathNodeType.DAMAGE_CACTUS) {
                lv = PathNodeType.DAMAGE_CACTUS;
            }
            if (lv2 == PathNodeType.DAMAGE_OTHER) {
                lv = PathNodeType.DAMAGE_OTHER;
            }
            if (lv2 == PathNodeType.STICKY_HONEY) {
                lv = PathNodeType.STICKY_HONEY;
            }
        }
        if (lv == PathNodeType.WALKABLE) {
            lv = LandPathNodeMaker.getNodeTypeFromNeighbors(arg, arg2.set(i, j, k), lv);
        }
        return lv;
    }

    public static PathNodeType getNodeTypeFromNeighbors(BlockView arg, BlockPos.Mutable arg2, PathNodeType arg3) {
        int i = arg2.getX();
        int j = arg2.getY();
        int k = arg2.getZ();
        for (int l = -1; l <= 1; ++l) {
            for (int m = -1; m <= 1; ++m) {
                for (int n = -1; n <= 1; ++n) {
                    if (l == 0 && n == 0) continue;
                    arg2.set(i + l, j + m, k + n);
                    BlockState lv = arg.getBlockState(arg2);
                    if (lv.isOf(Blocks.CACTUS)) {
                        return PathNodeType.DANGER_CACTUS;
                    }
                    if (lv.isOf(Blocks.SWEET_BERRY_BUSH)) {
                        return PathNodeType.DANGER_OTHER;
                    }
                    if (!LandPathNodeMaker.method_27138(lv)) continue;
                    return PathNodeType.DANGER_FIRE;
                }
            }
        }
        return arg3;
    }

    protected static PathNodeType getCommonNodeType(BlockView arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        Block lv2 = lv.getBlock();
        Material lv3 = lv.getMaterial();
        if (lv.isAir()) {
            return PathNodeType.OPEN;
        }
        if (lv.isIn(BlockTags.TRAPDOORS) || lv.isOf(Blocks.LILY_PAD)) {
            return PathNodeType.TRAPDOOR;
        }
        if (lv.isOf(Blocks.CACTUS)) {
            return PathNodeType.DAMAGE_CACTUS;
        }
        if (lv.isOf(Blocks.SWEET_BERRY_BUSH)) {
            return PathNodeType.DAMAGE_OTHER;
        }
        if (lv.isOf(Blocks.HONEY_BLOCK)) {
            return PathNodeType.STICKY_HONEY;
        }
        if (lv.isOf(Blocks.COCOA)) {
            return PathNodeType.COCOA;
        }
        if (LandPathNodeMaker.method_27138(lv)) {
            return PathNodeType.DAMAGE_FIRE;
        }
        if (DoorBlock.isWoodenDoor(lv) && !lv.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_WOOD_CLOSED;
        }
        if (lv2 instanceof DoorBlock && lv3 == Material.METAL && !lv.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_IRON_CLOSED;
        }
        if (lv2 instanceof DoorBlock && lv.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_OPEN;
        }
        if (lv2 instanceof AbstractRailBlock) {
            return PathNodeType.RAIL;
        }
        if (lv2 instanceof LeavesBlock) {
            return PathNodeType.LEAVES;
        }
        if (lv2.isIn(BlockTags.FENCES) || lv2.isIn(BlockTags.WALLS) || lv2 instanceof FenceGateBlock && !lv.get(FenceGateBlock.OPEN).booleanValue()) {
            return PathNodeType.FENCE;
        }
        if (!lv.canPathfindThrough(arg, arg2, NavigationType.LAND)) {
            return PathNodeType.BLOCKED;
        }
        FluidState lv4 = arg.getFluidState(arg2);
        if (lv4.matches(FluidTags.WATER)) {
            return PathNodeType.WATER;
        }
        if (lv4.matches(FluidTags.LAVA)) {
            return PathNodeType.LAVA;
        }
        return PathNodeType.OPEN;
    }

    private static boolean method_27138(BlockState arg) {
        return arg.isIn(BlockTags.FIRE) || arg.isOf(Blocks.LAVA) || arg.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(arg);
    }
}

