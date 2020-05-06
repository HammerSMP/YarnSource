/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock
extends FacingBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public ObserverBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.SOUTH)).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, POWERED);
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
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg.get(POWERED).booleanValue()) {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, false), 2);
        } else {
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, true), 2);
            arg2.getBlockTickScheduler().schedule(arg3, this, 2);
        }
        this.updateNeighbors(arg2, arg3, arg);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(FACING) == arg2 && !arg.get(POWERED).booleanValue()) {
            this.scheduleTick(arg4, arg5);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    private void scheduleTick(IWorld arg, BlockPos arg2) {
        if (!arg.isClient() && !arg.getBlockTickScheduler().isScheduled(arg2, this)) {
            arg.getBlockTickScheduler().schedule(arg2, this, 2);
        }
    }

    protected void updateNeighbors(World arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING);
        BlockPos lv2 = arg2.offset(lv.getOpposite());
        arg.updateNeighbor(lv2, this, arg2);
        arg.updateNeighborsExcept(lv2, this, lv);
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.getWeakRedstonePower(arg2, arg3, arg4);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (arg.get(POWERED).booleanValue() && arg.get(FACING) == arg4) {
            return 15;
        }
        return 0;
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        if (!arg2.isClient() && arg.get(POWERED).booleanValue() && !arg2.getBlockTickScheduler().isScheduled(arg3, this)) {
            BlockState lv = (BlockState)arg.with(POWERED, false);
            arg2.setBlockState(arg3, lv, 18);
            this.updateNeighbors(arg2, arg3, lv);
        }
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        if (!arg2.isClient && arg.get(POWERED).booleanValue() && arg2.getBlockTickScheduler().isScheduled(arg3, this)) {
            this.updateNeighbors(arg2, arg3, (BlockState)arg.with(POWERED, false));
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getPlayerLookDirection().getOpposite().getOpposite());
    }
}

