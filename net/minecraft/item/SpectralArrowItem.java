/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpectralArrowItem
extends ArrowItem {
    public SpectralArrowItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public PersistentProjectileEntity createArrow(World arg, ItemStack arg2, LivingEntity arg3) {
        return new SpectralArrowEntity(arg, arg3);
    }
}

