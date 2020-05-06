/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.biome.source;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
    private final Storage storage;
    private final long seed;
    private final BiomeAccessType type;

    public BiomeAccess(Storage arg, long l, BiomeAccessType arg2) {
        this.storage = arg;
        this.seed = l;
        this.type = arg2;
    }

    public BiomeAccess withSource(BiomeSource arg) {
        return new BiomeAccess(arg, this.seed, this.type);
    }

    public Biome getBiome(BlockPos arg) {
        return this.type.getBiome(this.seed, arg.getX(), arg.getY(), arg.getZ(), this.storage);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome getBiome(double d, double e, double f) {
        int i = MathHelper.floor(d) >> 2;
        int j = MathHelper.floor(e) >> 2;
        int k = MathHelper.floor(f) >> 2;
        return this.getBiomeForNoiseGen(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome method_27344(BlockPos arg) {
        int i = arg.getX() >> 2;
        int j = arg.getY() >> 2;
        int k = arg.getZ() >> 2;
        return this.getBiomeForNoiseGen(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        return this.storage.getBiomeForNoiseGen(i, j, k);
    }

    public static interface Storage {
        public Biome getBiomeForNoiseGen(int var1, int var2, int var3);
    }
}

