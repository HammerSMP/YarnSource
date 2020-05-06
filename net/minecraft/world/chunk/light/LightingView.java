/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public interface LightingView {
    default public void updateSectionStatus(BlockPos arg, boolean bl) {
        this.updateSectionStatus(ChunkSectionPos.from(arg), bl);
    }

    public void updateSectionStatus(ChunkSectionPos var1, boolean var2);
}

