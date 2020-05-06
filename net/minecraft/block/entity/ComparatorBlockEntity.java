/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public class ComparatorBlockEntity
extends BlockEntity {
    private int outputSignal;

    public ComparatorBlockEntity() {
        super(BlockEntityType.COMPARATOR);
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        arg.putInt("OutputSignal", this.outputSignal);
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.outputSignal = arg2.getInt("OutputSignal");
    }

    public int getOutputSignal() {
        return this.outputSignal;
    }

    public void setOutputSignal(int i) {
        this.outputSignal = i;
    }
}

