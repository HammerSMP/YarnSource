/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlintAndSteelItem
extends Item {
    public FlintAndSteelItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockPos lv3;
        PlayerEntity lv = arg.getPlayer();
        World lv2 = arg.getWorld();
        BlockState lv4 = lv2.getBlockState(lv3 = arg.getBlockPos());
        if (CampfireBlock.method_30035(lv4)) {
            lv2.playSound(lv, lv3, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            lv2.setBlockState(lv3, (BlockState)lv4.with(Properties.LIT, true), 11);
            if (lv != null) {
                arg.getStack().damage(1, lv, arg2 -> arg2.sendToolBreakStatus(arg.getHand()));
            }
            return ActionResult.success(lv2.isClient());
        }
        BlockPos lv5 = lv3.offset(arg.getSide());
        if (AbstractFireBlock.method_30032(lv2, lv5)) {
            lv2.playSound(lv, lv5, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            BlockState lv6 = AbstractFireBlock.getState(lv2, lv5);
            lv2.setBlockState(lv5, lv6, 11);
            ItemStack lv7 = arg.getStack();
            if (lv instanceof ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)lv, lv5, lv7);
                lv7.damage(1, lv, arg2 -> arg2.sendToolBreakStatus(arg.getHand()));
            }
            return ActionResult.success(lv2.isClient());
        }
        return ActionResult.FAIL;
    }
}

