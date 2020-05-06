/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen.slot;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Slot {
    private final int index;
    public final Inventory inventory;
    public int id;
    public final int x;
    public final int y;

    public Slot(Inventory arg, int i, int j, int k) {
        this.inventory = arg;
        this.index = i;
        this.x = j;
        this.y = k;
    }

    public void onStackChanged(ItemStack arg, ItemStack arg2) {
        int i = arg2.getCount() - arg.getCount();
        if (i > 0) {
            this.onCrafted(arg2, i);
        }
    }

    protected void onCrafted(ItemStack arg, int i) {
    }

    protected void onTake(int i) {
    }

    protected void onCrafted(ItemStack arg) {
    }

    public ItemStack onTakeItem(PlayerEntity arg, ItemStack arg2) {
        this.markDirty();
        return arg2;
    }

    public boolean canInsert(ItemStack arg) {
        return true;
    }

    public ItemStack getStack() {
        return this.inventory.getStack(this.index);
    }

    public boolean hasStack() {
        return !this.getStack().isEmpty();
    }

    public void setStack(ItemStack arg) {
        this.inventory.setStack(this.index, arg);
        this.markDirty();
    }

    public void markDirty() {
        this.inventory.markDirty();
    }

    public int getMaxStackAmount() {
        return this.inventory.getMaxCountPerStack();
    }

    public int getMaxStackAmount(ItemStack arg) {
        return this.getMaxStackAmount();
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return null;
    }

    public ItemStack takeStack(int i) {
        return this.inventory.removeStack(this.index, i);
    }

    public boolean canTakeItems(PlayerEntity arg) {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean doDrawHoveringEffect() {
        return true;
    }
}

