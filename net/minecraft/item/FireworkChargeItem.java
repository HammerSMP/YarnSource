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
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class FireworkChargeItem
extends Item {
    public FireworkChargeItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        CompoundTag lv = arg.getSubTag("Explosion");
        if (lv != null) {
            FireworkChargeItem.appendFireworkTooltip(lv, list);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendFireworkTooltip(CompoundTag arg, List<Text> list) {
        int[] js;
        FireworkItem.Type lv = FireworkItem.Type.byId(arg.getByte("Type"));
        list.add(new TranslatableText("item.minecraft.firework_star.shape." + lv.getName()).formatted(Formatting.GRAY));
        int[] is = arg.getIntArray("Colors");
        if (is.length > 0) {
            list.add(FireworkChargeItem.appendColors(new LiteralText("").formatted(Formatting.GRAY), is));
        }
        if ((js = arg.getIntArray("FadeColors")).length > 0) {
            list.add(FireworkChargeItem.appendColors(new TranslatableText("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.GRAY), js));
        }
        if (arg.getBoolean("Trail")) {
            list.add(new TranslatableText("item.minecraft.firework_star.trail").formatted(Formatting.GRAY));
        }
        if (arg.getBoolean("Flicker")) {
            list.add(new TranslatableText("item.minecraft.firework_star.flicker").formatted(Formatting.GRAY));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Text appendColors(MutableText arg, int[] is) {
        for (int i = 0; i < is.length; ++i) {
            if (i > 0) {
                arg.append(", ");
            }
            arg.append(FireworkChargeItem.getColorText(is[i]));
        }
        return arg;
    }

    @Environment(value=EnvType.CLIENT)
    private static Text getColorText(int i) {
        DyeColor lv = DyeColor.byFireworkColor(i);
        if (lv == null) {
            return new TranslatableText("item.minecraft.firework_star.custom_color");
        }
        return new TranslatableText("item.minecraft.firework_star." + lv.getName());
    }
}

