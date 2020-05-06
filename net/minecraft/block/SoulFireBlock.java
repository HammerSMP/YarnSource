/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class SoulFireBlock
extends AbstractFireBlock {
    public SoulFireBlock(AbstractBlock.Settings arg) {
        super(arg, 2.0f);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (this.canPlaceAt(arg, arg4, arg5)) {
            return this.getDefaultState();
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return SoulFireBlock.isSoulBase(arg2.getBlockState(arg3.down()).getBlock());
    }

    public static boolean isSoulBase(Block arg) {
        return arg.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
    }

    @Override
    protected boolean isFlammable(BlockState arg) {
        return true;
    }
}

