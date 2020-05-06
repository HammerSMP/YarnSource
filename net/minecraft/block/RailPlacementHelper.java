/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RailPlacementHelper {
    private final World world;
    private final BlockPos pos;
    private final AbstractRailBlock block;
    private BlockState state;
    private final boolean allowCurves;
    private final List<BlockPos> neighbors = Lists.newArrayList();

    public RailPlacementHelper(World arg, BlockPos arg2, BlockState arg3) {
        this.world = arg;
        this.pos = arg2;
        this.state = arg3;
        this.block = (AbstractRailBlock)arg3.getBlock();
        RailShape lv = arg3.get(this.block.getShapeProperty());
        this.allowCurves = this.block.canMakeCurves();
        this.computeNeighbors(lv);
    }

    public List<BlockPos> getNeighbors() {
        return this.neighbors;
    }

    private void computeNeighbors(RailShape arg) {
        this.neighbors.clear();
        switch (arg) {
            case NORTH_SOUTH: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south());
                break;
            }
            case EAST_WEST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east());
                break;
            }
            case ASCENDING_EAST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east().up());
                break;
            }
            case ASCENDING_WEST: {
                this.neighbors.add(this.pos.west().up());
                this.neighbors.add(this.pos.east());
                break;
            }
            case ASCENDING_NORTH: {
                this.neighbors.add(this.pos.north().up());
                this.neighbors.add(this.pos.south());
                break;
            }
            case ASCENDING_SOUTH: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south().up());
                break;
            }
            case SOUTH_EAST: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.south());
                break;
            }
            case SOUTH_WEST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.south());
                break;
            }
            case NORTH_WEST: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.north());
                break;
            }
            case NORTH_EAST: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.north());
            }
        }
    }

    private void updateNeighborPositions() {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            RailPlacementHelper lv = this.getNeighboringRail(this.neighbors.get(i));
            if (lv == null || !lv.isNeighbor(this)) {
                this.neighbors.remove(i--);
                continue;
            }
            this.neighbors.set(i, lv.pos);
        }
    }

    private boolean isVerticallyNearRail(BlockPos arg) {
        return AbstractRailBlock.isRail(this.world, arg) || AbstractRailBlock.isRail(this.world, arg.up()) || AbstractRailBlock.isRail(this.world, arg.down());
    }

    @Nullable
    private RailPlacementHelper getNeighboringRail(BlockPos arg) {
        BlockPos lv = arg;
        BlockState lv2 = this.world.getBlockState(lv);
        if (AbstractRailBlock.isRail(lv2)) {
            return new RailPlacementHelper(this.world, lv, lv2);
        }
        lv = arg.up();
        lv2 = this.world.getBlockState(lv);
        if (AbstractRailBlock.isRail(lv2)) {
            return new RailPlacementHelper(this.world, lv, lv2);
        }
        lv = arg.down();
        lv2 = this.world.getBlockState(lv);
        if (AbstractRailBlock.isRail(lv2)) {
            return new RailPlacementHelper(this.world, lv, lv2);
        }
        return null;
    }

    private boolean isNeighbor(RailPlacementHelper arg) {
        return this.isNeighbor(arg.pos);
    }

    private boolean isNeighbor(BlockPos arg) {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            BlockPos lv = this.neighbors.get(i);
            if (lv.getX() != arg.getX() || lv.getZ() != arg.getZ()) continue;
            return true;
        }
        return false;
    }

    protected int getNeighborCount() {
        int i = 0;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            if (!this.isVerticallyNearRail(this.pos.offset(lv))) continue;
            ++i;
        }
        return i;
    }

    private boolean canConnect(RailPlacementHelper arg) {
        return this.isNeighbor(arg) || this.neighbors.size() != 2;
    }

    private void computeRailShape(RailPlacementHelper arg) {
        this.neighbors.add(arg.pos);
        BlockPos lv = this.pos.north();
        BlockPos lv2 = this.pos.south();
        BlockPos lv3 = this.pos.west();
        BlockPos lv4 = this.pos.east();
        boolean bl = this.isNeighbor(lv);
        boolean bl2 = this.isNeighbor(lv2);
        boolean bl3 = this.isNeighbor(lv3);
        boolean bl4 = this.isNeighbor(lv4);
        RailShape lv5 = null;
        if (bl || bl2) {
            lv5 = RailShape.NORTH_SOUTH;
        }
        if (bl3 || bl4) {
            lv5 = RailShape.EAST_WEST;
        }
        if (!this.allowCurves) {
            if (bl2 && bl4 && !bl && !bl3) {
                lv5 = RailShape.SOUTH_EAST;
            }
            if (bl2 && bl3 && !bl && !bl4) {
                lv5 = RailShape.SOUTH_WEST;
            }
            if (bl && bl3 && !bl2 && !bl4) {
                lv5 = RailShape.NORTH_WEST;
            }
            if (bl && bl4 && !bl2 && !bl3) {
                lv5 = RailShape.NORTH_EAST;
            }
        }
        if (lv5 == RailShape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail(this.world, lv.up())) {
                lv5 = RailShape.ASCENDING_NORTH;
            }
            if (AbstractRailBlock.isRail(this.world, lv2.up())) {
                lv5 = RailShape.ASCENDING_SOUTH;
            }
        }
        if (lv5 == RailShape.EAST_WEST) {
            if (AbstractRailBlock.isRail(this.world, lv4.up())) {
                lv5 = RailShape.ASCENDING_EAST;
            }
            if (AbstractRailBlock.isRail(this.world, lv3.up())) {
                lv5 = RailShape.ASCENDING_WEST;
            }
        }
        if (lv5 == null) {
            lv5 = RailShape.NORTH_SOUTH;
        }
        this.state = (BlockState)this.state.with(this.block.getShapeProperty(), lv5);
        this.world.setBlockState(this.pos, this.state, 3);
    }

    private boolean canConnect(BlockPos arg) {
        RailPlacementHelper lv = this.getNeighboringRail(arg);
        if (lv == null) {
            return false;
        }
        lv.updateNeighborPositions();
        return lv.canConnect(this);
    }

    public RailPlacementHelper updateBlockState(boolean bl, boolean bl2, RailShape arg) {
        boolean bl12;
        boolean bl8;
        BlockPos lv = this.pos.north();
        BlockPos lv2 = this.pos.south();
        BlockPos lv3 = this.pos.west();
        BlockPos lv4 = this.pos.east();
        boolean bl3 = this.canConnect(lv);
        boolean bl4 = this.canConnect(lv2);
        boolean bl5 = this.canConnect(lv3);
        boolean bl6 = this.canConnect(lv4);
        RailShape lv5 = null;
        boolean bl7 = bl3 || bl4;
        boolean bl9 = bl8 = bl5 || bl6;
        if (bl7 && !bl8) {
            lv5 = RailShape.NORTH_SOUTH;
        }
        if (bl8 && !bl7) {
            lv5 = RailShape.EAST_WEST;
        }
        boolean bl92 = bl4 && bl6;
        boolean bl10 = bl4 && bl5;
        boolean bl11 = bl3 && bl6;
        boolean bl13 = bl12 = bl3 && bl5;
        if (!this.allowCurves) {
            if (bl92 && !bl3 && !bl5) {
                lv5 = RailShape.SOUTH_EAST;
            }
            if (bl10 && !bl3 && !bl6) {
                lv5 = RailShape.SOUTH_WEST;
            }
            if (bl12 && !bl4 && !bl6) {
                lv5 = RailShape.NORTH_WEST;
            }
            if (bl11 && !bl4 && !bl5) {
                lv5 = RailShape.NORTH_EAST;
            }
        }
        if (lv5 == null) {
            if (bl7 && bl8) {
                lv5 = arg;
            } else if (bl7) {
                lv5 = RailShape.NORTH_SOUTH;
            } else if (bl8) {
                lv5 = RailShape.EAST_WEST;
            }
            if (!this.allowCurves) {
                if (bl) {
                    if (bl92) {
                        lv5 = RailShape.SOUTH_EAST;
                    }
                    if (bl10) {
                        lv5 = RailShape.SOUTH_WEST;
                    }
                    if (bl11) {
                        lv5 = RailShape.NORTH_EAST;
                    }
                    if (bl12) {
                        lv5 = RailShape.NORTH_WEST;
                    }
                } else {
                    if (bl12) {
                        lv5 = RailShape.NORTH_WEST;
                    }
                    if (bl11) {
                        lv5 = RailShape.NORTH_EAST;
                    }
                    if (bl10) {
                        lv5 = RailShape.SOUTH_WEST;
                    }
                    if (bl92) {
                        lv5 = RailShape.SOUTH_EAST;
                    }
                }
            }
        }
        if (lv5 == RailShape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail(this.world, lv.up())) {
                lv5 = RailShape.ASCENDING_NORTH;
            }
            if (AbstractRailBlock.isRail(this.world, lv2.up())) {
                lv5 = RailShape.ASCENDING_SOUTH;
            }
        }
        if (lv5 == RailShape.EAST_WEST) {
            if (AbstractRailBlock.isRail(this.world, lv4.up())) {
                lv5 = RailShape.ASCENDING_EAST;
            }
            if (AbstractRailBlock.isRail(this.world, lv3.up())) {
                lv5 = RailShape.ASCENDING_WEST;
            }
        }
        if (lv5 == null) {
            lv5 = arg;
        }
        this.computeNeighbors(lv5);
        this.state = (BlockState)this.state.with(this.block.getShapeProperty(), lv5);
        if (bl2 || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);
            for (int i = 0; i < this.neighbors.size(); ++i) {
                RailPlacementHelper lv6 = this.getNeighboringRail(this.neighbors.get(i));
                if (lv6 == null) continue;
                lv6.updateNeighborPositions();
                if (!lv6.canConnect(this)) continue;
                lv6.computeRailShape(this);
            }
        }
        return this;
    }

    public BlockState getBlockState() {
        return this.state;
    }
}

