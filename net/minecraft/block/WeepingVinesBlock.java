/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineLogic;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class WeepingVinesBlock
extends AbstractPlantStemBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);

    public WeepingVinesBlock(AbstractBlock.Settings arg) {
        super(arg, Direction.DOWN, SHAPE, false, 0.1);
    }

    @Override
    protected int method_26376(Random random) {
        return VineLogic.method_26381(random);
    }

    @Override
    protected Block getPlant() {
        return Blocks.WEEPING_VINES_PLANT;
    }

    @Override
    protected boolean chooseStemState(BlockState arg) {
        return VineLogic.isValidForWeepingStem(arg);
    }
}

