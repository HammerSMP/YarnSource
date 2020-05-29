/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
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
import net.minecraft.world.WorldView;

public class BellBlock
extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
    private static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    private static final VoxelShape BELL_WAIST_SHAPE = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
    private static final VoxelShape BELL_LIP_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
    private static final VoxelShape BELL_SHAPE = VoxelShapes.union(BELL_LIP_SHAPE, BELL_WAIST_SHAPE);
    private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
    private static final VoxelShape EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
    private static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
    private static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
    private static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
    private static final VoxelShape HANGING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

    public BellBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(ATTACHMENT, Attachment.FLOOR)).with(POWERED, false));
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        boolean bl2 = arg2.isReceivingRedstonePower(arg3);
        if (bl2 != arg.get(POWERED)) {
            if (bl2) {
                this.ring(arg2, arg3, null);
            }
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, bl2), 3);
        }
    }

    @Override
    public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
        Entity lv = arg4.getOwner();
        PlayerEntity lv2 = lv instanceof PlayerEntity ? (PlayerEntity)lv : null;
        this.ring(arg, arg2, arg3, lv2, true);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        return this.ring(arg2, arg, arg6, arg4, true) ? ActionResult.method_29236(arg2.isClient) : ActionResult.PASS;
    }

    public boolean ring(World arg, BlockState arg2, BlockHitResult arg3, @Nullable PlayerEntity arg4, boolean bl) {
        boolean bl2;
        Direction lv = arg3.getSide();
        BlockPos lv2 = arg3.getBlockPos();
        boolean bl3 = bl2 = !bl || this.isPointOnBell(arg2, lv, arg3.getPos().y - (double)lv2.getY());
        if (bl2) {
            boolean bl32 = this.ring(arg, lv2, lv);
            if (bl32 && arg4 != null) {
                arg4.incrementStat(Stats.BELL_RING);
            }
            return true;
        }
        return false;
    }

    private boolean isPointOnBell(BlockState arg, Direction arg2, double d) {
        if (arg2.getAxis() == Direction.Axis.Y || d > (double)0.8124f) {
            return false;
        }
        Direction lv = arg.get(FACING);
        Attachment lv2 = arg.get(ATTACHMENT);
        switch (lv2) {
            case FLOOR: {
                return lv.getAxis() == arg2.getAxis();
            }
            case SINGLE_WALL: 
            case DOUBLE_WALL: {
                return lv.getAxis() != arg2.getAxis();
            }
            case CEILING: {
                return true;
            }
        }
        return false;
    }

    public boolean ring(World arg, BlockPos arg2, @Nullable Direction arg3) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (!arg.isClient && lv instanceof BellBlockEntity) {
            if (arg3 == null) {
                arg3 = arg.getBlockState(arg2).get(FACING);
            }
            ((BellBlockEntity)lv).activate(arg3);
            arg.playSound(null, arg2, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0f, 1.0f);
            return true;
        }
        return false;
    }

    private VoxelShape getShape(BlockState arg) {
        Direction lv = arg.get(FACING);
        Attachment lv2 = arg.get(ATTACHMENT);
        if (lv2 == Attachment.FLOOR) {
            if (lv == Direction.NORTH || lv == Direction.SOUTH) {
                return NORTH_SOUTH_SHAPE;
            }
            return EAST_WEST_SHAPE;
        }
        if (lv2 == Attachment.CEILING) {
            return HANGING_SHAPE;
        }
        if (lv2 == Attachment.DOUBLE_WALL) {
            if (lv == Direction.NORTH || lv == Direction.SOUTH) {
                return NORTH_SOUTH_WALLS_SHAPE;
            }
            return EAST_WEST_WALLS_SHAPE;
        }
        if (lv == Direction.NORTH) {
            return NORTH_WALL_SHAPE;
        }
        if (lv == Direction.SOUTH) {
            return SOUTH_WALL_SHAPE;
        }
        if (lv == Direction.EAST) {
            return EAST_WALL_SHAPE;
        }
        return WEST_WALL_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.getShape(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.getShape(arg);
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction lv = arg.getSide();
        BlockPos lv2 = arg.getBlockPos();
        World lv3 = arg.getWorld();
        Direction.Axis lv4 = lv.getAxis();
        if (lv4 == Direction.Axis.Y) {
            BlockState lv5 = (BlockState)((BlockState)this.getDefaultState().with(ATTACHMENT, lv == Direction.DOWN ? Attachment.CEILING : Attachment.FLOOR)).with(FACING, arg.getPlayerFacing());
            if (lv5.canPlaceAt(arg.getWorld(), lv2)) {
                return lv5;
            }
        } else {
            boolean bl = lv4 == Direction.Axis.X && lv3.getBlockState(lv2.west()).isSideSolidFullSquare(lv3, lv2.west(), Direction.EAST) && lv3.getBlockState(lv2.east()).isSideSolidFullSquare(lv3, lv2.east(), Direction.WEST) || lv4 == Direction.Axis.Z && lv3.getBlockState(lv2.north()).isSideSolidFullSquare(lv3, lv2.north(), Direction.SOUTH) && lv3.getBlockState(lv2.south()).isSideSolidFullSquare(lv3, lv2.south(), Direction.NORTH);
            BlockState lv6 = (BlockState)((BlockState)this.getDefaultState().with(FACING, lv.getOpposite())).with(ATTACHMENT, bl ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
            if (lv6.canPlaceAt(arg.getWorld(), arg.getBlockPos())) {
                return lv6;
            }
            boolean bl2 = lv3.getBlockState(lv2.down()).isSideSolidFullSquare(lv3, lv2.down(), Direction.UP);
            if ((lv6 = (BlockState)lv6.with(ATTACHMENT, bl2 ? Attachment.FLOOR : Attachment.CEILING)).canPlaceAt(arg.getWorld(), arg.getBlockPos())) {
                return lv6;
            }
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        Attachment lv = arg.get(ATTACHMENT);
        Direction lv2 = BellBlock.getPlacementSide(arg).getOpposite();
        if (lv2 == arg2 && !arg.canPlaceAt(arg4, arg5) && lv != Attachment.DOUBLE_WALL) {
            return Blocks.AIR.getDefaultState();
        }
        if (arg2.getAxis() == arg.get(FACING).getAxis()) {
            if (lv == Attachment.DOUBLE_WALL && !arg3.isSideSolidFullSquare(arg4, arg6, arg2)) {
                return (BlockState)((BlockState)arg.with(ATTACHMENT, Attachment.SINGLE_WALL)).with(FACING, arg2.getOpposite());
            }
            if (lv == Attachment.SINGLE_WALL && lv2.getOpposite() == arg2 && arg3.isSideSolidFullSquare(arg4, arg6, arg.get(FACING))) {
                return (BlockState)arg.with(ATTACHMENT, Attachment.DOUBLE_WALL);
            }
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        Direction lv = BellBlock.getPlacementSide(arg).getOpposite();
        if (lv == Direction.UP) {
            return Block.sideCoversSmallSquare(arg2, arg3.up(), Direction.DOWN);
        }
        return WallMountedBlock.canPlaceAt(arg2, arg3, lv);
    }

    private static Direction getPlacementSide(BlockState arg) {
        switch (arg.get(ATTACHMENT)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return arg.get(FACING).getOpposite();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.DESTROY;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, ATTACHMENT, POWERED);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView arg) {
        return new BellBlockEntity();
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

