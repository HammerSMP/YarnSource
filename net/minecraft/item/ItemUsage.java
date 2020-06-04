/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemUsage {
    public static TypedActionResult<ItemStack> consumeHeldItem(World arg, PlayerEntity arg2, Hand arg3) {
        arg2.setCurrentHand(arg3);
        return TypedActionResult.consume(arg2.getStackInHand(arg3));
    }
}

