/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MushroomBlock
extends Block {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

    public MushroomBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, true)).with(EAST, true)).with(SOUTH, true)).with(WEST, true)).with(UP, true)).with(DOWN, true));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockPos lv2;
        World lv;
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, this != (lv = arg.getWorld()).getBlockState((lv2 = arg.getBlockPos()).down()).getBlock())).with(UP, this != lv.getBlockState(lv2.up()).getBlock())).with(NORTH, this != lv.getBlockState(lv2.north()).getBlock())).with(EAST, this != lv.getBlockState(lv2.east()).getBlock())).with(SOUTH, this != lv.getBlockState(lv2.south()).getBlock())).with(WEST, this != lv.getBlockState(lv2.west()).getBlock());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg3.isOf(this)) {
            return (BlockState)arg.with(FACING_PROPERTIES.get(arg2), false);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)arg.with(FACING_PROPERTIES.get(arg2.rotate(Direction.NORTH)), arg.get(NORTH))).with(FACING_PROPERTIES.get(arg2.rotate(Direction.SOUTH)), arg.get(SOUTH))).with(FACING_PROPERTIES.get(arg2.rotate(Direction.EAST)), arg.get(EAST))).with(FACING_PROPERTIES.get(arg2.rotate(Direction.WEST)), arg.get(WEST))).with(FACING_PROPERTIES.get(arg2.rotate(Direction.UP)), arg.get(UP))).with(FACING_PROPERTIES.get(arg2.rotate(Direction.DOWN)), arg.get(DOWN));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)arg.with(FACING_PROPERTIES.get(arg2.apply(Direction.NORTH)), arg.get(NORTH))).with(FACING_PROPERTIES.get(arg2.apply(Direction.SOUTH)), arg.get(SOUTH))).with(FACING_PROPERTIES.get(arg2.apply(Direction.EAST)), arg.get(EAST))).with(FACING_PROPERTIES.get(arg2.apply(Direction.WEST)), arg.get(WEST))).with(FACING_PROPERTIES.get(arg2.apply(Direction.UP)), arg.get(UP))).with(FACING_PROPERTIES.get(arg2.apply(Direction.DOWN)), arg.get(DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}

