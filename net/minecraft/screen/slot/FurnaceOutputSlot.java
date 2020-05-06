/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen.slot;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FurnaceOutputSlot
extends Slot {
    private final PlayerEntity player;
    private int amount;

    public FurnaceOutputSlot(PlayerEntity arg, Inventory arg2, int i, int j, int k) {
        super(arg2, i, j, k);
        this.player = arg;
    }

    @Override
    public boolean canInsert(ItemStack arg) {
        return false;
    }

    @Override
    public ItemStack takeStack(int i) {
        if (this.hasStack()) {
            this.amount += Math.min(i, this.getStack().getCount());
        }
        return super.takeStack(i);
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity arg, ItemStack arg2) {
        this.onCrafted(arg2);
        super.onTakeItem(arg, arg2);
        return arg2;
    }

    @Override
    protected void onCrafted(ItemStack arg, int i) {
        this.amount += i;
        this.onCrafted(arg);
    }

    @Override
    protected void onCrafted(ItemStack arg) {
        arg.onCraft(this.player.world, this.player, this.amount);
        if (!this.player.world.isClient && this.inventory instanceof AbstractFurnaceBlockEntity) {
            ((AbstractFurnaceBlockEntity)this.inventory).dropExperience(this.player);
        }
        this.amount = 0;
    }
}

