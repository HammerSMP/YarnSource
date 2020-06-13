/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class DoorBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

    protected DoorBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(OPEN, false)).with(HINGE, DoorHinge.LEFT)).with(POWERED, false)).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        Direction lv = arg.get(FACING);
        boolean bl = arg.get(OPEN) == false;
        boolean bl2 = arg.get(HINGE) == DoorHinge.RIGHT;
        switch (lv) {
            default: {
                return bl ? WEST_SHAPE : (bl2 ? SOUTH_SHAPE : NORTH_SHAPE);
            }
            case SOUTH: {
                return bl ? NORTH_SHAPE : (bl2 ? WEST_SHAPE : EAST_SHAPE);
            }
            case WEST: {
                return bl ? EAST_SHAPE : (bl2 ? NORTH_SHAPE : SOUTH_SHAPE);
            }
            case NORTH: 
        }
        return bl ? SOUTH_SHAPE : (bl2 ? EAST_SHAPE : WEST_SHAPE);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        DoubleBlockHalf lv = arg.get(HALF);
        if (arg2.getAxis() == Direction.Axis.Y && lv == DoubleBlockHalf.LOWER == (arg2 == Direction.UP)) {
            if (arg3.isOf(this) && arg3.get(HALF) != lv) {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(FACING, arg3.get(FACING))).with(OPEN, arg3.get(OPEN))).with(HINGE, arg3.get(HINGE))).with(POWERED, arg3.get(POWERED));
            }
            return Blocks.AIR.getDefaultState();
        }
        if (lv == DoubleBlockHalf.LOWER && arg2 == Direction.DOWN && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        if (!arg.isClient && arg4.isCreative()) {
            TallPlantBlock.method_30036(arg, arg2, arg3, arg4);
        }
        super.onBreak(arg, arg2, arg3, arg4);
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

    private int getOpenSoundEventId() {
        return this.material == Material.METAL ? 1011 : 1012;
    }

    private int getCloseSoundEventId() {
        return this.material == Material.METAL ? 1005 : 1006;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockPos lv = arg.getBlockPos();
        if (lv.getY() < 255 && arg.getWorld().getBlockState(lv.up()).canReplace(arg)) {
            World lv2 = arg.getWorld();
            boolean bl = lv2.isReceivingRedstonePower(lv) || lv2.isReceivingRedstonePower(lv.up());
            return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing())).with(HINGE, this.getHinge(arg))).with(POWERED, bl)).with(OPEN, bl)).with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        arg.setBlockState(arg2.up(), (BlockState)arg3.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHinge getHinge(ItemPlacementContext arg) {
        boolean bl2;
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        Direction lv3 = arg.getPlayerFacing();
        BlockPos lv4 = lv2.up();
        Direction lv5 = lv3.rotateYCounterclockwise();
        BlockPos lv6 = lv2.offset(lv5);
        BlockState lv7 = lv.getBlockState(lv6);
        BlockPos lv8 = lv4.offset(lv5);
        BlockState lv9 = lv.getBlockState(lv8);
        Direction lv10 = lv3.rotateYClockwise();
        BlockPos lv11 = lv2.offset(lv10);
        BlockState lv12 = lv.getBlockState(lv11);
        BlockPos lv13 = lv4.offset(lv10);
        BlockState lv14 = lv.getBlockState(lv13);
        int i = (lv7.isFullCube(lv, lv6) ? -1 : 0) + (lv9.isFullCube(lv, lv8) ? -1 : 0) + (lv12.isFullCube(lv, lv11) ? 1 : 0) + (lv14.isFullCube(lv, lv13) ? 1 : 0);
        boolean bl = lv7.isOf(this) && lv7.get(HALF) == DoubleBlockHalf.LOWER;
        boolean bl3 = bl2 = lv12.isOf(this) && lv12.get(HALF) == DoubleBlockHalf.LOWER;
        if (bl && !bl2 || i > 0) {
            return DoorHinge.RIGHT;
        }
        if (bl2 && !bl || i < 0) {
            return DoorHinge.LEFT;
        }
        int j = lv3.getOffsetX();
        int k = lv3.getOffsetZ();
        Vec3d lv15 = arg.getHitPos();
        double d = lv15.x - (double)lv2.getX();
        double e = lv15.z - (double)lv2.getZ();
        return j < 0 && e < 0.5 || j > 0 && e > 0.5 || k < 0 && d > 0.5 || k > 0 && d < 0.5 ? DoorHinge.RIGHT : DoorHinge.LEFT;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (this.material == Material.METAL) {
            return ActionResult.PASS;
        }
        arg = (BlockState)arg.cycle(OPEN);
        arg2.setBlockState(arg3, arg, 10);
        arg2.syncWorldEvent(arg4, arg.get(OPEN) != false ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), arg3, 0);
        return ActionResult.success(arg2.isClient);
    }

    public void setOpen(World arg, BlockPos arg2, boolean bl) {
        BlockState lv = arg.getBlockState(arg2);
        if (!lv.isOf(this) || lv.get(OPEN) == bl) {
            return;
        }
        arg.setBlockState(arg2, (BlockState)lv.with(OPEN, bl), 10);
        this.playOpenCloseSound(arg, arg2, bl);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        boolean bl2;
        boolean bl3 = arg2.isReceivingRedstonePower(arg3) || arg2.isReceivingRedstonePower(arg3.offset(arg.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN)) ? true : (bl2 = false);
        if (arg4 != this && bl2 != arg.get(POWERED)) {
            if (bl2 != arg.get(OPEN)) {
                this.playOpenCloseSound(arg2, arg3, bl2);
            }
            arg2.setBlockState(arg3, (BlockState)((BlockState)arg.with(POWERED, bl2)).with(OPEN, bl2), 2);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        BlockState lv2 = arg2.getBlockState(lv);
        if (arg.get(HALF) == DoubleBlockHalf.LOWER) {
            return lv2.isSideSolidFullSquare(arg2, lv, Direction.UP);
        }
        return lv2.isOf(this);
    }

    private void playOpenCloseSound(World arg, BlockPos arg2, boolean bl) {
        arg.syncWorldEvent(null, bl ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), arg2, 0);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        if (arg2 == BlockMirror.NONE) {
            return arg;
        }
        return (BlockState)arg.rotate(arg2.getRotation(arg.get(FACING))).cycle(HINGE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState arg, BlockPos arg2) {
        return MathHelper.hashCode(arg2.getX(), arg2.down(arg.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), arg2.getZ());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(HALF, FACING, OPEN, HINGE, POWERED);
    }

    public static boolean isWoodenDoor(World arg, BlockPos arg2) {
        return DoorBlock.isWoodenDoor(arg.getBlockState(arg2));
    }

    public static boolean isWoodenDoor(BlockState arg) {
        return arg.getBlock() instanceof DoorBlock && (arg.getMaterial() == Material.WOOD || arg.getMaterial() == Material.NETHER_WOOD);
    }
}

