/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadBushBlock
extends PlantBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    protected DeadBushBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        Block lv = floor.getBlock();
        return lv == Blocks.SAND || lv == Blocks.RED_SAND || lv == Blocks.TERRACOTTA || lv == Blocks.WHITE_TERRACOTTA || lv == Blocks.ORANGE_TERRACOTTA || lv == Blocks.MAGENTA_TERRACOTTA || lv == Blocks.LIGHT_BLUE_TERRACOTTA || lv == Blocks.YELLOW_TERRACOTTA || lv == Blocks.LIME_TERRACOTTA || lv == Blocks.PINK_TERRACOTTA || lv == Blocks.GRAY_TERRACOTTA || lv == Blocks.LIGHT_GRAY_TERRACOTTA || lv == Blocks.CYAN_TERRACOTTA || lv == Blocks.PURPLE_TERRACOTTA || lv == Blocks.BLUE_TERRACOTTA || lv == Blocks.BROWN_TERRACOTTA || lv == Blocks.GREEN_TERRACOTTA || lv == Blocks.RED_TERRACOTTA || lv == Blocks.BLACK_TERRACOTTA || lv == Blocks.DIRT || lv == Blocks.COARSE_DIRT || lv == Blocks.PODZOL;
    }
}

