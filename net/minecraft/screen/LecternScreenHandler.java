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
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
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

    public LecternScreenHandler(int syncId) {
        this(syncId, new SimpleInventory(1), new ArrayPropertyDelegate(1));
    }

    public LecternScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerType.LECTERN, syncId);
        LecternScreenHandler.checkSize(inventory, 1);
        LecternScreenHandler.checkDataCount(propertyDelegate, 1);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new Slot(inventory, 0, 0, 0){

            @Override
            public void markDirty() {
                super.markDirty();
                LecternScreenHandler.this.onContentChanged(this.inventory);
            }
        });
        this.addProperties(propertyDelegate);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 100) {
            int j = id - 100;
            this.setProperty(0, j);
            return true;
        }
        switch (id) {
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
                if (!player.canModifyBlocks()) {
                    return false;
                }
                ItemStack lv = this.inventory.removeStack(0);
                this.inventory.markDirty();
                if (!player.inventory.insertStack(lv)) {
                    player.dropItem(lv, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
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

