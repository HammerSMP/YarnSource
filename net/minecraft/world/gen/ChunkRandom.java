/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen;

import java.util.Random;

public class ChunkRandom
extends Random {
    private int sampleCount;

    public ChunkRandom() {
    }

    public ChunkRandom(long seed) {
        super(seed);
    }

    public void consume(int count) {
        for (int j = 0; j < count; ++j) {
            this.next(1);
        }
    }

    @Override
    protected int next(int bound) {
        ++this.sampleCount;
        return super.next(bound);
    }

    public long setTerrainSeed(int chunkX, int chunkZ) {
        long l = (long)chunkX * 341873128712L + (long)chunkZ * 132897987541L;
        this.setSeed(l);
        return l;
    }

    public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
        this.setSeed(worldSeed);
        long m = this.nextLong() | 1L;
        long n = this.nextLong() | 1L;
        long o = (long)blockX * m + (long)blockZ * n ^ worldSeed;
        this.setSeed(o);
        return o;
    }

    public long setDecoratorSeed(long populationSeed, int index, int step) {
        long m = populationSeed + (long)index + (long)(10000 * step);
        this.setSeed(m);
        return m;
    }

    public long setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
        this.setSeed(worldSeed);
        long m = this.nextLong();
        long n = this.nextLong();
        long o = (long)chunkX * m ^ (long)chunkZ * n ^ worldSeed;
        this.setSeed(o);
        return o;
    }

    public long setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
        long m = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + (long)salt;
        this.setSeed(m);
        return m;
    }

    public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
        return new Random(worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler);
    }
}

