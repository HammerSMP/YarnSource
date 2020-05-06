/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock
extends Block
implements Waterloggable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape TOP_SHAPE = SlabBlock.TOP_SHAPE;
    protected static final VoxelShape BOTTOM_SHAPE = SlabBlock.BOTTOM_SHAPE;
    protected static final VoxelShape BOTTOM_NORTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
    protected static final VoxelShape BOTTOM_SOUTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
    protected static final VoxelShape TOP_NORTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
    protected static final VoxelShape TOP_SOUTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_NORTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
    protected static final VoxelShape BOTTOM_SOUTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_NORTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
    protected static final VoxelShape TOP_SOUTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape[] TOP_SHAPES = StairsBlock.composeShapes(TOP_SHAPE, BOTTOM_NORTH_WEST_CORNER_SHAPE, BOTTOM_NORTH_EAST_CORNER_SHAPE, BOTTOM_SOUTH_WEST_CORNER_SHAPE, BOTTOM_SOUTH_EAST_CORNER_SHAPE);
    protected static final VoxelShape[] BOTTOM_SHAPES = StairsBlock.composeShapes(BOTTOM_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_EAST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE);
    private static final int[] SHAPE_INDICES = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
    private final Block baseBlock;
    private final BlockState baseBlockState;

    private static VoxelShape[] composeShapes(VoxelShape arg, VoxelShape arg2, VoxelShape arg3, VoxelShape arg4, VoxelShape arg5) {
        return (VoxelShape[])IntStream.range(0, 16).mapToObj(i -> StairsBlock.composeShape(i, arg, arg2, arg3, arg4, arg5)).toArray(VoxelShape[]::new);
    }

    private static VoxelShape composeShape(int i, VoxelShape arg, VoxelShape arg2, VoxelShape arg3, VoxelShape arg4, VoxelShape arg5) {
        VoxelShape lv = arg;
        if ((i & 1) != 0) {
            lv = VoxelShapes.union(lv, arg2);
        }
        if ((i & 2) != 0) {
            lv = VoxelShapes.union(lv, arg3);
        }
        if ((i & 4) != 0) {
            lv = VoxelShapes.union(lv, arg4);
        }
        if ((i & 8) != 0) {
            lv = VoxelShapes.union(lv, arg5);
        }
        return lv;
    }

    protected StairsBlock(BlockState arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(HALF, BlockHalf.BOTTOM)).with(SHAPE, StairShape.STRAIGHT)).with(WATERLOGGED, false));
        this.baseBlock = arg.getBlock();
        this.baseBlockState = arg;
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return (arg.get(HALF) == BlockHalf.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_INDICES[this.getShapeIndexIndex(arg)]];
    }

    private int getShapeIndexIndex(BlockState arg) {
        return arg.get(SHAPE).ordinal() * 4 + arg.get(FACING).getHorizontal();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        this.baseBlock.randomDisplayTick(arg, arg2, arg3, random);
    }

    @Override
    public void onBlockBreakStart(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        this.baseBlockState.onBlockBreakStart(arg2, arg3, arg4);
    }

    @Override
    public void onBroken(IWorld arg, BlockPos arg2, BlockState arg3) {
        this.baseBlock.onBroken(arg, arg2, arg3);
    }

    @Override
    public float getBlastResistance() {
        return this.baseBlock.getBlastResistance();
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg.getBlock())) {
            return;
        }
        this.baseBlockState.neighborUpdate(arg2, arg3, Blocks.AIR, arg3, false);
        this.baseBlock.onBlockAdded(this.baseBlockState, arg2, arg3, arg4, false);
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        this.baseBlockState.onBlockRemoved(arg2, arg3, arg4, bl);
    }

    @Override
    public void onSteppedOn(World arg, BlockPos arg2, Entity arg3) {
        this.baseBlock.onSteppedOn(arg, arg2, arg3);
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return this.baseBlock.hasRandomTicks(arg);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.baseBlock.randomTick(arg, arg2, arg3, random);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.baseBlock.scheduledTick(arg, arg2, arg3, random);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        return this.baseBlockState.onUse(arg2, arg4, arg5, arg6);
    }

    @Override
    public void onDestroyedByExplosion(World arg, BlockPos arg2, Explosion arg3) {
        this.baseBlock.onDestroyedByExplosion(arg, arg2, arg3);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction lv = arg.getSide();
        BlockPos lv2 = arg.getBlockPos();
        FluidState lv3 = arg.getWorld().getFluidState(lv2);
        BlockState lv4 = (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing())).with(HALF, lv == Direction.DOWN || lv != Direction.UP && arg.getHitPos().y - (double)lv2.getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM)).with(WATERLOGGED, lv3.getFluid() == Fluids.WATER);
        return (BlockState)lv4.with(SHAPE, StairsBlock.getStairShape(lv4, arg.getWorld(), lv2));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2.getAxis().isHorizontal()) {
            return (BlockState)arg.with(SHAPE, StairsBlock.getStairShape(arg, arg4, arg5));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    private static StairShape getStairShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        Direction lv5;
        Direction lv3;
        Direction lv = arg.get(FACING);
        BlockState lv2 = arg2.getBlockState(arg3.offset(lv));
        if (StairsBlock.isStairs(lv2) && arg.get(HALF) == lv2.get(HALF) && (lv3 = lv2.get(FACING)).getAxis() != arg.get(FACING).getAxis() && StairsBlock.method_10678(arg, arg2, arg3, lv3.getOpposite())) {
            if (lv3 == lv.rotateYCounterclockwise()) {
                return StairShape.OUTER_LEFT;
            }
            return StairShape.OUTER_RIGHT;
        }
        BlockState lv4 = arg2.getBlockState(arg3.offset(lv.getOpposite()));
        if (StairsBlock.isStairs(lv4) && arg.get(HALF) == lv4.get(HALF) && (lv5 = lv4.get(FACING)).getAxis() != arg.get(FACING).getAxis() && StairsBlock.method_10678(arg, arg2, arg3, lv5)) {
            if (lv5 == lv.rotateYCounterclockwise()) {
                return StairShape.INNER_LEFT;
            }
            return StairShape.INNER_RIGHT;
        }
        return StairShape.STRAIGHT;
    }

    private static boolean method_10678(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        BlockState lv = arg2.getBlockState(arg3.offset(arg4));
        return !StairsBlock.isStairs(lv) || lv.get(FACING) != arg.get(FACING) || lv.get(HALF) != arg.get(HALF);
    }

    public static boolean isStairs(BlockState arg) {
        return arg.getBlock() instanceof StairsBlock;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        Direction lv = arg.get(FACING);
        StairShape lv2 = arg.get(SHAPE);
        switch (arg2) {
            case LEFT_RIGHT: {
                if (lv.getAxis() != Direction.Axis.Z) break;
                switch (lv2) {
                    case INNER_LEFT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                    }
                    case INNER_RIGHT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                    }
                    case OUTER_LEFT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                    }
                    case OUTER_RIGHT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                    }
                }
                return arg.rotate(BlockRotation.CLOCKWISE_180);
            }
            case FRONT_BACK: {
                if (lv.getAxis() != Direction.Axis.X) break;
                switch (lv2) {
                    case INNER_LEFT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
                    }
                    case INNER_RIGHT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
                    }
                    case OUTER_LEFT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
                    }
                    case OUTER_RIGHT: {
                        return (BlockState)arg.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
                    }
                    case STRAIGHT: {
                        return arg.rotate(BlockRotation.CLOCKWISE_180);
                    }
                }
                break;
            }
        }
        return super.mirror(arg, arg2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, HALF, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

