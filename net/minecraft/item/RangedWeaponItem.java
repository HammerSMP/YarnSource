/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;

public abstract class RangedWeaponItem
extends Item {
    public static final Predicate<ItemStack> BOW_PROJECTILES = arg -> arg.getItem().isIn(ItemTags.ARROWS);
    public static final Predicate<ItemStack> CROSSBOW_HELD_PROJECTILES = BOW_PROJECTILES.or(arg -> arg.getItem() == Items.FIREWORK_ROCKET);

    public RangedWeaponItem(Item.Settings arg) {
        super(arg);
    }

    public Predicate<ItemStack> getHeldProjectiles() {
        return this.getProjectiles();
    }

    public abstract Predicate<ItemStack> getProjectiles();

    public static ItemStack getHeldProjectile(LivingEntity arg, Predicate<ItemStack> predicate) {
        if (predicate.test(arg.getStackInHand(Hand.OFF_HAND))) {
            return arg.getStackInHand(Hand.OFF_HAND);
        }
        if (predicate.test(arg.getStackInHand(Hand.MAIN_HAND))) {
            return arg.getStackInHand(Hand.MAIN_HAND);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    public abstract int getRange();
}

