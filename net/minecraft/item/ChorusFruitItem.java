/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem
extends Item {
    public ChorusFruitItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ItemStack finishUsing(ItemStack arg, World arg2, LivingEntity arg3) {
        ItemStack lv = super.finishUsing(arg, arg2, arg3);
        if (!arg2.isClient) {
            double d = arg3.getX();
            double e = arg3.getY();
            double f = arg3.getZ();
            for (int i = 0; i < 16; ++i) {
                double g = arg3.getX() + (arg3.getRandom().nextDouble() - 0.5) * 16.0;
                double h = MathHelper.clamp(arg3.getY() + (double)(arg3.getRandom().nextInt(16) - 8), 0.0, (double)(arg2.getDimensionHeight() - 1));
                double j = arg3.getZ() + (arg3.getRandom().nextDouble() - 0.5) * 16.0;
                if (arg3.hasVehicle()) {
                    arg3.stopRiding();
                }
                if (!arg3.teleport(g, h, j, true)) continue;
                SoundEvent lv2 = arg3 instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                arg2.playSound(null, d, e, f, lv2, SoundCategory.PLAYERS, 1.0f, 1.0f);
                arg3.playSound(lv2, 1.0f, 1.0f);
                break;
            }
            if (arg3 instanceof PlayerEntity) {
                ((PlayerEntity)arg3).getItemCooldownManager().set(this, 20);
            }
        }
        return lv;
    }
}

