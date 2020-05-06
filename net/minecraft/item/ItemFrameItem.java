/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemFrameItem
extends DecorationItem {
    public ItemFrameItem(Item.Settings arg) {
        super(EntityType.ITEM_FRAME, arg);
    }

    @Override
    protected boolean canPlaceOn(PlayerEntity arg, Direction arg2, ItemStack arg3, BlockPos arg4) {
        return !World.isHeightInvalid(arg4) && arg.canPlaceOn(arg4, arg2, arg3);
    }
}

