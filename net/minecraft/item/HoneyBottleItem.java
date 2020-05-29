/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.class_5328;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class HoneyBottleItem
extends Item {
    public HoneyBottleItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ItemStack finishUsing(ItemStack arg, World arg2, LivingEntity arg3) {
        super.finishUsing(arg, arg2, arg3);
        if (arg3 instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)arg3;
            Criteria.CONSUME_ITEM.trigger(lv, arg);
            lv.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        if (!arg2.isClient) {
            arg3.removeStatusEffect(StatusEffects.POISON);
        }
        if (arg.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (arg3 instanceof PlayerEntity && !((PlayerEntity)arg3).abilities.creativeMode) {
            ItemStack lv2 = new ItemStack(Items.GLASS_BOTTLE);
            PlayerEntity lv3 = (PlayerEntity)arg3;
            if (!lv3.inventory.insertStack(lv2)) {
                lv3.dropItem(lv2, false);
            }
        }
        return arg;
    }

    @Override
    public int getMaxUseTime(ItemStack arg) {
        return 40;
    }

    @Override
    public UseAction getUseAction(ItemStack arg) {
        return UseAction.DRINK;
    }

    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        return class_5328.method_29282(arg, arg2, arg3);
    }
}

