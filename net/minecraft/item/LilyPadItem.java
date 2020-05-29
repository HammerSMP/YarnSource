/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LilyPadItem
extends BlockItem {
    public LilyPadItem(Block arg, Item.Settings arg2) {
        super(arg, arg2);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        BlockHitResult lv = LilyPadItem.rayTrace(arg, arg2, RayTraceContext.FluidHandling.SOURCE_ONLY);
        BlockHitResult lv2 = lv.method_29328(lv.getBlockPos().up());
        ActionResult lv3 = super.useOnBlock(new ItemUsageContext(arg2, arg3, lv2));
        return new TypedActionResult<ItemStack>(lv3, arg2.getStackInHand(arg3));
    }
}

