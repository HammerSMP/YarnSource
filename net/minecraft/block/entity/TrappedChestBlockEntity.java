/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;

public class TrappedChestBlockEntity
extends ChestBlockEntity {
    public TrappedChestBlockEntity() {
        super(BlockEntityType.TRAPPED_CHEST);
    }

    @Override
    protected void onInvOpenOrClose() {
        super.onInvOpenOrClose();
        this.world.updateNeighborsAlways(this.pos.down(), this.getCachedState().getBlock());
    }
}

