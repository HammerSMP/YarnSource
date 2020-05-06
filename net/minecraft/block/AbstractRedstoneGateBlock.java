/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AbstractRedstoneGateBlock
extends HorizontalFacingBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final BooleanProperty POWERED = Properties.POWERED;

    protected AbstractRedstoneGateBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return AbstractRedstoneGateBlock.hasTopRim(arg2, arg3.down());
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (this.isLocked(arg2, arg3, arg)) {
            return;
        }
        boolean bl = arg.get(POWERED);
        boolean bl2 = this.hasPower(arg2, arg3, arg);
        if (bl && !bl2) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, false), 2);
        } else if (!bl) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, true), 2);
            if (!bl2) {
                ((ServerTickScheduler)arg2.getBlockTickScheduler()).schedule(arg3, this, this.getUpdateDelayInternal(arg), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.getWeakRedstonePower(arg2, arg3, arg4);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (!arg.get(POWERED).booleanValue()) {
            return 0;
        }
        if (arg.get(FACING) == arg4) {
            return this.getOutputLevel(arg2, arg3, arg);
        }
        return 0;
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg.canPlaceAt(arg2, arg3)) {
            this.updatePowered(arg2, arg3, arg);
            return;
        }
        BlockEntity lv = this.hasBlockEntity() ? arg2.getBlockEntity(arg3) : null;
        AbstractRedstoneGateBlock.dropStacks(arg, arg2, arg3, lv);
        arg2.removeBlock(arg3, false);
        for (Direction lv2 : Direction.values()) {
            arg2.updateNeighborsAlways(arg3.offset(lv2), this);
        }
    }

    protected void updatePowered(World arg, BlockPos arg2, BlockState arg3) {
        boolean bl2;
        if (this.isLocked(arg, arg2, arg3)) {
            return;
        }
        boolean bl = arg3.get(POWERED);
        if (bl != (bl2 = this.hasPower(arg, arg2, arg3)) && !arg.getBlockTickScheduler().isTicking(arg2, this)) {
            TickPriority lv = TickPriority.HIGH;
            if (this.isTargetNotAligned(arg, arg2, arg3)) {
                lv = TickPriority.EXTREMELY_HIGH;
            } else if (bl) {
                lv = TickPriority.VERY_HIGH;
            }
            arg.getBlockTickScheduler().schedule(arg2, this, this.getUpdateDelayInternal(arg3), lv);
        }
    }

    public boolean isLocked(WorldView arg, BlockPos arg2, BlockState arg3) {
        return false;
    }

    protected boolean hasPower(World arg, BlockPos arg2, BlockState arg3) {
        return this.getPower(arg, arg2, arg3) > 0;
    }

    protected int getPower(World arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING);
        BlockPos lv2 = arg2.offset(lv);
        int i = arg.getEmittedRedstonePower(lv2, lv);
        if (i >= 15) {
            return i;
        }
        BlockState lv3 = arg.getBlockState(lv2);
        return Math.max(i, lv3.isOf(Blocks.REDSTONE_WIRE) ? lv3.get(RedstoneWireBlock.POWER) : 0);
    }

    protected int getMaxInputLevelSides(WorldView arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING);
        Direction lv2 = lv.rotateYClockwise();
        Direction lv3 = lv.rotateYCounterclockwise();
        return Math.max(this.getInputLevel(arg, arg2.offset(lv2), lv2), this.getInputLevel(arg, arg2.offset(lv3), lv3));
    }

    protected int getInputLevel(WorldView arg, BlockPos arg2, Direction arg3) {
        BlockState lv = arg.getBlockState(arg2);
        if (this.isValidInput(lv)) {
            if (lv.isOf(Blocks.REDSTONE_BLOCK)) {
                return 15;
            }
            if (lv.isOf(Blocks.REDSTONE_WIRE)) {
                return lv.get(RedstoneWireBlock.POWER);
            }
            return arg.getStrongRedstonePower(arg2, arg3);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        if (this.hasPower(arg, arg2, arg3)) {
            arg.getBlockTickScheduler().schedule(arg2, this, 1);
        }
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        this.updateTarget(arg2, arg3, arg);
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl || arg.isOf(arg4.getBlock())) {
            return;
        }
        super.onBlockRemoved(arg, arg2, arg3, arg4, bl);
        this.updateTarget(arg2, arg3, arg);
    }

    protected void updateTarget(World arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING);
        BlockPos lv2 = arg2.offset(lv.getOpposite());
        arg.updateNeighbor(lv2, this, arg2);
        arg.updateNeighborsExcept(lv2, this, lv);
    }

    protected boolean isValidInput(BlockState arg) {
        return arg.emitsRedstonePower();
    }

    protected int getOutputLevel(BlockView arg, BlockPos arg2, BlockState arg3) {
        return 15;
    }

    public static boolean isRedstoneGate(BlockState arg) {
        return arg.getBlock() instanceof AbstractRedstoneGateBlock;
    }

    public boolean isTargetNotAligned(BlockView arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING).getOpposite();
        BlockState lv2 = arg.getBlockState(arg2.offset(lv));
        return AbstractRedstoneGateBlock.isRedstoneGate(lv2) && lv2.get(FACING) != lv;
    }

    protected abstract int getUpdateDelayInternal(BlockState var1);
}

