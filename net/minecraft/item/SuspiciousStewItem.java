/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.World;

public class SuspiciousStewItem
extends Item {
    public SuspiciousStewItem(Item.Settings arg) {
        super(arg);
    }

    public static void addEffectToStew(ItemStack stew, StatusEffect effect, int duration) {
        CompoundTag lv = stew.getOrCreateTag();
        ListTag lv2 = lv.getList("Effects", 9);
        CompoundTag lv3 = new CompoundTag();
        lv3.putByte("EffectId", (byte)StatusEffect.getRawId(effect));
        lv3.putInt("EffectDuration", duration);
        lv2.add(lv3);
        lv.put("Effects", lv2);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack lv = super.finishUsing(stack, world, user);
        CompoundTag lv2 = stack.getTag();
        if (lv2 != null && lv2.contains("Effects", 9)) {
            ListTag lv3 = lv2.getList("Effects", 10);
            for (int i = 0; i < lv3.size(); ++i) {
                StatusEffect lv5;
                int j = 160;
                CompoundTag lv4 = lv3.getCompound(i);
                if (lv4.contains("EffectDuration", 3)) {
                    j = lv4.getInt("EffectDuration");
                }
                if ((lv5 = StatusEffect.byRawId(lv4.getByte("EffectId"))) == null) continue;
                user.addStatusEffect(new StatusEffectInstance(lv5, j));
            }
        }
        if (user instanceof PlayerEntity && ((PlayerEntity)user).abilities.creativeMode) {
            return lv;
        }
        return new ItemStack(Items.BOWL);
    }
}

