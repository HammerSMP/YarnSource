/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior
extends ItemDispenserBehavior {
    private boolean success = true;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean bl) {
        this.success = bl;
    }

    @Override
    protected void playSound(BlockPointer arg) {
        arg.getWorld().syncWorldEvent(this.isSuccess() ? 1000 : 1001, arg.getBlockPos(), 0);
    }
}

