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
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HorseScreenHandler
extends ScreenHandler {
    private final Inventory inventory;
    private final HorseBaseEntity entity;

    public HorseScreenHandler(int i, PlayerInventory arg, Inventory arg2, final HorseBaseEntity arg3) {
        super(null, i);
        this.inventory = arg2;
        this.entity = arg3;
        int j = 3;
        arg2.onOpen(arg.player);
        int k = -18;
        this.addSlot(new Slot(arg2, 0, 8, 18){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg.getItem() == Items.SADDLE && !this.hasStack() && arg3.canBeSaddled();
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return arg3.canBeSaddled();
            }
        });
        this.addSlot(new Slot(arg2, 1, 8, 36){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg3.canEquip(arg);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return arg3.canEquip();
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        if (arg3 instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)arg3).hasChest()) {
            for (int l = 0; l < 3; ++l) {
                for (int m = 0; m < ((AbstractDonkeyEntity)arg3).getInventoryColumns(); ++m) {
                    this.addSlot(new Slot(arg2, 2 + m + l * ((AbstractDonkeyEntity)arg3).getInventoryColumns(), 80 + m * 18, 18 + l * 18));
                }
            }
        }
        for (int n = 0; n < 3; ++n) {
            for (int o = 0; o < 9; ++o) {
                this.addSlot(new Slot(arg, o + n * 9 + 9, 8 + o * 18, 102 + n * 18 + -18));
            }
        }
        for (int p = 0; p < 9; ++p) {
            this.addSlot(new Slot(arg, p, 8 + p * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return this.inventory.canPlayerUse(arg) && this.entity.isAlive() && this.entity.distanceTo(arg) < 8.0f;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            int j = this.inventory.size();
            if (i < j) {
                if (!this.insertItem(lv3, j, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(lv3) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(lv3, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(lv3)) {
                if (!this.insertItem(lv3, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (j <= 2 || !this.insertItem(lv3, 2, j, false)) {
                int l;
                int k = j;
                int m = l = k + 27;
                int n = m + 9;
                if (i >= m && i < n ? !this.insertItem(lv3, k, l, false) : (i >= k && i < l ? !this.insertItem(lv3, m, n, false) : !this.insertItem(lv3, m, l, false))) {
                    return ItemStack.EMPTY;
                }
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

