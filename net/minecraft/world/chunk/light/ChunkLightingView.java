/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingView;

public interface ChunkLightingView
extends LightingView {
    @Nullable
    public ChunkNibbleArray getLightSection(ChunkSectionPos var1);

    public int getLightLevel(BlockPos var1);

    public static enum Empty implements ChunkLightingView
    {
        INSTANCE;


        @Override
        @Nullable
        public ChunkNibbleArray getLightSection(ChunkSectionPos pos) {
            return null;
        }

        @Override
        public int getLightLevel(BlockPos arg) {
            return 0;
        }

        @Override
        public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
        }
    }
}

