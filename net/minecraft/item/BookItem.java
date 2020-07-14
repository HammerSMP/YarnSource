/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BookItem
extends Item {
    public BookItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

