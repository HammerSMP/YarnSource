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
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.update(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        this.update(world, pos);
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    protected void update(World world, BlockPos pos) {
        if (this.absorbWater(world, pos)) {
            world.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
        }
    }

    private boolean absorbWater(World world, BlockPos pos) {
        LinkedList queue = Lists.newLinkedList();
        queue.add(new Pair<BlockPos, Integer>(pos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Pair lv = (Pair)queue.poll();
            BlockPos lv2 = (BlockPos)lv.getLeft();
            int j = (Integer)lv.getRight();
            for (Direction lv3 : Direction.values()) {
                BlockPos lv4 = lv2.offset(lv3);
                BlockState lv5 = world.getBlockState(lv4);
                FluidState lv6 = world.getFluidState(lv4);
                Material lv7 = lv5.getMaterial();
                if (!lv6.isIn(FluidTags.WATER)) continue;
                if (lv5.getBlock() instanceof FluidDrainable && ((FluidDrainable)((Object)lv5.getBlock())).tryDrainFluid(world, lv4, lv5) != Fluids.EMPTY) {
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(lv4, j + 1));
                    continue;
                }
                if (lv5.getBlock() instanceof FluidBlock) {
                    world.setBlockState(lv4, Blocks.AIR.getDefaultState(), 3);
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(lv4, j + 1));
                    continue;
                }
                if (lv7 != Material.UNDERWATER_PLANT && lv7 != Material.REPLACEABLE_UNDERWATER_PLANT) continue;
                BlockEntity lv8 = lv5.getBlock().hasBlockEntity() ? world.getBlockEntity(lv4) : null;
                SpongeBlock.dropStacks(lv5, world, lv4, lv8);
                world.setBlockState(lv4, Blocks.AIR.getDefaultState(), 3);
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

