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
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;

public class ShulkerBoxScreenHandler
extends ScreenHandler {
    private final Inventory inventory;

    public ShulkerBoxScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, new BasicInventory(27));
    }

    public ShulkerBoxScreenHandler(int i, PlayerInventory arg, Inventory arg2) {
        super(ScreenHandlerType.SHULKER_BOX, i);
        ShulkerBoxScreenHandler.checkSize(arg2, 27);
        this.inventory = arg2;
        arg2.onOpen(arg.player);
        int j = 3;
        int k = 9;
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new ShulkerBoxSlot(arg2, m + l * 9, 8 + m * 18, 18 + l * 18));
            }
        }
        for (int n = 0; n < 3; ++n) {
            for (int o = 0; o < 9; ++o) {
                this.addSlot(new Slot(arg, o + n * 9 + 9, 8 + o * 18, 84 + n * 18));
            }
        }
        for (int p = 0; p < 9; ++p) {
            this.addSlot(new Slot(arg, p, 8 + p * 18, 142));
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

