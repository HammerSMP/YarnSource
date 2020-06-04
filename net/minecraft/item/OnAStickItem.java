/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OnAStickItem<T extends Entity>
extends Item {
    private final EntityType<T> target;
    private final int damagePerUse;

    public OnAStickItem(Item.Settings arg, EntityType<T> arg2, int i) {
        super(arg);
        this.target = arg2;
        this.damagePerUse = i;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg22, Hand arg3) {
        ItemSteerable lv3;
        ItemStack lv = arg22.getStackInHand(arg3);
        if (arg.isClient) {
            return TypedActionResult.pass(lv);
        }
        Entity lv2 = arg22.getVehicle();
        if (arg22.hasVehicle() && lv2 instanceof ItemSteerable && lv2.getType() == this.target && (lv3 = (ItemSteerable)((Object)lv2)).consumeOnAStickItem()) {
            lv.damage(this.damagePerUse, arg22, arg2 -> arg2.sendToolBreakStatus(arg3));
            if (lv.isEmpty()) {
                ItemStack lv4 = new ItemStack(Items.FISHING_ROD);
                lv4.setTag(lv.getTag());
                return TypedActionResult.success(lv4);
            }
            return TypedActionResult.success(lv);
        }
        arg22.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.pass(lv);
    }
}

