/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public interface BlockEntityProvider {
    @Nullable
    public BlockEntity createBlockEntity(BlockView var1);
}

