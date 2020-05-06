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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class CommandBlockItem
extends BlockItem {
    public CommandBlockItem(Block arg, Item.Settings arg2) {
        super(arg, arg2);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext arg) {
        PlayerEntity lv = arg.getPlayer();
        return lv == null || lv.isCreativeLevelTwoOp() ? super.getPlacementState(arg) : null;
    }
}

