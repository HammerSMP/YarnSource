/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ShearsDispenserBehavior
extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
        World lv = arg.getWorld();
        if (!lv.isClient()) {
            BlockPos lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING));
            this.setSuccess(ShearsDispenserBehavior.tryShearBlock((ServerWorld)lv, lv2) || ShearsDispenserBehavior.tryShearEntity((ServerWorld)lv, lv2));
            if (this.isSuccess() && arg2.damage(1, lv.getRandom(), null)) {
                arg2.setCount(0);
            }
        }
        return arg2;
    }

    private static boolean tryShearBlock(ServerWorld arg, BlockPos arg2) {
        int i;
        BlockState lv = arg.getBlockState(arg2);
        if (lv.isIn(BlockTags.BEEHIVES) && (i = lv.get(BeehiveBlock.HONEY_LEVEL).intValue()) >= 5) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb(arg, arg2);
            ((BeehiveBlock)lv.getBlock()).takeHoney(arg, lv, arg2, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
            return true;
        }
        return false;
    }

    private static boolean tryShearEntity(ServerWorld arg, BlockPos arg2) {
        List<Entity> list = arg.getEntities(LivingEntity.class, new Box(arg2), EntityPredicates.EXCEPT_SPECTATOR);
        for (LivingEntity livingEntity : list) {
            Shearable lv2;
            if (!(livingEntity instanceof Shearable) || !(lv2 = (Shearable)((Object)livingEntity)).isShearable()) continue;
            lv2.sheared(SoundCategory.BLOCKS);
            return true;
        }
        return false;
    }
}

