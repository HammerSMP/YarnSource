/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostWalkerEnchantment
extends Enchantment {
    public FrostWalkerEnchantment(Enchantment.Rarity arg, EquipmentSlot ... args) {
        super(arg, EnchantmentTarget.ARMOR_FEET, args);
    }

    @Override
    public int getMinPower(int i) {
        return i * 10;
    }

    @Override
    public int getMaxPower(int i) {
        return this.getMinPower(i) + 15;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public static void freezeWater(LivingEntity arg, World arg2, BlockPos arg3, int i) {
        if (!arg.isOnGround()) {
            return;
        }
        BlockState lv = Blocks.FROSTED_ICE.getDefaultState();
        float f = Math.min(16, 2 + i);
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (BlockPos lv3 : BlockPos.iterate(arg3.add(-f, -1.0, -f), arg3.add(f, -1.0, f))) {
            BlockState lv5;
            if (!lv3.isWithinDistance(arg.getPos(), (double)f)) continue;
            lv2.set(lv3.getX(), lv3.getY() + 1, lv3.getZ());
            BlockState lv4 = arg2.getBlockState(lv2);
            if (!lv4.isAir() || (lv5 = arg2.getBlockState(lv3)).getMaterial() != Material.WATER || lv5.get(FluidBlock.LEVEL) != 0 || !lv.canPlaceAt(arg2, lv3) || !arg2.canPlace(lv, lv3, ShapeContext.absent())) continue;
            arg2.setBlockState(lv3, lv);
            arg2.getBlockTickScheduler().schedule(lv3, Blocks.FROSTED_ICE, MathHelper.nextInt(arg.getRandom(), 60, 120));
        }
    }

    @Override
    public boolean canAccept(Enchantment arg) {
        return super.canAccept(arg) && arg != Enchantments.DEPTH_STRIDER;
    }
}

