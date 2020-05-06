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
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class SandBlock
extends FallingBlock {
    private final int color;

    public SandBlock(int i, AbstractBlock.Settings arg) {
        super(arg);
        this.color = i;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState arg, BlockView arg2, BlockPos arg3) {
        return this.color;
    }
}

