/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class MushroomStewItem
extends Item {
    public MushroomStewItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ItemStack finishUsing(ItemStack arg, World arg2, LivingEntity arg3) {
        ItemStack lv = super.finishUsing(arg, arg2, arg3);
        if (arg3 instanceof PlayerEntity && ((PlayerEntity)arg3).abilities.creativeMode) {
            return lv;
        }
        return new ItemStack(Items.BOWL);
    }
}

