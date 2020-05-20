/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PaneBlock
extends HorizontalConnectingBlock {
    protected PaneBlock(AbstractBlock.Settings arg) {
        super(1.0f, 1.0f, 16.0f, 16.0f, 16.0f, arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        FluidState lv3 = arg.getWorld().getFluidState(arg.getBlockPos());
        BlockPos lv4 = lv2.north();
        BlockPos lv5 = lv2.south();
        BlockPos lv6 = lv2.west();
        BlockPos lv7 = lv2.east();
        BlockState lv8 = lv.getBlockState(lv4);
        BlockState lv9 = lv.getBlockState(lv5);
        BlockState lv10 = lv.getBlockState(lv6);
        BlockState lv11 = lv.getBlockState(lv7);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.connectsTo(lv8, lv8.isSideSolidFullSquare(lv, lv4, Direction.SOUTH)))).with(SOUTH, this.connectsTo(lv9, lv9.isSideSolidFullSquare(lv, lv5, Direction.NORTH)))).with(WEST, this.connectsTo(lv10, lv10.isSideSolidFullSquare(lv, lv6, Direction.EAST)))).with(EAST, this.connectsTo(lv11, lv11.isSideSolidFullSquare(lv, lv7, Direction.WEST)))).with(WATERLOGGED, lv3.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2.getAxis().isHorizontal()) {
            return (BlockState)arg.with((Property)FACING_PROPERTIES.get(arg2), this.connectsTo(arg3, arg3.isSideSolidFullSquare(arg4, arg6, arg2.getOpposite())));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public VoxelShape getVisualShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.empty();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState arg, BlockState arg2, Direction arg3) {
        if (arg2.isOf(this)) {
            if (!arg3.getAxis().isHorizontal()) {
                return true;
            }
            if (((Boolean)arg.get((Property)FACING_PROPERTIES.get(arg3))).booleanValue() && ((Boolean)arg2.get((Property)FACING_PROPERTIES.get(arg3.getOpposite()))).booleanValue()) {
                return true;
            }
        }
        return super.isSideInvisible(arg, arg2, arg3);
    }

    public final boolean connectsTo(BlockState arg, boolean bl) {
        Block lv = arg.getBlock();
        return !PaneBlock.cannotConnect(lv) && bl || lv instanceof PaneBlock || lv.isIn(BlockTags.WALLS);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}

