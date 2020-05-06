/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class TallBlockItem
extends BlockItem {
    public TallBlockItem(Block arg, Item.Settings arg2) {
        super(arg, arg2);
    }

    @Override
    protected boolean place(ItemPlacementContext arg, BlockState arg2) {
        arg.getWorld().setBlockState(arg.getBlockPos().up(), Blocks.AIR.getDefaultState(), 27);
        return super.place(arg, arg2);
    }
}

