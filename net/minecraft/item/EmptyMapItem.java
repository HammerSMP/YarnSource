/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem
extends NetworkSyncedItem {
    public EmptyMapItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = FilledMapItem.createMap(arg, MathHelper.floor(arg2.getX()), MathHelper.floor(arg2.getZ()), (byte)0, true, false);
        ItemStack lv2 = arg2.getStackInHand(arg3);
        if (!arg2.abilities.creativeMode) {
            lv2.decrement(1);
        }
        arg2.incrementStat(Stats.USED.getOrCreateStat(this));
        if (lv2.isEmpty()) {
            return TypedActionResult.success(lv);
        }
        if (!arg2.inventory.insertStack(lv.copy())) {
            arg2.dropItem(lv, false);
        }
        return TypedActionResult.success(lv2);
    }
}

