/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TransparentBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class AbstractGlassBlock
extends TransparentBlock {
    protected AbstractGlassBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getVisualShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.empty();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState arg, BlockView arg2, BlockPos arg3) {
        return 1.0f;
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return true;
    }
}

