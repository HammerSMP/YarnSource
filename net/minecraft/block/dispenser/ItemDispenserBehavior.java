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

    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction lv = pointer.getBlockState().get(DispenserBlock.FACING);
        Position lv2 = DispenserBlock.getOutputLocation(pointer);
        ItemStack lv3 = stack.split(1);
        ItemDispenserBehavior.spawnItem(pointer.getWorld(), lv3, 6, lv, lv2);
        return stack;
    }

    public static void spawnItem(World world, ItemStack stack, int offset, Direction side, Position pos) {
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        e = side.getAxis() == Direction.Axis.Y ? (e -= 0.125) : (e -= 0.15625);
        ItemEntity lv = new ItemEntity(world, d, e, f, stack);
        double g = world.random.nextDouble() * 0.1 + 0.2;
        lv.setVelocity(world.random.nextGaussian() * (double)0.0075f * (double)offset + (double)side.getOffsetX() * g, world.random.nextGaussian() * (double)0.0075f * (double)offset + (double)0.2f, world.random.nextGaussian() * (double)0.0075f * (double)offset + (double)side.getOffsetZ() * g);
        world.spawnEntity(lv);
    }

    protected void playSound(BlockPointer pointer) {
        pointer.getWorld().syncWorldEvent(1000, pointer.getBlockPos(), 0);
    }

    protected void spawnParticles(BlockPointer pointer, Direction side) {
        pointer.getWorld().syncWorldEvent(2000, pointer.getBlockPos(), side.getId());
    }
}

