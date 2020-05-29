/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class GenericContainerScreenHandler
extends ScreenHandler {
    private final Inventory inventory;
    private final int rows;

    private GenericContainerScreenHandler(ScreenHandlerType<?> arg, int i, PlayerInventory arg2, int j) {
        this(arg, i, arg2, new SimpleInventory(9 * j), j);
    }

    public static GenericContainerScreenHandler createGeneric9x1(int i, PlayerInventory arg) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, i, arg, 1);
    }

    public static GenericContainerScreenHandler createGeneric9x2(int i, PlayerInventory arg) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X2, i, arg, 2);
    }

    public static GenericContainerScreenHandler createGeneric9x3(int i, PlayerInventory arg) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, i, arg, 3);
    }

    public static GenericContainerScreenHandler createGeneric9x4(int i, PlayerInventory arg) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, i, arg, 4);
    }

    public static GenericContainerScreenHandler createGeneric9x5(int i, PlayerInventory arg) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, i, arg, 5);
    }

    public static GenericContainerScreenHandler createGeneric9x6(int i, PlayerInventory arg) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, i, arg, 6);
    }

    public static GenericContainerScreenHandler createGeneric9x3(int i, PlayerInventory arg, Inventory arg2) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, i, arg, arg2, 3);
    }

    public static GenericContainerScreenHandler createGeneric9x6(int i, PlayerInventory arg, Inventory arg2) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, i, arg, arg2, 6);
    }

    public GenericContainerScreenHandler(ScreenHandlerType<?> arg, int i, PlayerInventory arg2, Inventory arg3, int j) {
        super(arg, i);
        GenericContainerScreenHandler.checkSize(arg3, j * 9);
        this.inventory = arg3;
        this.rows = j;
        arg3.onOpen(arg2.player);
        int k = (this.rows - 4) * 18;
        for (int l = 0; l < this.rows; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(arg3, m + l * 9, 8 + m * 18, 18 + l * 18));
            }
        }
        for (int n = 0; n < 3; ++n) {
            for (int o = 0; o < 9; ++o) {
                this.addSlot(new Slot(arg2, o + n * 9 + 9, 8 + o * 18, 103 + n * 18 + k));
            }
        }
        for (int p = 0; p < 9; ++p) {
            this.addSlot(new Slot(arg2, p, 8 + p * 18, 161 + k));
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
            if (i < this.rows * 9 ? !this.insertItem(lv3, this.rows * 9, this.slots.size(), true) : !this.insertItem(lv3, 0, this.rows * 9, false)) {
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

    public Inventory getInventory() {
        return this.inventory;
    }

    @Environment(value=EnvType.CLIENT)
    public int getRows() {
        return this.rows;
    }
}

