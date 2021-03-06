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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem
extends Item {
    public FireChargeItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World lv = context.getWorld();
        BlockPos lv2 = context.getBlockPos();
        BlockState lv3 = lv.getBlockState(lv2);
        boolean bl = false;
        if (CampfireBlock.method_30035(lv3)) {
            this.playUseSound(lv, lv2);
            lv.setBlockState(lv2, (BlockState)lv3.with(CampfireBlock.LIT, true));
            bl = true;
        } else if (AbstractFireBlock.method_30032(lv, lv2 = lv2.offset(context.getSide()), context.getPlayerFacing())) {
            this.playUseSound(lv, lv2);
            lv.setBlockState(lv2, AbstractFireBlock.getState(lv, lv2));
            bl = true;
        }
        if (bl) {
            context.getStack().decrement(1);
            return ActionResult.success(lv.isClient);
        }
        return ActionResult.FAIL;
    }

    private void playUseSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2f + 1.0f);
    }
}

