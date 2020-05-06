/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

public class ToolItem
extends Item {
    private final ToolMaterial material;

    public ToolItem(ToolMaterial arg, Item.Settings arg2) {
        super(arg2.maxDamageIfAbsent(arg.getDurability()));
        this.material = arg;
    }

    public ToolMaterial getMaterial() {
        return this.material;
    }

    @Override
    public int getEnchantability() {
        return this.material.getEnchantability();
    }

    @Override
    public boolean canRepair(ItemStack arg, ItemStack arg2) {
        return this.material.getRepairIngredient().test(arg2) || super.canRepair(arg, arg2);
    }
}

