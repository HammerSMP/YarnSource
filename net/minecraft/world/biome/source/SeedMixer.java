/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

public class SeedMixer {
    public static long mixSeed(long l, long m) {
        l *= l * 6364136223846793005L + 1442695040888963407L;
        return l += m;
    }
}

