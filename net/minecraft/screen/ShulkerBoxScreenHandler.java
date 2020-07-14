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
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.screen.slot.Slot;

public class ShulkerBoxScreenHandler
extends ScreenHandler {
    private final Inventory inventory;

    public ShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(27));
    }

    public ShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.SHULKER_BOX, syncId);
        ShulkerBoxScreenHandler.checkSize(inventory, 27);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        int j = 3;
        int k = 9;
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new ShulkerBoxSlot(inventory, m + l * 9, 8 + m * 18, 18 + l * 18));
            }
        }
        for (int n = 0; n < 3; ++n) {
            for (int o = 0; o < 9; ++o) {
                this.addSlot(new Slot(playerInventory, o + n * 9 + 9, 8 + o * 18, 84 + n * 18));
            }
        }
        for (int p = 0; p < 9; ++p) {
            this.addSlot(new Slot(playerInventory, p, 8 + p * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(index);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (index < this.inventory.size() ? !this.insertItem(lv3, this.inventory.size(), this.slots.size(), true) : !this.insertItem(lv3, 0, this.inventory.size(), false)) {
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
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}

