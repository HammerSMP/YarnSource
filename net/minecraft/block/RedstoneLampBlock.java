/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneLampBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneLampBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(LIT, arg.getWorld().isReceivingRedstonePower(arg.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        boolean bl2 = arg.get(LIT);
        if (bl2 != arg2.isReceivingRedstonePower(arg3)) {
            if (bl2) {
                arg2.getBlockTickScheduler().schedule(arg3, this, 4);
            } else {
                arg2.setBlockState(arg3, (BlockState)arg.cycle(LIT), 2);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg.get(LIT).booleanValue() && !arg2.isReceivingRedstonePower(arg3)) {
            arg2.setBlockState(arg3, (BlockState)arg.cycle(LIT), 2);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LIT);
    }
}

