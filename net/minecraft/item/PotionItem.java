/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.class_5328;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PotionItem
extends Item {
    public PotionItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getStackForRender() {
        return PotionUtil.setPotion(super.getStackForRender(), Potions.WATER);
    }

    @Override
    public ItemStack finishUsing(ItemStack arg, World arg2, LivingEntity arg3) {
        PlayerEntity lv;
        PlayerEntity playerEntity = lv = arg3 instanceof PlayerEntity ? (PlayerEntity)arg3 : null;
        if (lv instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)lv, arg);
        }
        if (!arg2.isClient) {
            List<StatusEffectInstance> list = PotionUtil.getPotionEffects(arg);
            for (StatusEffectInstance lv2 : list) {
                if (lv2.getEffectType().isInstant()) {
                    lv2.getEffectType().applyInstantEffect(lv, lv, arg3, lv2.getAmplifier(), 1.0);
                    continue;
                }
                arg3.addStatusEffect(new StatusEffectInstance(lv2));
            }
        }
        if (lv != null) {
            lv.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!lv.abilities.creativeMode) {
                arg.decrement(1);
            }
        }
        if (lv == null || !lv.abilities.creativeMode) {
            if (arg.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (lv != null) {
                lv.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
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
        return class_5328.method_29282(arg, arg2, arg3);
    }

    @Override
    public String getTranslationKey(ItemStack arg) {
        return PotionUtil.getPotion(arg).finishTranslationKey(this.getTranslationKey() + ".effect.");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        PotionUtil.buildTooltip(arg, list, 1.0f);
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack arg) {
        return super.hasEnchantmentGlint(arg) || !PotionUtil.getPotionEffects(arg).isEmpty();
    }

    @Override
    public void appendStacks(ItemGroup arg, DefaultedList<ItemStack> arg2) {
        if (this.isIn(arg)) {
            for (Potion lv : Registry.POTION) {
                if (lv == Potions.EMPTY) continue;
                arg2.add(PotionUtil.setPotion(new ItemStack(this), lv));
            }
        }
    }
}

