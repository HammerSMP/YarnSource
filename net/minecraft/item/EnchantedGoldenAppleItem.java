/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnchantedGoldenAppleItem
extends Item {
    public EnchantedGoldenAppleItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack arg) {
        return true;
    }
}

