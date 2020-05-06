/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface ModifiableWorld {
    public boolean setBlockState(BlockPos var1, BlockState var2, int var3);

    public boolean removeBlock(BlockPos var1, boolean var2);

    default public boolean breakBlock(BlockPos arg, boolean bl) {
        return this.breakBlock(arg, bl, null);
    }

    public boolean breakBlock(BlockPos var1, boolean var2, @Nullable Entity var3);

    default public boolean spawnEntity(Entity arg) {
        return false;
    }
}

