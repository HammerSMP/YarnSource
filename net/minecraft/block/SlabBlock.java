/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class SlabBlock
extends Block
implements Waterloggable {
    public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

    public SlabBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, false));
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return arg.get(TYPE) != SlabType.DOUBLE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        SlabType lv = arg.get(TYPE);
        switch (lv) {
            case DOUBLE: {
                return VoxelShapes.fullCube();
            }
            case TOP: {
                return TOP_SHAPE;
            }
        }
        return BOTTOM_SHAPE;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv3;
        BlockPos lv = arg.getBlockPos();
        BlockState lv2 = arg.getWorld().getBlockState(lv);
        if (lv2.isOf(this)) {
            return (BlockState)((BlockState)lv2.with(TYPE, SlabType.DOUBLE)).with(WATERLOGGED, false);
        }
        BlockState lv4 = (BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, (lv3 = arg.getWorld().getFluidState(lv)).getFluid() == Fluids.WATER);
        Direction lv5 = arg.getSide();
        if (lv5 == Direction.DOWN || lv5 != Direction.UP && arg.getHitPos().y - (double)lv.getY() > 0.5) {
            return (BlockState)lv4.with(TYPE, SlabType.TOP);
        }
        return lv4;
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        ItemStack lv = arg2.getStack();
        SlabType lv2 = arg.get(TYPE);
        if (lv2 == SlabType.DOUBLE || lv.getItem() != this.asItem()) {
            return false;
        }
        if (arg2.canReplaceExisting()) {
            boolean bl = arg2.getHitPos().y - (double)arg2.getBlockPos().getY() > 0.5;
            Direction lv3 = arg2.getSide();
            if (lv2 == SlabType.BOTTOM) {
                return lv3 == Direction.UP || bl && lv3.getAxis().isHorizontal();
            }
            return lv3 == Direction.DOWN || !bl && lv3.getAxis().isHorizontal();
        }
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public boolean tryFillWithFluid(IWorld arg, BlockPos arg2, BlockState arg3, FluidState arg4) {
        if (arg3.get(TYPE) != SlabType.DOUBLE) {
            return Waterloggable.super.tryFillWithFluid(arg, arg2, arg3, arg4);
        }
        return false;
    }

    @Override
    public boolean canFillWithFluid(BlockView arg, BlockPos arg2, BlockState arg3, Fluid arg4) {
        if (arg3.get(TYPE) != SlabType.DOUBLE) {
            return Waterloggable.super.canFillWithFluid(arg, arg2, arg3, arg4);
        }
        return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        switch (arg4) {
            case LAND: {
                return false;
            }
            case WATER: {
                return arg2.getFluidState(arg3).matches(FluidTags.WATER);
            }
            case AIR: {
                return false;
            }
        }
        return false;
    }
}

