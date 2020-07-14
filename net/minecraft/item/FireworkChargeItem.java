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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag lv = stack.getSubTag("Explosion");
        if (lv != null) {
            FireworkChargeItem.appendFireworkTooltip(lv, tooltip);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendFireworkTooltip(CompoundTag tag, List<Text> tooltip) {
        int[] js;
        FireworkItem.Type lv = FireworkItem.Type.byId(tag.getByte("Type"));
        tooltip.add(new TranslatableText("item.minecraft.firework_star.shape." + lv.getName()).formatted(Formatting.GRAY));
        int[] is = tag.getIntArray("Colors");
        if (is.length > 0) {
            tooltip.add(FireworkChargeItem.appendColors(new LiteralText("").formatted(Formatting.GRAY), is));
        }
        if ((js = tag.getIntArray("FadeColors")).length > 0) {
            tooltip.add(FireworkChargeItem.appendColors(new TranslatableText("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.GRAY), js));
        }
        if (tag.getBoolean("Trail")) {
            tooltip.add(new TranslatableText("item.minecraft.firework_star.trail").formatted(Formatting.GRAY));
        }
        if (tag.getBoolean("Flicker")) {
            tooltip.add(new TranslatableText("item.minecraft.firework_star.flicker").formatted(Formatting.GRAY));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Text appendColors(MutableText line, int[] colors) {
        for (int i = 0; i < colors.length; ++i) {
            if (i > 0) {
                line.append(", ");
            }
            line.append(FireworkChargeItem.getColorText(colors[i]));
        }
        return line;
    }

    @Environment(value=EnvType.CLIENT)
    private static Text getColorText(int color) {
        DyeColor lv = DyeColor.byFireworkColor(color);
        if (lv == null) {
            return new TranslatableText("item.minecraft.firework_star.custom_color");
        }
        return new TranslatableText("item.minecraft.firework_star." + lv.getName());
    }
}

