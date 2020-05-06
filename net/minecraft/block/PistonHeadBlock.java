/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PistonHeadBlock
extends FacingBlock {
    public static final EnumProperty<PistonType> TYPE = Properties.PISTON_TYPE;
    public static final BooleanProperty SHORT = Properties.SHORT;
    protected static final VoxelShape EAST_HEAD_SHAPE = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
    protected static final VoxelShape UP_HEAD_SHAPE = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    protected static final VoxelShape UP_ARM_SHAPE = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
    protected static final VoxelShape SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
    protected static final VoxelShape EAST_ARM_SHAPE = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_UP_ARM_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape SHORT_DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape SHORT_SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape SHORT_NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape SHORT_EAST_ARM_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);

    public PistonHeadBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TYPE, PistonType.DEFAULT)).with(SHORT, false));
    }

    private VoxelShape getHeadShape(BlockState arg) {
        switch (arg.get(FACING)) {
            default: {
                return DOWN_HEAD_SHAPE;
            }
            case UP: {
                return UP_HEAD_SHAPE;
            }
            case NORTH: {
                return NORTH_HEAD_SHAPE;
            }
            case SOUTH: {
                return SOUTH_HEAD_SHAPE;
            }
            case WEST: {
                return WEST_HEAD_SHAPE;
            }
            case EAST: 
        }
        return EAST_HEAD_SHAPE;
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.union(this.getHeadShape(arg), this.getArmShape(arg));
    }

    private VoxelShape getArmShape(BlockState arg) {
        boolean bl = arg.get(SHORT);
        switch (arg.get(FACING)) {
            default: {
                return bl ? SHORT_DOWN_ARM_SHAPE : DOWN_ARM_SHAPE;
            }
            case UP: {
                return bl ? SHORT_UP_ARM_SHAPE : UP_ARM_SHAPE;
            }
            case NORTH: {
                return bl ? SHORT_NORTH_ARM_SHAPE : NORTH_ARM_SHAPE;
            }
            case SOUTH: {
                return bl ? SHORT_SOUTH_ARM_SHAPE : SOUTH_ARM_SHAPE;
            }
            case WEST: {
                return bl ? SHORT_WEST_ARM_SHAPE : WEST_ARM_SHAPE;
            }
            case EAST: 
        }
        return bl ? SHORT_EAST_ARM_SHAPE : EAST_ARM_SHAPE;
    }

    private boolean method_26980(BlockState arg, BlockState arg2) {
        Block lv = arg.get(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
        return arg2.isOf(lv) && arg2.get(PistonBlock.EXTENDED) != false && arg2.get(FACING) == arg.get(FACING);
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        BlockPos lv;
        if (!arg.isClient && arg4.abilities.creativeMode && this.method_26980(arg3, arg.getBlockState(lv = arg2.offset(arg3.get(FACING).getOpposite())))) {
            arg.breakBlock(lv, false);
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        super.onBlockRemoved(arg, arg2, arg3, arg4, bl);
        BlockPos lv = arg3.offset(arg.get(FACING).getOpposite());
        if (this.method_26980(arg, arg2.getBlockState(lv))) {
            arg2.breakBlock(lv, true);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2.getOpposite() == arg.get(FACING) && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.offset(arg.get(FACING).getOpposite()));
        return this.method_26980(arg, lv) || lv.isOf(Blocks.MOVING_PISTON) && lv.get(FACING) == arg.get(FACING);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg.canPlaceAt(arg2, arg3)) {
            BlockPos lv = arg3.offset(arg.get(FACING).getOpposite());
            arg2.getBlockState(lv).neighborUpdate(arg2, lv, arg4, arg5, false);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(arg3.get(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
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
        arg.add(FACING, TYPE, SHORT);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

