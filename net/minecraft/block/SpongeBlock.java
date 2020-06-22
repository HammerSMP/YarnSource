/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock
extends Block {
    protected SpongeBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        this.update(arg2, arg3);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        this.update(arg2, arg3);
        super.neighborUpdate(arg, arg2, arg3, arg4, arg5, bl);
    }

    protected void update(World arg, BlockPos arg2) {
        if (this.absorbWater(arg, arg2)) {
            arg.setBlockState(arg2, Blocks.WET_SPONGE.getDefaultState(), 2);
            arg.syncWorldEvent(2001, arg2, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
        }
    }

    private boolean absorbWater(World arg, BlockPos arg2) {
        LinkedList queue = Lists.newLinkedList();
        queue.add(new Pair<BlockPos, Integer>(arg2, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Pair lv = (Pair)queue.poll();
            BlockPos lv2 = (BlockPos)lv.getLeft();
            int j = (Integer)lv.getRight();
            for (Direction lv3 : Direction.values()) {
                BlockPos lv4 = lv2.offset(lv3);
                BlockState lv5 = arg.getBlockState(lv4);
                FluidState lv6 = arg.getFluidState(lv4);
                Material lv7 = lv5.getMaterial();
                if (!lv6.isIn(FluidTags.WATER)) continue;
                if (lv5.getBlock() instanceof FluidDrainable && ((FluidDrainable)((Object)lv5.getBlock())).tryDrainFluid(arg, lv4, lv5) != Fluids.EMPTY) {
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(lv4, j + 1));
                    continue;
                }
                if (lv5.getBlock() instanceof FluidBlock) {
                    arg.setBlockState(lv4, Blocks.AIR.getDefaultState(), 3);
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(lv4, j + 1));
                    continue;
                }
                if (lv7 != Material.UNDERWATER_PLANT && lv7 != Material.REPLACEABLE_UNDERWATER_PLANT) continue;
                BlockEntity lv8 = lv5.getBlock().hasBlockEntity() ? arg.getBlockEntity(lv4) : null;
                SpongeBlock.dropStacks(lv5, arg, lv4, lv8);
                arg.setBlockState(lv4, Blocks.AIR.getDefaultState(), 3);
                ++i;
                if (j >= 6) continue;
                queue.add(new Pair<BlockPos, Integer>(lv4, j + 1));
            }
            if (i <= 64) continue;
            break;
        }
        return i > 0;
    }
}

