/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TallSeagrassBlock
extends TallPlantBlock
implements FluidFillable {
    public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public TallSeagrassBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isSideSolidFullSquare(arg2, arg3, Direction.UP) && !arg.isOf(Blocks.MAGMA_BLOCK);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(Blocks.SEAGRASS);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv2;
        BlockState lv = super.getPlacementState(arg);
        if (lv != null && (lv2 = arg.getWorld().getFluidState(arg.getBlockPos().up())).isIn(FluidTags.WATER) && lv2.getLevel() == 8) {
            return lv;
        }
        return null;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        if (arg.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState lv = arg2.getBlockState(arg3.down());
            return lv.isOf(this) && lv.get(HALF) == DoubleBlockHalf.LOWER;
        }
        FluidState lv2 = arg2.getFluidState(arg3);
        return super.canPlaceAt(arg, arg2, arg3) && lv2.isIn(FluidTags.WATER) && lv2.getLevel() == 8;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public boolean canFillWithFluid(BlockView arg, BlockPos arg2, BlockState arg3, Fluid arg4) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess arg, BlockPos arg2, BlockState arg3, FluidState arg4) {
        return false;
    }
}

