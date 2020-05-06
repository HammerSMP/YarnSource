/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock
extends AbstractRailBlock {
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    protected PoweredRailBlock(AbstractBlock.Settings arg) {
        super(true, arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SHAPE, RailShape.NORTH_SOUTH)).with(POWERED, false));
    }

    protected boolean isPoweredByOtherRails(World arg, BlockPos arg2, BlockState arg3, boolean bl, int i) {
        if (i >= 8) {
            return false;
        }
        int j = arg2.getX();
        int k = arg2.getY();
        int l = arg2.getZ();
        boolean bl2 = true;
        RailShape lv = arg3.get(SHAPE);
        switch (lv) {
            case NORTH_SOUTH: {
                if (bl) {
                    ++l;
                    break;
                }
                --l;
                break;
            }
            case EAST_WEST: {
                if (bl) {
                    --j;
                    break;
                }
                ++j;
                break;
            }
            case ASCENDING_EAST: {
                if (bl) {
                    --j;
                } else {
                    ++j;
                    ++k;
                    bl2 = false;
                }
                lv = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_WEST: {
                if (bl) {
                    --j;
                    ++k;
                    bl2 = false;
                } else {
                    ++j;
                }
                lv = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_NORTH: {
                if (bl) {
                    ++l;
                } else {
                    --l;
                    ++k;
                    bl2 = false;
                }
                lv = RailShape.NORTH_SOUTH;
                break;
            }
            case ASCENDING_SOUTH: {
                if (bl) {
                    ++l;
                    ++k;
                    bl2 = false;
                } else {
                    --l;
                }
                lv = RailShape.NORTH_SOUTH;
            }
        }
        if (this.isPoweredByOtherRails(arg, new BlockPos(j, k, l), bl, i, lv)) {
            return true;
        }
        return bl2 && this.isPoweredByOtherRails(arg, new BlockPos(j, k - 1, l), bl, i, lv);
    }

    protected boolean isPoweredByOtherRails(World arg, BlockPos arg2, boolean bl, int i, RailShape arg3) {
        BlockState lv = arg.getBlockState(arg2);
        if (!lv.isOf(this)) {
            return false;
        }
        RailShape lv2 = lv.get(SHAPE);
        if (arg3 == RailShape.EAST_WEST && (lv2 == RailShape.NORTH_SOUTH || lv2 == RailShape.ASCENDING_NORTH || lv2 == RailShape.ASCENDING_SOUTH)) {
            return false;
        }
        if (arg3 == RailShape.NORTH_SOUTH && (lv2 == RailShape.EAST_WEST || lv2 == RailShape.ASCENDING_EAST || lv2 == RailShape.ASCENDING_WEST)) {
            return false;
        }
        if (lv.get(POWERED).booleanValue()) {
            if (arg.isReceivingRedstonePower(arg2)) {
                return true;
            }
            return this.isPoweredByOtherRails(arg, arg2, lv, bl, i + 1);
        }
        return false;
    }

    @Override
    protected void updateBlockState(BlockState arg, World arg2, BlockPos arg3, Block arg4) {
        boolean bl2;
        boolean bl = arg.get(POWERED);
        boolean bl3 = bl2 = arg2.isReceivingRedstonePower(arg3) || this.isPoweredByOtherRails(arg2, arg3, arg, true, 0) || this.isPoweredByOtherRails(arg2, arg3, arg, false, 0);
        if (bl2 != bl) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, bl2), 3);
            arg2.updateNeighborsAlways(arg3.down(), this);
            if (arg.get(SHAPE).isAscending()) {
                arg2.updateNeighborsAlways(arg3.up(), this);
            }
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                switch (arg.get(SHAPE)) {
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (arg.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (arg.get(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        RailShape lv = arg.get(SHAPE);
        switch (arg2) {
            case LEFT_RIGHT: {
                switch (lv) {
                    case ASCENDING_NORTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
                break;
            }
            case FRONT_BACK: {
                switch (lv) {
                    case ASCENDING_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)arg.with(SHAPE, RailShape.NORTH_WEST);
                    }
                }
                break;
            }
        }
        return super.mirror(arg, arg2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(SHAPE, POWERED);
    }
}

