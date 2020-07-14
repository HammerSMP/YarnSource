/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class VineBlock
extends Block {
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    public VineBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(UP, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape lv = VoxelShapes.empty();
        if (state.get(UP).booleanValue()) {
            lv = VoxelShapes.union(lv, UP_SHAPE);
        }
        if (state.get(NORTH).booleanValue()) {
            lv = VoxelShapes.union(lv, NORTH_SHAPE);
        }
        if (state.get(EAST).booleanValue()) {
            lv = VoxelShapes.union(lv, EAST_SHAPE);
        }
        if (state.get(SOUTH).booleanValue()) {
            lv = VoxelShapes.union(lv, SOUTH_SHAPE);
        }
        if (state.get(WEST).booleanValue()) {
            lv = VoxelShapes.union(lv, WEST_SHAPE);
        }
        return lv;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.hasAdjacentBlocks(this.getPlacementShape(state, world, pos));
    }

    private boolean hasAdjacentBlocks(BlockState state) {
        return this.getAdjacentBlockCount(state) > 0;
    }

    private int getAdjacentBlockCount(BlockState state) {
        int i = 0;
        for (BooleanProperty lv : FACING_PROPERTIES.values()) {
            if (!state.get(lv).booleanValue()) continue;
            ++i;
        }
        return i;
    }

    private boolean shouldHaveSide(BlockView world, BlockPos pos, Direction side) {
        if (side == Direction.DOWN) {
            return false;
        }
        BlockPos lv = pos.offset(side);
        if (VineBlock.shouldConnectTo(world, lv, side)) {
            return true;
        }
        if (side.getAxis() != Direction.Axis.Y) {
            BooleanProperty lv2 = FACING_PROPERTIES.get(side);
            BlockState lv3 = world.getBlockState(pos.up());
            return lv3.isOf(this) && lv3.get(lv2) != false;
        }
        return false;
    }

    public static boolean shouldConnectTo(BlockView world, BlockPos pos, Direction direction) {
        BlockState lv = world.getBlockState(pos);
        return Block.isFaceFullSquare(lv.getCollisionShape(world, pos), direction.getOpposite());
    }

    private BlockState getPlacementShape(BlockState state, BlockView world, BlockPos pos) {
        BlockPos lv = pos.up();
        if (state.get(UP).booleanValue()) {
            state = (BlockState)state.with(UP, VineBlock.shouldConnectTo(world, lv, Direction.DOWN));
        }
        AbstractBlock.AbstractBlockState lv2 = null;
        for (Direction lv3 : Direction.Type.HORIZONTAL) {
            BooleanProperty lv4 = VineBlock.getFacingProperty(lv3);
            if (!state.get(lv4).booleanValue()) continue;
            boolean bl = this.shouldHaveSide(world, pos, lv3);
            if (!bl) {
                if (lv2 == null) {
                    lv2 = world.getBlockState(lv);
                }
                bl = lv2.isOf(this) && lv2.get(lv4) != false;
            }
            state = (BlockState)state.with(lv4, bl);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }
        BlockState lv = this.getPlacementShape(state, world, pos);
        if (!this.hasAdjacentBlocks(lv)) {
            return Blocks.AIR.getDefaultState();
        }
        return lv;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState lv15;
        BlockState lv14;
        BlockPos lv12;
        BlockState lv13;
        if (world.random.nextInt(4) != 0) {
            return;
        }
        Direction lv = Direction.random(random);
        BlockPos lv2 = pos.up();
        if (lv.getAxis().isHorizontal() && !state.get(VineBlock.getFacingProperty(lv)).booleanValue()) {
            if (!this.canGrowAt(world, pos)) {
                return;
            }
            BlockPos lv3 = pos.offset(lv);
            BlockState lv4 = world.getBlockState(lv3);
            if (lv4.isAir()) {
                Direction lv5 = lv.rotateYClockwise();
                Direction lv6 = lv.rotateYCounterclockwise();
                boolean bl = state.get(VineBlock.getFacingProperty(lv5));
                boolean bl2 = state.get(VineBlock.getFacingProperty(lv6));
                BlockPos lv7 = lv3.offset(lv5);
                BlockPos lv8 = lv3.offset(lv6);
                if (bl && VineBlock.shouldConnectTo(world, lv7, lv5)) {
                    world.setBlockState(lv3, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv5), true), 2);
                } else if (bl2 && VineBlock.shouldConnectTo(world, lv8, lv6)) {
                    world.setBlockState(lv3, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv6), true), 2);
                } else {
                    Direction lv9 = lv.getOpposite();
                    if (bl && world.isAir(lv7) && VineBlock.shouldConnectTo(world, pos.offset(lv5), lv9)) {
                        world.setBlockState(lv7, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv9), true), 2);
                    } else if (bl2 && world.isAir(lv8) && VineBlock.shouldConnectTo(world, pos.offset(lv6), lv9)) {
                        world.setBlockState(lv8, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv9), true), 2);
                    } else if ((double)world.random.nextFloat() < 0.05 && VineBlock.shouldConnectTo(world, lv3.up(), Direction.UP)) {
                        world.setBlockState(lv3, (BlockState)this.getDefaultState().with(UP, true), 2);
                    }
                }
            } else if (VineBlock.shouldConnectTo(world, lv3, lv)) {
                world.setBlockState(pos, (BlockState)state.with(VineBlock.getFacingProperty(lv), true), 2);
            }
            return;
        }
        if (lv == Direction.UP && pos.getY() < 255) {
            if (this.shouldHaveSide(world, pos, lv)) {
                world.setBlockState(pos, (BlockState)state.with(UP, true), 2);
                return;
            }
            if (world.isAir(lv2)) {
                if (!this.canGrowAt(world, pos)) {
                    return;
                }
                BlockState lv10 = state;
                for (Direction lv11 : Direction.Type.HORIZONTAL) {
                    if (!random.nextBoolean() && VineBlock.shouldConnectTo(world, lv2.offset(lv11), Direction.UP)) continue;
                    lv10 = (BlockState)lv10.with(VineBlock.getFacingProperty(lv11), false);
                }
                if (this.hasHorizontalSide(lv10)) {
                    world.setBlockState(lv2, lv10, 2);
                }
                return;
            }
        }
        if (pos.getY() > 0 && ((lv13 = world.getBlockState(lv12 = pos.down())).isAir() || lv13.isOf(this)) && (lv14 = lv13.isAir() ? this.getDefaultState() : lv13) != (lv15 = this.getGrownState(state, lv14, random)) && this.hasHorizontalSide(lv15)) {
            world.setBlockState(lv12, lv15, 2);
        }
    }

    private BlockState getGrownState(BlockState above, BlockState state, Random random) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BooleanProperty lv2;
            if (!random.nextBoolean() || !above.get(lv2 = VineBlock.getFacingProperty(lv)).booleanValue()) continue;
            state = (BlockState)state.with(lv2, true);
        }
        return state;
    }

    private boolean hasHorizontalSide(BlockState state) {
        return state.get(NORTH) != false || state.get(EAST) != false || state.get(SOUTH) != false || state.get(WEST) != false;
    }

    private boolean canGrowAt(BlockView world, BlockPos pos) {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.iterate(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
        int j = 5;
        for (BlockPos lv : iterable) {
            if (!world.getBlockState(lv).isOf(this) || --j > 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        BlockState lv = context.getWorld().getBlockState(context.getBlockPos());
        if (lv.isOf(this)) {
            return this.getAdjacentBlockCount(lv) < FACING_PROPERTIES.size();
        }
        return super.canReplace(state, context);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState lv = ctx.getWorld().getBlockState(ctx.getBlockPos());
        boolean bl = lv.isOf(this);
        BlockState lv2 = bl ? lv : this.getDefaultState();
        for (Direction lv3 : ctx.getPlacementDirections()) {
            boolean bl2;
            if (lv3 == Direction.DOWN) continue;
            BooleanProperty lv4 = VineBlock.getFacingProperty(lv3);
            boolean bl3 = bl2 = bl && lv.get(lv4) != false;
            if (bl2 || !this.shouldHaveSide(ctx.getWorld(), ctx.getBlockPos(), lv3)) continue;
            return (BlockState)lv2.with(lv4, true);
        }
        return bl ? lv2 : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(EAST, state.get(WEST))).with(SOUTH, state.get(NORTH))).with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(EAST))).with(EAST, state.get(SOUTH))).with(SOUTH, state.get(WEST))).with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, state.get(WEST))).with(EAST, state.get(NORTH))).with(SOUTH, state.get(EAST))).with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(EAST, state.get(WEST))).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    public static BooleanProperty getFacingProperty(Direction direction) {
        return FACING_PROPERTIES.get(direction);
    }
}

