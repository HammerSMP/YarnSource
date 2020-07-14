/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public abstract class ProjectileDispenserBehavior
extends ItemDispenserBehavior {
    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld lv = pointer.getWorld();
        Position lv2 = DispenserBlock.getOutputLocation(pointer);
        Direction lv3 = pointer.getBlockState().get(DispenserBlock.FACING);
        ProjectileEntity lv4 = this.createProjectile(lv, lv2, stack);
        lv4.setVelocity(lv3.getOffsetX(), (float)lv3.getOffsetY() + 0.1f, lv3.getOffsetZ(), this.getForce(), this.getVariation());
        lv.spawnEntity(lv4);
        stack.decrement(1);
        return stack;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.getWorld().syncWorldEvent(1002, pointer.getBlockPos(), 0);
    }

    protected abstract ProjectileEntity createProjectile(World var1, Position var2, ItemStack var3);

    protected float getVariation() {
        return 6.0f;
    }

    protected float getForce() {
        return 1.1f;
    }
}

