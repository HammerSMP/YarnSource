/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior
extends ItemDispenserBehavior {
    protected boolean success = true;

    @Override
    protected void playSound(BlockPointer arg) {
        arg.getWorld().syncWorldEvent(this.success ? 1000 : 1001, arg.getBlockPos(), 0);
    }
}

