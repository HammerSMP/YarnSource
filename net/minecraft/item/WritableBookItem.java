/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WritableBookItem
extends Item {
    public WritableBookItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos lv2;
        World lv = context.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = context.getBlockPos());
        if (lv3.isOf(Blocks.LECTERN)) {
            return LecternBlock.putBookIfAbsent(lv, lv2, lv3, context.getStack()) ? ActionResult.success(lv.isClient) : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        user.openEditBookScreen(lv, hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.method_29237(lv, world.isClient());
    }

    public static boolean isValid(@Nullable CompoundTag tag) {
        if (tag == null) {
            return false;
        }
        if (!tag.contains("pages", 9)) {
            return false;
        }
        ListTag lv = tag.getList("pages", 8);
        for (int i = 0; i < lv.size(); ++i) {
            String string = lv.getString(i);
            if (string.length() <= 32767) continue;
            return false;
        }
        return true;
    }
}

