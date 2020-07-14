/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignItem
extends WallStandingBlockItem {
    public SignItem(Item.Settings settings, Block standingBlock, Block wallBlock) {
        super(standingBlock, wallBlock, settings);
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        boolean bl = super.postPlacement(pos, world, player, stack, state);
        if (!world.isClient && !bl && player != null) {
            player.openEditSignScreen((SignBlockEntity)world.getBlockEntity(pos));
        }
        return bl;
    }
}

