/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;

public class EnderEyeItem
extends Item {
    public EnderEyeItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockPos lv2;
        World lv = arg.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = arg.getBlockPos());
        if (!lv3.isOf(Blocks.END_PORTAL_FRAME) || lv3.get(EndPortalFrameBlock.EYE).booleanValue()) {
            return ActionResult.PASS;
        }
        if (lv.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockState lv4 = (BlockState)lv3.with(EndPortalFrameBlock.EYE, true);
        Block.pushEntitiesUpBeforeBlockChange(lv3, lv4, lv, lv2);
        lv.setBlockState(lv2, lv4, 2);
        lv.updateComparators(lv2, Blocks.END_PORTAL_FRAME);
        arg.getStack().decrement(1);
        lv.syncWorldEvent(1503, lv2, 0);
        BlockPattern.Result lv5 = EndPortalFrameBlock.getCompletedFramePattern().searchAround(lv, lv2);
        if (lv5 != null) {
            BlockPos lv6 = lv5.getFrontTopLeft().add(-3, 0, -3);
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    lv.setBlockState(lv6.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), 2);
                }
            }
            lv.syncGlobalEvent(1038, lv6.add(1, 0, 1), 0);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        BlockPos lv3;
        ItemStack lv = arg2.getStackInHand(arg3);
        BlockHitResult lv2 = EnderEyeItem.rayTrace(arg, arg2, RayTraceContext.FluidHandling.NONE);
        if (((HitResult)lv2).getType() == HitResult.Type.BLOCK && arg.getBlockState(lv2.getBlockPos()).isOf(Blocks.END_PORTAL_FRAME)) {
            return TypedActionResult.pass(lv);
        }
        arg2.setCurrentHand(arg3);
        if (arg instanceof ServerWorld && (lv3 = ((ServerWorld)arg).getChunkManager().getChunkGenerator().locateStructure((ServerWorld)arg, StructureFeature.STRONGHOLD, arg2.getBlockPos(), 100, false)) != null) {
            EyeOfEnderEntity lv4 = new EyeOfEnderEntity(arg, arg2.getX(), arg2.getBodyY(0.5), arg2.getZ());
            lv4.setItem(lv);
            lv4.moveTowards(lv3);
            arg.spawnEntity(lv4);
            if (arg2 instanceof ServerPlayerEntity) {
                Criteria.USED_ENDER_EYE.trigger((ServerPlayerEntity)arg2, lv3);
            }
            arg.playSound(null, arg2.getX(), arg2.getY(), arg2.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
            arg.syncWorldEvent(null, 1003, arg2.getBlockPos(), 0);
            if (!arg2.abilities.creativeMode) {
                lv.decrement(1);
            }
            arg2.incrementStat(Stats.USED.getOrCreateStat(this));
            arg2.swingHand(arg3, true);
            return TypedActionResult.success(lv);
        }
        return TypedActionResult.consume(lv);
    }
}

