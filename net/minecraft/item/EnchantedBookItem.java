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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EnchantedBookItem
extends Item {
    public EnchantedBookItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack arg) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack arg) {
        return false;
    }

    public static ListTag getEnchantmentTag(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        if (lv != null) {
            return lv.getList("StoredEnchantments", 10);
        }
        return new ListTag();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        super.appendTooltip(arg, arg2, list, arg3);
        ItemStack.appendEnchantments(list, EnchantedBookItem.getEnchantmentTag(arg));
    }

    public static void addEnchantment(ItemStack arg, EnchantmentLevelEntry arg2) {
        ListTag lv = EnchantedBookItem.getEnchantmentTag(arg);
        boolean bl = true;
        Identifier lv2 = Registry.ENCHANTMENT.getId(arg2.enchantment);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv3 = lv.getCompound(i);
            Identifier lv4 = Identifier.tryParse(lv3.getString("id"));
            if (lv4 == null || !lv4.equals(lv2)) continue;
            if (lv3.getInt("lvl") < arg2.level) {
                lv3.putShort("lvl", (short)arg2.level);
            }
            bl = false;
            break;
        }
        if (bl) {
            CompoundTag lv5 = new CompoundTag();
            lv5.putString("id", String.valueOf(lv2));
            lv5.putShort("lvl", (short)arg2.level);
            lv.add(lv5);
        }
        arg.getOrCreateTag().put("StoredEnchantments", lv);
    }

    public static ItemStack forEnchantment(EnchantmentLevelEntry arg) {
        ItemStack lv = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(lv, arg);
        return lv;
    }

    @Override
    public void appendStacks(ItemGroup arg, DefaultedList<ItemStack> arg2) {
        block4: {
            block3: {
                if (arg != ItemGroup.SEARCH) break block3;
                for (Enchantment lv : Registry.ENCHANTMENT) {
                    if (lv.type == null) continue;
                    for (int i = lv.getMinimumLevel(); i <= lv.getMaximumLevel(); ++i) {
                        arg2.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(lv, i)));
                    }
                }
                break block4;
            }
            if (arg.getEnchantments().length == 0) break block4;
            for (Enchantment lv2 : Registry.ENCHANTMENT) {
                if (!arg.containsEnchantments(lv2.type)) continue;
                arg2.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(lv2, lv2.getMaximumLevel())));
            }
        }
    }
}

