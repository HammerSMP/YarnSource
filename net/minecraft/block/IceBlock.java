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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.TransparentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceBlock
extends TransparentBlock {
    public IceBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public void afterBreak(World arg, PlayerEntity arg2, BlockPos arg3, BlockState arg4, @Nullable BlockEntity arg5, ItemStack arg6) {
        super.afterBreak(arg, arg2, arg3, arg4, arg5, arg6);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, arg6) == 0) {
            if (arg.getDimension().method_27999()) {
                arg.removeBlock(arg3, false);
                return;
            }
            Material lv = arg.getBlockState(arg3.down()).getMaterial();
            if (lv.blocksMovement() || lv.isLiquid()) {
                arg.setBlockState(arg3, Blocks.WATER.getDefaultState());
            }
        }
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg2.getLightLevel(LightType.BLOCK, arg3) > 11 - arg.getOpacity(arg2, arg3)) {
            this.melt(arg, arg2, arg3);
        }
    }

    protected void melt(BlockState arg, World arg2, BlockPos arg3) {
        if (arg2.getDimension().method_27999()) {
            arg2.removeBlock(arg3, false);
            return;
        }
        arg2.setBlockState(arg3, Blocks.WATER.getDefaultState());
        arg2.updateNeighbor(arg3, Blocks.WATER, arg3);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.NORMAL;
    }
}

