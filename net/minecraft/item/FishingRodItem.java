/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FishingRodItem
extends Item
implements Vanishable {
    public FishingRodItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg22, Hand arg3) {
        ItemStack lv = arg22.getStackInHand(arg3);
        if (arg22.fishHook != null) {
            if (!arg.isClient) {
                int i = arg22.fishHook.use(lv);
                lv.damage(i, arg22, arg2 -> arg2.sendToolBreakStatus(arg3));
            }
            arg.playSound(null, arg22.getX(), arg22.getY(), arg22.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        } else {
            arg.playSound(null, arg22.getX(), arg22.getY(), arg22.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
            if (!arg.isClient) {
                int j = EnchantmentHelper.getLure(lv);
                int k = EnchantmentHelper.getLuckOfTheSea(lv);
                arg.spawnEntity(new FishingBobberEntity(arg22, arg, k, j));
            }
            arg22.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.method_29237(lv, arg.isClient());
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

