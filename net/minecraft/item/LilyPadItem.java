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
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockHitResult lv = LilyPadItem.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        BlockHitResult lv2 = lv.method_29328(lv.getBlockPos().up());
        ActionResult lv3 = super.useOnBlock(new ItemUsageContext(user, hand, lv2));
        return new TypedActionResult<ItemStack>(lv3, user.getStackInHand(hand));
    }
}

