/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PlantBlock
extends Block {
    protected PlantBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isOf(Blocks.GRASS_BLOCK) || arg.isOf(Blocks.DIRT) || arg.isOf(Blocks.COARSE_DIRT) || arg.isOf(Blocks.PODZOL) || arg.isOf(Blocks.FARMLAND);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        return this.canPlantOnTop(arg2.getBlockState(lv), arg2, lv);
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.getFluidState().isEmpty();
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        if (arg4 == NavigationType.AIR && !this.collidable) {
            return true;
        }
        return super.canPathfindThrough(arg, arg2, arg3, arg4);
    }
}

