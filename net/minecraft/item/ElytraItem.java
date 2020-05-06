/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Wearable;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem
extends Item
implements Wearable {
    public ElytraItem(Item.Settings arg) {
        super(arg);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    public static boolean isUsable(ItemStack arg) {
        return arg.getDamage() < arg.getMaxDamage() - 1;
    }

    @Override
    public boolean canRepair(ItemStack arg, ItemStack arg2) {
        return arg2.getItem() == Items.PHANTOM_MEMBRANE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        EquipmentSlot lv2 = MobEntity.getPreferredEquipmentSlot(lv);
        ItemStack lv3 = arg2.getEquippedStack(lv2);
        if (lv3.isEmpty()) {
            arg2.equipStack(lv2, lv.copy());
            lv.setCount(0);
            return TypedActionResult.success(lv);
        }
        return TypedActionResult.fail(lv);
    }
}

