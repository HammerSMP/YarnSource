/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem
extends Item {
    public ShearsItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public boolean postMine(ItemStack arg2, World arg22, BlockState arg3, BlockPos arg4, LivingEntity arg5) {
        if (!arg22.isClient && !arg3.getBlock().isIn(BlockTags.FIRE)) {
            arg2.damage(1, arg5, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        if (arg3.isIn(BlockTags.LEAVES) || arg3.isOf(Blocks.COBWEB) || arg3.isOf(Blocks.GRASS) || arg3.isOf(Blocks.FERN) || arg3.isOf(Blocks.DEAD_BUSH) || arg3.isOf(Blocks.VINE) || arg3.isOf(Blocks.TRIPWIRE) || arg3.isIn(BlockTags.WOOL)) {
            return true;
        }
        return super.postMine(arg2, arg22, arg3, arg4, arg5);
    }

    @Override
    public boolean isEffectiveOn(BlockState arg) {
        return arg.isOf(Blocks.COBWEB) || arg.isOf(Blocks.REDSTONE_WIRE) || arg.isOf(Blocks.TRIPWIRE);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack arg, BlockState arg2) {
        if (arg2.isOf(Blocks.COBWEB) || arg2.isIn(BlockTags.LEAVES)) {
            return 15.0f;
        }
        if (arg2.isIn(BlockTags.WOOL)) {
            return 5.0f;
        }
        return super.getMiningSpeedMultiplier(arg, arg2);
    }
}

