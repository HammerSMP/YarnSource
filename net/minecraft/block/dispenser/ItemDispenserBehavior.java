/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ItemDispenserBehavior
implements DispenserBehavior {
    @Override
    public final ItemStack dispense(BlockPointer arg, ItemStack arg2) {
        ItemStack lv = this.dispenseSilently(arg, arg2);
        this.playSound(arg);
        this.spawnParticles(arg, arg.getBlockState().get(DispenserBlock.FACING));
        return lv;
    }

    protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
        Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
        Position lv2 = DispenserBlock.getOutputLocation(arg);
        ItemStack lv3 = arg2.split(1);
        ItemDispenserBehavior.spawnItem(arg.getWorld(), lv3, 6, lv, lv2);
        return arg2;
    }

    public static void spawnItem(World arg, ItemStack arg2, int i, Direction arg3, Position arg4) {
        double d = arg4.getX();
        double e = arg4.getY();
        double f = arg4.getZ();
        e = arg3.getAxis() == Direction.Axis.Y ? (e -= 0.125) : (e -= 0.15625);
        ItemEntity lv = new ItemEntity(arg, d, e, f, arg2);
        double g = arg.random.nextDouble() * 0.1 + 0.2;
        lv.setVelocity(arg.random.nextGaussian() * (double)0.0075f * (double)i + (double)arg3.getOffsetX() * g, arg.random.nextGaussian() * (double)0.0075f * (double)i + (double)0.2f, arg.random.nextGaussian() * (double)0.0075f * (double)i + (double)arg3.getOffsetZ() * g);
        arg.spawnEntity(lv);
    }

    protected void playSound(BlockPointer arg) {
        arg.getWorld().syncWorldEvent(1000, arg.getBlockPos(), 0);
    }

    protected void spawnParticles(BlockPointer arg, Direction arg2) {
        arg.getWorld().syncWorldEvent(2000, arg.getBlockPos(), arg2.getId());
    }
}

