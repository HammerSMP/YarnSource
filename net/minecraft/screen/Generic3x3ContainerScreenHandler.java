/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class Generic3x3ContainerScreenHandler
extends ScreenHandler {
    private final Inventory inventory;

    public Generic3x3ContainerScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, new BasicInventory(9));
    }

    public Generic3x3ContainerScreenHandler(int i, PlayerInventory arg, Inventory arg2) {
        super(ScreenHandlerType.GENERIC_3X3, i);
        Generic3x3ContainerScreenHandler.checkSize(arg2, 9);
        this.inventory = arg2;
        arg2.onOpen(arg.player);
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                this.addSlot(new Slot(arg2, k + j * 3, 62 + k * 18, 17 + j * 18));
            }
        }
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(arg, m + l * 9 + 9, 8 + m * 18, 84 + l * 18));
            }
        }
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(arg, n, 8 + n * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return this.inventory.canPlayerUse(arg);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i < 9 ? !this.insertItem(lv3, 9, 45, true) : !this.insertItem(lv3, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            } else {
                lv2.markDirty();
            }
            if (lv3.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
            lv2.onTakeItem(arg, lv3);
        }
        return lv;
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.inventory.onClose(arg);
    }
}

