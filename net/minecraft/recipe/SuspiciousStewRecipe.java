/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SuspiciousStewRecipe
extends SpecialCraftingRecipe {
    public SuspiciousStewRecipe(Identifier arg) {
        super(arg);
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv = arg.getStack(i);
            if (lv.isEmpty()) continue;
            if (lv.getItem() == Blocks.BROWN_MUSHROOM.asItem() && !bl3) {
                bl3 = true;
                continue;
            }
            if (lv.getItem() == Blocks.RED_MUSHROOM.asItem() && !bl2) {
                bl2 = true;
                continue;
            }
            if (lv.getItem().isIn(ItemTags.SMALL_FLOWERS) && !bl) {
                bl = true;
                continue;
            }
            if (lv.getItem() == Items.BOWL && !bl4) {
                bl4 = true;
                continue;
            }
            return false;
        }
        return bl && bl3 && bl2 && bl4;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        ItemStack lv = ItemStack.EMPTY;
        for (int i = 0; i < arg.size(); ++i) {
            ItemStack lv2 = arg.getStack(i);
            if (lv2.isEmpty() || !lv2.getItem().isIn(ItemTags.SMALL_FLOWERS)) continue;
            lv = lv2;
            break;
        }
        ItemStack lv3 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        if (lv.getItem() instanceof BlockItem && ((BlockItem)lv.getItem()).getBlock() instanceof FlowerBlock) {
            FlowerBlock lv4 = (FlowerBlock)((BlockItem)lv.getItem()).getBlock();
            StatusEffect lv5 = lv4.getEffectInStew();
            SuspiciousStewItem.addEffectToStew(lv3, lv5, lv4.getEffectInStewDuration());
        }
        return lv3;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i >= 2 && j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}

