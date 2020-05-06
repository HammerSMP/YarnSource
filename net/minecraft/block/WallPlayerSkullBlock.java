/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallPlayerSkullBlock
extends WallSkullBlock {
    protected WallPlayerSkullBlock(AbstractBlock.Settings arg) {
        super(SkullBlock.Type.PLAYER, arg);
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
        Blocks.PLAYER_HEAD.onPlaced(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState arg, LootContext.Builder arg2) {
        return Blocks.PLAYER_HEAD.getDroppedStacks(arg, arg2);
    }
}

