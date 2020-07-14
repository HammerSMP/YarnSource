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

    public OnAStickItem(Item.Settings settings, EntityType<T> target, int damagePerUse) {
        super(settings);
        this.target = target;
        this.damagePerUse = damagePerUse;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemSteerable lv3;
        ItemStack lv = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.pass(lv);
        }
        Entity lv2 = user.getVehicle();
        if (user.hasVehicle() && lv2 instanceof ItemSteerable && lv2.getType() == this.target && (lv3 = (ItemSteerable)((Object)lv2)).consumeOnAStickItem()) {
            lv.damage(this.damagePerUse, user, p -> p.sendToolBreakStatus(hand));
            if (lv.isEmpty()) {
                ItemStack lv4 = new ItemStack(Items.FISHING_ROD);
                lv4.setTag(lv.getTag());
                return TypedActionResult.success(lv4);
            }
            return TypedActionResult.success(lv);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.pass(lv);
    }
}

