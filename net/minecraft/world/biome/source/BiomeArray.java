/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome.source;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeArray
implements BiomeAccess.Storage {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int HORIZONTAL_SECTION_COUNT = (int)Math.round(Math.log(16.0) / Math.log(2.0)) - 2;
    private static final int VERTICAL_SECTION_COUNT = (int)Math.round(Math.log(256.0) / Math.log(2.0)) - 2;
    public static final int DEFAULT_LENGTH = 1 << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT + VERTICAL_SECTION_COUNT;
    public static final int HORIZONTAL_BIT_MASK = (1 << HORIZONTAL_SECTION_COUNT) - 1;
    public static final int VERTICAL_BIT_MASK = (1 << VERTICAL_SECTION_COUNT) - 1;
    private final IndexedIterable<Biome> field_25831;
    private final Biome[] data;

    public BiomeArray(IndexedIterable<Biome> arg, Biome[] args) {
        this.field_25831 = arg;
        this.data = args;
    }

    private BiomeArray(IndexedIterable<Biome> arg) {
        this(arg, new Biome[DEFAULT_LENGTH]);
    }

    @Environment(value=EnvType.CLIENT)
    public BiomeArray(IndexedIterable<Biome> arg, int[] is) {
        this(arg);
        for (int i = 0; i < this.data.length; ++i) {
            int j = is[i];
            Biome lv = arg.get(j);
            if (lv == null) {
                LOGGER.warn("Received invalid biome id: " + j);
                this.data[i] = Biomes.PLAINS;
                continue;
            }
            this.data[i] = lv;
        }
    }

    public BiomeArray(IndexedIterable<Biome> arg, ChunkPos arg2, BiomeSource arg3) {
        this(arg);
        int i = arg2.getStartX() >> 2;
        int j = arg2.getStartZ() >> 2;
        for (int k = 0; k < this.data.length; ++k) {
            int l = k & HORIZONTAL_BIT_MASK;
            int m = k >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
            int n = k >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
            this.data[k] = arg3.getBiomeForNoiseGen(i + l, m, j + n);
        }
    }

    public BiomeArray(IndexedIterable<Biome> arg, ChunkPos arg2, BiomeSource arg3, @Nullable int[] is) {
        this(arg);
        int i = arg2.getStartX() >> 2;
        int j = arg2.getStartZ() >> 2;
        if (is != null) {
            for (int k = 0; k < is.length; ++k) {
                this.data[k] = arg.get(is[k]);
                if (this.data[k] != null) continue;
                int l = k & HORIZONTAL_BIT_MASK;
                int m = k >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
                int n = k >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
                this.data[k] = arg3.getBiomeForNoiseGen(i + l, m, j + n);
            }
        } else {
            for (int o = 0; o < this.data.length; ++o) {
                int p = o & HORIZONTAL_BIT_MASK;
                int q = o >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
                int r = o >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
                this.data[o] = arg3.getBiomeForNoiseGen(i + p, q, j + r);
            }
        }
    }

    public int[] toIntArray() {
        int[] is = new int[this.data.length];
        for (int i = 0; i < this.data.length; ++i) {
            is[i] = this.field_25831.getRawId(this.data[i]);
        }
        return is;
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int l = biomeX & HORIZONTAL_BIT_MASK;
        int m = MathHelper.clamp(biomeY, 0, VERTICAL_BIT_MASK);
        int n = biomeZ & HORIZONTAL_BIT_MASK;
        return this.data[m << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT | n << HORIZONTAL_SECTION_COUNT | l];
    }
}

