/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class class_5328 {
    public static TypedActionResult<ItemStack> method_29282(World arg, PlayerEntity arg2, Hand arg3) {
        arg2.setCurrentHand(arg3);
        return TypedActionResult.consume(arg2.getStackInHand(arg3));
    }
}

