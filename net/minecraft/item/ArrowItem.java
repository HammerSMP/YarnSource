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

    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        ArrowEntity lv = new ArrowEntity(world, shooter);
        lv.initFromStack(stack);
        return lv;
    }
}

