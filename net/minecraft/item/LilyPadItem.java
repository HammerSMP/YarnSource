/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
        ItemStack lv = arg2.getStackInHand(arg3);
        HitResult lv2 = LilyPadItem.rayTrace(arg, arg2, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (lv2.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(lv);
        }
        if (lv2.getType() == HitResult.Type.BLOCK) {
            BlockHitResult lv3 = (BlockHitResult)lv2;
            BlockPos lv4 = lv3.getBlockPos();
            Direction lv5 = lv3.getSide();
            if (!arg.canPlayerModifyAt(arg2, lv4) || !arg2.canPlaceOn(lv4.offset(lv5), lv5, lv)) {
                return TypedActionResult.fail(lv);
            }
            BlockPos lv6 = lv4.up();
            BlockState lv7 = arg.getBlockState(lv4);
            Material lv8 = lv7.getMaterial();
            FluidState lv9 = arg.getFluidState(lv4);
            if ((lv9.getFluid() == Fluids.WATER || lv8 == Material.ICE) && arg.isAir(lv6)) {
                arg.setBlockState(lv6, Blocks.LILY_PAD.getDefaultState(), 11);
                if (arg2 instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)arg2, lv6, lv);
                }
                if (!arg2.abilities.creativeMode) {
                    lv.decrement(1);
                }
                arg2.incrementStat(Stats.USED.getOrCreateStat(this));
                arg.playSound(arg2, lv4, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return TypedActionResult.success(lv);
            }
        }
        return TypedActionResult.fail(lv);
    }
}

