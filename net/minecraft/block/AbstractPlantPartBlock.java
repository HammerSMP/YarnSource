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
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AbstractPlantPartBlock
extends Block {
    protected final Direction growthDirection;
    protected final boolean tickWater;
    protected final VoxelShape outlineShape;

    protected AbstractPlantPartBlock(AbstractBlock.Settings arg, Direction arg2, VoxelShape arg3, boolean bl) {
        super(arg);
        this.growthDirection = arg2;
        this.outlineShape = arg3;
        this.tickWater = bl;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos().offset(this.growthDirection));
        if (lv.isOf(this.getStem()) || lv.isOf(this.getPlant())) {
            return this.getPlant().getDefaultState();
        }
        return this.getRandomGrowthState(arg.getWorld());
    }

    public BlockState getRandomGrowthState(WorldAccess arg) {
        return this.getDefaultState();
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.offset(this.growthDirection.getOpposite());
        BlockState lv2 = arg2.getBlockState(lv);
        Block lv3 = lv2.getBlock();
        if (!this.canAttachTo(lv3)) {
            return false;
        }
        return lv3 == this.getStem() || lv3 == this.getPlant() || lv2.isSideSolidFullSquare(arg2, lv, this.growthDirection);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    protected boolean canAttachTo(Block arg) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.outlineShape;
    }

    protected abstract AbstractPlantStemBlock getStem();

    protected abstract Block getPlant();
}

