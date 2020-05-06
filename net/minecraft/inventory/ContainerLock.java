/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package net.minecraft.inventory;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Immutable
public class ContainerLock {
    public static final ContainerLock EMPTY = new ContainerLock("");
    private final String key;

    public ContainerLock(String string) {
        this.key = string;
    }

    public boolean canOpen(ItemStack arg) {
        return this.key.isEmpty() || !arg.isEmpty() && arg.hasCustomName() && this.key.equals(arg.getName().getString());
    }

    public void toTag(CompoundTag arg) {
        if (!this.key.isEmpty()) {
            arg.putString("Lock", this.key);
        }
    }

    public static ContainerLock fromTag(CompoundTag arg) {
        if (arg.contains("Lock", 8)) {
            return new ContainerLock(arg.getString("Lock"));
        }
        return EMPTY;
    }
}

