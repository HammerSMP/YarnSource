/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
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
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.offset(this.growthDirection.getOpposite());
        BlockState lv2 = arg2.getBlockState(lv);
        Block lv3 = lv2.getBlock();
        if (!this.canAttachTo(lv3)) {
            return false;
        }
        return lv3 == this.getStem() || lv3 == this.getPlant() || lv2.isSideSolidFullSquare(arg2, lv, this.growthDirection);
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

