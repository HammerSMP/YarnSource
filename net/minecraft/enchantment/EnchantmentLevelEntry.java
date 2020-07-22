/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.collection.WeightedPicker;

public class EnchantmentLevelEntry
extends WeightedPicker.Entry {
    public final Enchantment enchantment;
    public final int level;

    public EnchantmentLevelEntry(Enchantment enchantment, int level) {
        super(enchantment.getRarity().getWeight());
        this.enchantment = enchantment;
        this.level = level;
    }
}

