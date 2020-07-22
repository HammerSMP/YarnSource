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

public class ShearsDispenserBehavior
extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld lv = pointer.getWorld();
        if (!lv.isClient()) {
            BlockPos lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            this.setSuccess(ShearsDispenserBehavior.tryShearBlock(lv, lv2) || ShearsDispenserBehavior.tryShearEntity(lv, lv2));
            if (this.isSuccess() && stack.damage(1, lv.getRandom(), null)) {
                stack.setCount(0);
            }
        }
        return stack;
    }

    private static boolean tryShearBlock(ServerWorld world, BlockPos pos) {
        int i;
        BlockState lv = world.getBlockState(pos);
        if (lv.isIn(BlockTags.BEEHIVES) && (i = lv.get(BeehiveBlock.HONEY_LEVEL).intValue()) >= 5) {
            world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb(world, pos);
            ((BeehiveBlock)lv.getBlock()).takeHoney(world, lv, pos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
            return true;
        }
        return false;
    }

    private static boolean tryShearEntity(ServerWorld world, BlockPos pos) {
        List<Entity> list = world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);
        for (LivingEntity livingEntity : list) {
            Shearable lv2;
            if (!(livingEntity instanceof Shearable) || !(lv2 = (Shearable)((Object)livingEntity)).isShearable()) continue;
            lv2.sheared(SoundCategory.BLOCKS);
            return true;
        }
        return false;
    }
}

