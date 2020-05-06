/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class SugarCaneBlock
extends Block {
    public static final IntProperty AGE = Properties.AGE_15;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    protected SugarCaneBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg2.isAir(arg3.up())) {
            int i = 1;
            while (arg2.getBlockState(arg3.down(i)).isOf(this)) {
                ++i;
            }
            if (i < 3) {
                int j = arg.get(AGE);
                if (j == 15) {
                    arg2.setBlockState(arg3.up(), this.getDefaultState());
                    arg2.setBlockState(arg3, (BlockState)arg.with(AGE, 0), 4);
                } else {
                    arg2.setBlockState(arg3, (BlockState)arg.with(AGE, j + 1), 4);
                }
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockState lv = arg2.getBlockState(arg3.down());
        if (lv.getBlock() == this) {
            return true;
        }
        if (lv.isOf(Blocks.GRASS_BLOCK) || lv.isOf(Blocks.DIRT) || lv.isOf(Blocks.COARSE_DIRT) || lv.isOf(Blocks.PODZOL) || lv.isOf(Blocks.SAND) || lv.isOf(Blocks.RED_SAND)) {
            BlockPos lv2 = arg3.down();
            for (Direction lv3 : Direction.Type.HORIZONTAL) {
                BlockState lv4 = arg2.getBlockState(lv2.offset(lv3));
                FluidState lv5 = arg2.getFluidState(lv2.offset(lv3));
                if (!lv5.matches(FluidTags.WATER) && !lv4.isOf(Blocks.FROSTED_ICE)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }
}

