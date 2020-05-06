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
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class WallSignBlock
extends AbstractSignBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.createCuboidShape(0.0, 4.5, 14.0, 16.0, 12.5, 16.0), (Object)Direction.SOUTH, (Object)Block.createCuboidShape(0.0, 4.5, 0.0, 16.0, 12.5, 2.0), (Object)Direction.EAST, (Object)Block.createCuboidShape(0.0, 4.5, 0.0, 2.0, 12.5, 16.0), (Object)Direction.WEST, (Object)Block.createCuboidShape(14.0, 4.5, 0.0, 16.0, 12.5, 16.0)));

    public WallSignBlock(AbstractBlock.Settings arg, SignType arg2) {
        super(arg, arg2);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return FACING_TO_SHAPE.get(arg.get(FACING));
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return arg2.getBlockState(arg3.offset(arg.get(FACING).getOpposite())).getMaterial().isSolid();
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction[] lvs;
        BlockState lv = this.getDefaultState();
        FluidState lv2 = arg.getWorld().getFluidState(arg.getBlockPos());
        World lv3 = arg.getWorld();
        BlockPos lv4 = arg.getBlockPos();
        for (Direction lv5 : lvs = arg.getPlacementDirections()) {
            Direction lv6;
            if (!lv5.getAxis().isHorizontal() || !(lv = (BlockState)lv.with(FACING, lv6 = lv5.getOpposite())).canPlaceAt(lv3, lv4)) continue;
            return (BlockState)lv.with(WATERLOGGED, lv2.getFluid() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2.getOpposite() == arg.get(FACING) && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
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
}

