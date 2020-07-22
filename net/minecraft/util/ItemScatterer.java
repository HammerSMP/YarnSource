/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScatterer {
    private static final Random RANDOM = new Random();

    public static void spawn(World arg, BlockPos arg2, Inventory arg3) {
        ItemScatterer.spawn(arg, (double)arg2.getX(), (double)arg2.getY(), (double)arg2.getZ(), arg3);
    }

    public static void spawn(World arg, Entity arg2, Inventory arg3) {
        ItemScatterer.spawn(arg, arg2.getX(), arg2.getY(), arg2.getZ(), arg3);
    }

    private static void spawn(World world, double x, double y, double z, Inventory inventory) {
        for (int i = 0; i < inventory.size(); ++i) {
            ItemScatterer.spawn(world, x, y, z, inventory.getStack(i));
        }
    }

    public static void spawn(World world, BlockPos pos, DefaultedList<ItemStack> items) {
        items.forEach(arg3 -> ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), arg3));
    }

    public static void spawn(World arg, double x, double y, double z, ItemStack item) {
        double g = EntityType.ITEM.getWidth();
        double h = 1.0 - g;
        double i = g / 2.0;
        double j = Math.floor(x) + RANDOM.nextDouble() * h + i;
        double k = Math.floor(y) + RANDOM.nextDouble() * h;
        double l = Math.floor(z) + RANDOM.nextDouble() * h + i;
        while (!item.isEmpty()) {
            ItemEntity lv = new ItemEntity(arg, j, k, l, item.split(RANDOM.nextInt(21) + 10));
            float m = 0.05f;
            lv.setVelocity(RANDOM.nextGaussian() * (double)0.05f, RANDOM.nextGaussian() * (double)0.05f + (double)0.2f, RANDOM.nextGaussian() * (double)0.05f);
            arg.spawnEntity(lv);
        }
    }
}

