/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Clearable;

public class JukeboxBlockEntity
extends BlockEntity
implements Clearable {
    private ItemStack record = ItemStack.EMPTY;

    public JukeboxBlockEntity() {
        super(BlockEntityType.JUKEBOX);
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        if (arg2.contains("RecordItem", 10)) {
            this.setRecord(ItemStack.fromTag(arg2.getCompound("RecordItem")));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        if (!this.getRecord().isEmpty()) {
            arg.put("RecordItem", this.getRecord().toTag(new CompoundTag()));
        }
        return arg;
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack arg) {
        this.record = arg;
        this.markDirty();
    }

    @Override
    public void clear() {
        this.setRecord(ItemStack.EMPTY);
    }
}

