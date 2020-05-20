/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.class_5284;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class CavesChunkGeneratorConfig
extends class_5284 {
    public CavesChunkGeneratorConfig(ChunkGeneratorConfig arg) {
        super(arg);
        arg.ruinedPortalSpacing = 25;
        arg.ruinedPortalSeparation = 10;
    }

    @Override
    public int getBedrockFloorY() {
        return 0;
    }

    @Override
    public int getBedrockCeilingY() {
        return 127;
    }
}

