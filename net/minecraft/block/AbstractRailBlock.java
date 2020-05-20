/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractRailBlock
extends Block {
    protected static final VoxelShape STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final boolean allowCurves;

    public static boolean isRail(World arg, BlockPos arg2) {
        return AbstractRailBlock.isRail(arg.getBlockState(arg2));
    }

    public static boolean isRail(BlockState arg) {
        return arg.isIn(BlockTags.RAILS) && arg.getBlock() instanceof AbstractRailBlock;
    }

    protected AbstractRailBlock(boolean bl, AbstractBlock.Settings arg) {
        super(arg);
        this.allowCurves = bl;
    }

    public boolean canMakeCurves() {
        return this.allowCurves;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        RailShape lv;
        RailShape railShape = lv = arg.isOf(this) ? arg.get(this.getShapeProperty()) : null;
        if (lv != null && lv.isAscending()) {
            return ASCENDING_SHAPE;
        }
        return STRAIGHT_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return AbstractRailBlock.hasTopRim(arg2, arg3.down());
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        this.updateCurves(arg, arg2, arg3, bl);
    }

    protected BlockState updateCurves(BlockState arg, World arg2, BlockPos arg3, boolean bl) {
        arg = this.updateBlockState(arg2, arg3, arg, true);
        if (this.allowCurves) {
            arg.neighborUpdate(arg2, arg3, this, arg3, bl);
        }
        return arg;
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        RailShape lv = arg.get(this.getShapeProperty());
        if (AbstractRailBlock.shouldDropRail(arg3, arg2, lv) && !arg2.isAir(arg3)) {
            if (!bl) {
                AbstractRailBlock.dropStacks(arg, arg2, arg3);
            }
            arg2.removeBlock(arg3, bl);
        } else {
            this.updateBlockState(arg, arg2, arg3, arg4);
        }
    }

    private static boolean shouldDropRail(BlockPos arg, World arg2, RailShape arg3) {
        if (!AbstractRailBlock.hasTopRim(arg2, arg.down())) {
            return true;
        }
        switch (arg3) {
            case ASCENDING_EAST: {
                return !AbstractRailBlock.hasTopRim(arg2, arg.east());
            }
            case ASCENDING_WEST: {
                return !AbstractRailBlock.hasTopRim(arg2, arg.west());
            }
            case ASCENDING_NORTH: {
                return !AbstractRailBlock.hasTopRim(arg2, arg.north());
            }
            case ASCENDING_SOUTH: {
                return !AbstractRailBlock.hasTopRim(arg2, arg.south());
            }
        }
        return false;
    }

    protected void updateBlockState(BlockState arg, World arg2, BlockPos arg3, Block arg4) {
    }

    protected BlockState updateBlockState(World arg, BlockPos arg2, BlockState arg3, boolean bl) {
        if (arg.isClient) {
            return arg3;
        }
        RailShape lv = arg3.get(this.getShapeProperty());
        return new RailPlacementHelper(arg, arg2, arg3).updateBlockState(arg.isReceivingRedstonePower(arg2), bl, lv).getBlockState();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.NORMAL;
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl) {
            return;
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
        if (arg.get(this.getShapeProperty()).isAscending()) {
            arg2.updateNeighborsAlways(arg3.up(), this);
        }
        if (this.allowCurves) {
            arg2.updateNeighborsAlways(arg3, this);
            arg2.updateNeighborsAlways(arg3.down(), this);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = super.getDefaultState();
        Direction lv2 = arg.getPlayerFacing();
        boolean bl = lv2 == Direction.EAST || lv2 == Direction.WEST;
        return (BlockState)lv.with(this.getShapeProperty(), bl ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    public abstract Property<RailShape> getShapeProperty();
}

