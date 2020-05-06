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
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class LecternScreenHandler
extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public LecternScreenHandler(int i) {
        this(i, new BasicInventory(1), new ArrayPropertyDelegate(1));
    }

    public LecternScreenHandler(int i, Inventory arg, PropertyDelegate arg2) {
        super(ScreenHandlerType.LECTERN, i);
        LecternScreenHandler.checkSize(arg, 1);
        LecternScreenHandler.checkDataCount(arg2, 1);
        this.inventory = arg;
        this.propertyDelegate = arg2;
        this.addSlot(new Slot(arg, 0, 0, 0){

            @Override
            public void markDirty() {
                super.markDirty();
                LecternScreenHandler.this.onContentChanged(this.inventory);
            }
        });
        this.addProperties(arg2);
    }

    @Override
    public boolean onButtonClick(PlayerEntity arg, int i) {
        if (i >= 100) {
            int j = i - 100;
            this.setProperty(0, j);
            return true;
        }
        switch (i) {
            case 2: {
                int k = this.propertyDelegate.get(0);
                this.setProperty(0, k + 1);
                return true;
            }
            case 1: {
                int l = this.propertyDelegate.get(0);
                this.setProperty(0, l - 1);
                return true;
            }
            case 3: {
                if (!arg.canModifyBlocks()) {
                    return false;
                }
                ItemStack lv = this.inventory.removeStack(0);
                this.inventory.markDirty();
                if (!arg.inventory.insertStack(lv)) {
                    arg.dropItem(lv, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setProperty(int i, int j) {
        super.setProperty(i, j);
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return this.inventory.canPlayerUse(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getBookItem() {
        return this.inventory.getStack(0);
    }

    @Environment(value=EnvType.CLIENT)
    public int getPage() {
        return this.propertyDelegate.get(0);
    }
}

