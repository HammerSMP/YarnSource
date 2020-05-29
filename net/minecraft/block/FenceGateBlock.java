/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FenceGateBlock
extends HorizontalFacingBlock {
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty IN_WALL = Properties.IN_WALL;
    protected static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    protected static final VoxelShape IN_WALL_Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
    protected static final VoxelShape IN_WALL_X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
    protected static final VoxelShape Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
    protected static final VoxelShape X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
    protected static final VoxelShape Z_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0));
    protected static final VoxelShape X_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0));
    protected static final VoxelShape IN_WALL_Z_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0));
    protected static final VoxelShape IN_WALL_X_AXIS_CULL_SHAPE = VoxelShapes.union(Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0));

    public FenceGateBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(OPEN, false)).with(POWERED, false)).with(IN_WALL, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg.get(IN_WALL).booleanValue()) {
            return arg.get(FACING).getAxis() == Direction.Axis.X ? IN_WALL_X_AXIS_SHAPE : IN_WALL_Z_AXIS_SHAPE;
        }
        return arg.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        Direction.Axis lv = arg2.getAxis();
        if (arg.get(FACING).rotateYClockwise().getAxis() == lv) {
            boolean bl = this.isWall(arg3) || this.isWall(arg4.getBlockState(arg5.offset(arg2.getOpposite())));
            return (BlockState)arg.with(IN_WALL, bl);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg.get(OPEN).booleanValue()) {
            return VoxelShapes.empty();
        }
        return arg.get(FACING).getAxis() == Direction.Axis.Z ? Z_AXIS_COLLISION_SHAPE : X_AXIS_COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        if (arg.get(IN_WALL).booleanValue()) {
            return arg.get(FACING).getAxis() == Direction.Axis.X ? IN_WALL_X_AXIS_CULL_SHAPE : IN_WALL_Z_AXIS_CULL_SHAPE;
        }
        return arg.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_CULL_SHAPE : Z_AXIS_CULL_SHAPE;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        switch (arg4) {
            case LAND: {
                return arg.get(OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return arg.get(OPEN);
            }
        }
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        boolean bl = lv.isReceivingRedstonePower(lv2);
        Direction lv3 = arg.getPlayerFacing();
        Direction.Axis lv4 = lv3.getAxis();
        boolean bl2 = lv4 == Direction.Axis.Z && (this.isWall(lv.getBlockState(lv2.west())) || this.isWall(lv.getBlockState(lv2.east()))) || lv4 == Direction.Axis.X && (this.isWall(lv.getBlockState(lv2.north())) || this.isWall(lv.getBlockState(lv2.south())));
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, lv3)).with(OPEN, bl)).with(POWERED, bl)).with(IN_WALL, bl2);
    }

    private boolean isWall(BlockState arg) {
        return arg.getBlock().isIn(BlockTags.WALLS);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg.get(OPEN).booleanValue()) {
            arg = (BlockState)arg.with(OPEN, false);
            arg2.setBlockState(arg3, arg, 10);
        } else {
            Direction lv = arg4.getHorizontalFacing();
            if (arg.get(FACING) == lv.getOpposite()) {
                arg = (BlockState)arg.with(FACING, lv);
            }
            arg = (BlockState)arg.with(OPEN, true);
            arg2.setBlockState(arg3, arg, 10);
        }
        arg2.syncWorldEvent(arg4, arg.get(OPEN) != false ? 1008 : 1014, arg3, 0);
        return ActionResult.method_29236(arg2.isClient);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        boolean bl2 = arg2.isReceivingRedstonePower(arg3);
        if (arg.get(POWERED) != bl2) {
            arg2.setBlockState(arg3, (BlockState)((BlockState)arg.with(POWERED, bl2)).with(OPEN, bl2), 2);
            if (arg.get(OPEN) != bl2) {
                arg2.syncWorldEvent(null, bl2 ? 1008 : 1014, arg3, 0);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, OPEN, POWERED, IN_WALL);
    }

    public static boolean canWallConnect(BlockState arg, Direction arg2) {
        return arg.get(FACING).getAxis() == arg2.rotateYClockwise().getAxis();
    }
}

