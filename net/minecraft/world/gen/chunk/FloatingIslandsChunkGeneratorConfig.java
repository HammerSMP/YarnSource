/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class FloatingIslandsChunkGeneratorConfig
extends ChunkGeneratorConfig {
    private BlockPos center;

    public FloatingIslandsChunkGeneratorConfig withCenter(BlockPos arg) {
        this.center = arg;
        return this;
    }

    public BlockPos getCenter() {
        return this.center;
    }
}

