/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ShieldItem
extends Item {
    public ShieldItem(Item.Settings arg) {
        super(arg);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public String getTranslationKey(ItemStack arg) {
        if (arg.getSubTag("BlockEntityTag") != null) {
            return this.getTranslationKey() + '.' + ShieldItem.getColor(arg).getName();
        }
        return super.getTranslationKey(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        BannerItem.appendBannerTooltip(arg, list);
    }

    @Override
    public UseAction getUseAction(ItemStack arg) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack arg) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        arg2.setCurrentHand(arg3);
        return TypedActionResult.consume(lv);
    }

    @Override
    public boolean canRepair(ItemStack arg, ItemStack arg2) {
        return ItemTags.PLANKS.contains(arg2.getItem()) || super.canRepair(arg, arg2);
    }

    public static DyeColor getColor(ItemStack arg) {
        return DyeColor.byId(arg.getOrCreateSubTag("BlockEntityTag").getInt("Base"));
    }
}

