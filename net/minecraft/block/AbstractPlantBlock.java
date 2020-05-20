/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractPlantBlock
extends AbstractPlantPartBlock
implements Fertilizable {
    protected AbstractPlantBlock(AbstractBlock.Settings arg, Direction arg2, VoxelShape arg3, boolean bl) {
        super(arg, arg2, arg3, bl);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        Block lv2;
        if (arg2 == this.growthDirection.getOpposite() && !arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        AbstractPlantStemBlock lv = this.getStem();
        if (arg2 == this.growthDirection && (lv2 = arg3.getBlock()) != this && lv2 != lv) {
            return lv.getRandomGrowthState(arg4);
        }
        if (this.tickWater) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(this.getStem());
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        Optional<BlockPos> optional = this.method_25960(arg, arg2, arg3);
        return optional.isPresent() && this.getStem().chooseStemState(arg.getBlockState(optional.get().offset(this.growthDirection)));
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        Optional<BlockPos> optional = this.method_25960(arg, arg2, arg3);
        if (optional.isPresent()) {
            BlockState lv = arg.getBlockState(optional.get());
            ((AbstractPlantStemBlock)lv.getBlock()).grow(arg, random, optional.get(), lv);
        }
    }

    private Optional<BlockPos> method_25960(BlockView arg, BlockPos arg2, BlockState arg3) {
        Block lv2;
        BlockPos lv = arg2;
        while ((lv2 = arg.getBlockState(lv = lv.offset(this.growthDirection)).getBlock()) == arg3.getBlock()) {
        }
        if (lv2 == this.getStem()) {
            return Optional.of(lv);
        }
        return Optional.empty();
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        boolean bl = super.canReplace(arg, arg2);
        if (bl && arg2.getStack().getItem() == this.getStem().asItem()) {
            return false;
        }
        return bl;
    }

    @Override
    protected Block getPlant() {
        return this;
    }
}

