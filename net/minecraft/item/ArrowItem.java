/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArrowItem
extends Item {
    public ArrowItem(Item.Settings arg) {
        super(arg);
    }

    public PersistentProjectileEntity createArrow(World arg, ItemStack arg2, LivingEntity arg3) {
        ArrowEntity lv = new ArrowEntity(arg, arg3);
        lv.initFromStack(arg2);
        return lv;
    }
}

