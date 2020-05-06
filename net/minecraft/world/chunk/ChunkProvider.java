/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;

public interface ChunkProvider {
    @Nullable
    public BlockView getChunk(int var1, int var2);

    default public void onLightUpdate(LightType arg, ChunkSectionPos arg2) {
    }

    public BlockView getWorld();
}

