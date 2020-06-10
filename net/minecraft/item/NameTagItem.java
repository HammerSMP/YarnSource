/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class NameTagItem
extends Item {
    public NameTagItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnEntity(ItemStack arg, PlayerEntity arg2, LivingEntity arg3, Hand arg4) {
        if (arg.hasCustomName() && !(arg3 instanceof PlayerEntity)) {
            if (!arg2.world.isClient && arg3.isAlive()) {
                arg3.setCustomName(arg.getName());
                if (arg3 instanceof MobEntity) {
                    ((MobEntity)arg3).setPersistent();
                }
                arg.decrement(1);
            }
            return ActionResult.success(arg2.world.isClient);
        }
        return ActionResult.PASS;
    }
}

