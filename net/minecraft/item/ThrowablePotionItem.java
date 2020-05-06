/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThrowablePotionItem
extends PotionItem {
    public ThrowablePotionItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        if (!arg.isClient) {
            PotionEntity lv2 = new PotionEntity(arg, arg2);
            lv2.setItem(lv);
            lv2.setProperties(arg2, arg2.pitch, arg2.yaw, -20.0f, 0.5f, 1.0f);
            arg.spawnEntity(lv2);
        }
        arg2.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!arg2.abilities.creativeMode) {
            lv.decrement(1);
        }
        return TypedActionResult.success(lv);
    }
}

