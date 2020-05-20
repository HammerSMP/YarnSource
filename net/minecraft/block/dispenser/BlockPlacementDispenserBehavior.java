/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockPlacementDispenserBehavior
extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
        this.setSuccess(false);
        Item lv = arg2.getItem();
        if (lv instanceof BlockItem) {
            Direction lv2 = arg.getBlockState().get(DispenserBlock.FACING);
            BlockPos lv3 = arg.getBlockPos().offset(lv2);
            Direction lv4 = arg.getWorld().isAir(lv3.down()) ? lv2 : Direction.UP;
            this.setSuccess(((BlockItem)lv).place(new AutomaticItemPlacementContext(arg.getWorld(), lv3, lv2, arg2, lv4)) == ActionResult.SUCCESS);
        }
        return arg2;
    }
}

