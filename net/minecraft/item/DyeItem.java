/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

public class DyeItem
extends Item {
    private static final Map<DyeColor, DyeItem> DYES = Maps.newEnumMap(DyeColor.class);
    private final DyeColor color;

    public DyeItem(DyeColor arg, Item.Settings arg2) {
        super(arg2);
        this.color = arg;
        DYES.put(arg, this);
    }

    @Override
    public ActionResult useOnEntity(ItemStack arg, PlayerEntity arg2, LivingEntity arg3, Hand arg4) {
        SheepEntity lv;
        if (arg3 instanceof SheepEntity && (lv = (SheepEntity)arg3).isAlive() && !lv.isSheared() && lv.getColor() != this.color) {
            lv.setColor(this.color);
            arg.decrement(1);
            return ActionResult.method_29236(arg2.world.isClient);
        }
        return ActionResult.PASS;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public static DyeItem byColor(DyeColor arg) {
        return DYES.get(arg);
    }
}

