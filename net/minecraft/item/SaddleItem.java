/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class SaddleItem
extends Item {
    public SaddleItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnEntity(ItemStack arg, PlayerEntity arg2, LivingEntity arg3, Hand arg4) {
        Saddleable lv;
        if (arg3 instanceof Saddleable && arg3.isAlive() && !(lv = (Saddleable)((Object)arg3)).isSaddled() && lv.canBeSaddled()) {
            if (!arg2.world.isClient) {
                lv.saddle(SoundCategory.NEUTRAL);
                arg.decrement(1);
            }
            return ActionResult.success(arg2.world.isClient);
        }
        return ActionResult.PASS;
    }
}

