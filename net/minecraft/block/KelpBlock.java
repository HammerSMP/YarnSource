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
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class KelpBlock
extends AbstractPlantStemBlock
implements FluidFillable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

    protected KelpBlock(AbstractBlock.Settings arg) {
        super(arg, Direction.UP, SHAPE, true, 0.14);
    }

    @Override
    protected boolean chooseStemState(BlockState arg) {
        return arg.isOf(Blocks.WATER);
    }

    @Override
    protected Block getPlant() {
        return Blocks.KELP_PLANT;
    }

    @Override
    protected boolean canAttachTo(Block arg) {
        return arg != Blocks.MAGMA_BLOCK;
    }

    @Override
    public boolean canFillWithFluid(BlockView arg, BlockPos arg2, BlockState arg3, Fluid arg4) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess arg, BlockPos arg2, BlockState arg3, FluidState arg4) {
        return false;
    }

    @Override
    protected int method_26376(Random random) {
        return 1;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv = arg.getWorld().getFluidState(arg.getBlockPos());
        if (lv.isIn(FluidTags.WATER) && lv.getLevel() == 8) {
            return super.getPlacementState(arg);
        }
        return null;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        return Fluids.WATER.getStill(false);
    }
}

