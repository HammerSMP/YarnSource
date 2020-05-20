/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.class_5284;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class OverworldChunkGeneratorConfig
extends class_5284 {
    private final boolean old;

    public OverworldChunkGeneratorConfig() {
        this(new ChunkGeneratorConfig(), false);
    }

    public OverworldChunkGeneratorConfig(ChunkGeneratorConfig arg, boolean bl) {
        super(arg);
        this.old = bl;
    }

    @Override
    public int getBedrockFloorY() {
        return 0;
    }

    public boolean isOld() {
        return this.old;
    }
}

