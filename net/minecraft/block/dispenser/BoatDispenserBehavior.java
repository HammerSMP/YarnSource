/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BoatDispenserBehavior
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior itemDispenser = new ItemDispenserBehavior();
    private final BoatEntity.Type boatType;

    public BoatDispenserBehavior(BoatEntity.Type arg) {
        this.boatType = arg;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
        void i;
        Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
        World lv2 = arg.getWorld();
        double d = arg.getX() + (double)((float)lv.getOffsetX() * 1.125f);
        double e = arg.getY() + (double)((float)lv.getOffsetY() * 1.125f);
        double f = arg.getZ() + (double)((float)lv.getOffsetZ() * 1.125f);
        BlockPos lv3 = arg.getBlockPos().offset(lv);
        if (lv2.getFluidState(lv3).matches(FluidTags.WATER)) {
            double g = 1.0;
        } else if (lv2.getBlockState(lv3).isAir() && lv2.getFluidState(lv3.down()).matches(FluidTags.WATER)) {
            double h = 0.0;
        } else {
            return this.itemDispenser.dispense(arg, arg2);
        }
        BoatEntity lv4 = new BoatEntity(lv2, d, e + i, f);
        lv4.setBoatType(this.boatType);
        lv4.yaw = lv.asRotation();
        lv2.spawnEntity(lv4);
        arg2.decrement(1);
        return arg2;
    }

    @Override
    protected void playSound(BlockPointer arg) {
        arg.getWorld().syncWorldEvent(1000, arg.getBlockPos(), 0);
    }
}

