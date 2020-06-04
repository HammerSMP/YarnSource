/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkBucketItem
extends Item {
    public MilkBucketItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ItemStack finishUsing(ItemStack arg, World arg2, LivingEntity arg3) {
        if (arg3 instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)arg3;
            Criteria.CONSUME_ITEM.trigger(lv, arg);
            lv.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        if (arg3 instanceof PlayerEntity && !((PlayerEntity)arg3).abilities.creativeMode) {
            arg.decrement(1);
        }
        if (!arg2.isClient) {
            arg3.clearStatusEffects();
        }
        if (arg.isEmpty()) {
            return new ItemStack(Items.BUCKET);
        }
        return arg;
    }

    @Override
    public int getMaxUseTime(ItemStack arg) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack arg) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        return ItemUsage.consumeHeldItem(arg, arg2, arg3);
    }
}

