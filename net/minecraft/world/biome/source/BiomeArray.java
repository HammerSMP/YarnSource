/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome.source;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeArray
implements BiomeAccess.Storage {
    private static final Logger field_21813 = LogManager.getLogger();
    private static final int HORIZONTAL_SECTION_COUNT = (int)Math.round(Math.log(16.0) / Math.log(2.0)) - 2;
    private static final int VERTICAL_SECTION_COUNT = (int)Math.round(Math.log(256.0) / Math.log(2.0)) - 2;
    public static final int DEFAULT_LENGTH = 1 << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT + VERTICAL_SECTION_COUNT;
    public static final int HORIZONTAL_BIT_MASK = (1 << HORIZONTAL_SECTION_COUNT) - 1;
    public static final int VERTICAL_BIT_MASK = (1 << VERTICAL_SECTION_COUNT) - 1;
    private final Biome[] data;

    public BiomeArray(Biome[] args) {
        this.data = args;
    }

    private BiomeArray() {
        this(new Biome[DEFAULT_LENGTH]);
    }

    public BiomeArray(PacketByteBuf arg) {
        this();
        for (int i = 0; i < this.data.length; ++i) {
            int j = arg.readInt();
            Biome lv = (Biome)Registry.BIOME.get(j);
            if (lv == null) {
                field_21813.warn("Received invalid biome id: " + j);
                this.data[i] = Biomes.PLAINS;
                continue;
            }
            this.data[i] = lv;
        }
    }

    public BiomeArray(ChunkPos arg, BiomeSource arg2) {
        this();
        int i = arg.getStartX() >> 2;
        int j = arg.getStartZ() >> 2;
        for (int k = 0; k < this.data.length; ++k) {
            int l = k & HORIZONTAL_BIT_MASK;
            int m = k >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
            int n = k >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
            this.data[k] = arg2.getBiomeForNoiseGen(i + l, m, j + n);
        }
    }

    public BiomeArray(ChunkPos arg, BiomeSource arg2, @Nullable int[] is) {
        this();
        int i = arg.getStartX() >> 2;
        int j = arg.getStartZ() >> 2;
        if (is != null) {
            for (int k = 0; k < is.length; ++k) {
                this.data[k] = (Biome)Registry.BIOME.get(is[k]);
                if (this.data[k] != null) continue;
                int l = k & HORIZONTAL_BIT_MASK;
                int m = k >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
                int n = k >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
                this.data[k] = arg2.getBiomeForNoiseGen(i + l, m, j + n);
            }
        } else {
            for (int o = 0; o < this.data.length; ++o) {
                int p = o & HORIZONTAL_BIT_MASK;
                int q = o >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
                int r = o >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
                this.data[o] = arg2.getBiomeForNoiseGen(i + p, q, j + r);
            }
        }
    }

    public int[] toIntArray() {
        int[] is = new int[this.data.length];
        for (int i = 0; i < this.data.length; ++i) {
            is[i] = Registry.BIOME.getRawId(this.data[i]);
        }
        return is;
    }

    public void toPacket(PacketByteBuf arg) {
        for (Biome lv : this.data) {
            arg.writeInt(Registry.BIOME.getRawId(lv));
        }
    }

    public BiomeArray copy() {
        return new BiomeArray((Biome[])this.data.clone());
    }

    @Override
    public Biome getBiomeForNoiseGen(int i, int j, int k) {
        int l = i & HORIZONTAL_BIT_MASK;
        int m = MathHelper.clamp(j, 0, VERTICAL_BIT_MASK);
        int n = k & HORIZONTAL_BIT_MASK;
        return this.data[m << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT | n << HORIZONTAL_SECTION_COUNT | l];
    }
}

