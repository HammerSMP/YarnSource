/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class HopperScreenHandler
extends ScreenHandler {
    private final Inventory inventory;

    public HopperScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, new SimpleInventory(5));
    }

    public HopperScreenHandler(int i, PlayerInventory arg, Inventory arg2) {
        super(ScreenHandlerType.HOPPER, i);
        this.inventory = arg2;
        HopperScreenHandler.checkSize(arg2, 5);
        arg2.onOpen(arg.player);
        int j = 51;
        for (int k = 0; k < 5; ++k) {
            this.addSlot(new Slot(arg2, k, 44 + k * 18, 20));
        }
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(arg, m + l * 9 + 9, 8 + m * 18, l * 18 + 51));
            }
        }
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(arg, n, 8 + n * 18, 109));
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
            if (i < this.inventory.size() ? !this.insertItem(lv3, this.inventory.size(), this.slots.size(), true) : !this.insertItem(lv3, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            } else {
                lv2.markDirty();
            }
        }
        return lv;
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.inventory.onClose(arg);
    }
}

