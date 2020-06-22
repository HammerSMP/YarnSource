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

    private static boolean canSurvive(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.up();
        BlockState lv2 = arg2.getBlockState(lv);
        if (lv2.isOf(Blocks.SNOW) && lv2.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        if (lv2.getFluidState().getLevel() == 8) {
            return false;
        }
        int i = ChunkLightProvider.getRealisticOpacity(arg2, arg, arg3, lv2, lv, Direction.UP, lv2.getOpacity(arg2, lv));
        return i < arg2.getMaxLightLevel();
    }

    private static boolean canSpread(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.up();
        return SpreadableBlock.canSurvive(arg, arg2, arg3) && !arg2.getFluidState(lv).isIn(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!SpreadableBlock.canSurvive(arg, arg2, arg3)) {
            arg2.setBlockState(arg3, Blocks.DIRT.getDefaultState());
            return;
        }
        if (arg2.getLightLevel(arg3.up()) >= 9) {
            BlockState lv = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos lv2 = arg3.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (!arg2.getBlockState(lv2).isOf(Blocks.DIRT) || !SpreadableBlock.canSpread(lv, arg2, lv2)) continue;
                arg2.setBlockState(lv2, (BlockState)lv.with(SNOWY, arg2.getBlockState(lv2.up()).isOf(Blocks.SNOW)));
            }
        }
    }
}

