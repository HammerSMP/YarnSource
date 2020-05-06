/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class FireworkStarRecipe
extends SpecialCraftingRecipe {
    private static final Ingredient TYPE_MODIFIER = Ingredient.ofItems(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
    private static final Ingredient TRAIL_MODIFIER = Ingredient.ofItems(Items.DIAMOND);
    private static final Ingredient FLICKER_MODIFIER = Ingredient.ofItems(Items.GLOWSTONE_DUST);
    private static final Map<Item, FireworkItem.Type> TYPE_MODIFIER_MAP = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(Items.FIRE_CHARGE, FireworkItem.Type.LARGE_BALL);
        hashMap.put(Items.FEATHER, FireworkItem.Type.BURST);
        hashMap.put(Items.GOLD_NUGGET, FireworkItem.Type.STAR);
        hashMap.put(Items.SKELETON_SKULL, FireworkItem.Type.CREEPER);
        hashMap.put(Items.WITHER_SKELETON_SKULL, FireworkItem.Type.CREEPER);
        hashMap.put(Items.CREEPER_HEAD, FireworkItem.Type.CREEPER);
        hashMap.put(Items.PLAYER_HEAD, FireworkItem.Type.CREEPER);
        hashMap.put(Items.DRAGON_HEAD, FireworkItem.Type.CREEPER);
        hashMap.put(Items.ZOMBIE_HEAD, FireworkItem.Type.CREEPER);
    });
    private static final Ingredient GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);

    public FireworkStarRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStack(i);
            if (lv.isEmpty()) continue;
            if (TYPE_MODIFIER.test(lv)) {
                if (bl3) {
                    return false;
                }
                bl3 = true;
                continue;
            }
            if (FLICKER_MODIFIER.test(lv)) {
                if (bl5) {
                    return false;
                }
                bl5 = true;
                continue;
            }
            if (TRAIL_MODIFIER.test(lv)) {
                if (bl4) {
                    return false;
                }
                bl4 = true;
                continue;
            }
            if (GUNPOWDER.test(lv)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (lv.getItem() instanceof DyeItem) {
                bl2 = true;
                continue;
            }
            return false;
        }
        return bl && bl2;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ItemStack lv = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag lv2 = lv.getOrCreateSubTag("Explosion");
        FireworkItem.Type lv3 = FireworkItem.Type.SMALL_BALL;
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv4 = arg.getStack(i);
            if (lv4.isEmpty()) continue;
            if (TYPE_MODIFIER.test(lv4)) {
                lv3 = TYPE_MODIFIER_MAP.get(lv4.getItem());
                continue;
            }
            if (FLICKER_MODIFIER.test(lv4)) {
                lv2.putBoolean("Flicker", true);
                continue;
            }
            if (TRAIL_MODIFIER.test(lv4)) {
                lv2.putBoolean("Trail", true);
                continue;
            }
            if (!(lv4.getItem() instanceof DyeItem)) continue;
            list.add(((DyeItem)lv4.getItem()).getColor().getFireworkColor());
        }
        lv2.putIntArray("Colors", list);
        lv2.putByte("Type", (byte)lv3.getId());
        return lv;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
}

