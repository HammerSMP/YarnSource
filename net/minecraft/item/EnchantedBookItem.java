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
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static ListTag getEnchantmentTag(ItemStack stack) {
        CompoundTag lv = stack.getTag();
        if (lv != null) {
            return lv.getList("StoredEnchantments", 10);
        }
        return new ListTag();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        ItemStack.appendEnchantments(tooltip, EnchantedBookItem.getEnchantmentTag(stack));
    }

    public static void addEnchantment(ItemStack stack, EnchantmentLevelEntry entry) {
        ListTag lv = EnchantedBookItem.getEnchantmentTag(stack);
        boolean bl = true;
        Identifier lv2 = Registry.ENCHANTMENT.getId(entry.enchantment);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv3 = lv.getCompound(i);
            Identifier lv4 = Identifier.tryParse(lv3.getString("id"));
            if (lv4 == null || !lv4.equals(lv2)) continue;
            if (lv3.getInt("lvl") < entry.level) {
                lv3.putShort("lvl", (short)entry.level);
            }
            bl = false;
            break;
        }
        if (bl) {
            CompoundTag lv5 = new CompoundTag();
            lv5.putString("id", String.valueOf(lv2));
            lv5.putShort("lvl", (short)entry.level);
            lv.add(lv5);
        }
        stack.getOrCreateTag().put("StoredEnchantments", lv);
    }

    public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
        ItemStack lv = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(lv, info);
        return lv;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        block4: {
            block3: {
                if (group != ItemGroup.SEARCH) break block3;
                for (Enchantment lv : Registry.ENCHANTMENT) {
                    if (lv.type == null) continue;
                    for (int i = lv.getMinLevel(); i <= lv.getMaxLevel(); ++i) {
                        stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(lv, i)));
                    }
                }
                break block4;
            }
            if (group.getEnchantments().length == 0) break block4;
            for (Enchantment lv2 : Registry.ENCHANTMENT) {
                if (!group.containsEnchantments(lv2.type)) continue;
                stacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(lv2, lv2.getMaxLevel())));
            }
        }
    }
}

