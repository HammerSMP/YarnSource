/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 */
package net.minecraft.block;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty EYE = Properties.EYE;
    protected static final VoxelShape FRAME_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    protected static final VoxelShape EYE_SHAPE = Block.createCuboidShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape FRAME_WITH_EYE_SHAPE = VoxelShapes.union(FRAME_SHAPE, EYE_SHAPE);
    private static BlockPattern COMPLETED_FRAME;

    public EndPortalFrameBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(EYE, false));
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return arg.get(EYE) != false ? FRAME_WITH_EYE_SHAPE : FRAME_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing().getOpposite())).with(EYE, false);
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        if (arg.get(EYE).booleanValue()) {
            return 15;
        }
        return 0;
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
        arg.add(FACING, EYE);
    }

    public static BlockPattern getCompletedFramePattern() {
        if (COMPLETED_FRAME == null) {
            COMPLETED_FRAME = BlockPatternBuilder.start().aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?").where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY)).where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.SOUTH)))).where('>', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.WEST)))).where('v', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.NORTH)))).where('<', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(EYE, (Predicate<Object>)Predicates.equalTo((Object)true)).with(FACING, (Predicate<Object>)Predicates.equalTo((Object)Direction.EAST)))).build();
        }
        return COMPLETED_FRAME;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

