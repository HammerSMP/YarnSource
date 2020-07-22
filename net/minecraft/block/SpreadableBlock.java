/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SnowyBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public abstract class SpreadableBlock
extends SnowyBlock {
    protected SpreadableBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    private static boolean canSurvive(BlockState state, WorldView arg2, BlockPos pos) {
        BlockPos lv = pos.up();
        BlockState lv2 = arg2.getBlockState(lv);
        if (lv2.isOf(Blocks.SNOW) && lv2.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        if (lv2.getFluidState().getLevel() == 8) {
            return false;
        }
        int i = ChunkLightProvider.getRealisticOpacity(arg2, state, pos, lv2, lv, Direction.UP, lv2.getOpacity(arg2, lv));
        return i < arg2.getMaxLightLevel();
    }

    private static boolean canSpread(BlockState state, WorldView arg2, BlockPos pos) {
        BlockPos lv = pos.up();
        return SpreadableBlock.canSurvive(state, arg2, pos) && !arg2.getFluidState(lv).isIn(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!SpreadableBlock.canSurvive(state, world, pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            return;
        }
        if (world.getLightLevel(pos.up()) >= 9) {
            BlockState lv = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos lv2 = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (!world.getBlockState(lv2).isOf(Blocks.DIRT) || !SpreadableBlock.canSpread(lv, world, lv2)) continue;
                world.setBlockState(lv2, (BlockState)lv.with(SNOWY, world.getBlockState(lv2.up()).isOf(Blocks.SNOW)));
            }
        }
    }
}

