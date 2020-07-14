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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractRedstoneGateBlock.hasTopRim(world, pos.down());
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.isLocked(world, pos, state)) {
            return;
        }
        boolean bl = state.get(POWERED);
        boolean bl2 = this.hasPower(world, pos, state);
        if (bl && !bl2) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, false), 2);
        } else if (!bl) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, true), 2);
            if (!bl2) {
                ((ServerTickScheduler)world.getBlockTickScheduler()).schedule(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!state.get(POWERED).booleanValue()) {
            return 0;
        }
        if (state.get(FACING) == direction) {
            return this.getOutputLevel(world, pos, state);
        }
        return 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (state.canPlaceAt(world, pos)) {
            this.updatePowered(world, pos, state);
            return;
        }
        BlockEntity lv = this.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        AbstractRedstoneGateBlock.dropStacks(state, world, pos, lv);
        world.removeBlock(pos, false);
        for (Direction lv2 : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(lv2), this);
        }
    }

    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        boolean bl2;
        if (this.isLocked(world, pos, state)) {
            return;
        }
        boolean bl = state.get(POWERED);
        if (bl != (bl2 = this.hasPower(world, pos, state)) && !world.getBlockTickScheduler().isTicking(pos, this)) {
            TickPriority lv = TickPriority.HIGH;
            if (this.isTargetNotAligned(world, pos, state)) {
                lv = TickPriority.EXTREMELY_HIGH;
            } else if (bl) {
                lv = TickPriority.VERY_HIGH;
            }
            world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), lv);
        }
    }

    public boolean isLocked(WorldView arg, BlockPos pos, BlockState state) {
        return false;
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        return this.getPower(world, pos, state) > 0;
    }

    protected int getPower(World world, BlockPos pos, BlockState state) {
        Direction lv = state.get(FACING);
        BlockPos lv2 = pos.offset(lv);
        int i = world.getEmittedRedstonePower(lv2, lv);
        if (i >= 15) {
            return i;
        }
        BlockState lv3 = world.getBlockState(lv2);
        return Math.max(i, lv3.isOf(Blocks.REDSTONE_WIRE) ? lv3.get(RedstoneWireBlock.POWER) : 0);
    }

    protected int getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state) {
        Direction lv = state.get(FACING);
        Direction lv2 = lv.rotateYClockwise();
        Direction lv3 = lv.rotateYCounterclockwise();
        return Math.max(this.getInputLevel(world, pos.offset(lv2), lv2), this.getInputLevel(world, pos.offset(lv3), lv3));
    }

    protected int getInputLevel(WorldView world, BlockPos pos, Direction dir) {
        BlockState lv = world.getBlockState(pos);
        if (this.isValidInput(lv)) {
            if (lv.isOf(Blocks.REDSTONE_BLOCK)) {
                return 15;
            }
            if (lv.isOf(Blocks.REDSTONE_WIRE)) {
                return lv.get(RedstoneWireBlock.POWER);
            }
            return world.getStrongRedstonePower(pos, dir);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (this.hasPower(world, pos, state)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
        this.updateTarget(world, pos, state);
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        Direction lv = state.get(FACING);
        BlockPos lv2 = pos.offset(lv.getOpposite());
        world.updateNeighbor(lv2, this, pos);
        world.updateNeighborsExcept(lv2, this, lv);
    }

    protected boolean isValidInput(BlockState state) {
        return state.emitsRedstonePower();
    }

    protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
        return 15;
    }

    public static boolean isRedstoneGate(BlockState state) {
        return state.getBlock() instanceof AbstractRedstoneGateBlock;
    }

    public boolean isTargetNotAligned(BlockView world, BlockPos pos, BlockState state) {
        Direction lv = state.get(FACING).getOpposite();
        BlockState lv2 = world.getBlockState(pos.offset(lv));
        return AbstractRedstoneGateBlock.isRedstoneGate(lv2) && lv2.get(FACING) != lv;
    }

    protected abstract int getUpdateDelayInternal(BlockState var1);
}

