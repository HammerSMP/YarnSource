/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem
extends Item {
    public FireChargeItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg2) {
        World lv = arg2.getWorld();
        BlockPos lv2 = arg2.getBlockPos();
        BlockState lv3 = lv.getBlockState(lv2);
        boolean bl = false;
        if (lv3.method_27851(BlockTags.CAMPFIRES, arg -> arg.contains(CampfireBlock.LIT) && arg.contains(CampfireBlock.WATERLOGGED))) {
            if (!lv3.get(CampfireBlock.LIT).booleanValue() && !lv3.get(CampfireBlock.WATERLOGGED).booleanValue()) {
                this.playUseSound(lv, lv2);
                lv.setBlockState(lv2, (BlockState)lv3.with(CampfireBlock.LIT, true));
                bl = true;
            }
        } else if (lv.getBlockState(lv2 = lv2.offset(arg2.getSide())).isAir()) {
            this.playUseSound(lv, lv2);
            lv.setBlockState(lv2, AbstractFireBlock.getState(lv, lv2));
            bl = true;
        }
        if (bl) {
            arg2.getStack().decrement(1);
            return ActionResult.success(lv.isClient);
        }
        return ActionResult.FAIL;
    }

    private void playUseSound(World arg, BlockPos arg2) {
        arg.playSound(null, arg2, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2f + 1.0f);
    }
}

