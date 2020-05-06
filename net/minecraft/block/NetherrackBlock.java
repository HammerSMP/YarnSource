/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NetherrackBlock
extends Block
implements Fertilizable {
    public NetherrackBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        if (!arg.getBlockState(arg2.up()).isTranslucent(arg, arg2)) {
            return false;
        }
        for (BlockPos lv : BlockPos.iterate(arg2.add(-1, -1, -1), arg2.add(1, 1, 1))) {
            if (!arg.getBlockState(lv).isIn(BlockTags.NYLIUM)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        boolean bl = false;
        boolean bl2 = false;
        for (BlockPos lv : BlockPos.iterate(arg2.add(-1, -1, -1), arg2.add(1, 1, 1))) {
            BlockState lv2 = arg.getBlockState(lv);
            if (lv2.isOf(Blocks.WARPED_NYLIUM)) {
                bl2 = true;
            }
            if (lv2.isOf(Blocks.CRIMSON_NYLIUM)) {
                bl = true;
            }
            if (!bl2 || !bl) continue;
            break;
        }
        if (bl2 && bl) {
            arg.setBlockState(arg2, random.nextBoolean() ? Blocks.WARPED_NYLIUM.getDefaultState() : Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
        } else if (bl2) {
            arg.setBlockState(arg2, Blocks.WARPED_NYLIUM.getDefaultState(), 3);
        } else if (bl) {
            arg.setBlockState(arg2, Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
        }
    }
}

