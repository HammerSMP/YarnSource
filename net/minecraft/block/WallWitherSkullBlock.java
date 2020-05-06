/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallWitherSkullBlock
extends WallSkullBlock {
    protected WallWitherSkullBlock(AbstractBlock.Settings arg) {
        super(SkullBlock.Type.WITHER_SKELETON, arg);
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
        Blocks.WITHER_SKELETON_SKULL.onPlaced(arg, arg2, arg3, arg4, arg5);
    }
}

