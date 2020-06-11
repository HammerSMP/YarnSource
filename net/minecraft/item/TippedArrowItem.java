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
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TippedArrowItem
extends ArrowItem {
    public TippedArrowItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ItemStack getStackForRender() {
        return PotionUtil.setPotion(super.getStackForRender(), Potions.POISON);
    }

    @Override
    public void appendStacks(ItemGroup arg, DefaultedList<ItemStack> arg2) {
        if (this.isIn(arg)) {
            for (Potion lv : Registry.POTION) {
                if (lv.getEffects().isEmpty()) continue;
                arg2.add(PotionUtil.setPotion(new ItemStack(this), lv));
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        PotionUtil.buildTooltip(arg, list, 0.125f);
    }

    @Override
    public String getTranslationKey(ItemStack arg) {
        return PotionUtil.getPotion(arg).finishTranslationKey(this.getTranslationKey() + ".effect.");
    }
}

