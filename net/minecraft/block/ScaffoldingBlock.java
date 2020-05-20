/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ScaffoldingBlock
extends Block
implements Waterloggable {
    private static final VoxelShape NORMAL_OUTLINE_SHAPE;
    private static final VoxelShape BOTTOM_OUTLINE_SHAPE;
    private static final VoxelShape COLLISION_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;
    public static final IntProperty DISTANCE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty BOTTOM;

    protected ScaffoldingBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DISTANCE, 7)).with(WATERLOGGED, false)).with(BOTTOM, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (!arg4.isHolding(arg.getBlock().asItem())) {
            return arg.get(BOTTOM) != false ? BOTTOM_OUTLINE_SHAPE : NORMAL_OUTLINE_SHAPE;
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return VoxelShapes.fullCube();
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        return arg2.getStack().getItem() == this.asItem();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockPos lv = arg.getBlockPos();
        World lv2 = arg.getWorld();
        int i = ScaffoldingBlock.calculateDistance(lv2, lv);
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, lv2.getFluidState(lv).getFluid() == Fluids.WATER)).with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(lv2, lv, i));
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (!arg2.isClient) {
            arg2.getBlockTickScheduler().schedule(arg3, this, 1);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (!arg4.isClient()) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        return arg;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        int i = ScaffoldingBlock.calculateDistance(arg2, arg3);
        BlockState lv = (BlockState)((BlockState)arg.with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(arg2, arg3, i));
        if (lv.get(DISTANCE) == 7) {
            if (arg.get(DISTANCE) == 7) {
                arg2.spawnEntity(new FallingBlockEntity(arg2, (double)arg3.getX() + 0.5, arg3.getY(), (double)arg3.getZ() + 0.5, (BlockState)lv.with(WATERLOGGED, false)));
            } else {
                arg2.breakBlock(arg3, true);
            }
        } else if (arg != lv) {
            arg2.setBlockState(arg3, lv, 3);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return ScaffoldingBlock.calculateDistance(arg2, arg3) < 7;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (!arg4.isAbove(VoxelShapes.fullCube(), arg3, true) || arg4.isDescending()) {
            if (arg.get(DISTANCE) != 0 && arg.get(BOTTOM).booleanValue() && arg4.isAbove(OUTLINE_SHAPE, arg3, true)) {
                return COLLISION_SHAPE;
            }
            return VoxelShapes.empty();
        }
        return NORMAL_OUTLINE_SHAPE;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    private boolean shouldBeBottom(BlockView arg, BlockPos arg2, int i) {
        return i > 0 && !arg.getBlockState(arg2.down()).isOf(this);
    }

    public static int calculateDistance(BlockView arg, BlockPos arg2) {
        Direction lv3;
        BlockState lv4;
        BlockPos.Mutable lv = arg2.mutableCopy().move(Direction.DOWN);
        BlockState lv2 = arg.getBlockState(lv);
        int i = 7;
        if (lv2.isOf(Blocks.SCAFFOLDING)) {
            i = lv2.get(DISTANCE);
        } else if (lv2.isSideSolidFullSquare(arg, lv, Direction.UP)) {
            return 0;
        }
        Iterator<Direction> iterator = Direction.Type.HORIZONTAL.iterator();
        while (iterator.hasNext() && (!(lv4 = arg.getBlockState(lv.set(arg2, lv3 = iterator.next()))).isOf(Blocks.SCAFFOLDING) || (i = Math.min(i, lv4.get(DISTANCE) + 1)) != 1)) {
        }
        return i;
    }

    static {
        COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0);
        DISTANCE = Properties.DISTANCE_0_7;
        WATERLOGGED = Properties.WATERLOGGED;
        BOTTOM = Properties.BOTTOM;
        VoxelShape lv = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape lv2 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
        VoxelShape lv3 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
        VoxelShape lv4 = Block.createCuboidShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
        VoxelShape lv5 = Block.createCuboidShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
        NORMAL_OUTLINE_SHAPE = VoxelShapes.union(lv, lv2, lv3, lv4, lv5);
        VoxelShape lv6 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
        VoxelShape lv7 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        VoxelShape lv8 = Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
        VoxelShape lv9 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
        BOTTOM_OUTLINE_SHAPE = VoxelShapes.union(COLLISION_SHAPE, NORMAL_OUTLINE_SHAPE, lv7, lv6, lv9, lv8);
    }
}

