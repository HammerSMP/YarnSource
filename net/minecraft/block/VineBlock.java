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
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        VoxelShape lv = VoxelShapes.empty();
        if (arg.get(UP).booleanValue()) {
            lv = VoxelShapes.union(lv, UP_SHAPE);
        }
        if (arg.get(NORTH).booleanValue()) {
            lv = VoxelShapes.union(lv, NORTH_SHAPE);
        }
        if (arg.get(EAST).booleanValue()) {
            lv = VoxelShapes.union(lv, EAST_SHAPE);
        }
        if (arg.get(SOUTH).booleanValue()) {
            lv = VoxelShapes.union(lv, SOUTH_SHAPE);
        }
        if (arg.get(WEST).booleanValue()) {
            lv = VoxelShapes.union(lv, WEST_SHAPE);
        }
        return lv;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return this.hasAdjacentBlocks(this.getPlacementShape(arg, arg2, arg3));
    }

    private boolean hasAdjacentBlocks(BlockState arg) {
        return this.getAdjacentBlockCount(arg) > 0;
    }

    private int getAdjacentBlockCount(BlockState arg) {
        int i = 0;
        for (BooleanProperty lv : FACING_PROPERTIES.values()) {
            if (!arg.get(lv).booleanValue()) continue;
            ++i;
        }
        return i;
    }

    private boolean shouldHaveSide(BlockView arg, BlockPos arg2, Direction arg3) {
        if (arg3 == Direction.DOWN) {
            return false;
        }
        BlockPos lv = arg2.offset(arg3);
        if (VineBlock.shouldConnectTo(arg, lv, arg3)) {
            return true;
        }
        if (arg3.getAxis() != Direction.Axis.Y) {
            BooleanProperty lv2 = FACING_PROPERTIES.get(arg3);
            BlockState lv3 = arg.getBlockState(arg2.up());
            return lv3.isOf(this) && lv3.get(lv2) != false;
        }
        return false;
    }

    public static boolean shouldConnectTo(BlockView arg, BlockPos arg2, Direction arg3) {
        BlockState lv = arg.getBlockState(arg2);
        return Block.isFaceFullSquare(lv.getCollisionShape(arg, arg2), arg3.getOpposite());
    }

    private BlockState getPlacementShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        BlockPos lv = arg3.up();
        if (arg.get(UP).booleanValue()) {
            arg = (BlockState)arg.with(UP, VineBlock.shouldConnectTo(arg2, lv, Direction.DOWN));
        }
        AbstractBlock.AbstractBlockState lv2 = null;
        for (Direction lv3 : Direction.Type.HORIZONTAL) {
            BooleanProperty lv4 = VineBlock.getFacingProperty(lv3);
            if (!arg.get(lv4).booleanValue()) continue;
            boolean bl = this.shouldHaveSide(arg2, arg3, lv3);
            if (!bl) {
                if (lv2 == null) {
                    lv2 = arg2.getBlockState(lv);
                }
                bl = lv2.isOf(this) && lv2.get(lv4) != false;
            }
            arg = (BlockState)arg.with(lv4, bl);
        }
        return arg;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.DOWN) {
            return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
        }
        BlockState lv = this.getPlacementShape(arg, arg4, arg5);
        if (!this.hasAdjacentBlocks(lv)) {
            return Blocks.AIR.getDefaultState();
        }
        return lv;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BlockState lv15;
        BlockState lv14;
        BlockPos lv12;
        BlockState lv13;
        if (arg2.random.nextInt(4) != 0) {
            return;
        }
        Direction lv = Direction.random(random);
        BlockPos lv2 = arg3.up();
        if (lv.getAxis().isHorizontal() && !arg.get(VineBlock.getFacingProperty(lv)).booleanValue()) {
            if (!this.canGrowAt(arg2, arg3)) {
                return;
            }
            BlockPos lv3 = arg3.offset(lv);
            BlockState lv4 = arg2.getBlockState(lv3);
            if (lv4.isAir()) {
                Direction lv5 = lv.rotateYClockwise();
                Direction lv6 = lv.rotateYCounterclockwise();
                boolean bl = arg.get(VineBlock.getFacingProperty(lv5));
                boolean bl2 = arg.get(VineBlock.getFacingProperty(lv6));
                BlockPos lv7 = lv3.offset(lv5);
                BlockPos lv8 = lv3.offset(lv6);
                if (bl && VineBlock.shouldConnectTo(arg2, lv7, lv5)) {
                    arg2.setBlockState(lv3, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv5), true), 2);
                } else if (bl2 && VineBlock.shouldConnectTo(arg2, lv8, lv6)) {
                    arg2.setBlockState(lv3, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv6), true), 2);
                } else {
                    Direction lv9 = lv.getOpposite();
                    if (bl && arg2.isAir(lv7) && VineBlock.shouldConnectTo(arg2, arg3.offset(lv5), lv9)) {
                        arg2.setBlockState(lv7, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv9), true), 2);
                    } else if (bl2 && arg2.isAir(lv8) && VineBlock.shouldConnectTo(arg2, arg3.offset(lv6), lv9)) {
                        arg2.setBlockState(lv8, (BlockState)this.getDefaultState().with(VineBlock.getFacingProperty(lv9), true), 2);
                    } else if ((double)arg2.random.nextFloat() < 0.05 && VineBlock.shouldConnectTo(arg2, lv3.up(), Direction.UP)) {
                        arg2.setBlockState(lv3, (BlockState)this.getDefaultState().with(UP, true), 2);
                    }
                }
            } else if (VineBlock.shouldConnectTo(arg2, lv3, lv)) {
                arg2.setBlockState(arg3, (BlockState)arg.with(VineBlock.getFacingProperty(lv), true), 2);
            }
            return;
        }
        if (lv == Direction.UP && arg3.getY() < 255) {
            if (this.shouldHaveSide(arg2, arg3, lv)) {
                arg2.setBlockState(arg3, (BlockState)arg.with(UP, true), 2);
                return;
            }
            if (arg2.isAir(lv2)) {
                if (!this.canGrowAt(arg2, arg3)) {
                    return;
                }
                BlockState lv10 = arg;
                for (Direction lv11 : Direction.Type.HORIZONTAL) {
                    if (!random.nextBoolean() && VineBlock.shouldConnectTo(arg2, lv2.offset(lv11), Direction.UP)) continue;
                    lv10 = (BlockState)lv10.with(VineBlock.getFacingProperty(lv11), false);
                }
                if (this.hasHorizontalSide(lv10)) {
                    arg2.setBlockState(lv2, lv10, 2);
                }
                return;
            }
        }
        if (arg3.getY() > 0 && ((lv13 = arg2.getBlockState(lv12 = arg3.down())).isAir() || lv13.isOf(this)) && (lv14 = lv13.isAir() ? this.getDefaultState() : lv13) != (lv15 = this.getGrownState(arg, lv14, random)) && this.hasHorizontalSide(lv15)) {
            arg2.setBlockState(lv12, lv15, 2);
        }
    }

    private BlockState getGrownState(BlockState arg, BlockState arg2, Random random) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BooleanProperty lv2;
            if (!random.nextBoolean() || !arg.get(lv2 = VineBlock.getFacingProperty(lv)).booleanValue()) continue;
            arg2 = (BlockState)arg2.with(lv2, true);
        }
        return arg2;
    }

    private boolean hasHorizontalSide(BlockState arg) {
        return arg.get(NORTH) != false || arg.get(EAST) != false || arg.get(SOUTH) != false || arg.get(WEST) != false;
    }

    private boolean canGrowAt(BlockView arg, BlockPos arg2) {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.iterate(arg2.getX() - 4, arg2.getY() - 1, arg2.getZ() - 4, arg2.getX() + 4, arg2.getY() + 1, arg2.getZ() + 4);
        int j = 5;
        for (BlockPos lv : iterable) {
            if (!arg.getBlockState(lv).isOf(this) || --j > 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        BlockState lv = arg2.getWorld().getBlockState(arg2.getBlockPos());
        if (lv.isOf(this)) {
            return this.getAdjacentBlockCount(lv) < FACING_PROPERTIES.size();
        }
        return super.canReplace(arg, arg2);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos());
        boolean bl = lv.isOf(this);
        BlockState lv2 = bl ? lv : this.getDefaultState();
        for (Direction lv3 : arg.getPlacementDirections()) {
            boolean bl2;
            if (lv3 == Direction.DOWN) continue;
            BooleanProperty lv4 = VineBlock.getFacingProperty(lv3);
            boolean bl3 = bl2 = bl && lv.get(lv4) != false;
            if (bl2 || !this.shouldHaveSide(arg.getWorld(), arg.getBlockPos(), lv3)) continue;
            return (BlockState)lv2.with(lv4, true);
        }
        return bl ? lv2 : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(UP, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(SOUTH))).with(EAST, arg.get(WEST))).with(SOUTH, arg.get(NORTH))).with(WEST, arg.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(EAST))).with(EAST, arg.get(SOUTH))).with(SOUTH, arg.get(WEST))).with(WEST, arg.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(NORTH, arg.get(WEST))).with(EAST, arg.get(NORTH))).with(SOUTH, arg.get(EAST))).with(WEST, arg.get(SOUTH));
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        switch (arg2) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)arg.with(NORTH, arg.get(SOUTH))).with(SOUTH, arg.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)arg.with(EAST, arg.get(WEST))).with(WEST, arg.get(EAST));
            }
        }
        return super.mirror(arg, arg2);
    }

    public static BooleanProperty getFacingProperty(Direction arg) {
        return FACING_PROPERTIES.get(arg);
    }
}

