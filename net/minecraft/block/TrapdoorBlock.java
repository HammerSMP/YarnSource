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
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapdoorBlock
extends HorizontalFacingBlock
implements Waterloggable {
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

    protected TrapdoorBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(OPEN, false)).with(HALF, BlockHalf.BOTTOM)).with(POWERED, false)).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (!arg.get(OPEN).booleanValue()) {
            return arg.get(HALF) == BlockHalf.TOP ? OPEN_TOP_SHAPE : OPEN_BOTTOM_SHAPE;
        }
        switch (arg.get(FACING)) {
            default: {
                return NORTH_SHAPE;
            }
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
            case EAST: 
        }
        return EAST_SHAPE;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        switch (arg4) {
            case LAND: {
                return arg.get(OPEN);
            }
            case WATER: {
                return arg.get(WATERLOGGED);
            }
            case AIR: {
                return arg.get(OPEN);
            }
        }
        return false;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (this.material == Material.METAL) {
            return ActionResult.PASS;
        }
        arg = (BlockState)arg.cycle(OPEN);
        arg2.setBlockState(arg3, arg, 2);
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg2.getFluidTickScheduler().schedule(arg3, Fluids.WATER, Fluids.WATER.getTickRate(arg2));
        }
        this.playToggleSound(arg4, arg2, arg3, arg.get(OPEN));
        return ActionResult.SUCCESS;
    }

    protected void playToggleSound(@Nullable PlayerEntity arg, World arg2, BlockPos arg3, boolean bl) {
        if (bl) {
            int i = this.material == Material.METAL ? 1037 : 1007;
            arg2.syncWorldEvent(arg, i, arg3, 0);
        } else {
            int j = this.material == Material.METAL ? 1036 : 1013;
            arg2.syncWorldEvent(arg, j, arg3, 0);
        }
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        boolean bl2 = arg2.isReceivingRedstonePower(arg3);
        if (bl2 != arg.get(POWERED)) {
            if (arg.get(OPEN) != bl2) {
                arg = (BlockState)arg.with(OPEN, bl2);
                this.playToggleSound(null, arg2, arg3, bl2);
            }
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, bl2), 2);
            if (arg.get(WATERLOGGED).booleanValue()) {
                arg2.getFluidTickScheduler().schedule(arg3, Fluids.WATER, Fluids.WATER.getTickRate(arg2));
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = this.getDefaultState();
        FluidState lv2 = arg.getWorld().getFluidState(arg.getBlockPos());
        Direction lv3 = arg.getSide();
        lv = arg.canReplaceExisting() || !lv3.getAxis().isHorizontal() ? (BlockState)((BlockState)lv.with(FACING, arg.getPlayerFacing().getOpposite())).with(HALF, lv3 == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP) : (BlockState)((BlockState)lv.with(FACING, lv3)).with(HALF, arg.getHitPos().y - (double)arg.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
        if (arg.getWorld().isReceivingRedstonePower(arg.getBlockPos())) {
            lv = (BlockState)((BlockState)lv.with(OPEN, true)).with(POWERED, true);
        }
        return (BlockState)lv.with(WATERLOGGED, lv2.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }
}

