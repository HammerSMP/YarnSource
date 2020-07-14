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

    public HorseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, final HorseBaseEntity entity) {
        super(null, syncId);
        this.inventory = inventory;
        this.entity = entity;
        int j = 3;
        inventory.onOpen(playerInventory.player);
        int k = -18;
        this.addSlot(new Slot(inventory, 0, 8, 18){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasStack() && entity.canBeSaddled();
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return entity.canBeSaddled();
            }
        });
        this.addSlot(new Slot(inventory, 1, 8, 36){

            @Override
            public boolean canInsert(ItemStack stack) {
                return entity.canEquip(stack);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public boolean doDrawHoveringEffect() {
                return entity.canEquip();
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        if (entity instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)entity).hasChest()) {
            for (int l = 0; l < 3; ++l) {
                for (int m = 0; m < ((AbstractDonkeyEntity)entity).getInventoryColumns(); ++m) {
                    this.addSlot(new Slot(inventory, 2 + m + l * ((AbstractDonkeyEntity)entity).getInventoryColumns(), 80 + m * 18, 18 + l * 18));
                }
            }
        }
        for (int n = 0; n < 3; ++n) {
            for (int o = 0; o < 9; ++o) {
                this.addSlot(new Slot(playerInventory, o + n * 9 + 9, 8 + o * 18, 102 + n * 18 + -18));
            }
        }
        for (int p = 0; p < 9; ++p) {
            this.addSlot(new Slot(playerInventory, p, 8 + p * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player) && this.entity.isAlive() && this.entity.distanceTo(player) < 8.0f;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(index);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            int j = this.inventory.size();
            if (index < j) {
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
                if (index >= m && index < n ? !this.insertItem(lv3, k, l, false) : (index >= k && index < l ? !this.insertItem(lv3, m, n, false) : !this.insertItem(lv3, m, l, false))) {
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
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}

