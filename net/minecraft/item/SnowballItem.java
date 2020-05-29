/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SnowballItem
extends Item {
    public SnowballItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        arg.playSound(null, arg2.getX(), arg2.getY(), arg2.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        if (!arg.isClient) {
            SnowballEntity lv2 = new SnowballEntity(arg, arg2);
            lv2.setItem(lv);
            lv2.setProperties(arg2, arg2.pitch, arg2.yaw, 0.0f, 1.5f, 1.0f);
            arg.spawnEntity(lv2);
        }
        arg2.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!arg2.abilities.creativeMode) {
            lv.decrement(1);
        }
        return TypedActionResult.method_29237(lv, arg.isClient());
    }
}

