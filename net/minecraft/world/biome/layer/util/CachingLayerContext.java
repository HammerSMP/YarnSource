/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 */
package net.minecraft.world.biome.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.SeedMixer;

public class CachingLayerContext
implements LayerSampleContext<CachingLayerSampler> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int cacheCapacity;
    private final PerlinNoiseSampler noiseSampler;
    private final long worldSeed;
    private long localSeed;

    public CachingLayerContext(int i, long l, long m) {
        this.worldSeed = CachingLayerContext.addSalt(l, m);
        this.noiseSampler = new PerlinNoiseSampler(new Random(l));
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25f);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.cacheCapacity = i;
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator arg) {
        return new CachingLayerSampler(this.cache, this.cacheCapacity, arg);
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator arg, CachingLayerSampler arg2) {
        return new CachingLayerSampler(this.cache, Math.min(1024, arg2.getCapacity() * 4), arg);
    }

    @Override
    public CachingLayerSampler createSampler(LayerOperator arg, CachingLayerSampler arg2, CachingLayerSampler arg3) {
        return new CachingLayerSampler(this.cache, Math.min(1024, Math.max(arg2.getCapacity(), arg3.getCapacity()) * 4), arg);
    }

    @Override
    public void initSeed(long l, long m) {
        long n = this.worldSeed;
        n = SeedMixer.mixSeed(n, l);
        n = SeedMixer.mixSeed(n, m);
        n = SeedMixer.mixSeed(n, l);
        this.localSeed = n = SeedMixer.mixSeed(n, m);
    }

    @Override
    public int nextInt(int i) {
        int j = (int)Math.floorMod(this.localSeed >> 24, (long)i);
        this.localSeed = SeedMixer.mixSeed(this.localSeed, this.worldSeed);
        return j;
    }

    @Override
    public PerlinNoiseSampler getNoiseSampler() {
        return this.noiseSampler;
    }

    private static long addSalt(long l, long m) {
        long n = m;
        n = SeedMixer.mixSeed(n, m);
        n = SeedMixer.mixSeed(n, m);
        n = SeedMixer.mixSeed(n, m);
        long o = l;
        o = SeedMixer.mixSeed(o, n);
        o = SeedMixer.mixSeed(o, n);
        o = SeedMixer.mixSeed(o, n);
        return o;
    }

    @Override
    public /* synthetic */ LayerSampler createSampler(LayerOperator arg) {
        return this.createSampler(arg);
    }
}

