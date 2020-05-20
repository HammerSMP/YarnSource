/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.source;

import net.minecraft.util.registry.Registry;

public class BiomeSourceType {
    public static final BiomeSourceType CHECKERBOARD = BiomeSourceType.register("checkerboard");
    public static final BiomeSourceType FIXED = BiomeSourceType.register("fixed");
    public static final BiomeSourceType VANILLA_LAYERED = BiomeSourceType.register("vanilla_layered");
    public static final BiomeSourceType THE_END = BiomeSourceType.register("the_end");
    public static final BiomeSourceType MULTI_NOISE = BiomeSourceType.register("multi_noise");

    private static BiomeSourceType register(String string) {
        return Registry.register(Registry.BIOME_SOURCE_TYPE, string, new BiomeSourceType());
    }
}

