/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EnderChestInventory
extends BasicInventory {
    private EnderChestBlockEntity activeBlockEntity;

    public EnderChestInventory() {
        super(27);
    }

    public void setActiveBlockEntity(EnderChestBlockEntity arg) {
        this.activeBlockEntity = arg;
    }

    @Override
    public void readTags(ListTag arg) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, ItemStack.EMPTY);
        }
        for (int j = 0; j < arg.size(); ++j) {
            CompoundTag lv = arg.getCompound(j);
            int k = lv.getByte("Slot") & 0xFF;
            if (k < 0 || k >= this.size()) continue;
            this.setStack(k, ItemStack.fromTag(lv));
        }
    }

    @Override
    public ListTag getTags() {
        ListTag lv = new ListTag();
        for (int i = 0; i < this.size(); ++i) {
            ItemStack lv2 = this.getStack(i);
            if (lv2.isEmpty()) continue;
            CompoundTag lv3 = new CompoundTag();
            lv3.putByte("Slot", (byte)i);
            lv2.toTag(lv3);
            lv.add(lv3);
        }
        return lv;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity arg) {
        if (this.activeBlockEntity != null && !this.activeBlockEntity.canPlayerUse(arg)) {
            return false;
        }
        return super.canPlayerUse(arg);
    }

    @Override
    public void onOpen(PlayerEntity arg) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onOpen();
        }
        super.onOpen(arg);
    }

    @Override
    public void onClose(PlayerEntity arg) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onClose();
        }
        super.onClose(arg);
        this.activeBlockEntity = null;
    }
}

