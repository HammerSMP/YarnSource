/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralFanBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DeadCoralWallFanBlock
extends DeadCoralFanBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.createCuboidShape(0.0, 4.0, 5.0, 16.0, 12.0, 16.0), (Object)Direction.SOUTH, (Object)Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 12.0, 11.0), (Object)Direction.WEST, (Object)Block.createCuboidShape(5.0, 4.0, 0.0, 16.0, 12.0, 16.0), (Object)Direction.EAST, (Object)Block.createCuboidShape(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)));

    protected DeadCoralWallFanBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, true));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return FACING_TO_SHAPE.get(arg.get(FACING));
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg.rotate(arg2.getRotation(arg.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2.getOpposite() == arg.get(FACING) && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return arg;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        Direction lv = arg.get(FACING);
        BlockPos lv2 = arg3.offset(lv.getOpposite());
        BlockState lv3 = arg2.getBlockState(lv2);
        return lv3.isSideSolidFullSquare(arg2, lv2, lv);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction[] lvs;
        BlockState lv = super.getPlacementState(arg);
        World lv2 = arg.getWorld();
        BlockPos lv3 = arg.getBlockPos();
        for (Direction lv4 : lvs = arg.getPlacementDirections()) {
            if (!lv4.getAxis().isHorizontal() || !(lv = (BlockState)lv.with(FACING, lv4.getOpposite())).canPlaceAt(lv2, lv3)) continue;
            return lv;
        }
        return null;
    }
}

