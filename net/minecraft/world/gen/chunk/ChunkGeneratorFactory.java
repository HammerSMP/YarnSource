/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

interface ChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> {
    public T create(IWorld var1, BiomeSource var2, C var3);
}

